package noppes.npcs.mixin.entity.ai.attributes;

import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = AbstractAttributeMap.class, priority = 502)
public interface IAbstractAttributeMapMixin {

    @Accessor Map<IAttribute, IAttributeInstance> getAttributes();

    @Accessor Map<String, IAttributeInstance> getAttributesByName();

    @Accessor Multimap<IAttribute, IAttribute> getDescendantsByParent();

}
