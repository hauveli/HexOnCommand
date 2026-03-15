package hauveli.hexoncommand.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
// import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hauveli.hexoncommand.HexOnCommand.HexOnCommandNetworking;

@Mixin(Entity.class)
public abstract class MixinEntityTagSync {

	@Inject(method = "addTag", at = @At("RETURN"))
	private void onTagAdded(String tag, CallbackInfoReturnable<Boolean> cir) {
		onTagChange(tag, cir, true);
	}

	@Inject(method = "removeTag", at = @At("RETURN"))
	private void onTagRemoved(String tag, CallbackInfoReturnable<Boolean> cir) {
		onTagChange(tag, cir, false);
	}

	// todo: ide whines about @Unique annotation
	// todo: if I knew how I wouldn't have this duplicate method in here but whatever
	private void onTagChange(String tag, CallbackInfoReturnable<Boolean> cir, boolean isMagical) {
		if (!cir.getReturnValue()) return;
		Entity self = (Entity)(Object)this;
		if (self instanceof ServerPlayer player) {
			if (tag.equals("magical")) {
				FriendlyByteBuf buf = PacketByteBufs.create();
				buf.writeBoolean(isMagical);
				ServerPlayNetworking.send(player, HexOnCommandNetworking.SYNC_TAG_PACKET, buf);
			}
		}
	}
}