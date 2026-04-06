package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.IColoredLine;

public class CustomGuiColoredLineWrapper extends CustomGuiComponentWrapper implements IColoredLine {

   protected int xEnd;
   protected int yEnd;
   protected int color;
   protected float thickness;

   public CustomGuiColoredLineWrapper() { }

   public CustomGuiColoredLineWrapper(int id, int xStart, int yStart, int xEnd, int yEnd, int colorIn, float thicknessIn) {
      setId(id);
      setPos(xStart, yStart);
      setEnd(xEnd, yEnd);
      color = colorIn;
      thickness = thicknessIn;
   }

   @Override
   public int getColor() { return color; }

   @Override
   public IColoredLine setColor(int colorIn) {
      color = colorIn;
      return this;
   }

   @Override
   public int getXEnd() { return xEnd; }

   @Override
   public int getYEnd() { return yEnd; }

   @Override
   public IColoredLine setEnd(int x, int y) {
      xEnd = x;
      yEnd = y;
      return this;
   }

   @Override
   public float getThickness() { return thickness; }

   @Override
   public IColoredLine setThickness(float thicknessIn) {
      thickness = thicknessIn;
      return this;
   }

   @Override
   public int getType() { return GuiComponentType.COLORED_LINE.get(); }

   @Override
   public CompoundTag toNBT(CompoundTag compound) {
      super.toNBT(compound);
      compound.putInt("xEnd", xEnd);
      compound.putInt("yEnd", yEnd);
      compound.putInt("color", color);
      compound.putFloat("thickness", thickness);
      return compound;
   }

   @Override
   public CustomGuiComponentWrapper fromNBT(CompoundTag compound) {
      super.fromNBT(compound);
      setColor(compound.getInt("color"));
      setThickness(compound.getFloat("thickness"));
      setEnd(compound.getInt("xEnd"), compound.getInt("yEnd"));
      return this;
   }

}
