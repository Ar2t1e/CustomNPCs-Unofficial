package noppes.npcs.client.model.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AniSit implements AnimationBase {

    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel<? extends LivingEntity> model, int animationStart) {
        model.riding = true;
    }

    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel<? extends LivingEntity> model, int animationStart) {
        model.riding = false;
    }

}
