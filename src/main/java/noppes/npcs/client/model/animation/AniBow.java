package noppes.npcs.client.model.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AniBow implements AnimationBase {

   public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel<? extends LivingEntity> model, int animationStart) {
   }

   public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, HumanoidModel<? extends LivingEntity> model, int animationStart) {
      float ticks = (float)(entity.tickCount - animationStart) / 10.0F;
      if (ticks > 1.0F) {
         ticks = 1.0F;
      }

      float ticks2 = (float)(entity.tickCount + 1 - animationStart) / 10.0F;
      if (ticks2 > 1.0F) {
         ticks2 = 1.0F;
      }

      ticks += (ticks2 - ticks) * Minecraft.getInstance().getDeltaFrameTime();
      model.body.xRot = ticks;
      model.head.xRot = ticks;
      model.leftArm.xRot = ticks;
      model.rightArm.xRot = ticks;
      model.body.z = -ticks * 10.0F;
      model.body.y = ticks * 6.0F;
      model.head.z = -ticks * 10.0F;
      model.head.y = ticks * 6.0F;
      model.leftArm.z = -ticks * 10.0F;
      ModelPart var10000 = model.leftArm;
      var10000.y += ticks * 6.0F;
      model.rightArm.z = -ticks * 10.0F;
      var10000 = model.rightArm;
      var10000.y += ticks * 6.0F;
   }

}
