package hauveli.hexoncommand

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

object HexOnCommandAttributes {

    val modid = "hexoncommand"
    val attributename = "command_permission"

    // function for registering COMMAND_PERMISSION attribute
    fun register(consumer: (Attribute, ResourceLocation) -> Unit) {
        consumer(COMMAND_PERMISSION, ResourceLocation(modid, attributename))
    }

    // Attribute describing whether the player is allowed to hexcast without a staff or not
    @JvmField
    val COMMAND_PERMISSION: Attribute = RangedAttribute(
        "attribute.$modid.$attributename",
        0.0,
        0.0,
        1.0
    ).setSyncable(true)
}