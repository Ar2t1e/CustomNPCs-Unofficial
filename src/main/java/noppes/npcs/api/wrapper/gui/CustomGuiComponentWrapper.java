package noppes.npcs.api.wrapper.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.util.Util;

public abstract class CustomGuiComponentWrapper implements ICustomGuiComponent {

   public static CustomGuiComponentWrapper createFromNBT(CompoundTag nbt) {
      return switch (nbt.getInt("type")) {
         case 0 -> (new CustomGuiButtonWrapper()).fromNBT(nbt);
         case 1 -> (new CustomGuiLabelWrapper()).fromNBT(nbt);
         case 2 -> (new CustomGuiTexturedRectWrapper()).fromNBT(nbt);
         case 3 -> (new CustomGuiTextFieldWrapper()).fromNBT(nbt);
         case 4 -> (new CustomGuiScrollWrapper()).fromNBT(nbt);
         case 5 -> (new CustomGuiItemSlotWrapper()).fromNBT(nbt);
         case 6 -> (new CustomGuiTextAreaWrapper()).fromNBT(nbt);
         case 7 -> (new CustomGuiButtonListWrapper()).fromNBT(nbt);
         case 8 -> (new CustomGuiSliderWrapper()).fromNBT(nbt);
         case 9 -> (new CustomGuiEntityDisplayWrapper()).fromNBT(nbt);
         case 10 -> (new CustomGuiAssetsSelectorWrapper()).fromNBT(nbt);
         case 11 -> (new CustomGuiColoredLineWrapper()).fromNBT(nbt);
         case 12 -> (new CustomGuiItemRendererWrapper()).fromNBT(nbt);
         default -> null;
      };
   }

   protected int id;
   protected int posX;
   protected int posY;
   protected int width;
   protected int height;
   protected List<Component> hoverText = new ArrayList<>();
   protected boolean enabled = true;
   protected boolean visible = true;
   protected UUID uniqueId = UUID.randomUUID();
   public boolean disablePackets = false;

   // New from Unofficial (BetaZavr)
   private int offsetType = 0;
   private final int[] offsets = new int[] { 0, 0 };

   public CustomGuiComponentWrapper setDisablePackets() {
      disablePackets = true;
      return this;
   }

   @Override
   public int getId() { return id; }

   @Override
   public CustomGuiComponentWrapper setId(int idIn) {
      id = idIn;
      return this;
   }

   @Override
   public boolean getEnabled() {
      return enabled;
   }

   @Override
   public CustomGuiComponentWrapper setEnabled(boolean bo) {
      enabled = bo;
      return this;
   }

   @Override
   public boolean getVisible() { return visible; }

   public CustomGuiComponentWrapper setVisible(boolean bo) {
      visible = bo;
      return this;
   }

   @Override
   public UUID getUniqueID() {
      return uniqueId;
   }

   @Override
   public int getPosX() {
      return posX;
   }

   @Override
   public int getPosY() {
      return posY;
   }

   @Override
   public CustomGuiComponentWrapper setPos(int x, int y) {
      posX = x;
      posY = y;
      return this;
   }

   @Override
   public int getWidth() {
      return width;
   }

   @Override
   public int getHeight() {
      return height;
   }

   @Override
   public CustomGuiComponentWrapper setSize(int widthIn, int heightIn) {
      width = widthIn;
      height = heightIn;
      return this;
   }

   @Override
   public boolean hasHoverText() {
      return !hoverText.isEmpty();
   }

   @Override
   public String[] getHoverText() {
      String[] ht = new String[hoverText.size()];
      for(int i = 0; i < hoverText.size(); ++i) { ht[i] = Util.instance.getOldFormattedText(hoverText.get(i)); }
      return ht;
   }

   public List<Component> getHoverTextList() { return hoverText; }

   @Override
   public CustomGuiComponentWrapper setHoverText(String text) {
      hoverText = new ArrayList<>();
      hoverText.add(Component.translatable(text));
      return this;
   }

   public CustomGuiComponentWrapper setHoverText(String[] text) {
      hoverText = new ArrayList<>();
      for (String obj : text) { hoverText.add(Component.translatable(obj)); }
      return this;
   }

   public CustomGuiComponentWrapper setHoverText(List<Object> list) {
      hoverText = new ArrayList<>();
      for (Object obj : list) {
         if (obj instanceof Component component) { hoverText.add(component); }
         else { hoverText.add(Component.translatable(String.valueOf(obj))); }
      }
      return this;
   }

   public CompoundTag toNBT(CompoundTag nbt) {
      nbt.putInt("id", id);
      nbt.putBoolean("enabled", enabled);
      nbt.putBoolean("visible", visible);
      nbt.putUUID("uniqueId", uniqueId);
      nbt.putIntArray("pos", new int[]{ posX, posY });
      nbt.putIntArray("size", new int[]{ width, height });
      if (hoverText != null) {
         ListTag list = new ListTag();
         for (Component component : hoverText) { list.add(StringTag.valueOf(Component.Serializer.toJson(component))); }
         if (!list.isEmpty()) { nbt.put("hover", list); }
      }
      nbt.putInt("type", getType());
      return nbt;
   }

   public CustomGuiComponentWrapper fromNBT(CompoundTag nbt) {
      setId(nbt.getInt("id"));
      setEnabled(nbt.getBoolean("enabled"));
      setVisible(nbt.getBoolean("visible"));
      uniqueId = nbt.getUUID("uniqueId");
      setPos(nbt.getIntArray("pos")[0], nbt.getIntArray("pos")[1]);
      setSize(nbt.getIntArray("size")[0], nbt.getIntArray("size")[1]);
      hoverText.clear();
      if (nbt.contains("hover", 9)) {
         ListTag list = nbt.getList("hover", 8);
         for (int i = 0; i < list.size(); ++i) { hoverText.add(Component.Serializer.fromJson(list.getString(i))); }
      }
      return this;
   }

   // New from Unofficial (BetaZavr)
   @Override
   public int getOffsetType() { return offsetType; }

   @Override
   public void offSet(int offsetType, double[] windowSize) {
      this.offsetType = offsetType;
      switch (offsetType) {
         case 1: { // left down
            this.offsets[0] = 0;
            this.offsets[1] = (int) windowSize[1];
            break;
         }
         case 2: { // right up
            this.offsets[0] = (int) windowSize[0];
            this.offsets[1] = 0;
            break;
         }
         case 3: { // right down
            this.offsets[0] = (int) windowSize[0];
            this.offsets[1] = (int) windowSize[1];
            break;
         }
         case 4: { // center
            this.offsets[0] = (int) (windowSize[0] / 2.0d);
            this.offsets[1] = (int) (windowSize[1] / 2.0d);
            break;
         }
         default: { // left up
            this.offsets[0] = 0;
            this.offsets[1] = 0;
         }
      }
   }

}
