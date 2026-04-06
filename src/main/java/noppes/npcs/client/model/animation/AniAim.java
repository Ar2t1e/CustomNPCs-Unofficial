package noppes.npcs.client.model.animation;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AniAim implements AnimationBase {

   public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel<? extends LivingEntity> model, int animationStart) {
      model.rightArmPose = ArmPose.BOW_AND_ARROW;
   }

   public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel<? extends LivingEntity> model, int animationStart) {
      model.rightArmPose = ArmPose.EMPTY;
   }

}
