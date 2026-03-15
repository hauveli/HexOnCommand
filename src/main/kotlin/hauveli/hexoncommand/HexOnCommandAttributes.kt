package hauveli.hexoncommand

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

object HexOnCommandAttributes {

    // function for registering FREECASTER attribute
    fun register(consumer: (Attribute, ResourceLocation) -> Unit) {
        consumer(FREECASTER, ResourceLocation("hexoncommand", "freecaster"))
    }

    // Attribute describing whether the player is allowed to hexcast without a staff or not
    @JvmField
    val FREECASTER: Attribute = RangedAttribute(
        "attribute.hexoncommand.freecaster",
        0.0,
        0.0,
        1.0
    ).setSyncable(true)
}