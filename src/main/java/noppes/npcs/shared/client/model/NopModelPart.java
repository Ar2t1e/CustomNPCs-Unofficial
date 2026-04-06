package noppes.npcs.shared.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.Random;
import java.util.UUID;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.shared.common.util.NopVector3f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class NopModelPart {

   public float xTexSize = 64.0F;
   public float yTexSize = 32.0F;
   public int xTexOffs;
   public int yTexOffs;
   public float x;
   public float y;
   public float z;
   public float xRot;
   public float yRot;
   public float zRot;
   public boolean mirror;
   public boolean visible = true;
   public final ObjectList<NopModelPart.ModelBox> cubes = new ObjectArrayList<>();
   public final Object2ObjectArrayMap<String, NopModelPart> children = new Object2ObjectArrayMap<>();
   public NopVector3f scale;

   public NopModelPart(int width, int height, int textureU, int textureV) {
      scale = NopVector3f.ONE;
      setTexSize(width, height);
      texOffs(textureU, textureV);
   }

   public NopModelPart(int width, int height) {
      scale = NopVector3f.ONE;
      setTexSize(width, height);
   }

   private NopModelPart() {
      scale = NopVector3f.ONE;
   }

   public NopModelPart createShallowCopy() {
      NopModelPart MpmModelPart = new NopModelPart();
      MpmModelPart.copyFrom(this);
      return MpmModelPart;
   }

   public void copyFrom(NopModelPart modelPart) {
      xRot = modelPart.xRot;
      yRot = modelPart.yRot;
      zRot = modelPart.zRot;
      x = modelPart.x;
      y = modelPart.y;
      z = modelPart.z;
   }

   public void addChild(String name, NopModelPart modelPart) {
      children.put(name, modelPart);
   }

   public void addChild(NopModelPart modelPart) {
      children.put(UUID.randomUUID().toString(), modelPart);
   }

   public NopModelPart texOffs(int textureU, int textureV) {
      xTexOffs = textureU;
      yTexOffs = textureV;
      return this;
   }

   public NopModelPart addBox(String ignoredName, float x, float y, float z, int dx, int dy, int dz, float size, int textureU, int textureV) {
      texOffs(textureU, textureV);
      addBox(xTexOffs, yTexOffs, x, y, z, (float)dx, (float)dy, (float)dz, size, size, size, mirror, false);
      return this;
   }

   public NopModelPart addBox(float x, float y, float z, float dx, float dy, float dz) {
      addBox(xTexOffs, yTexOffs, x, y, z, dx, dy, dz, 0.0F, 0.0F, 0.0F, mirror, false);
      return this;
   }

   public NopModelPart addBox(float x, float y, float z, float dx, float dy, float dz, boolean isMirror) {
      addBox(xTexOffs, yTexOffs, x, y, z, dx, dy, dz, 0.0F, 0.0F, 0.0F, isMirror, false);
      return this;
   }

   public void addBox(float x, float y, float z, float dx, float dy, float dz, float size) {
      addBox(xTexOffs, yTexOffs, x, y, z, dx, dy, dz, size, size, size, mirror, false);
   }

   public void addBox(float x, float y, float z, float dx, float dy, float dz, float width, float height, float depth) {
      addBox(xTexOffs, yTexOffs, x, y, z, dx, dy, dz, width, height, depth, mirror, false);
   }

   public void addBox(float x, float y, float z, float dx, float dy, float dz, float size, boolean isMirror) {
      addBox(xTexOffs, yTexOffs, x, y, z, dx, dy, dz, size, size, size, isMirror, false);
   }

   private void addBox(int textureU, int textureV, float x, float y, float z, float dx, float dy, float dz, float width, float height, float depth, boolean isMirror, boolean ignoredIs) {
      cubes.add(new NopModelPart.ModelBox(textureU, textureV, x, y, z, dx, dy, dz, width, height, depth, isMirror, xTexSize, yTexSize));
   }

   public void setPos(float xIn, float yIn, float zIn) {
      x = xIn;
      y = yIn;
      z = zIn;
   }

   public void setRotation(NopModelPart model, float x, float y, float z) {
      model.xRot = x;
      model.yRot = y;
      model.zRot = z;
   }

   public NopModelPart setRotation(NopVector3f rotate) {
      xRot = rotate.x;
      yRot = rotate.y;
      zRot = rotate.z;
      return this;
   }

   public NopModelPart setPos(NopVector3f pos) {
      x = pos.x;
      y = pos.y;
      z = pos.z;
      return this;
   }

   public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int lightMapUV, int overlayCoords, float alpha) {
      render(poseStack, vertexConsumer, lightMapUV, overlayCoords, 1.0F, 1.0F, 1.0F, alpha);
   }

   public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int lightMapUV, int overlayCoords, float red, float green, float blue, float alpha) {
      if (visible && (!cubes.isEmpty() || !children.isEmpty())) {
         poseStack.pushPose();
         translateAndRotate(poseStack);
         compile(poseStack.last(), vertexConsumer, lightMapUV, overlayCoords, red, green, blue, alpha);
         for (NopModelPart MpmModelPart : children.values()) { MpmModelPart.render(poseStack, vertexConsumer, lightMapUV, overlayCoords, red, green, blue, alpha); }
         poseStack.popPose();
      }
   }

   public void translateAndRotate(PoseStack poseStack) {
      poseStack.translate(x / 16.0F, y / 16.0F, (double)(z / 16.0F));
      if (zRot != 0.0F) { poseStack.mulPose(Axis.ZP.rotation(zRot)); }
      if (yRot != 0.0F) { poseStack.mulPose(Axis.YP.rotation(yRot)); }
      if (xRot != 0.0F) { poseStack.mulPose(Axis.XP.rotation(xRot)); }
      poseStack.scale(scale.x, scale.y, scale.z);
   }

   private void compile(Pose pose, VertexConsumer vertexConsumer, int lightMapUV, int overlayCoords, float red, float green, float blue, float alpha) {
      Matrix4f matrix4f = pose.pose();
      Matrix3f matrix3f = pose.normal();
      for (ModelBox MpmModelPart$modelbox : cubes) {
         TexturedQuad[] var13 = MpmModelPart$modelbox.polygons;
         for (TexturedQuad MpmModelPart$texturedquad : var13) {
            Vector3f vector3f = new Vector3f(MpmModelPart$texturedquad.normal.x, MpmModelPart$texturedquad.normal.y, MpmModelPart$texturedquad.normal.z);
            vector3f.mul(matrix3f);
            float f = vector3f.x();
            float f1 = vector3f.y();
            float f2 = vector3f.z();

            for (int i = 0; i < 4; ++i) {
               PositionTextureVertex positionTextureVertex = MpmModelPart$texturedquad.vertices[i];
               float f3 = positionTextureVertex.pos.x() / 16.0F;
               float f4 = positionTextureVertex.pos.y() / 16.0F;
               float f5 = positionTextureVertex.pos.z() / 16.0F;
               Vector4f vector4f = new Vector4f(f3, f4, f5, 1.0F);
               vector4f.mul(matrix4f);
               vertexConsumer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, positionTextureVertex.u, positionTextureVertex.v, overlayCoords, lightMapUV, f, f1, f2);
            }
         }
      }
   }

   public NopModelPart setTexSize(float width, float height) {
      xTexSize = width;
      yTexSize = height;
      return this;
   }

   public NopModelPart.ModelBox getRandomCube(Random rnd) {
      return cubes.get(rnd.nextInt(cubes.size()));
   }

   @OnlyIn(Dist.CLIENT)
   public static class ModelBox {
      public NopModelPart.TexturedQuad[] polygons;
      public float minX;
      public float minY;
      public float minZ;
      public float maxX;
      public float maxY;
      public float maxZ;

      public ModelBox(int textureU, int textureV, float x, float y, float z, float dx, float dy, float dz, float width, float height, float depth, boolean isMirror, float textureWidth, float textureHeight) {
         minX = x;
         minY = y;
         minZ = z;
         maxX = x + dx;
         maxY = y + dy;
         maxZ = z + dz;
         polygons = new NopModelPart.TexturedQuad[6];
         float f = x + dx;
         float f1 = y + dy;
         float f2 = z + dz;
         x -= width;
         y -= height;
         z -= depth;
         f += width;
         f1 += height;
         f2 += depth;
         if (isMirror) {
            float f3 = f;
            f = x;
            x = f3;
         }
         NopModelPart.PositionTextureVertex positionTextureVertex7 = new NopModelPart.PositionTextureVertex(x, y, z, 0.0F, 0.0F);
         NopModelPart.PositionTextureVertex positionTextureVertex = new NopModelPart.PositionTextureVertex(f, y, z, 0.0F, 8.0F);
         NopModelPart.PositionTextureVertex positionTextureVertex1 = new NopModelPart.PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
         NopModelPart.PositionTextureVertex positionTextureVertex2 = new NopModelPart.PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
         NopModelPart.PositionTextureVertex positionTextureVertex3 = new NopModelPart.PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
         NopModelPart.PositionTextureVertex positionTextureVertex4 = new NopModelPart.PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
         NopModelPart.PositionTextureVertex positionTextureVertex5 = new NopModelPart.PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
         NopModelPart.PositionTextureVertex positionTextureVertex6 = new NopModelPart.PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
         float f4 = (float)textureU;
         float f5 = (float)textureU + dz;
         float f6 = (float)textureU + dz + dx;
         float f7 = (float)textureU + dz + dx + dx;
         float f8 = (float)textureU + dz + dx + dz;
         float f9 = (float)textureU + dz + dx + dz + dx;
         float f10 = (float)textureV;
         float f11 = (float)textureV + dz;
         float f12 = (float)textureV + dz + dy;
         polygons[2] = new NopModelPart.TexturedQuad(new NopModelPart.PositionTextureVertex[]{positionTextureVertex4, positionTextureVertex3, positionTextureVertex7, positionTextureVertex}, f5, f10, f6, f11, textureWidth, textureHeight, isMirror, Direction.DOWN);
         polygons[3] = new NopModelPart.TexturedQuad(new NopModelPart.PositionTextureVertex[]{positionTextureVertex1, positionTextureVertex2, positionTextureVertex6, positionTextureVertex5}, f6, f11, f7, f10, textureWidth, textureHeight, isMirror, Direction.UP);
         polygons[1] = new NopModelPart.TexturedQuad(new NopModelPart.PositionTextureVertex[]{positionTextureVertex7, positionTextureVertex3, positionTextureVertex6, positionTextureVertex2}, f4, f11, f5, f12, textureWidth, textureHeight, isMirror, Direction.WEST);
         polygons[4] = new NopModelPart.TexturedQuad(new NopModelPart.PositionTextureVertex[]{positionTextureVertex, positionTextureVertex7, positionTextureVertex2, positionTextureVertex1}, f5, f11, f6, f12, textureWidth, textureHeight, isMirror, Direction.NORTH);
         polygons[0] = new NopModelPart.TexturedQuad(new NopModelPart.PositionTextureVertex[]{positionTextureVertex4, positionTextureVertex, positionTextureVertex1, positionTextureVertex5}, f6, f11, f8, f12, textureWidth, textureHeight, isMirror, Direction.EAST);
         polygons[5] = new NopModelPart.TexturedQuad(new NopModelPart.PositionTextureVertex[]{positionTextureVertex3, positionTextureVertex4, positionTextureVertex5, positionTextureVertex6}, f8, f11, f9, f12, textureWidth, textureHeight, isMirror, Direction.SOUTH);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TexturedQuad {

      public final NopModelPart.PositionTextureVertex[] vertices;
      public final Vector3f normal;

      public TexturedQuad(NopModelPart.PositionTextureVertex[] positionTextureVertexes, float pointPos_0, float pointPos_1, float pointPos_2, float pointPos_3, float textureWidth, float textureHeight, boolean isMirror, Direction direction) {
         vertices = positionTextureVertexes;
         float f = 0.0F / textureWidth;
         float f1 = 0.0F / textureHeight;
         positionTextureVertexes[0] = positionTextureVertexes[0].remap(pointPos_2 / textureWidth - f, pointPos_1 / textureHeight + f1);
         positionTextureVertexes[1] = positionTextureVertexes[1].remap(pointPos_0 / textureWidth + f, pointPos_1 / textureHeight + f1);
         positionTextureVertexes[2] = positionTextureVertexes[2].remap(pointPos_0 / textureWidth + f, pointPos_3 / textureHeight - f1);
         positionTextureVertexes[3] = positionTextureVertexes[3].remap(pointPos_2 / textureWidth - f, pointPos_3 / textureHeight - f1);
         if (isMirror) {
            int i = positionTextureVertexes.length;
            for(int j = 0; j < i / 2; ++j) {
               NopModelPart.PositionTextureVertex positionTextureVertex = positionTextureVertexes[j];
               positionTextureVertexes[j] = positionTextureVertexes[i - 1 - j];
               positionTextureVertexes[i - 1 - j] = positionTextureVertex;
            }
         }
         normal = direction.step();
         if (isMirror) { normal.mul(-1.0F, 1.0F, 1.0F); }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class PositionTextureVertex {

      public final Vector3f pos;
      public final float u;
      public final float v;

      public PositionTextureVertex(float x, float y, float z, float posU, float posV) {
         this(new Vector3f(x, y, z), posU, posV);
      }

      public NopModelPart.PositionTextureVertex remap(float posU, float posV) {
         return new NopModelPart.PositionTextureVertex(pos, posU, posV);
      }

      public PositionTextureVertex(Vector3f vecPos, float posU, float posV) {
         pos = vecPos;
         u = posU;
         v = posV;
      }
   }

}
