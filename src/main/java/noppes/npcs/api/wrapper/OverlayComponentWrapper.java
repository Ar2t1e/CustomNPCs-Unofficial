package noppes.npcs.api.wrapper;

import noppes.npcs.api.INbt;
import noppes.npcs.api.overlay.IOverlayComponent;

public abstract class OverlayComponentWrapper implements IOverlayComponent {

   private int id;
   private int x;
   private int y;

   public OverlayComponentWrapper(int idIn, int xIn, int yIn) {
      x = xIn;
      y = yIn;
      id = idIn;
   }

   @Override
   public int getId() { return id; }

   @Override
   public int getPosX() { return x; }

   @Override
   public int getPosY() { return y; }

   @Override
   public IOverlayComponent setPos(int xIn, int yIn) {
      x = xIn;
      y = yIn;
      return this;
   }

   @Override
   public void toNbt(INbt iNbt) {
      iNbt.setInteger("id", id);
      iNbt.setInteger("type", getType());
      iNbt.setIntegerArray("pos", new int[]{ x, y });
   }

   @Override
   public void fromNbt(INbt iNbt) {
      int[] pos = iNbt.getIntegerArray("pos");
      x = pos[0];
      y = pos[1];
      id = iNbt.getInteger("id");
   }

}
