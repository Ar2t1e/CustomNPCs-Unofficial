package noppes.npcs.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.client.parts.ModelData;
import noppes.npcs.client.parts.ModelPartData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.model.NopModelPart;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class LayerInterface<T extends EntityNPCInterface, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

   protected LivingEntityRenderer<T, M> render;
   protected EntityCustomNpc npc;
   protected ModelData playerdata;
   public HumanoidModel<T> base;
   private int color;

   public LayerInterface(LivingEntityRenderer<T, M> renderIn) {
      super(renderIn);
      render = renderIn;
      base = renderIn.getModel();
   }

   public void setColor(ModelPartData data, LivingEntity entity) { }

   protected float red() { return npc.hurtTime <= 0 && npc.deathTime <= 0 ? (float)(color >> 16 & 255) / 255.0F : 1.0F; }

   protected float green() { return npc.hurtTime <= 0 && npc.deathTime <= 0 ? (float)(color >> 8 & 255) / 255.0F : 0.0F; }

   protected float blue() { return npc.hurtTime <= 0 && npc.deathTime <= 0 ? (float)(color & 255) / 255.0F : 0.0F; }

   protected float alpha() {
      boolean flag = !npc.isInvisible();
      boolean flag1 = !flag && Minecraft.getInstance().player != null && !npc.isInvisibleTo(Minecraft.getInstance().player);
      return flag1 ? 0.15F : 0.99F;
   }

   public void preRender(ModelPartData data) {
      if (npc.hurtTime <= 0 && npc.deathTime <= 0) {
         color = data.color;
         int white = new Color(0xFFFFFF).getRGB();
         if (npc.display.getTint() != white) {
            if (data.color != white) { color = blend(data.color, npc.display.getTint(), 0.5F); }
            else { color = npc.display.getTint(); }
         }
      }
   }

   public int blend(int color1, int color2, float ratio) {
      if (ratio >= 1.0F) {
         return color2;
      } else if (ratio <= 0.0F) {
         return color1;
      } else {
         int aR = (color1 & 16711680) >> 16;
         int aG = (color1 & '\uff00') >> 8;
         int aB = color1 & 255;
         int bR = (color2 & 16711680) >> 16;
         int bG = (color2 & '\uff00') >> 8;
         int bB = color2 & 255;
         int R = (int)((float)aR + (float)(bR - aR) * ratio);
         int G = (int)((float)aG + (float)(bG - aG) * ratio);
         int B = (int)((float)aB + (float)(bB - aB) * ratio);
         return R << 16 | G << 8 | B;
      }
   }

   @Override
   public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, @NotNull T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      npc = (EntityCustomNpc)entity;
      if (Minecraft.getInstance().player == null || !npc.isInvisibleTo(Minecraft.getInstance().player)) {
         playerdata = npc.modelData;
         base = render.getModel();
         rotate(matrixStackIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);

         matrixStackIn.pushPose();
         render(matrixStackIn, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
         matrixStackIn.popPose();
      }
   }

   public RenderType getRenderType(ModelPartData data) {
      ResourceLocation resource = npc.textureLocation;
      if (!data.playerTexture) {
         resource = data.getResource();
      }

      return RenderType.entityTranslucent(resource);
   }

   public void setRotation(NopModelPart model, float x, float y, float z) {
      model.xRot = x;
      model.yRot = y;
      model.zRot = z;
   }

   public abstract void render(PoseStack var1, MultiBufferSource var2, int var3, float var4, float var5, float var6, float var7, float var8, float var9);

   public abstract void rotate(PoseStack var1, float var2, float var3, float var4, float var5, float var6, float var7);

}
