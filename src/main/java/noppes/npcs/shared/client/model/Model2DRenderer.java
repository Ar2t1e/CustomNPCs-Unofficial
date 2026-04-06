package noppes.npcs.shared.client.model;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexBuffer.Usage;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.*;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import noppes.npcs.shared.client.model.util.BatchRenderer;
import noppes.npcs.shared.client.model.util.CustomRenderStates;
import noppes.npcs.shared.client.model.util.Polygon;
import noppes.npcs.shared.client.model.util.Vertex;
import noppes.npcs.shared.client.util.ImageDownloadAlt;
import noppes.npcs.shared.client.util.ResourceDownloader;
import noppes.npcs.shared.common.util.NopVector2i;
import noppes.npcs.shared.common.util.NopVector3f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Model2DRenderer extends NopModelPart {

   private final float x1;
   private final float x2;
   private final float y1;
   private final float y2;
   private final int width;
   private final int height;
   private final NopVector2i texPos;
   private float rotationOffsetX;
   private float rotationOffsetY;
   private float rotationOffsetZ;
   public static ResourceLocation textureOverride = null;
   private final ResourceLocation location;
   private float scaleX = 1.0F;
   private float scaleY = 1.0F;
   private float thickness = 1.0F;
   private final Map<ResourceLocation, Polygon[]> compiled = new HashMap<>();
   VertexBuffer cache;

   public Model2DRenderer(int texWidth, int texHeight, int x, int y, int widthIn, int heightIn, ResourceLocation locationIn) {
      super(texWidth, texHeight, 0, 0);
      width = widthIn;
      height = heightIn;
      texPos = new NopVector2i(x, y);
      setTexSize(texWidth, texHeight);
      location = locationIn;
      x1 = (float)x / (float)texWidth;
      y1 = (float)y / (float)texHeight;
      x2 = ((float)x + (float) widthIn) / (float)texWidth;
      y2 = ((float)y + (float) heightIn) / (float)texHeight;
      init(location);
   }

   public Polygon[] init(ResourceLocation location) {
      Polygon[] polygons = compiled.get(location);
      if (polygons == null && location != null && !location.toString().isEmpty()) {
         if (ResourceDownloader.contains(location)) {
            return null;
         } else {
            BufferedImage image = null;
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(location).orElse(null);
            if (resource != null) {
               try {
                  image = ImageIO.read(resource.open());
               } catch (Exception var25) {
                  AbstractTexture text = Minecraft.getInstance().getTextureManager().getTexture(location, null);
                  if (text instanceof ImageDownloadAlt) {
                     try {
                        FileInputStream input = new FileInputStream(((ImageDownloadAlt)text).cacheFile);
                        try {
                           image = ImageIO.read(input);
                        } catch (Throwable var21) {
                           try {
                              input.close();
                           } catch (Throwable var20) {
                              var21.addSuppressed(var20);
                           }

                           throw var21;
                        }

                        input.close();
                     } catch (Exception ignored) {
                     }
                  }
               }
            }

            int scaleW = 1;
            int scaleH = 1;
            if (image != null) {
               scaleW = Math.max(1, (int)((float)image.getWidth() / xTexSize));
               scaleH = Math.max(1, (int)((float)image.getHeight() / yTexSize));
            }

            int widthIn = width * scaleW;
            int heightIn = height * scaleH;
            NopVector2i texPosIn = texPos.mul(scaleW, scaleH);
            polygons = new Polygon[]{
                    new Polygon(
                            new Vector3f(0.0F, 0.0F, 1.0F),
                            new Vertex(0.0F, 0.0F, 0.0F, x1, y2),
                            new Vertex(1.0F, 0.0F, 0.0F, x2, y2),
                            new Vertex(1.0F, 1.0F, 0.0F, x2, y1),
                            new Vertex(0.0F, 1.0F, 0.0F, x1, y1)
                    ),
                    new Polygon(
                            new Vector3f(0.0F, 0.0F, -1.0F),
                            new Vertex(0.0F, 1.0F, -0.0625F, x1, y1),
                            new Vertex(1.0F, 1.0F, -0.0625F, x2, y1),
                            new Vertex(1.0F, 0.0F, -0.0625F, x2, y2),
                            new Vertex(0.0F, 0.0F, -0.0625F, x1, y2)
                    ),
                    null, null, null, null};
            List<Vertex> list = new ArrayList<>();
            List<Vertex> list2 = new ArrayList<>();

            int k;
            float f7;
            float f8;
            float f9;
            boolean top;
            boolean bottom;
            int n;
            for(k = 0; k < widthIn; ++k) {
               f7 = (float)k / (float) widthIn;
               f8 = x1 + (x2 - x1) * f7 - 0.5F * (x1 - x2) / (float) widthIn;
               f9 = f7 + 1.0F / (float) widthIn;
               top = false;
               bottom = false;
               if (image == null) {
                  top = true;
                  bottom = true;
               } else {
                  try {
                     for(n = 0; n < heightIn; ++n) {
                        if ((image.getRGB(texPosIn.x + k, texPosIn.y + n) >> 24 & 255) >= 128) {
                           if (k + 1 < widthIn && (image.getRGB(texPosIn.x + k + 1, texPosIn.y + n) >> 24 & 255) < 128) {
                              bottom = true;
                           } else if (k + 1 == widthIn) {
                              bottom = true;
                           }

                           if (k > 0 && (image.getRGB(texPosIn.x + k - 1, texPosIn.y + n) >> 24 & 255) < 128) {
                              top = true;
                           } else if (k == 0) {
                              top = true;
                           }
                        }
                     }
                  } catch (Exception ignored) {
                  }
               }

               if (top) {
                  list.add(new Vertex(f7, 0.0F, -0.0625F, f8, y2));
                  list.add(new Vertex(f7, 0.0F, 0.0F, f8, y2));
                  list.add(new Vertex(f7, 1.0F, 0.0F, f8, y1));
                  list.add(new Vertex(f7, 1.0F, -0.0625F, f8, y1));
               }

               if (bottom) {
                  list2.add(new Vertex(f9, 1.0F, -0.0625F, f8, y1));
                  list2.add(new Vertex(f9, 1.0F, 0.0F, f8, y1));
                  list2.add(new Vertex(f9, 0.0F, 0.0F, f8, y2));
                  list2.add(new Vertex(f9, 0.0F, -0.0625F, f8, y2));
               }
            }

            polygons[2] = new Polygon(new Vector3f(-1.0F, 0.0F, 0.0F), list.toArray(new Vertex[0]));
            polygons[3] = new Polygon(new Vector3f(1.0F, 0.0F, 0.0F), list2.toArray(new Vertex[0]));
            list = new ArrayList<>();
            list2 = new ArrayList<>();
            for(k = 0; k < heightIn; ++k) {
               f7 = (float)k / (float) heightIn;
               f8 = y2 + (y1 - y2) * f7 - 0.5F * (y2 - y1) / (float) heightIn;
               f9 = f7 + 1.0F / (float) heightIn;
               top = false;
               bottom = false;
               if (image == null) {
                  top = true;
                  bottom = true;
               } else {
                  try {
                     for(n = 0; n < widthIn; ++n) {
                        int m = heightIn - k - 1;
                        if ((image.getRGB(texPosIn.x + n, texPosIn.y + m) >> 24 & 255) >= 128) {
                           if (m > 0 && (image.getRGB(texPosIn.x + n, texPosIn.y + m - 1) >> 24 & 255) < 128) {
                              top = true;
                           } else if (m == 0) {
                              top = true;
                           }

                           if (m + 1 < heightIn && (image.getRGB(texPosIn.x + n, texPosIn.y + m + 1) >> 24 & 255) < 128) {
                              bottom = true;
                           } else if (m + 1 == heightIn) {
                              bottom = true;
                           }
                        }
                     }
                  } catch (Exception ignored) {
                  }
               }

               if (bottom) {
                  list2.add(new Vertex(1.0F, f7, 0.0F, x2, f8));
                  list2.add(new Vertex(0.0F, f7, 0.0F, x1, f8));
                  list2.add(new Vertex(0.0F, f7, -0.0625F, x1, f8));
                  list2.add(new Vertex(1.0F, f7, -0.0625F, x2, f8));
               }

               if (top) {
                  list.add(new Vertex(0.0F, f9, 0.0F, x1, f8));
                  list.add(new Vertex(1.0F, f9, 0.0F, x2, f8));
                  list.add(new Vertex(1.0F, f9, -0.0625F, x2, f8));
                  list.add(new Vertex(0.0F, f9, -0.0625F, x1, f8));
               }
            }

            polygons[4] = new Polygon(new Vector3f(0.0F, 1.0F, 0.0F), list.toArray(new Vertex[0]));
            polygons[5] = new Polygon(new Vector3f(0.0F, -1.0F, 0.0F), list2.toArray(new Vertex[0]));
            compiled.put(location, polygons);
            return polygons;
         }
      } else {
         return polygons;
      }
   }

   @Override
   public void render(PoseStack mstack, VertexConsumer builder, int lightMapUV, int overlayUV, float red, float green, float blue, float alpha) {
      render(textureOverride != null ? textureOverride : location, mstack, builder, lightMapUV, overlayUV, red, green, blue, alpha);
   }

   public void render(ResourceLocation location, PoseStack mstack, VertexConsumer builder, int lightMapUV, int overlayUV, float red, float green, float blue, float alpha) {
      if (visible && location != null && !location.toString().isEmpty()) {
         mstack.pushPose();
         translateAndRotate(mstack);
         float f = 0.0625F;
         mstack.translate(rotationOffsetX * f, rotationOffsetY * f, rotationOffsetZ * f);
         mstack.scale(scaleX * (float)width / (float)height, scaleY, thickness);
         mstack.mulPose(Axis.XP.rotationDegrees(180.0F));
         if (mirror) {
            mstack.translate(0.0F, 0.0F, -1.0F * f);
            mstack.mulPose(Axis.YP.rotationDegrees(180.0F));
         }
         renderModel(location, mstack.last().normal(), mstack.last().pose(), builder, lightMapUV, overlayUV, red, green, blue, alpha);
         mstack.popPose();
      }
   }

   public void render(ResourceLocation resource, PoseStack mstack, int lightMapUV, int overlayUV, float red, float green, float blue, float alpha) {
      if (visible && resource != null) {
         Minecraft.getInstance().getTextureManager().bindForSetup(resource);
         CustomRenderStates.entityCutout(resource);
         RenderSystem.setShader(() -> CustomRenderStates.posTexNormalShader);
         RenderSystem.setShaderTexture(0, resource);
         RenderSystem.setTextureMatrix(BatchRenderer.createTranslateMatrix((float)texPos.x, (float)texPos.y, 0.0F));
         if (cache == null) {
            cache = new VertexBuffer(Usage.STATIC);
            PoseStack mmstack = new PoseStack();
            mmstack.pushPose();
            Tesselator t = Tesselator.getInstance();
            BufferBuilder bufferbuilder = t.getBuilder();
            bufferbuilder.begin(Mode.TRIANGLES, CustomRenderStates.POS_TEX_NORMAL);
            renderModel(resource, mmstack.last().normal(), mmstack.last().pose(), bufferbuilder, lightMapUV, overlayUV, red, green, blue, alpha);
            cache.upload(bufferbuilder.end());
            mmstack.popPose();
         }

         mstack.pushPose();
         translateAndRotate(mstack);
         float f = 0.0625F;
         mstack.translate(rotationOffsetX * f, rotationOffsetY * f, rotationOffsetZ * f);
         mstack.scale(scaleX * (float)width / (float)height, scaleY, thickness);
         mstack.mulPose(Axis.XP.rotationDegrees(180.0F));
         if (mirror) {
            mstack.translate(0.0F, 0.0F, -1.0F * f);
            mstack.mulPose(Axis.YP.rotationDegrees(180.0F));
         }

         Pose entry = mstack.last();
         Matrix4f matrix = entry.pose();
         cache.drawWithShader(matrix, new Matrix4f(), Objects.requireNonNull(RenderSystem.getShader()));
         mstack.popPose();
      }
   }

   public void renderModel(ResourceLocation resource, Matrix3f matrix3f, Matrix4f matrix4f, VertexConsumer builder, int lightMapUV, int overlayUV, float red, float green, float blue, float alpha) {
      Polygon[] polygons = init(resource);
      if (polygons != null) {
         for (Polygon p : polygons) {
            Vector3f vector3f = new Vector3f(p.normal.x, p.normal.y, p.normal.z);
            vector3f.mul(matrix3f);
            float nX = vector3f.x();
            float nY = vector3f.y();
            float nZ = vector3f.z();

            for (int j = 0; j < p.vertexes.length; ++j) {
               Vertex vec = p.vertexes[j];
               Vector4f vector4f = new Vector4f(vec.pos.x, vec.pos.y, vec.pos.z, 1.0F);
               vector4f.mul(matrix4f);
               builder.vertex(vector4f.x(), vector4f.y(), vector4f.z());
               builder.color(red, green, blue, alpha);
               builder.uv(vec.texCoords.x, vec.texCoords.y);
               builder.overlayCoords(overlayUV);
               builder.uv2(lightMapUV);
               builder.normal(nX, nY, nZ);
               builder.endVertex();
            }
         }
      }
   }

   private void addVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightMapUV, float normalX, float normalY, float normalZ) {
      Vector4f v = new Vector4f(x, y, z, 1.0F);
      v.mul(matrix);
      builder.vertex(v.x(), v.y(), v.z());
      builder.color(red, green, blue, alpha);
      builder.uv(texU, texV);
      builder.overlayCoords(overlayUV);
      builder.uv2(lightMapUV);
      builder.normal(normalX, normalY, normalZ);
      builder.endVertex();
   }

   public Model2DRenderer setRotationOffset(float x, float y, float z) {
      rotationOffsetX = x;
      rotationOffsetY = y;
      rotationOffsetZ = z;
      return this;
   }

   public Model2DRenderer setRotationOffset(NopVector3f scale) {
      rotationOffsetX = scale.x;
      rotationOffsetY = scale.y;
      rotationOffsetZ = scale.z;
      return this;
   }

   public void setScale(float scale) {
      scaleX = scale;
      scaleY = scale;
   }

   public void setScale(float x, float y) {
      scaleX = x;
      scaleY = y;
   }

   public Model2DRenderer setScale(NopVector3f scale) {
      scaleX = scale.x;
      scaleY = scale.y;
      thickness = scale.z;
      return this;
   }

   public void setThickness(float thicknessIn) { thickness = thicknessIn; }

}
