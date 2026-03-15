package hauveli.hexoncommand.mixin;

// homework copied from https://github.com/FallingColors/HexMod/blob/532fe9a60138544112e096812c7aefb78b3d7364/Fabric/src/main/java/at/petrak/hexcasting/fabric/mixin/FabricPlayerMixin.java
import hauveli.hexoncommand.HexOnCommandAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class FabricPlayerMixin extends LivingEntity {

    protected FabricPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    // Need to add after hecasting? not quite sure
    @Inject(at = @At("RETURN"), method = "createAttributes")
    private static void hex$addAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        var out = cir.getReturnValue();
        out.add(HexOnCommandAttributes.FREECASTER);
    }
}