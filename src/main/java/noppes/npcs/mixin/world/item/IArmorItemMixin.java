package noppes.npcs.mixin.world.item;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.EnumMap;
import java.util.UUID;

@Mixin(value = ArmorItem.class, priority = 502)
public interface IArmorItemMixin {

    @Accessor("ARMOR_MODIFIER_UUID_PER_TYPE")
    EnumMap<ArmorItem.Type, UUID> getArmorModifiers();

    @Accessor("defense")
    int defense();

    @Accessor("toughness")
    float toughness();

    @Accessor
    void setDefense(int maxStDam);

    @Accessor
    void setToughness(float newToughness);

    @Accessor
    void setKnockbackResistance(float newKnockbackResistance);

}
