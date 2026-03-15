package hauveli.hexoncommand.mixin.client;

import hauveli.hexoncommand.HexOnCommandAttributes;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// import java.util.List;

/**
 * This Mixin injects at the start of GuiSpellcasting.tick()
 * and prevents the GUI from being closed early if the player has the tag magical
 * Note: this sounds like it might be a really bad idea if player == null,
 * but I am checking for that, so unless multi-threaded race condition it is safe?
 * Side effect: When a player has the freecaster attribute, they do not mishap or cancel casts
 * if gui open and staff is dropped. Do I care? not really
 */
@Mixin(GuiSpellcasting.class)
public abstract class MixinGuiSpellcasting {
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void injected(CallbackInfo ci) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && player.getAttributeValue(HexOnCommandAttributes.FREECASTER) > 0) {
			ci.cancel();
		}
	}
}