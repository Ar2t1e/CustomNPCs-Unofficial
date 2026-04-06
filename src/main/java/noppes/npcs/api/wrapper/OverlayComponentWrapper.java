package noppes.npcs.api.wrapper;

import noppes.npcs.api.INbt;
import noppes.npcs.api.overlay.IOverlayComponent;

public abstract class OverlayComponentWrapper implements IOverlayComponent {

   private int id;
   private int x;
   private int y;

   public OverlayComponentWrapper(int id, int x, int y) {
      this.x = x;
      this.y = y;
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public int getPosX() {
      return this.x;
   }

   public int getPosY() {
      return this.y;
   }

   public IOverlayComponent setPos(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
   }

   public void toNbt(INbt iNbt) {
      iNbt.setInteger("id", this.id);
      iNbt.setInteger("type", this.getType());
      iNbt.setIntegerArray("pos", new int[]{this.x, this.y});
   }

   public void fromNbt(INbt iNbt) {
      int[] pos = iNbt.getIntegerArray("pos");
      this.x = pos[0];
      this.y = pos[1];
      this.id = iNbt.getInteger("id");
   }

}
