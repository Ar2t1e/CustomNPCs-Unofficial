package noppes.npcs.client.model.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AniPoint implements AnimationBase {

   public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel<? extends LivingEntity> model, int animationStart) {
      model.rightArm.xRot = -1.570796F;
      model.rightArm.yRot = netHeadYaw / 57.295776F;
      model.rightArm.zRot = 0.0F;
   }

   public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel<? extends LivingEntity> model, int animationStart) {
   }

}
