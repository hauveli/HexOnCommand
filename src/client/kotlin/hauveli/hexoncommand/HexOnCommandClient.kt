package hauveli.hexoncommand

import hauveli.hexoncommand.HexOnCommand.HexOnCommandNetworking
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.FriendlyByteBuf


class HexOnCommandClient : ClientModInitializer {
	override fun onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(
			HexOnCommandNetworking.SYNC_TAG_PACKET,
			ClientPlayNetworking.PlayChannelHandler { client: Minecraft?, handler: ClientPacketListener?, buf: FriendlyByteBuf?, responseSender: PacketSender? ->
				val value = buf!!.readBoolean()
				client!!.execute(Runnable {
					HAS_MAGICAL_TAG = value
				})
			}
		)
	}

	companion object {
		var HAS_MAGICAL_TAG: Boolean = false
	}
}