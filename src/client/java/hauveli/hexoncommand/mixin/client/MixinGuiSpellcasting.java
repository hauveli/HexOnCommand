package hauveli.hexoncommand.mixin.client;

// import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.client.gui.GuiSpellcasting;
// import at.petrak.hexcasting.common.lib.HexAttributes;


import hauveli.hexoncommand.HexOnCommandClient;
// import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
// import net.minecraft.resources.ResourceLocation;
// import net.minecraft.world.InteractionHand;
// import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
// import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// import java.util.List;

/**
 * This Mixin injects at the start of GuiSpellcasting.tick()
 * and prevents the GUI from being closed early if the player has the tag magical
 * Note: this sounds like it might be a really bad idea if player == null,
 * but I am checking for that, so unless multi-threaded race condition it is safe?
 */
@Mixin(GuiSpellcasting.class)
public abstract class MixinGuiSpellcasting {
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void injected(CallbackInfo ci) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			if (HexOnCommandClient.Companion.getHAS_MAGICAL_TAG()) {
				ci.cancel();
			}
		}
	}

	/**
	 * Returns true if the player has the specified tag
	 */
	private static boolean hasTag(LocalPlayer player, String tag) {
		System.out.println(player.getTags());
		return player.getTags().contains(tag);
	}
}