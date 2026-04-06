package noppes.npcs.client.parts;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.shared.client.util.ImageDownloadAlt;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.shared.client.util.ResourceDownloader;
import noppes.npcs.shared.common.util.NopVector3f;

public class MpmPartData {

   public static final NopVector3f WHITE = new NopVector3f(1.0F, 1.0F, 1.0F);

   protected ResourceLocation textureUrl = null;

   public ResourceLocation partId;
   public ResourceLocation texture = null;
   public boolean usePlayerSkin = false;
   public NopVector3f color = WHITE;
   public String url = "";

   public MpmPart getPart() { return MpmPartReader.PARTS.get(partId); }

   public ResourceLocation getTexture() {
      if (getUrlTexture() != null) { return getUrlTexture(); }
      if (texture != null) { return texture; }
      MpmPart part = getPart();
      return part != null && part.texture != null ? getPart().texture : MissingTextureAtlasSprite.getLocation();
   }

   public ResourceLocation getUrlTexture() {
       if (textureUrl == null) {
          if (!url.isEmpty()) {
             ResourceLocation resource = ResourceDownloader.getUrlResourceLocation(url, false);
             File file = ResourceDownloader.getUrlFile(url, false);
             TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
             SimpleTexture empty = new SimpleTexture(resource);
             AbstractTexture object = texturemanager.getTexture(resource, empty);
             if (object == empty) {
                textureUrl = getDefaultTexture();
                ResourceDownloader.load(new ImageDownloadAlt(file, url, resource, getDefaultTexture(), false, () -> textureUrl = resource));
             } else {
                textureUrl = resource;
             }
          }
       }
       return textureUrl;
   }

   public void setTexture(String s) {
      texture = s != null && !s.isEmpty() ? ResourceLocation.tryParse(s) : null;
   }

   public void setUrl(String urlIn) {
      if (!NoppesStringUtils.areEqual(url, urlIn)) {
         url = urlIn;
         textureUrl = null;
      }
   }

   public ResourceLocation getDefaultTexture() { return texture != null ? texture : getPart().texture; }

   public int getColor() {
      int r = (int)(color.x * 255.0F) << 16;
      int g = (int)(color.y * 255.0F) << 8;
      int b = (int)(color.z * 255.0F);
      return r + g + b;
   }

   public void setColor(int colorIn) {
      color = new NopVector3f((float) (colorIn >> 16 & 255) / 255.0F, (float) (colorIn >> 8 & 255) / 255.0F, (float) (colorIn & 255) / 255.0F);
   }

   public CompoundTag getNbt() {
      CompoundTag item = new CompoundTag();
      item.putString("Id", partId.toString());
      item.putBoolean("UsePlayerSkin", usePlayerSkin);
      item.putString("Url", url);
      item.putString("Texture", texture == null ? "" : texture.toString());
      item.putFloat("ColorR", color.x);
      item.putFloat("ColorG", color.y);
      item.putFloat("ColorB", color.z);
      return item;
   }

   public void setNbt(CompoundTag compound) {
      partId = ResourceLocation.tryParse(compound.getString("Id"));
      usePlayerSkin = compound.getBoolean("UsePlayerSkin");
      setUrl(compound.getString("Url"));
      setTexture(compound.getString("Texture"));
      color = new NopVector3f(compound.getFloat("ColorR"), compound.getFloat("ColorG"), compound.getFloat("ColorB"));
   }

}
