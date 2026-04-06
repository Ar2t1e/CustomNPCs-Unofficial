package noppes.npcs.shared.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class CTextureUtil {

   private static final IntBuffer DATA_BUFFER = MemoryTracker.create(16777216).asIntBuffer();
   private static final float[] COLOR_GAMMAS = new float[256];

   public static void deleteTexture(int textureId) {
      RenderSystem.deleteTexture(textureId);
   }

   public static int uploadTextureImage(int textureId, BufferedImage texture) {
      return uploadTextureImageSub(textureId, texture, 0, 0);
   }

   public static void allocateTexture(int textureId, int width, int height) {
      allocateTextureImpl(textureId, 0, width, height);
   }

   public static void allocateTextureImpl(int glTextureId, int mipmapLevels, int width, int height) {
      RenderSystem.assertOnRenderThreadOrInit();
      deleteTexture(glTextureId);
      bind(glTextureId);
      if (mipmapLevels >= 0) {
         RenderSystem.texParameter(3553, 33085, mipmapLevels);
         RenderSystem.texParameter(3553, 33082, 0);
         RenderSystem.texParameter(3553, 33083, mipmapLevels);
         GlStateManager._texParameter(3553, 34049, 0.0F);
      }

      for(int i = 0; i <= mipmapLevels; ++i) {
         GlStateManager._texImage2D(3553, i, 6408, width >> i, height >> i, 0, 6408, 5121, null);
      }

   }

   public static int uploadTextureImageSub(int textureId, BufferedImage bufferedimage, int width, int height) {
      uploadTextureImageSubImpl(textureId, bufferedimage, width, height);
      return textureId;
   }

   private static void uploadTextureImageSubImpl(int textureId, BufferedImage bufferedimage, int width, int height) {
      if (bufferedimage != null) {
         int i = bufferedimage.getWidth();
         int j = bufferedimage.getHeight();
         int[] aint = new int[i * j];
         bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
         IntBuffer intbuffer = BufferUtils.createIntBuffer(i * j);
         intbuffer.put(aint);
         RenderSystem.activeTexture(33984);
         RenderSystem.bindTextureForSetup(textureId);
         GL11.glPixelStorei(3312, 0);
         GL11.glPixelStorei(3313, 0);
         GL11.glPixelStorei(3314, 0);
         GL11.glPixelStorei(3315, 0);
         GL11.glPixelStorei(3316, 0);
         GL11.glPixelStorei(3317, 4);
         GL11.glTexImage2D(3553, 0, 6408, bufferedimage.getWidth(), bufferedimage.getHeight(), 0, 32993, 33639, intbuffer);
         GL11.glTexParameteri(3553, 10240, 9728);
         GL11.glTexParameteri(3553, 10241, 9729);
      }
   }

   private static void setTextureClamped(boolean p_110997_0_) {
      if (p_110997_0_) {
         RenderSystem.texParameter(3553, 10242, 10496);
         RenderSystem.texParameter(3553, 10243, 10496);
      } else {
         RenderSystem.texParameter(3553, 10242, 10497);
         RenderSystem.texParameter(3553, 10243, 10497);
      }

   }

   private static void setTextureBlurred(boolean p_147951_0_) {
      setTextureBlurMipmap(p_147951_0_, false);
   }

   private static void setTextureBlurMipmap(boolean p_147954_0_, boolean p_147954_1_) {
      if (p_147954_0_) {
         RenderSystem.texParameter(3553, 10241, p_147954_1_ ? 9987 : 9729);
         RenderSystem.texParameter(3553, 10240, 9729);
      } else {
         RenderSystem.texParameter(3553, 10241, p_147954_1_ ? 9986 : 9728);
         RenderSystem.texParameter(3553, 10240, 9728);
      }

   }

   private static void copyToBuffer(int[] src, int length) {
      copyToBufferPos(src, 0, length);
   }

   private static void copyToBufferPos(int[] src, int offset, int length) {
      DATA_BUFFER.clear();
      DATA_BUFFER.put(src, offset, length);
      DATA_BUFFER.position(0).limit(length);
   }

   static void bind(int binding) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager._bindTexture(binding);
   }

   public static int[] updateAnaglyph(int[] colors) {
      int[] aint = new int[colors.length];
      for(int i = 0; i < colors.length; ++i) { aint[i] = anaglyphColor(colors[i]); }
      return aint;
   }

   public static int anaglyphColor(int color) {
      int i = color >> 24 & 255;
      int j = color >> 16 & 255;
      int k = color >> 8 & 255;
      int l = color & 255;
      int i1 = (j * 30 + k * 59 + l * 11) / 100;
      int j1 = (j * 30 + k * 70) / 100;
      int k1 = (j * 30 + l * 70) / 100;
      return i << 24 | i1 << 16 | j1 << 8 | k1;
   }

   static {
      for(int i1 = 0; i1 < COLOR_GAMMAS.length; ++i1) {
         COLOR_GAMMAS[i1] = (float)Math.pow((float)i1 / 255.0F, 2.2D);
      }
   }

}
