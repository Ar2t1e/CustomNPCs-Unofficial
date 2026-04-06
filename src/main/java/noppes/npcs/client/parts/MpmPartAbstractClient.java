package noppes.npcs.client.parts;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.common.util.NopVector3f;

public abstract class MpmPartAbstractClient extends MpmPart {

   public NopVector3f pos;
   public NopVector3f rot;
   protected Map<String, ModelPartWrapper> defaultPose;

   public MpmPartAbstractClient() {
      this.pos = NopVector3f.ZERO;
      this.rot = NopVector3f.ZERO;
      this.defaultPose = new HashMap<>();
   }

   public void render(MpmPartData data, PoseStack mStack, MultiBufferSource typeBuffer, int lightMapUV, LivingEntity player) {
      VertexConsumer c = typeBuffer.getBuffer(RenderType.entityTranslucent(data.usePlayerSkin ? ((EntityCustomNpc)player).textureLocation : data.getTexture()));
      render(data, mStack, c, lightMapUV, player);
   }

   public void render(MpmPartData data, PoseStack mStack, VertexConsumer c, int lightMapUV, LivingEntity player) {
   }

   public final ModelPartWrapper getPart(String name) {
      return this.defaultPose.get(name);
   }

}
