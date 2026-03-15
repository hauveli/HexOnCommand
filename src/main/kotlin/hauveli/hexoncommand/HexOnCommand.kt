package hauveli.hexoncommand

import at.petrak.hexcasting.common.lib.HexAttributes
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.Commands.literal
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry;


class HexOnCommand : ModInitializer {

	// from: https://github.com/FallingColors/HexMod/blob/532fe9a60138544112e096812c7aefb78b3d7364/Common/src/main/java/at/petrak/hexcasting/common/items/ItemStaff.java#L28
	fun handleCastingGridPacket(
		player: ServerPlayer
	) {
		if (player.getAttributeValue(HexAttributes.FEEBLE_MIND) > 0.0) {
			// InteractionResultHolder.fail(player.getItemInHand(hand))
			// can't do this from outside of Item#use?
			return
		}
		val level = player.serverLevel()
		val hand = InteractionHand.MAIN_HAND

		// Is player crouching? if so reset casting grid
		if (player.isShiftKeyDown) {
			if (!level.isClientSide) {
				player.playSound(HexSounds.STAFF_RESET, 1f, 1f)
			} else if (player is ServerPlayer) { // Is the point of this check when this runs on non-dedicated server?
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

		// Register attribute hexoncommand:freecaster
		HexOnCommandAttributes.register { attribute, id ->
			Registry.register(BuiltInRegistries.ATTRIBUTE, id, attribute)
		}

		// Register command hexcastinggui
		CommandRegistrationCallback.EVENT.register({ dispatcher, registryAccess, environment ->
			dispatcher.register(
				literal("hexcastinggui")
					.executes({ context ->
						val player = context.source.player
						if (player != null &&
							player.getAttributeValue(HexOnCommandAttributes.COMMAND_PERMISSION) > 0) {
							handleCastingGridPacket(player)
						}
						1
					})
			)
		})
	}

}