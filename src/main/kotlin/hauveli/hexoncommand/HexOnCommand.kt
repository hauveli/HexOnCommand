package hauveli.hexoncommand

//hexcasting:
// import net.minecraft.nbt.CompoundTag
import at.petrak.hexcasting.common.lib.HexAttributes
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.commands.Commands.literal
// import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.InteractionHand


// import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking


class HexOnCommand : ModInitializer {

	object HexOnCommandNetworking {
		// JvmField because it needs a public getter. Do I want to do this some other way?
		@JvmField
		val SYNC_TAG_PACKET = ResourceLocation("hexoncommand", "sync_magical_tag")
	}

	// from: https://github.com/FallingColors/HexMod/blob/532fe9a60138544112e096812c7aefb78b3d7364/Common/src/main/java/at/petrak/hexcasting/common/items/ItemStaff.java#L28
	fun handleCastingGridPacket(
		server: MinecraftServer?,
		player: ServerPlayer
	) {
		// Feeble Mind check
		if (player.getAttributeValue(HexAttributes.FEEBLE_MIND) > 0.0 ||
			!player.tags.contains("magical")) {
			// InteractionResultHolder.fail(player.getItemInHand(hand))
			// can't do this from outside of Item#use?
			return
		}
		val level = player.serverLevel()
		val hand = InteractionHand.MAIN_HAND

		if (player.isShiftKeyDown) {
			if (!level.isClientSide) {
				player.playSound(HexSounds.STAFF_RESET, 1f, 1f)
			} else if (player is ServerPlayer) { // I really don't get the point of this check
				IXplatAbstractions.INSTANCE.clearCastingData(player)
				val packet = MsgClearSpiralPatternsS2C(player.uuid)
				IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, packet)
				IXplatAbstractions.INSTANCE.sendPacketTracking(player, packet)
			}
		}

		// Server-side only logic
		if (!level.isClientSide) {
			val vm = IXplatAbstractions.INSTANCE.getStaffcastVM(player, hand)
			val patterns = IXplatAbstractions.INSTANCE.getPatternsSavedInUi(player)
			val descs = vm.generateDescs()
			IXplatAbstractions.INSTANCE.sendPacketToPlayer(
				player,
				MsgOpenSpellGuiS2C(
					hand,
					patterns,
					descs.first,
					descs.second,
					0
				)
			)
		}

		// no Mishap penalty?
		// todo: copypaste that too or what?
		// player.awardStat(Stats.ITEM_USED.get(player.mainHandItem.item))
		//        player.gameEvent(GameEvent.ITEM_INTERACT_START);
		return // InteractionResultHolder.success<T?>(player.getItemInHand(hand))
	}

	override fun onInitialize() {

		// I don't know how to obtain a reference to MixinEntityTagSync.syncTag from this scope
		ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEvents.Join { handler: ServerGamePacketListenerImpl?, sender: PacketSender?, server: MinecraftServer? ->
			val player = handler!!.getPlayer() // can this player be non-server?
			val hasTag = player.tags.contains("magical")
			val buf = PacketByteBufs.create() // This is just a boolean
			buf.writeBoolean(hasTag)
			ServerPlayNetworking.send(player, HexOnCommandNetworking.SYNC_TAG_PACKET, buf)
		})

		CommandRegistrationCallback.EVENT.register({ dispatcher, registryAccess, environment ->
			dispatcher.register(
				literal("hexcastinggui")
					.executes({ context ->
						val player = context.source.player
						if (player != null) {
							// can server ever be null in this scenario, while player is not?
							handleCastingGridPacket(context.source.server, player)
						}
						1
					})
			)
		})
	}
}