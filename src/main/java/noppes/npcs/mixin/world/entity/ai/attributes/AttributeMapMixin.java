package noppes.npcs.mixin.world.entity.ai.attributes;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import noppes.npcs.api.mixin.world.entity.ai.attributes.IAttributeMap;
import noppes.npcs.api.mixin.world.entity.ai.attributes.IAttributeSupplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Set;

@Mixin(value = AttributeMap.class, priority = 499)
public class AttributeMapMixin implements IAttributeMap {

    @Final
    @Shadow
    private Map<Attribute, AttributeInstance> attributes;
    @Final
    @Shadow
    private Set<AttributeInstance> dirtyAttributes;
    @Final
    @Shadow
    private AttributeSupplier supplier;

    @Override
    public void npcs$register(AttributeInstance attribute) {
        if (attribute.getAttribute().isClientSyncable()) {
            for (AttributeInstance attr : dirtyAttributes) {
                if (attr.getAttribute().getDescriptionId().equals(attribute.getAttribute().getDescriptionId())) {
                    dirtyAttributes.remove(attr);
                    break;
                }
            }
            dirtyAttributes.add(attribute);
        }
        Attribute key = attribute.getAttribute();
        for (Map.Entry<Attribute, AttributeInstance> entry : attributes.entrySet()) {
            if (entry.getKey().getDescriptionId().equals(attribute.getAttribute().getDescriptionId())) {
                key = entry.getKey();
                break;
            }
        }
        attributes.put(key, attribute);
        ((IAttributeSupplier) supplier).npcs$register(attribute);
    }

    @Override
    public void npcs$remove(AttributeInstance attribute) {
        if (attribute.getAttribute().isClientSyncable()) {
            for (AttributeInstance attr : dirtyAttributes) {
                if (attr.getAttribute().getDescriptionId().equals(attribute.getAttribute().getDescriptionId())) {
                    dirtyAttributes.remove(attr);
                    break;
                }
            }
        }
        for (Map.Entry<Attribute, AttributeInstance> entry : attributes.entrySet()) {
            if (entry.getKey().getDescriptionId().equals(attribute.getAttribute().getDescriptionId())) {
                attributes.remove(entry.getKey());
                break;
            }
        }
        ((IAttributeSupplier) supplier).npcs$remove(attribute);
    }

}
