package noppes.npcs.api.wrapper;

import noppes.npcs.api.INbt;
import noppes.npcs.api.overlay.ITexturedRect;

public class OverlayTexturedRectWrapper extends OverlayComponentWrapper implements ITexturedRect {

   private String texture;
   private int width;
   private int height;
   private float[] uv;
   private float[] rgb;
   int textureX;
   int textureY = -1;
   int textureMaxX;
   int textureMaxY = -1;

   public OverlayTexturedRectWrapper(int id, int x, int y, String texture, int width, int height) {
      super(id, x, y);
      this.texture = texture;
      this.width = width;
      this.height = height;
   }

   public OverlayTexturedRectWrapper(int id, int x, int y, String texture, int width, int height, int textureX, int textureY) {
      super(id, x, y);
      this.texture = texture;
      this.width = width;
      this.height = height;
      this.setTextureOffset(textureX, textureY);
   }

   public OverlayTexturedRectWrapper(int id, int x, int y, String texture, int width, int height, int textureX, int textureY, int textureMaxX, int textureMaxY) {
      super(id, x, y);
      this.texture = texture;
      this.width = width;
      this.height = height;
      this.setTextureOffset(textureX, textureY);
      this.setTextureMaxSize(textureMaxX, textureMaxY);
   }

   public int getTextureX() {
      return this.textureX;
   }

   public int getTextureY() {
      return this.textureY;
   }

   public int getTextureMaxX() {
      return this.textureMaxX;
   }

   public int getTextureMaxY() {
      return this.textureMaxY;
   }

   public ITexturedRect setTextureOffset(int offsetX, int offsetY) {
      this.textureX = offsetX;
      this.textureY = offsetY;
      return this;
   }

   public ITexturedRect setTextureMaxSize(int textureMaxX, int textureMaxY) {
      this.textureMaxX = textureMaxX;
      this.textureMaxY = textureMaxY;
      return this;
   }

   public String getTexture() {
      return this.texture;
   }

   public ITexturedRect setTexture(String texture) {
      this.texture = texture;
      return this;
   }

   public int getWidth() {
      return this.width;
   }

   public ITexturedRect setWidth(int width) {
      this.width = width;
      return this;
   }

   public int getHeight() {
      return this.height;
   }

   public ITexturedRect setHeight(int height) {
      this.height = height;
      return this;
   }

   public int getType() {
      return 1;
   }

   public ITexturedRect setUV(float x1, float y1, float x2, float y2) {
      this.uv = new float[] {x1, y1, x2, y2};
      return this;
   }

   public ITexturedRect setRGB(float r, float g, float b, float a) {
      this.rgb = new float[]{r, g, b, a};
      return this;
   }

   public float[] getRGB() {
      return this.rgb;
   }

   public float[] getUV() {
      return this.uv;
   }

   public void toNbt(INbt iNbt) {
      super.toNbt(iNbt);
      iNbt.setString("texture", this.texture);
      iNbt.setInteger("width", this.width);
      iNbt.setInteger("height", this.height);
      int r;
      int g;
      int b;
      int a;
      int rgb;
      if (this.uv != null) {
         r = (int)(this.uv[0] * 255.0F);
         g = (int)(this.uv[1] * 255.0F);
         b = (int)(this.uv[2] * 255.0F);
         a = (int)(this.uv[3] * 255.0F);
         rgb = (r << 24) + (g << 16) + (b << 8) + a;
         iNbt.setInteger("u", rgb);
      }

      if (this.rgb != null) {
         r = (int)(this.rgb[0] * 255.0F);
         g = (int)(this.rgb[1] * 255.0F);
         b = (int)(this.rgb[2] * 255.0F);
         a = (int)(this.rgb[3] * 255.0F);
         rgb = (r << 24) + (g << 16) + (b << 8) + a;
         iNbt.setInteger("c", rgb);
      }
      if (this.textureX >= 0 && this.textureY >= 0) {
         iNbt.setIntegerArray("texPos", new int[] {this.textureX, this.textureY});
      }
      if (this.textureMaxX >= 0 && this.textureMaxY >= 0) {
         iNbt.setIntegerArray("texPosMax", new int[] {this.textureMaxX, this.textureMaxY});
      }
   }

   public void fromNbt(INbt iNbt) {
      super.fromNbt(iNbt);
      this.texture = iNbt.getString("texture");
      this.width = iNbt.getInteger("width");
      this.height = iNbt.getInteger("height");
      int uv;
      if (iNbt.has("c", 3)) {
         uv = iNbt.getInteger("c");
         this.setRGB((float)(uv >> 24 & 255) / 255.0F, (float)(uv >> 16 & 255) / 255.0F, (float)(uv >> 8 & 255) / 255.0F, (float)(uv & 255) / 255.0F);
      } else {
         this.setRGB(1.0F, 1.0F, 1.0F, 1.0F);
      }
      if (iNbt.has("u", 3)) {
         uv = iNbt.getInteger("u");
         this.setUV((float)(uv >> 24 & 255) / 255.0F, (float)(uv >> 16 & 255) / 255.0F, (float)(uv >> 8 & 255) / 255.0F, (float)(uv & 255) / 255.0F);
      } else {
         this.setUV(0.0F, 0.0F, 1.0F, 1.0F);
      }
      if (iNbt.has("texPos", 11)) {
         this.setTextureOffset(iNbt.getIntegerArray("texPos")[0], iNbt.getIntegerArray("texPos")[1]);
      }
      if (iNbt.has("texPosMax", 11)) {
         this.setTextureMaxSize(iNbt.getIntegerArray("texPosMax")[0], iNbt.getIntegerArray("texPosMax")[1]);
      }
   }

}
