package noppes.npcs.api.wrapper;

import noppes.npcs.api.INbt;
import noppes.npcs.api.overlay.IOverlayLabel;

public class OverlayLabelWrapper extends OverlayComponentWrapper implements IOverlayLabel {

   private String text;
   private boolean isCenter = false;
   private float scale = 1.0F;

   public OverlayLabelWrapper(int id, int x, int y, String textIn) {
      super(id, x, y);
      text = textIn;
   }

   @Override
   public String getText() { return text; }

   @Override
   public IOverlayLabel setText(String textIn) {
      text = textIn;
      return this;
   }

   @Override
   public float getScale() { return scale; }

   @Override
   public void setScale(float scaleIn) { scale = scaleIn; }

   @Override
   public IOverlayLabel setCentered(boolean centered) {
      isCenter = centered;
      return this;
   }

   @Override
   public boolean isCentered() { return isCenter; }

   @Override
   public int getType() { return 0; }

   @Override
   public void toNbt(INbt iNbt) {
      super.toNbt(iNbt);
      iNbt.setString("text", text);
      iNbt.setFloat("scale", scale);
      iNbt.setBoolean("centered", isCenter);
   }

   @Override
   public void fromNbt(INbt iNbt) {
      super.fromNbt(iNbt);
      text = iNbt.getString("text");
      scale = iNbt.getFloat("scale");
      isCenter = iNbt.getBoolean("centered");
   }

}
