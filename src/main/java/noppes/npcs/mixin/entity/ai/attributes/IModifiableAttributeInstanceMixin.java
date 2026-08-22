package noppes.npcs.mixin.entity.ai.attributes;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ModifiableAttributeInstance.class, priority = 502)
public interface IModifiableAttributeInstanceMixin {

    @Accessor IAttribute getGenericAttribute();

}
