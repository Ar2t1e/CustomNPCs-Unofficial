package noppes.npcs.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import noppes.npcs.entity.EntityNpcPony;
import noppes.npcs.shared.client.model.ModelPlaneRenderer;
import noppes.npcs.shared.client.model.NopModelPart;
import org.jetbrains.annotations.NotNull;

public class ModelPony<T extends EntityNpcPony> extends EntityModel<T> {

   public float WingRotateAngleX;
   public float WingRotateAngleY;
   public float WingRotateAngleZ;
   public float TailRotateAngleY;
   public NopModelPart Head;
   public NopModelPart[] Headpiece;
   public NopModelPart Helmet;
   public NopModelPart Body;
   public ModelPlaneRenderer[] BodyPiece;
   public NopModelPart RightArm;
   public NopModelPart LeftArm;
   public NopModelPart RightLeg;
   public NopModelPart LeftLeg;
   public NopModelPart unicornArm;
   public ModelPlaneRenderer[] Tail;
   public NopModelPart[] LeftWing;
   public NopModelPart[] RightWing;
   public NopModelPart[] LeftWingExt;
   public NopModelPart[] RightWingExt;
   public boolean isPegasus;
   public boolean isUnicorn;
   public boolean isFlying;
   public boolean isSleeping;
   public boolean isSneak;
   public boolean aimedBow;
   public int heldItemRight;

   public ModelPony() {
      this.init(0.0F, 0.0F);
   }

   public void init(float ignoredStrech, float f) {
      float f2 = 0.0F;
      float f3 = 0.0F;
      float f4 = 0.0F;
      this.Head = new NopModelPart(64, 32, 0, 0);
      this.Head.addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 8.0F);
      this.Head.setPos(f2, f3 + f, f4);
      this.Headpiece = new NopModelPart[3];
      this.Headpiece[0] = new NopModelPart(64, 32, 12, 16);
      this.Headpiece[0].addBox(-4.0F, -6.0F, -1.0F, 2.0F, 2.0F, 2.0F);
      this.Headpiece[0].setPos(f2, f3 + f, f4);
      this.Headpiece[1] = new NopModelPart(64, 32, 12, 16);
      this.Headpiece[1].addBox(2.0F, -6.0F, -1.0F, 2.0F, 2.0F, 2.0F);
      this.Headpiece[1].setPos(f2, f3 + f, f4);
      this.Headpiece[2] = new NopModelPart(64, 32, 56, 0);
      this.Headpiece[2].addBox(-0.5F, -10.0F, -4.0F, 1.0F, 4.0F, 1.0F);
      this.Headpiece[2].setPos(f2, f3 + f, f4);
      this.Helmet = new NopModelPart(64, 32, 32, 0);
      this.Helmet.addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 8.5F);
      this.Helmet.setPos(f2, f3, f4);
      float f5 = 0.0F;
      float f6 = 0.0F;
      float f7 = 0.0F;
      this.Body = new NopModelPart(64, 32, 16, 16);
      this.Body.addBox(-4.0F, 4.0F, -2.0F, 8.0F, 8.0F, 4.0F);
      this.Body.setPos(f5, f6 + f, f7);
      this.BodyPiece = new ModelPlaneRenderer[13];
      this.BodyPiece[0] = new ModelPlaneRenderer(64, 32, 24, 0);
      this.BodyPiece[0].addSidePlane(-4.0F, 4.0F, 2.0F, 8, 8);
      this.BodyPiece[0].setPos(f5, f6 + f, f7);
      this.BodyPiece[1] = new ModelPlaneRenderer(64, 32, 24, 0);
      this.BodyPiece[1].addSidePlane(4.0F, 4.0F, 2.0F, 8, 8);
      this.BodyPiece[1].setPos(f5, f6 + f, f7);
      this.BodyPiece[2] = new ModelPlaneRenderer(64, 32, 24, 0);
      this.BodyPiece[2].addTopPlane(-4.0F, 4.0F, 2.0F, 8, 8);
      this.BodyPiece[2].setPos(f2, f3 + f, f4);
      this.BodyPiece[3] = new ModelPlaneRenderer(64, 32, 24, 0);
      this.BodyPiece[3].addTopPlane(-4.0F, 12.0F, 2.0F, 8, 8);
      this.BodyPiece[3].setPos(f2, f3 + f, f4);
      this.BodyPiece[4] = new ModelPlaneRenderer(64, 32, 0, 20);
      this.BodyPiece[4].addSidePlane(-4.0F, 4.0F, 10.0F, 8, 4);
      this.BodyPiece[4].setPos(f5, f6 + f, f7);
      this.BodyPiece[5] = new ModelPlaneRenderer(64, 32, 0, 20);
      this.BodyPiece[5].addSidePlane(4.0F, 4.0F, 10.0F, 8, 4);
      this.BodyPiece[5].setPos(f5, f6 + f, f7);
      this.BodyPiece[6] = new ModelPlaneRenderer(64, 32, 24, 0);
      this.BodyPiece[6].addTopPlane(-4.0F, 4.0F, 10.0F, 8, 4);
      this.BodyPiece[6].setPos(f2, f3 + f, f4);
      this.BodyPiece[7] = new ModelPlaneRenderer(64, 32, 24, 0);
      this.BodyPiece[7].addTopPlane(-4.0F, 12.0F, 10.0F, 8, 4);
      this.BodyPiece[7].setPos(f2, f3 + f, f4);
      this.BodyPiece[8] = new ModelPlaneRenderer(64, 32, 24, 0);
      this.BodyPiece[8].addBackPlane(-4.0F, 4.0F, 14.0F, 8, 8);
      this.BodyPiece[8].setPos(f2, f3 + f, f4);
      this.BodyPiece[9] = new ModelPlaneRenderer(64, 32, 32, 0);
      this.BodyPiece[9].addTopPlane(-1.0F, 10.0F, 8.0F, 2, 6);
      this.BodyPiece[9].setPos(f2, f3 + f, f4);
      this.BodyPiece[10] = new ModelPlaneRenderer(64, 32, 32, 0);
      this.BodyPiece[10].addTopPlane(-1.0F, 12.0F, 8.0F, 2, 6);
      this.BodyPiece[10].setPos(f2, f3 + f, f4);
      this.BodyPiece[11] = new ModelPlaneRenderer(64, 32, 32, 0);
      this.BodyPiece[11].mirror = true;
      this.BodyPiece[11].addSidePlane(-1.0F, 10.0F, 8.0F, 2, 6);
      this.BodyPiece[11].setPos(f2, f3 + f, f4);
      this.BodyPiece[12] = new ModelPlaneRenderer(64, 32, 32, 0);
      this.BodyPiece[12].addSidePlane(1.0F, 10.0F, 8.0F, 2, 6);
      this.BodyPiece[12].setPos(f2, f3 + f, f4);
      this.RightArm = new NopModelPart(64, 32, 40, 16);
      this.RightArm.addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.RightArm.setPos(-3.0F, 8.0F + f, 0.0F);
      this.LeftArm = new NopModelPart(64, 32, 40, 16);
      this.LeftArm.mirror = true;
      this.LeftArm.addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.LeftArm.setPos(3.0F, 8.0F + f, 0.0F);
      this.RightLeg = new NopModelPart(64, 32, 40, 16);
      this.RightLeg.addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.RightLeg.setPos(-3.0F, 0.0F + f, 0.0F);
      this.LeftLeg = new NopModelPart(64, 32, 40, 16);
      this.LeftLeg.mirror = true;
      this.LeftLeg.addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.LeftLeg.setPos(3.0F, 0.0F + f, 0.0F);
      this.unicornArm = new NopModelPart(64, 32, 40, 16);
      this.unicornArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F);
      this.unicornArm.setPos(-5.0F, 2.0F + f, 0.0F);
      float f8 = 0.0F;
      float f9 = 8.0F;
      float f10 = -14.0F;
      float f11 = 0.0F - f8;
      float f12 = 10.0F - f9;
      float f13 = 0.0F;
      this.Tail = new ModelPlaneRenderer[10];
      this.Tail[0] = new ModelPlaneRenderer(64, 32, 32, 0);
      this.Tail[0].addTopPlane(-2.0F + f8, -7.0F + f9, 16.0F + f10, 4, 4);
      this.Tail[0].setPos(f11, f12 + f, f13);
      this.Tail[1] = new ModelPlaneRenderer(64, 32, 32, 0);
      this.Tail[1].addTopPlane(-2.0F + f8, 9.0F + f9, 16.0F + f10, 4, 4);
      this.Tail[1].setPos(f11, f12 + f, f13);
      this.Tail[2] = new ModelPlaneRenderer(64, 32, 32, 0);
      this.Tail[2].addBackPlane(-2.0F + f8, -7.0F + f9, 16.0F + f10, 4, 8);
      this.Tail[2].setPos(f11, f12 + f, f13);
      this.Tail[3] = new ModelPlaneRenderer(64, 32, 32, 0);
      this.Tail[3].addBackPlane(-2.0F + f8, -7.0F + f9, 20.0F + f10, 4, 8);
      this.Tail[3].setPos(f11, f12 + f, f13);
      this.Tail[4] = new ModelPlaneRenderer(64, 32, 32, 0);
      this.Tail[4].addBackPlane(-2.0F + f8, 1.0F + f9, 16.0F + f10, 4, 8);
      this.Tail[4].setPos(f11, f12 + f, f13);
      this.Tail[5] = new ModelPlaneRenderer(64, 32, 32, 0);
      this.Tail[5].addBackPlane(-2.0F + f8, 1.0F + f9, 20.0F + f10, 4, 8);
      this.Tail[5].setPos(f11, f12 + f, f13);
      this.Tail[6] = new ModelPlaneRenderer(64, 32, 36, 0);
      this.Tail[6].mirror = true;
      this.Tail[6].addSidePlane(2.0F + f8, -7.0F + f9, 16.0F + f10, 8, 4);
      this.Tail[6].setPos(f11, f12 + f, f13);
      this.Tail[7] = new ModelPlaneRenderer(64, 32, 36, 0);
      this.Tail[7].addSidePlane(-2.0F + f8, -7.0F + f9, 16.0F + f10, 8, 4);
      this.Tail[7].setPos(f11, f12 + f, f13);
      this.Tail[8] = new ModelPlaneRenderer(64, 32, 36, 0);
      this.Tail[8].mirror = true;
      this.Tail[8].addSidePlane(2.0F + f8, 1.0F + f9, 16.0F + f10, 8, 4);
      this.Tail[8].setPos(f11, f12 + f, f13);
      this.Tail[9] = new ModelPlaneRenderer(64, 32, 36, 0);
      this.Tail[9].addSidePlane(-2.0F + f8, 1.0F + f9, 16.0F + f10, 8, 4);
      this.Tail[9].setPos(f11, f12 + f, f13);
      this.TailRotateAngleY = this.Tail[0].yRot;
      float f14 = 0.0F;
      float f15 = 0.0F;
      float f16 = 0.0F;
      this.LeftWing = new NopModelPart[3];
      this.LeftWing[0] = new NopModelPart(64, 32, 56, 16);
      this.LeftWing[0].mirror = true;
      this.LeftWing[0].addBox(4.0F, 5.0F, 2.0F, 2.0F, 6.0F, 2.0F);
      this.LeftWing[0].setPos(f14, f15 + f, f16);
      this.LeftWing[1] = new NopModelPart(64, 32, 56, 16);
      this.LeftWing[1].mirror = true;
      this.LeftWing[1].addBox(4.0F, 5.0F, 4.0F, 2.0F, 8.0F, 2.0F);
      this.LeftWing[1].setPos(f14, f15 + f, f16);
      this.LeftWing[2] = new NopModelPart(64, 32, 56, 16);
      this.LeftWing[2].mirror = true;
      this.LeftWing[2].addBox(4.0F, 5.0F, 6.0F, 2.0F, 6.0F, 2.0F);
      this.LeftWing[2].setPos(f14, f15 + f, f16);
      this.RightWing = new NopModelPart[3];
      this.RightWing[0] = new NopModelPart(64, 32, 56, 16);
      this.RightWing[0].addBox(-6.0F, 5.0F, 2.0F, 2.0F, 6.0F, 2.0F);
      this.RightWing[0].setPos(f14, f15 + f, f16);
      this.RightWing[1] = new NopModelPart(64, 32, 56, 16);
      this.RightWing[1].addBox(-6.0F, 5.0F, 4.0F, 2.0F, 8.0F, 2.0F);
      this.RightWing[1].setPos(f14, f15 + f, f16);
      this.RightWing[2] = new NopModelPart(64, 32, 56, 16);
      this.RightWing[2].addBox(-6.0F, 5.0F, 6.0F, 2.0F, 6.0F, 2.0F);
      this.RightWing[2].setPos(f14, f15 + f, f16);
      float f17 = f2 + 4.5F;
      float f18 = f3 + 5.0F;
      float f19 = f4 + 6.0F;
      this.LeftWingExt = new NopModelPart[7];
      this.LeftWingExt[0] = new NopModelPart(64, 32, 56, 19);
      this.LeftWingExt[0].mirror = true;
      this.LeftWingExt[0].addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 2.1F);
      this.LeftWingExt[0].setPos(f17, f18 + f, f19);
      this.LeftWingExt[1] = new NopModelPart(64, 32, 56, 19);
      this.LeftWingExt[1].mirror = true;
      this.LeftWingExt[1].addBox(0.0F, 8.0F, 0.0F, 1.0F, 6.0F, 2.1F);
      this.LeftWingExt[1].setPos(f17, f18 + f, f19);
      this.LeftWingExt[2] = new NopModelPart(64, 32, 56, 19);
      this.LeftWingExt[2].mirror = true;
      this.LeftWingExt[2].addBox(0.0F, -1.2F, -0.2F, 1.0F, 8.0F, 1.8F);
      this.LeftWingExt[2].setPos(f17, f18 + f, f19);
      this.LeftWingExt[3] = new NopModelPart(64, 32, 56, 19);
      this.LeftWingExt[3].mirror = true;
      this.LeftWingExt[3].addBox(0.0F, 1.8F, 1.3F, 1.0F, 8.0F, 1.9F);
      this.LeftWingExt[3].setPos(f17, f18 + f, f19);
      this.LeftWingExt[4] = new NopModelPart(64, 32, 56, 19);
      this.LeftWingExt[4].mirror = true;
      this.LeftWingExt[4].addBox(0.0F, 5.0F, 2.0F, 1.0F, 8.0F, 2.0F);
      this.LeftWingExt[4].setPos(f17, f18 + f, f19);
      this.LeftWingExt[5] = new NopModelPart(64, 32, 56, 19);
      this.LeftWingExt[5].mirror = true;
      this.LeftWingExt[5].addBox(0.0F, 0.0F, -0.2F, 1.0F, 6.0F, 2.3F);
      this.LeftWingExt[5].setPos(f17, f18 + f, f19);
      this.LeftWingExt[6] = new NopModelPart(64, 32, 56, 19);
      this.LeftWingExt[6].mirror = true;
      this.LeftWingExt[6].addBox(0.0F, 0.0F, 0.2F, 1.0F, 3.0F, 2.2F);
      this.LeftWingExt[6].setPos(f17, f18 + f, f19);
      float f20 = f2 - 4.5F;
      float f21 = f3 + 5.0F;
      float f22 = f4 + 6.0F;
      this.RightWingExt = new NopModelPart[7];
      this.RightWingExt[0] = new NopModelPart(64, 32, 56, 19);
      this.RightWingExt[0].mirror = true;
      this.RightWingExt[0].addBox(0.0F, 0.0F, 0.0F, 1.0F, 8.0F, 2.1F);
      this.RightWingExt[0].setPos(f20, f21 + f, f22);
      this.RightWingExt[1] = new NopModelPart(64, 32, 56, 19);
      this.RightWingExt[1].mirror = true;
      this.RightWingExt[1].addBox(0.0F, 8.0F, 0.0F, 1.0F, 6.0F, 2.1F);
      this.RightWingExt[1].setPos(f20, f21 + f, f22);
      this.RightWingExt[2] = new NopModelPart(64, 32, 56, 19);
      this.RightWingExt[2].mirror = true;
      this.RightWingExt[2].addBox(0.0F, -1.2F, -0.2F, 1.0F, 8.0F, 1.8F);
      this.RightWingExt[2].setPos(f20, f21 + f, f22);
      this.RightWingExt[3] = new NopModelPart(64, 32, 56, 19);
      this.RightWingExt[3].mirror = true;
      this.RightWingExt[3].addBox(0.0F, 1.8F, 1.3F, 1.0F, 8.0F, 1.9F);
      this.RightWingExt[3].setPos(f20, f21 + f, f22);
      this.RightWingExt[4] = new NopModelPart(64, 32, 56, 19);
      this.RightWingExt[4].mirror = true;
      this.RightWingExt[4].addBox(0.0F, 5.0F, 2.0F, 1.0F, 8.0F, 2.0F);
      this.RightWingExt[4].setPos(f20, f21 + f, f22);
      this.RightWingExt[5] = new NopModelPart(64, 32, 56, 19);
      this.RightWingExt[5].mirror = true;
      this.RightWingExt[5].addBox(0.0F, 0.0F, -0.2F, 1.0F, 6.0F, 2.3F);
      this.RightWingExt[5].setPos(f20, f21 + f, f22);
      this.RightWingExt[6] = new NopModelPart(64, 32, 56, 19);
      this.RightWingExt[6].mirror = true;
      this.RightWingExt[6].addBox(0.0F, 0.0F, 0.2F, 1.0F, 3.0F, 2.2F);
      this.RightWingExt[6].setPos(f20, f21 + f, f22);
      this.WingRotateAngleX = this.LeftWingExt[0].xRot;
      this.WingRotateAngleY = this.LeftWingExt[0].yRot;
      this.WingRotateAngleZ = this.LeftWingExt[0].zRot;
   }

   public void setupAnim(T npc, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      if (npc.textureLocation != npc.checked && npc.textureLocation != null) {
         Resource resource = Minecraft.getInstance().getResourceManager().getResource(npc.textureLocation).orElse(null);
         if (resource != null) {
            try {
               BufferedImage bufferedimage = ImageIO.read(resource.open());
               npc.isPegasus = false;
               npc.isUnicorn = false;
               Color color = new Color(bufferedimage.getRGB(0, 0), true);
               Color color2 = new Color(136, 202, 240, 255);
               Color color3 = new Color(209, 159, 228, 255);
               Color color4 = new Color(254, 249, 252, 255);
               if (color.equals(color2)) {
                  npc.isPegasus = true;
               }

               if (color.equals(color3)) {
                  npc.isUnicorn = true;
               }

               if (color.equals(color4)) {
                  npc.isPegasus = true;
                  npc.isUnicorn = true;
               }

               npc.checked = npc.textureLocation;
            } catch (IOException ignored) { }
         }
      }

      this.isSleeping = npc.isSleeping();
      this.isUnicorn = npc.isUnicorn;
      this.isPegasus = npc.isPegasus;
      this.isSneak = npc.isCrouching();
      npc.getMainHandItem();
      this.heldItemRight = 1;
      this.riding = npc.isPassenger();
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
         f6 = headPitch / 57.29578F;
         f7 = netHeadYaw / 57.29578F;
      }

      this.Head.yRot = f6;
      this.Head.xRot = f7;
      this.Headpiece[0].yRot = f6;
      this.Headpiece[0].xRot = f7;
      this.Headpiece[1].yRot = f6;
      this.Headpiece[1].xRot = f7;
      this.Headpiece[2].yRot = f6;
      this.Headpiece[2].xRot = f7;
      this.Helmet.yRot = f6;
      this.Helmet.xRot = f7;
      this.Headpiece[2].xRot = f7 + 0.5F;
      float f8;
      float f9;
      float f10;
      float f11;
      if (this.isFlying && this.isPegasus) {
         if (limbSwingAmount < 0.9999F) {
            f8 = Mth.sin(0.0F - limbSwingAmount * 0.5F);
            f9 = Mth.sin(0.0F - limbSwingAmount * 0.5F);
            f10 = Mth.sin(limbSwingAmount * 0.5F);
            f11 = Mth.sin(limbSwingAmount * 0.5F);
         } else {
            rainBoom = true;
            f8 = 4.712F;
            f9 = 4.712F;
            f10 = 1.571F;
            f11 = 1.571F;
         }

         this.RightArm.yRot = 0.2F;
         this.LeftArm.yRot = -0.2F;
         this.RightLeg.yRot = -0.2F;
         this.LeftLeg.yRot = 0.2F;
      } else {
         f8 = Mth.cos(limbSwing * 0.6662F + 3.141593F) * 0.6F * limbSwingAmount;
         f9 = Mth.cos(limbSwing * 0.6662F) * 0.6F * limbSwingAmount;
         f10 = Mth.cos(limbSwing * 0.6662F) * 0.3F * limbSwingAmount;
         f11 = Mth.cos(limbSwing * 0.6662F + 3.141593F) * 0.3F * limbSwingAmount;
         this.RightArm.yRot = 0.0F;
         this.unicornArm.yRot = 0.0F;
         this.LeftArm.yRot = 0.0F;
         this.RightLeg.yRot = 0.0F;
         this.LeftLeg.yRot = 0.0F;
      }

      if (this.isSleeping) {
         f8 = 4.712F;
         f9 = 4.712F;
         f10 = 1.571F;
         f11 = 1.571F;
      }

      this.RightArm.xRot = f8;
      this.unicornArm.xRot = 0.0F;
      this.LeftArm.xRot = f9;
      this.RightLeg.xRot = f10;
      this.LeftLeg.xRot = f11;
      this.RightArm.zRot = 0.0F;
      this.unicornArm.zRot = 0.0F;
      this.LeftArm.zRot = 0.0F;
      for (ModelPlaneRenderer planeRenderer : this.Tail) {
         if (rainBoom) {
            planeRenderer.zRot = 0.0F;
         } else {
            planeRenderer.zRot = Mth.cos(limbSwing * 0.8F) * 0.2F * limbSwingAmount;
         }
      }

      if (this.heldItemRight != 0 && !rainBoom && !this.isUnicorn) {
         this.RightArm.xRot = this.RightArm.xRot * 0.5F - 0.3141593F;
      }

      float f12 = 0.0F;
      this.Body.yRot = (float)((double)f12 * 0.2D);

      int i1;
      for(i1 = 0; i1 < this.BodyPiece.length; ++i1) {
         this.BodyPiece[i1].yRot = (float)((double)f12 * 0.2D);
      }

      for(i1 = 0; i1 < this.LeftWing.length; ++i1) {
         this.LeftWing[i1].yRot = (float)((double)f12 * 0.2D);
      }

      for(i1 = 0; i1 < this.RightWing.length; ++i1) {
         this.RightWing[i1].yRot = (float)((double)f12 * 0.2D);
      }

      for(i1 = 0; i1 < this.Tail.length; ++i1) {
         this.Tail[i1].yRot = f12;
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
         this.RightArm.z = f13 + 2.0F;
         this.LeftArm.z = 0.0F - f13 + 2.0F;
      } else {
         this.RightArm.z = f13 + 1.0F;
         this.LeftArm.z = 0.0F - f13 + 1.0F;
      }

      this.RightArm.x = 0.0F - f14 - 1.0F + f15;
      this.LeftArm.x = f14 + 1.0F - f15;
      this.RightLeg.x = 0.0F - f14 - 1.0F + f15;
      this.LeftLeg.x = f14 + 1.0F - f15;
      RightArm.yRot += this.Body.yRot;
      LeftArm.xRot += this.Body.yRot;
      LeftArm.yRot += this.Body.yRot;
      this.RightArm.y = 8.0F;
      this.LeftArm.y = 8.0F;
      float f20;
      float f26;
      float f30;
      int l3;
      float f39;
      int l4;
      float f44;
      int j5;
      float f48;
      float f50;
      float f52;
      float f55;
      float f57;
      float f58;
      float f61;
      float f35;
      float f41;
      float f46;
      if (this.isSneak && !this.isFlying) {
         f20 = 0.4F;
         f26 = 7.0F;
         f30 = -4.0F;
         this.Body.xRot = f20;
         this.Body.y = f26;
         this.Body.z = f30;

         for(l3 = 0; l3 < this.BodyPiece.length; ++l3) {
            this.BodyPiece[l3].xRot = f20;
            this.BodyPiece[l3].y = f26;
            this.BodyPiece[l3].z = f30;
         }

         f35 = 3.5F;
         f39 = 6.0F;

         for(l4 = 0; l4 < this.LeftWingExt.length; ++l4) {
            this.LeftWingExt[l4].xRot = (float)((double)f20 + 2.3561947345733643D);
            this.LeftWingExt[l4].y = f26 + f35;
            this.LeftWingExt[l4].z = f30 + f39;
            this.LeftWingExt[l4].xRot = 2.5F;
            this.LeftWingExt[l4].zRot = -6.0F;
         }

         f41 = 4.5F;
         f44 = 6.0F;

         for(j5 = 0; j5 < this.LeftWingExt.length; ++j5) {
            this.RightWingExt[j5].xRot = (float)((double)f20 + 2.3561947345733643D);
            this.RightWingExt[j5].y = f26 + f41;
            this.RightWingExt[j5].z = f30 + f44;
            this.RightWingExt[j5].xRot = 2.5F;
            this.RightWingExt[j5].zRot = 6.0F;
         }
         RightLeg.xRot -= 0.0F;
         LeftLeg.xRot -= 0.0F;
         RightArm.xRot -= 0.4F;
         unicornArm.xRot += 0.4F;
         LeftArm.xRot -= 0.4F;
         this.RightLeg.z = 10.0F;
         this.LeftLeg.z = 10.0F;
         this.RightLeg.y = 7.0F;
         this.LeftLeg.y = 7.0F;
         if (this.isSleeping) {
            f46 = 2.0F;
            f48 = -1.0F;
            f50 = 1.0F;
         } else {
            f46 = 6.0F;
            f48 = -2.0F;
            f50 = 0.0F;
         }

         this.Head.y = f46;
         this.Head.z = f48;
         this.Head.x = f50;
         this.Helmet.y = f46;
         this.Helmet.z = f48;
         this.Helmet.x = f50;
         this.Headpiece[0].y = f46;
         this.Headpiece[0].z = f48;
         this.Headpiece[0].x = f50;
         this.Headpiece[1].y = f46;
         this.Headpiece[1].z = f48;
         this.Headpiece[1].x = f50;
         this.Headpiece[2].y = f46;
         this.Headpiece[2].z = f48;
         this.Headpiece[2].x = f50;
         f52 = 0.0F;
         f55 = 8.0F;
         f57 = -14.0F;
         f58 = 0.0F - f52;
         f61 = 9.0F - f55;
         float f62 = -4.0F - f57;
         float f63 = 0.0F;
         for (ModelPlaneRenderer modelPlaneRenderer : this.Tail) {
            modelPlaneRenderer.x = f58;
            modelPlaneRenderer.y = f61;
            modelPlaneRenderer.z = f62;
            modelPlaneRenderer.xRot = f63;
         }
      } else {
         f20 = 0.0F;
         f26 = 0.0F;
         f30 = 0.0F;
         this.Body.xRot = f20;
         this.Body.y = f26;
         this.Body.z = f30;

         for(l3 = 0; l3 < this.BodyPiece.length; ++l3) {
            this.BodyPiece[l3].xRot = f20;
            this.BodyPiece[l3].y = f26;
            this.BodyPiece[l3].z = f30;
         }

         if (this.isPegasus) {
            if (!this.isFlying) {
               for(l3 = 0; l3 < this.LeftWing.length; ++l3) {
                  this.LeftWing[l3].xRot = (float)((double)f20 + 1.5707964897155762D);
                  this.LeftWing[l3].y = f26 + 13.0F;
                  this.LeftWing[l3].z = f30 - 3.0F;
               }

               for(l3 = 0; l3 < this.RightWing.length; ++l3) {
                  this.RightWing[l3].xRot = (float)((double)f20 + 1.5707964897155762D);
                  this.RightWing[l3].y = f26 + 13.0F;
                  this.RightWing[l3].z = f30 - 3.0F;
               }
            } else {
               f35 = 5.5F;
               f39 = 3.0F;

               for(l4 = 0; l4 < this.LeftWingExt.length; ++l4) {
                  this.LeftWingExt[l4].xRot = (float)((double)f20 + 1.5707964897155762D);
                  this.LeftWingExt[l4].y = f26 + f35;
                  this.LeftWingExt[l4].z = f30 + f39;
               }

               f41 = 6.5F;
               f44 = 3.0F;

               for(j5 = 0; j5 < this.RightWingExt.length; ++j5) {
                  this.RightWingExt[j5].xRot = (float)((double)f20 + 1.5707964897155762D);
                  this.RightWingExt[j5].y = f26 + f41;
                  this.RightWingExt[j5].z = f30 + f44;
               }
            }
         }

         this.RightLeg.z = 10.0F;
         this.LeftLeg.z = 10.0F;
         this.RightLeg.y = 8.0F;
         this.LeftLeg.y = 8.0F;
         f35 = Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
         f39 = Mth.sin(ageInTicks * 0.067F) * 0.05F;
         unicornArm.zRot += f35;
         unicornArm.xRot += f39;
         if (this.isPegasus && this.isFlying) {
            this.WingRotateAngleY = Mth.sin(ageInTicks * 0.067F * 8.0F);
            this.WingRotateAngleZ = Mth.sin(ageInTicks * 0.067F * 8.0F);

            for(l4 = 0; l4 < this.LeftWingExt.length; ++l4) {
               this.LeftWingExt[l4].xRot = 2.5F;
               this.LeftWingExt[l4].zRot = -this.WingRotateAngleZ - 4.712F - 0.4F;
            }

            for(l4 = 0; l4 < this.RightWingExt.length; ++l4) {
               this.RightWingExt[l4].xRot = 2.5F;
               this.RightWingExt[l4].zRot = this.WingRotateAngleZ + 4.712F + 0.4F;
            }
         }

         if (this.isSleeping) {
            f41 = 2.0F;
            f44 = 1.0F;
            f46 = 1.0F;
         } else {
            f41 = 0.0F;
            f44 = 0.0F;
            f46 = 0.0F;
         }

         this.Head.y = f41;
         this.Head.z = f44;
         this.Head.x = f46;
         this.Helmet.y = f41;
         this.Helmet.z = f44;
         this.Helmet.x = f46;
         this.Headpiece[0].y = f41;
         this.Headpiece[0].z = f44;
         this.Headpiece[0].x = f46;
         this.Headpiece[1].y = f41;
         this.Headpiece[1].z = f44;
         this.Headpiece[1].x = f46;
         this.Headpiece[2].y = f41;
         this.Headpiece[2].z = f44;
         this.Headpiece[2].x = f46;
         f48 = 0.0F;
         f50 = 8.0F;
         f52 = -14.0F;
         f55 = 0.0F - f48;
         f57 = 9.0F - f50;
         f58 = 0.0F - f52;
         f61 = 0.5F * limbSwingAmount;

         int l5;
         for(l5 = 0; l5 < this.Tail.length; ++l5) {
            this.Tail[l5].x = f55;
            this.Tail[l5].y = f57;
            this.Tail[l5].z = f58;
            if (rainBoom) {
               this.Tail[l5].xRot = 1.571F + 0.1F * Mth.sin(limbSwing);
            } else {
               this.Tail[l5].xRot = f61;
            }
         }

         for(l5 = 0; l5 < this.Tail.length; ++l5) {
            if (!rainBoom) {
               ModelPlaneRenderer var50 = this.Tail[l5];
               var50.xRot += f39;
            }
         }
      }

      this.LeftWingExt[2].xRot -= 0.85F;
      this.LeftWingExt[3].xRot -= 0.75F;
      this.LeftWingExt[4].xRot -= 0.5F;
      this.LeftWingExt[6].xRot -= 0.85F;
      this.RightWingExt[2].xRot -= 0.85F;
      this.RightWingExt[3].xRot -= 0.75F;
      this.RightWingExt[4].xRot -= 0.5F;
      this.RightWingExt[6].xRot -= 0.85F;
      this.BodyPiece[9].xRot += 0.5F;
      this.BodyPiece[10].xRot += 0.5F;
      this.BodyPiece[11].xRot += 0.5F;
      this.BodyPiece[12].xRot += 0.5F;
      if (rainBoom) {
         for (ModelPlaneRenderer modelPlaneRenderer : this.Tail) {
            modelPlaneRenderer.y += 6.0F;
            ++modelPlaneRenderer.z;
         }
      }

      if (this.isSleeping) {
         this.RightArm.z += 6.0F;
         this.LeftArm.z += 6.0F;
         this.RightLeg.z -= 8.0F;
         this.LeftLeg.z -= 8.0F;
         this.RightArm.y += 2.0F;
         this.LeftArm.y += 2.0F;
         this.RightLeg.y += 2.0F;
         this.LeftLeg.y += 2.0F;
      }

      if (this.aimedBow) {
         f20 = 0.0F;
         f26 = 0.0F;
         this.unicornArm.zRot = 0.0F;
         this.unicornArm.yRot = -(0.1F - f20 * 0.6F) + this.Head.yRot;
         this.unicornArm.xRot = 4.712F + this.Head.xRot;
         unicornArm.xRot -= f20 * 1.2F - f26 * 0.4F;
         unicornArm.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
         unicornArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
         if (!this.isUnicorn) {
            ++this.RightArm.z;
         }
      }

   }

   public void renderToBuffer(PoseStack mStack, @NotNull VertexConsumer iVertex, int lightMapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
      mStack.pushPose();
      if (this.isSleeping) {
         mStack.mulPose(Axis.XP.rotationDegrees(90.0F));
         mStack.translate(0.0F, -0.5F, -0.9F);
      }

      this.Head.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.Headpiece[0].render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.Headpiece[1].render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      if (this.isUnicorn) {
         this.Headpiece[2].render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      }

      this.Helmet.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.Body.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);

      int l;
      for(l = 0; l < this.BodyPiece.length; ++l) {
         this.BodyPiece[l].render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      }

      this.LeftArm.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.RightArm.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.LeftLeg.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      this.RightLeg.render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);

      for(l = 0; l < this.Tail.length; ++l) {
         this.Tail[l].render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
      }

      if (this.isPegasus) {
         if (!this.isFlying && !this.isSneak) {
            for(l = 0; l < this.LeftWing.length; ++l) {
               this.LeftWing[l].render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
            }

            for(l = 0; l < this.RightWing.length; ++l) {
               this.RightWing[l].render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
            }
         } else {
            for(l = 0; l < this.LeftWingExt.length; ++l) {
               this.LeftWingExt[l].render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
            }

            for(l = 0; l < this.RightWingExt.length; ++l) {
               this.RightWingExt[l].render(mStack, iVertex, lightMapUV, packedOverlayIn, red, green, blue, alpha);
            }
         }
      }

      mStack.popPose();
   }
}
