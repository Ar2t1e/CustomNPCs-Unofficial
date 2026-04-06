package noppes.npcs.client.parts;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.util.ValueUtil;

public class ModelPartConfig {

   public boolean notShared = false;
   public float scaleX = 1.0F;
   public float scaleY = 1.0F;
   public float scaleZ = 1.0F;
   public float transX = 0.0F;
   public float transY = 0.0F;
   public float transZ = 0.0F;

   public CompoundTag save() {
      CompoundTag compound = new CompoundTag();
      compound.putFloat("ScaleX", scaleX);
      compound.putFloat("ScaleY", scaleY);
      compound.putFloat("ScaleZ", scaleZ);
      compound.putFloat("TransX", transX);
      compound.putFloat("TransY", transY);
      compound.putFloat("TransZ", transZ);
      compound.putBoolean("NotShared", notShared);
      return compound;
   }

   public void load(CompoundTag compound) {
      scaleX = ValueUtil.correctFloat(compound.getFloat("ScaleX"), 0.5f, 1.5f);
      scaleY = ValueUtil.correctFloat(compound.getFloat("ScaleY"), 0.5f, 1.5f);
      scaleZ = ValueUtil.correctFloat(compound.getFloat("ScaleZ"), 0.5f, 1.5f);
      transX = ValueUtil.correctFloat(compound.getFloat("TransX"), -1.0f, 1.0f);
      transY = ValueUtil.correctFloat(compound.getFloat("TransY"), -1.0f, 1.0f);
      transZ = ValueUtil.correctFloat(compound.getFloat("TransZ"), -1.0f, 1.0f);
      notShared = compound.getBoolean("NotShared");
   }


   public void setScale(float x, float y) {
      scaleZ = scaleX = x;
      scaleY = y;
   }

   public void setScale(float x, float y, float z) {
      scaleX = x;
      scaleY = y;
      scaleZ = z;
   }

   public void setTranslate(float transXIn, float transYIn, float transZIn) {
      transX = transXIn;
      transY = transYIn;
      transZ = transZIn;
   }

   public void copyValues(ModelPartConfig config) {
      scaleX = config.scaleX;
      scaleY = config.scaleY;
      scaleZ = config.scaleZ;
      transX = config.transX;
      transY = config.transY;
      transZ = config.transZ;
   }

   public String toString() { return "ModelPartConfig {ScaleX: " + scaleX + "; ScaleY: " + scaleY + "; ScaleZ: " + scaleZ +
           "; transX: " + transX + "; transY: " + transY + "; transZ: " + transZ + "; notShared: " + notShared + "}"; }

}
