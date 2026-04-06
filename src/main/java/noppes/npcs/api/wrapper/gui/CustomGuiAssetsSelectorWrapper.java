package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.functions.gui.GuiComponentClicked;
import noppes.npcs.api.functions.gui.GuiComponentUpdate;
import noppes.npcs.api.gui.IAssetsSelector;
import noppes.npcs.api.gui.ICustomGui;

public class CustomGuiAssetsSelectorWrapper extends CustomGuiComponentWrapper implements IAssetsSelector {

   protected String selected = "";
   protected String root = "textures";
   protected String type = "png";
   protected GuiComponentUpdate<IAssetsSelector> onChange = null;
   protected GuiComponentClicked<IAssetsSelector> onPress = null;

   public CustomGuiAssetsSelectorWrapper() { }

   public CustomGuiAssetsSelectorWrapper(int id, int x, int y, int width, int height) {
      setId(id);
      setPos(x, y);
      setSize(width, height);
   }

   @Override
   public String getSelected() { return selected; }

   @Override
   public CustomGuiAssetsSelectorWrapper setSelected(String selectedIn) {
      selected = selectedIn;
      return this;
   }

   @Override
   public String getRoot() { return root; }

   @Override
   public CustomGuiAssetsSelectorWrapper setRoot(String rootIn) {
      root = rootIn;
      return this;
   }

   @Override
   public String getFileType() { return type; }

   @Override
   public CustomGuiAssetsSelectorWrapper setFileType(String typeIn) {
      type = typeIn;
      return this;
   }

   public final void onPress(ICustomGui gui) {
      if (onPress != null) { onPress.onClick(gui, this); }
   }

   @Override
   public CustomGuiAssetsSelectorWrapper setOnPress(GuiComponentClicked<IAssetsSelector> onPressIn) {
      onPress = onPressIn;
      return this;
   }

   public final void onChange(ICustomGui gui) {
      if (onChange != null) { onChange.onChange(gui, this); }
   }

   @Override
   public CustomGuiAssetsSelectorWrapper setOnChange(GuiComponentUpdate<IAssetsSelector> onChangeIn) {
      onChange = onChangeIn;
      return this;
   }

   @Override
   public int getType() { return GuiComponentType.ASSETS_SELECTOR.get(); }

   @Override
   public CompoundTag toNBT(CompoundTag nbt) {
      super.toNBT(nbt);
      nbt.putString("selected", selected);
      nbt.putString("filetype", type);
      nbt.putString("root", root);
      return nbt;
   }

   @Override
   public CustomGuiComponentWrapper fromNBT(CompoundTag nbt) {
      super.fromNBT(nbt);
      setSelected(nbt.getString("selected"));
      setFileType(nbt.getString("filetype"));
      setRoot(nbt.getString("root"));
      return this;
   }

}
