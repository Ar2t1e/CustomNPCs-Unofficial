package noppes.npcs.mixin.client.renderer.entity.layers;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = HumanoidArmorLayer.class, priority = 499)
public interface IHumanoidArmorLayerMixin<T extends LivingEntity, A extends HumanoidModel<T>> {

   @Accessor A getInnerModel();

   @Accessor A getOuterModel();

}
