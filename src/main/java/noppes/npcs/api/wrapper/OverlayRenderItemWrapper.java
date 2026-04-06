package noppes.npcs.api.wrapper;

import net.minecraft.world.item.ItemStack;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.overlay.IRenderItemOverlay;

import java.util.Objects;

public class OverlayRenderItemWrapper extends OverlayComponentWrapper implements IRenderItemOverlay {

   private ItemStack item;

   public OverlayRenderItemWrapper(int id, int x, int y, IItemStack item) {
      super(id, x, y);
      if (item == null) {
         this.item = ItemStack.EMPTY;
      } else {
         this.item = item.getMCItemStack();
      }

   }

   public IItemStack getItem() {
      return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(this.item);
   }

   public IRenderItemOverlay setItem(IItemStack item) {
      this.item = item.getMCItemStack();
      return this;
   }

   public int getType() {
      return 2;
   }

   public void toNbt(INbt iNbt) {
      super.toNbt(iNbt);
      iNbt.mcSetTag("item", item.serializeNBT());
   }

   public void fromNbt(INbt iNbt) {
      super.fromNbt(iNbt);
      this.item = ItemStack.of(iNbt.getCompound("item").getMCNBT());
   }

}
