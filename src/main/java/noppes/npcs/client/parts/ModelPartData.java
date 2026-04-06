package noppes.npcs.client.parts;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ModelPartData {

   private static final Map<String, ResourceLocation> resources = new HashMap<>();
   public int color = new Color(0xFFFFFF).getRGB();
   public byte type = 0;
   public byte pattern = 0;
   public boolean playerTexture = false;
   public String name;
   private ResourceLocation location;

   public ModelPartData(String nameIn) {
      name = nameIn;
   }

   public CompoundTag save() {
      CompoundTag compound = new CompoundTag();
      compound.putByte("Type", type);
      compound.putInt("Color", color);
      compound.putBoolean("PlayerTexture", playerTexture);
      compound.putByte("Pattern", pattern);
      return compound;
   }

   public void load(CompoundTag compound) {
      if (!compound.contains("Type")) {
         type = -1;
      } else {
         type = compound.getByte("Type");
         color = compound.getInt("Color");
         playerTexture = compound.getBoolean("PlayerTexture");
         pattern = compound.getByte("Pattern");
         location = null;
      }
   }

   public ResourceLocation getResource() {
       if (location == null) {
           String texture = name + "/" + type;
           if ((location = resources.get(texture)) == null) {
               location = ResourceLocation.tryParse("moreplayermodels:textures/" + texture + ".png");
               resources.put(texture, location);
           }
       }
       return location;
   }

   public void setType(int typeIn) {
      type = (byte) typeIn;
      location = null;
   }

   public String toString() {
      return "Color: " + color + " Type: " + type;
   }

   public String getColor() {
      StringBuilder str = new StringBuilder(Integer.toHexString(color));
      while (str.length() < 6) { str.insert(0, "0"); }
      return str.toString();
   }
}
