package noppes.npcs.api.mixin.world.entity.ai.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public interface IAttributeSupplier {

    void npcs$register(AttributeInstance attribute);

    void npcs$remove(AttributeInstance attribute);

}
