package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.gui.IComponentsScrollableWrapper;
import noppes.npcs.api.gui.ICustomGuiComponent;

public class GuiComponentsScrollableWrapper extends GuiComponentsWrapper implements IComponentsScrollableWrapper {

   private boolean enabled = false;
   public int x;
   public int y;
   public int width;
   public int height;
   public int scrollAmount = 0;
   public GuiComponentsWrapper parent;

   public GuiComponentsScrollableWrapper(GuiComponentsWrapper parentIn, IPlayer<?> player) {
      super(player);
      parent = parentIn;
   }

   public GuiComponentsScrollableWrapper init(int xIn, int yIn, int widthIn, int heightIn) {
      enabled = true;
      x = xIn;
      y = yIn;
      width = widthIn;
      height = heightIn;
      return this;
   }

   public CompoundTag getComponentNbt() {
      CompoundTag comp = super.getComponentNbt();
      comp.putBoolean("enabled", enabled);
      comp.putInt("x", x);
      comp.putInt("y", y);
      comp.putInt("width", width);
      comp.putInt("height", height);
      return comp;
   }

   public void setComponentNbt(CompoundTag comp) {
      super.setComponentNbt(comp);
      enabled = comp.getBoolean("enabled");
      x = comp.getInt("x");
      y = comp.getInt("y");
      width = comp.getInt("width");
      height = comp.getInt("height");
   }

   public boolean isVisible(ICustomGuiComponent component) {
      return component.getPosY() >= scrollAmount && component.getPosY() + component.getHeight() <= height + scrollAmount;
   }

}
