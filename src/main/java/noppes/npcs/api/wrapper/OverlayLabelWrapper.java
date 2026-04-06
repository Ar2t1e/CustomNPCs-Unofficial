package noppes.npcs.api.wrapper;

import noppes.npcs.api.INbt;
import noppes.npcs.api.overlay.ILabel;

public class OverlayLabelWrapper extends OverlayComponentWrapper implements ILabel {

   private String text;
   private boolean isCenter = false;
   private float scale = 1.0F;

   public OverlayLabelWrapper(int id, int x, int y, String text) {
      super(id, x, y);
      this.text = text;
   }

   public String getText() {
      return this.text;
   }

   public ILabel setText(String text) {
      this.text = text;
      return this;
   }

   public float getScale() {
      return this.scale;
   }

   public void setScale(float scale) {
      this.scale = scale;
   }

   public ILabel setCentered(boolean centered) {
      this.isCenter = centered;
      return this;
   }

   public boolean isCentered() {
      return this.isCenter;
   }

   public int getType() {
      return 0;
   }

   public void toNbt(INbt iNbt) {
      super.toNbt(iNbt);
      iNbt.setString("text", this.text);
      iNbt.setFloat("scale", this.scale);
      if (this.isCenter) {
         iNbt.setBoolean("centered", true);
      }
   }

   public void fromNbt(INbt iNbt) {
      super.fromNbt(iNbt);
      this.text = iNbt.getString("text");
      this.scale = iNbt.getFloat("scale");
      this.isCenter = iNbt.getBoolean("centered");
   }

}
