package noppes.npcs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.util.Mth;
import noppes.npcs.entity.EntityNPCGolem;
import noppes.npcs.shared.client.model.NopModelPart;
import org.jetbrains.annotations.NotNull;

public class ModelNPCGolem<T extends EntityNPCGolem> extends EntityModel<T> {

   public NopModelPart head;
   public NopModelPart hat;
   public NopModelPart body;
   public NopModelPart rightArm;
   public NopModelPart leftArm;
   public NopModelPart rightLeg;
   public NopModelPart leftLeg;
   private NopModelPart bipedLowerBody;

   public ModelNPCGolem() {
      this.init(0.0F);
   }

   public void init(float f) {
      short short1 = 128;
      short short2 = 128;
      float f2 = -7.0F;
      this.head = (new NopModelPart(128, 128)).setTexSize(short1, short2);
      this.head.setPos(0.0F, f2, -2.0F);
      this.head.texOffs(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, f);
      this.head.texOffs(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, f);
      this.hat = (new NopModelPart(128, 128)).setTexSize(short1, short2);
      this.hat.setPos(0.0F, f2, -2.0F);
      this.hat.texOffs(0, 85).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, f + 0.5F);
      this.body = (new NopModelPart(128, 128)).setTexSize(short1, short2);
      this.body.setPos(0.0F, 0.0F + f2, 0.0F);
      this.body.texOffs(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, f + 0.2F);
      this.body.texOffs(0, 21).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 8.0F, 11.0F, f);
      this.bipedLowerBody = (new NopModelPart(128, 128)).setTexSize(short1, short2);
      this.bipedLowerBody.setPos(0.0F, 0.0F + f2, 0.0F);
      this.bipedLowerBody.texOffs(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, f + 0.5F);
      this.bipedLowerBody.texOffs(30, 70).addBox(-4.5F, 6.0F, -3.0F, 9.0F, 9.0F, 6.0F, f + 0.4F);
      this.rightArm = (new NopModelPart(128, 128)).setTexSize(short1, short2);
      this.rightArm.setPos(0.0F, f2, 0.0F);
      this.rightArm.texOffs(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, f + 0.2F);
      this.rightArm.texOffs(80, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 20.0F, 6.0F, f);
      this.rightArm.texOffs(100, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 20.0F, 6.0F, f + 1.0F);
      this.leftArm = (new NopModelPart(128, 128)).setTexSize(short1, short2);
      this.leftArm.setPos(0.0F, f2, 0.0F);
      this.leftArm.texOffs(60, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, f + 0.2F);
      this.leftArm.texOffs(80, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 20.0F, 6.0F, f);
      this.leftArm.texOffs(100, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 20.0F, 6.0F, f + 1.0F);
      this.leftLeg = (new NopModelPart(64, 64, 0, 22)).setTexSize(short1, short2);
      this.leftLeg.setPos(-4.0F, 18.0F + f2, 0.0F);
      this.leftLeg.texOffs(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, f);
      this.rightLeg = (new NopModelPart(64, 64, 0, 22)).setTexSize(short1, short2);
      this.rightLeg.mirror = true;
      this.rightLeg.texOffs(60, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, f);
      this.rightLeg.setPos(5.0F, 18.0F + f2, 0.0F);
   }

   public void renderToBuffer(@NotNull PoseStack mStack, @NotNull VertexConsumer iVertex, int lightMapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
      this.head.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.hat.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.body.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.rightArm.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.leftArm.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.rightLeg.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.leftLeg.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.bipedLowerBody.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
   }

   public void setupAnim(@NotNull T npc, float par1, float limbSwingAmount, float par3, float par4, float par5) {
      this.riding = npc.isPassenger();
      this.head.yRot = par4 / 57.295776F;
      this.head.xRot = par5 / 57.295776F;
      this.hat.yRot = this.head.yRot;
      this.hat.xRot = this.head.xRot;
      this.leftLeg.xRot = -1.5F * this.triangleWave(par1) * limbSwingAmount;
      this.rightLeg.xRot = 1.5F * this.triangleWave(par1) * limbSwingAmount;
      this.leftLeg.yRot = 0.0F;
      this.rightLeg.yRot = 0.0F;
      float f6 = Mth.sin(this.attackTime * 3.1415927F);
      float f7 = Mth.sin((16.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * 3.1415927F);
      if ((double)this.attackTime > 0.0D) {
         this.rightArm.zRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.rightArm.yRot = -(0.1F - f6 * 0.6F);
         this.leftArm.yRot = 0.1F - f6 * 0.6F;
         this.rightArm.xRot = -1.5707964F;
         this.leftArm.xRot = -1.5707964F;
         rightArm.xRot -= f6 * 1.2F - f7 * 0.4F;
         leftArm.xRot -= f6 * 1.2F - f7 * 0.4F;
      } else {
         this.rightArm.xRot = (-0.2F + 1.5F * this.triangleWave(par1)) * limbSwingAmount;
         this.leftArm.xRot = (-0.2F - 1.5F * this.triangleWave(par1)) * limbSwingAmount;
         this.body.yRot = 0.0F;
         this.rightArm.yRot = 0.0F;
         this.leftArm.yRot = 0.0F;
         this.rightArm.zRot = 0.0F;
         this.leftArm.zRot = 0.0F;
      }

      if (this.riding) {
         rightArm.xRot -= 0.62831855F;
         leftArm.xRot -= 0.62831855F;
         this.leftLeg.xRot = -1.2566371F;
         this.rightLeg.xRot = -1.2566371F;
         this.leftLeg.yRot = 0.31415927F;
         this.rightLeg.yRot = -0.31415927F;
      }
   }

   private float triangleWave(float par1) {
      return (Math.abs(par1 % (float) 13.0 - (float) 13.0 * 0.5F) - (float) 13.0 * 0.25F) / ((float) 13.0 * 0.25F);
   }

}
