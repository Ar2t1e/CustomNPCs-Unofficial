package noppes.npcs.mixin.world.entity.ai.attributes;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import noppes.npcs.api.mixin.world.entity.ai.attributes.IAttributeSupplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = AttributeSupplier.class, priority = 499)
public class AttributeSupplierMixin implements IAttributeSupplier {

    @Final
    @Shadow
    private Map<Attribute, AttributeInstance> instances;

    @Override
    public void npcs$register(AttributeInstance attribute) {
        Attribute key = attribute.getAttribute();
        for (Map.Entry<Attribute, AttributeInstance> entry : instances.entrySet()) {
            if (entry.getKey().getDescriptionId().equals(attribute.getAttribute().getDescriptionId())) {
                key = entry.getKey();
                break;
            }
        }
        instances.put(key, attribute);
    }

    @Override
    public void npcs$remove(AttributeInstance attribute) {
        for (Map.Entry<Attribute, AttributeInstance> entry : instances.entrySet()) {
            if (entry.getKey().getDescriptionId().equals(attribute.getAttribute().getDescriptionId())) {
                instances.remove(entry.getKey());
                break;
            }
        }
    }

}
