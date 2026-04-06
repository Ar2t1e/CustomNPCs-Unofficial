package noppes.npcs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.util.Mth;
import noppes.npcs.entity.EntityNpcPony;
import noppes.npcs.shared.client.model.NopModelPart;
import org.jetbrains.annotations.NotNull;

public class ModelPonyArmor<T extends EntityNpcPony> extends EntityModel<T> {

    public NopModelPart head;
   public NopModelPart Body;
   public NopModelPart BodyBack;
   public NopModelPart rightArm;
   public NopModelPart LeftArm;
   public NopModelPart RightLeg;
   public NopModelPart LeftLeg;
   public NopModelPart rightArm2;
   public NopModelPart LeftArm2;
   public NopModelPart RightLeg2;
   public NopModelPart LeftLeg2;
   public boolean isPegasus = false;
   public boolean isUnicorn = false;
   public boolean isSleeping = false;
   public boolean isFlying = false;
   public boolean isSneak = false;
   public boolean aimedBow;
   public int heldItemRight;

   public ModelPonyArmor(float f) {
      this.init(f, 0.0F);
   }

   public void init(float strech, float f) {
      float f2 = 0.0F;
      float f3 = 0.0F;
      float f4 = 0.0F;
      this.head = new NopModelPart(64, 32, 0, 0);
      this.head.addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 8.0F, strech);
      this.head.setPos(f2, f3, f4);
      float f5 = 0.0F;
      float f6 = 0.0F;
      float f7 = 0.0F;
      this.Body = new NopModelPart(64, 32, 16, 16);
      this.Body.addBox(-4.0F, 4.0F, -2.0F, 8.0F, 8.0F, 4.0F, strech);
      this.Body.setPos(f5, f6 + f, f7);
      this.BodyBack = new NopModelPart(64, 32, 0, 0);
      this.BodyBack.addBox(-4.0F, 4.0F, 6.0F, 8.0F, 8.0F, 8.0F, strech);
      this.BodyBack.setPos(f5, f6 + f, f7);
      this.rightArm = new NopModelPart(64, 32, 0, 16);
      this.rightArm.addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F, strech);
      this.rightArm.setPos(-3.0F, 8.0F + f, 0.0F);
      this.LeftArm = new NopModelPart(64, 32, 0, 16);
      this.LeftArm.mirror = true;
      this.LeftArm.addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F, strech);
      this.LeftArm.setPos(3.0F, 8.0F + f, 0.0F);
      this.RightLeg = new NopModelPart(64, 32, 0, 16);
      this.RightLeg.addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F, strech);
      this.RightLeg.setPos(-3.0F, 0.0F + f, 0.0F);
      this.LeftLeg = new NopModelPart(64, 32, 0, 16);
      this.LeftLeg.mirror = true;
      this.LeftLeg.addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F, strech);
      this.LeftLeg.setPos(3.0F, 0.0F + f, 0.0F);
      this.rightArm2 = new NopModelPart(64, 32, 0, 16);
      this.rightArm2.addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F, strech * 0.5F);
      this.rightArm2.setPos(-3.0F, 8.0F + f, 0.0F);
      this.LeftArm2 = new NopModelPart(64, 32, 0, 16);
      this.LeftArm2.mirror = true;
      this.LeftArm2.addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F, strech * 0.5F);
      this.LeftArm2.setPos(3.0F, 8.0F + f, 0.0F);
      this.RightLeg2 = new NopModelPart(64, 32, 0, 16);
      this.RightLeg2.addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F, strech * 0.5F);
      this.RightLeg2.setPos(-3.0F, 0.0F + f, 0.0F);
      this.LeftLeg2 = new NopModelPart(64, 32, 0, 16);
      this.LeftLeg2.mirror = true;
      this.LeftLeg2.addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F, strech * 0.5F);
      this.LeftLeg2.setPos(3.0F, 0.0F + f, 0.0F);
   }

   public void setupAnim(@NotNull T npc, float aniPosition, float aniSpeed, float age, float yHead, float xHead) {
      if (!this.riding) {
         this.riding = npc.currentAnimation == 1;
      }

      if (this.isSneak && (npc.currentAnimation == 7 || npc.currentAnimation == 2)) {
         this.isSneak = false;
      }

      boolean rainBoom = false;
      float f6;
      float f7;
      if (this.isSleeping) {
         f6 = 1.4F;
         f7 = 0.1F;
      } else {
         f6 = yHead / 57.29578F;
         f7 = xHead / 57.29578F;
      }

      this.head.yRot = f6;
      this.head.xRot = f7;
      float f8;
      float f9;
      float f10;
      float f11;
      if (this.isFlying && this.isPegasus) {
         if (aniSpeed < 0.9999F) {
            f8 = Mth.sin(0.0F - aniSpeed * 0.5F);
            f9 = Mth.sin(0.0F - aniSpeed * 0.5F);
            f10 = Mth.sin(aniSpeed * 0.5F);
            f11 = Mth.sin(aniSpeed * 0.5F);
         } else {
            rainBoom = true;
            f8 = 4.712F;
            f9 = 4.712F;
            f10 = 1.571F;
            f11 = 1.571F;
         }

         this.rightArm.yRot = 0.2F;
         this.LeftArm.yRot = -0.2F;
         this.RightLeg.yRot = -0.2F;
         this.LeftLeg.yRot = 0.2F;
         this.rightArm2.yRot = 0.2F;
         this.LeftArm2.yRot = -0.2F;
         this.RightLeg2.yRot = -0.2F;
         this.LeftLeg2.yRot = 0.2F;
      } else {
         f8 = Mth.cos(aniPosition * 0.6662F + 3.141593F) * 0.6F * aniSpeed;
         f9 = Mth.cos(aniPosition * 0.6662F) * 0.6F * aniSpeed;
         f10 = Mth.cos(aniPosition * 0.6662F) * 0.3F * aniSpeed;
         f11 = Mth.cos(aniPosition * 0.6662F + 3.141593F) * 0.3F * aniSpeed;
         this.rightArm.yRot = 0.0F;
         this.LeftArm.yRot = 0.0F;
         this.RightLeg.yRot = 0.0F;
         this.LeftLeg.yRot = 0.0F;
         this.rightArm2.yRot = 0.0F;
         this.LeftArm2.yRot = 0.0F;
         this.RightLeg2.yRot = 0.0F;
         this.LeftLeg2.yRot = 0.0F;
      }

      if (this.isSleeping) {
         f8 = 4.712F;
         f9 = 4.712F;
         f10 = 1.571F;
         f11 = 1.571F;
      }

      this.rightArm.xRot = f8;
      this.LeftArm.xRot = f9;
      this.RightLeg.xRot = f10;
      this.LeftLeg.xRot = f11;
      this.rightArm.zRot = 0.0F;
      this.LeftArm.zRot = 0.0F;
      this.rightArm2.xRot = f8;
      this.LeftArm2.xRot = f9;
      this.RightLeg2.xRot = f10;
      this.LeftLeg2.xRot = f11;
      this.rightArm2.zRot = 0.0F;
      this.LeftArm2.zRot = 0.0F;
      if (this.heldItemRight != 0 && !rainBoom && !this.isUnicorn) {
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.3141593F;
         this.rightArm2.xRot = this.rightArm2.xRot * 0.5F - 0.3141593F;
      }

      float f13 = Mth.sin(this.Body.yRot) * 5.0F;
      float f14 = Mth.cos(this.Body.yRot) * 5.0F;
      float f15 = 4.0F;
      if (this.isSneak && !this.isFlying) {
         f15 = 0.0F;
      }

      if (this.isSleeping) {
         f15 = 2.6F;
      }

      if (rainBoom) {
         this.rightArm.z = f13 + 2.0F;
         this.rightArm2.z = f13 + 2.0F;
         this.LeftArm.z = 0.0F - f13 + 2.0F;
         this.LeftArm2.z = 0.0F - f13 + 2.0F;
      } else {
         this.rightArm.z = f13 + 1.0F;
         this.rightArm2.z = f13 + 1.0F;
         this.LeftArm.z = 0.0F - f13 + 1.0F;
         this.LeftArm2.z = 0.0F - f13 + 1.0F;
      }

      this.rightArm.x = 0.0F - f14 - 1.0F + f15;
      this.rightArm2.x = 0.0F - f14 - 1.0F + f15;
      this.LeftArm.x = f14 + 1.0F - f15;
      this.LeftArm2.x = f14 + 1.0F - f15;
      this.RightLeg.x = 0.0F - f14 - 1.0F + f15;
      this.RightLeg2.x = 0.0F - f14 - 1.0F + f15;
      this.LeftLeg.x = f14 + 1.0F - f15;
      this.LeftLeg2.x = f14 + 1.0F - f15;
      rightArm.yRot += this.Body.yRot;
      rightArm2.yRot += this.Body.yRot;
      LeftArm.yRot += this.Body.yRot;
      LeftArm2.yRot += this.Body.yRot;
      LeftArm.xRot += this.Body.yRot;
      LeftArm2.xRot += this.Body.yRot;
      this.rightArm.y = 8.0F;
      this.LeftArm.y = 8.0F;
      this.rightArm2.y = 8.0F;
      this.LeftArm2.y = 8.0F;
      float f20;
      float f25;
      float f28;
      float f31;
      float f33;
      if (this.isSneak && !this.isFlying) {
         f20 = 0.4F;
         f25 = 7.0F;
         f28 = -4.0F;
         this.Body.xRot = f20;
         this.Body.y = f25;
         this.Body.z = f28;
         this.BodyBack.xRot = f20;
         this.BodyBack.y = f25;
         this.BodyBack.z = f28;
         RightLeg.xRot -= 0.0F;
         LeftLeg.xRot -= 0.0F;
         rightArm.xRot -= 0.4F;
         LeftArm.xRot -= 0.4F;
         this.RightLeg.z = 10.0F;
         this.LeftLeg.z = 10.0F;
         this.RightLeg.y = 7.0F;
         this.LeftLeg.y = 7.0F;
         RightLeg2.xRot -= 0.0F;
         LeftLeg2.xRot -= 0.0F;
         rightArm2.xRot -= 0.4F;
         LeftArm2.xRot -= 0.4F;
         this.RightLeg2.z = 10.0F;
         this.LeftLeg2.z = 10.0F;
         this.RightLeg2.y = 7.0F;
         this.LeftLeg2.y = 7.0F;
         float f35;
         if (this.isSleeping) {
            f31 = 2.0F;
            f33 = -1.0F;
            f35 = 1.0F;
         } else {
            f31 = 6.0F;
            f33 = -2.0F;
            f35 = 0.0F;
         }
         this.head.y = f31;
         this.head.z = f33;
         this.head.x = f35;
      } else {
         f20 = 0.0F;
         f25 = 0.0F;
         f28 = 0.0F;
         this.Body.xRot = f20;
         this.Body.y = f25;
         this.Body.z = f28;
         this.BodyBack.xRot = f20;
         this.BodyBack.y = f25;
         this.BodyBack.z = f28;
         this.RightLeg.z = 10.0F;
         this.LeftLeg.z = 10.0F;
         this.RightLeg.y = 8.0F;
         this.LeftLeg.y = 8.0F;
         this.RightLeg2.z = 10.0F;
         this.LeftLeg2.z = 10.0F;
         this.RightLeg2.y = 8.0F;
         this.LeftLeg2.y = 8.0F;
         f31 = 0.0F;
         f33 = 0.0F;
         this.head.y = f31;
         this.head.z = f33;
      }

      if (this.isSleeping) {
         this.rightArm.z += 6.0F;
         this.LeftArm.z += 6.0F;
         this.RightLeg.z -= 8.0F;
         this.LeftLeg.z -= 8.0F;
         this.rightArm.y += 2.0F;
         this.LeftArm.y += 2.0F;
         this.RightLeg.y += 2.0F;
         this.LeftLeg.y += 2.0F;
         this.rightArm2.z += 6.0F;
         this.LeftArm2.z += 6.0F;
         this.RightLeg2.z -= 8.0F;
         this.LeftLeg2.z -= 8.0F;
         this.rightArm2.y += 2.0F;
         this.LeftArm2.y += 2.0F;
         this.RightLeg2.y += 2.0F;
         this.LeftLeg2.y += 2.0F;
      }

      if (this.aimedBow && !this.isUnicorn) {
         f20 = 0.0F;
         f25 = 0.0F;
         this.rightArm.zRot = 0.0F;
         this.rightArm.yRot = -(0.1F - f20 * 0.6F) + this.head.yRot;
         this.rightArm.xRot = 4.712F + this.head.xRot;
         rightArm.xRot -= f20 * 1.2F - f25 * 0.4F;
         rightArm.zRot += Mth.cos(age * 0.09F) * 0.05F + 0.05F;
         rightArm.xRot += Mth.sin(age * 0.067F) * 0.05F;
         this.rightArm2.zRot = 0.0F;
         this.rightArm2.yRot = -(0.1F - f20 * 0.6F) + this.head.yRot;
         this.rightArm2.xRot = 4.712F + this.head.xRot;
         rightArm2.xRot -= f20 * 1.2F - f25 * 0.4F;
         rightArm2.zRot += Mth.cos(age * 0.09F) * 0.05F + 0.05F;
         rightArm2.xRot += Mth.sin(age * 0.067F) * 0.05F;
         ++this.rightArm.z;
         ++this.rightArm2.z;
      }

   }

   public void renderToBuffer(@NotNull PoseStack mStack, @NotNull VertexConsumer iVertex, int lightMapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
      this.head.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.Body.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.BodyBack.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.LeftArm.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.rightArm.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.LeftLeg.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.RightLeg.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.LeftArm2.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.rightArm2.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.LeftLeg2.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.RightLeg2.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
   }

}
