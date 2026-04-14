package noppes.npcs.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import noppes.npcs.api.gui.IDimensionGetter;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.DimensionController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.packets.server.SPacketDimensionsGet;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNpcDimension extends GuiNPCInterface
        implements IDimensionGetter, IGuiData, ICustomScrollListener {

   protected final HashMap<Component, ResourceLocation> data = new HashMap<>();
   protected GuiCustomScrollNop scroll;

   public GuiNpcDimension() {
      super();
      setBackground("menubg.png");
      imageWidth = 256;

      Packets.sendServer(new SPacketDimensionsGet());
   }

   @Override
   public void init() {
      super.init();
      if (scroll == null) { scroll = addScroll(0).setSize(186, 199); }
      if (minecraft == null) { minecraft = Minecraft.getInstance(); }
      if (!scroll.hasSelected()) {
         for (Component key : data.keySet()) {
            if (data.get(key).equals(minecraft.player != null ?
                    minecraft.player.level().dimension().location() : Level.OVERWORLD.location())) { scroll.setSelectedIndex(key); }
         }
      }
      add(scroll.setPos(guiLeft + 4, guiTop + 4));
      ResourceLocation id = data.getOrDefault(scroll.getNormalSelected(), Level.OVERWORLD.location());
      // title
      addLabel(0, guiLeft, guiTop + 4, "gui.dimensions")
              .setCenter(imageWidth);
      // settings
      addButton(1, guiLeft + 192, guiTop + 36, "gui.settings")
              .setSize(60, 20)
              .setIsEnabled(scroll.hasSelected() && DimensionController.has(id))
              .setHoverTexts("dimensions.hover.settings");
      // add
      addButton(2, guiLeft + 192, guiTop + 80, "gui.add")
              .setSize(60, 20)
              .setHoverTexts("dimensions.hover.add");
      // del
      addButton(3, guiLeft + 192, guiTop + 102, "gui.remove")
              .setSize(60, 20)
              .setIsEnabled(scroll.hasSelected() && DimensionController.has(id))
              .setHoverTexts("dimensions.hover.del");
      // tp to
      addButton(4, guiLeft + 192, guiTop + 14, "TP")
              .setSize(60, 20)
              .setIsEnabled(scroll.hasSelected())
              .setHoverTexts("dimensions.hover.tp");
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 1: {
            player.sendSystemMessage(Component.translatable("gui.wip"));
            if (data.containsKey(scroll.getNormalSelected())) {
               ResourceLocation id = data.get(scroll.getNormalSelected());
               if (DimensionController.has(id)) {
                  //CustomNpcs.proxy.openGui(null, EnumGuiType.DimensionSetting, id, 0, 0);
               }
            }
            break;
         } // settings
         case 2: {
            player.sendSystemMessage(Component.translatable("gui.wip"));
            //CustomNpcs.proxy.openGui(null, EnumGuiType.DimensionSetting, 0, 0, 0);
            break;
         } // add
         case 3: {
            if (data.containsKey(scroll.getNormalSelected())) {
               player.sendSystemMessage(Component.translatable("gui.wip"));
               ResourceLocation id = data.get(scroll.getNormalSelected());
               if (DimensionController.has(id)) {
                  /*ConfirmScreen guiYesNo = new ConfirmScreen((agree) -> {
                     if (agree) {
                        Client.sendData(EnumPacketServer.DimensionDelete, id);
                     }
                     NoppesUtil.openGUI(player, this);
                  },
                          Component.literal("ID: " + id),
                          Component.translatable("message.delete"));
                  setScreen(guiYesNo);*/
               }
            }
            break;
         } // remove
         case 4: tp(); break;
      }
   }

   @Override
   public void resetDimension() { init(); }

   // New from Unofficial (BetaZavr)
   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) { init(); }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) { tp(); }

   @Override
   public void setGuiData(CompoundTag compound) {
      data.clear();
      ListTag dimsData = compound.getList("Data", 10);
      List<Component> list = new ArrayList<>();
      List<Component> suffixes = new ArrayList<>();
      for (int i = 0; i < dimsData.size(); i++) {
         CompoundTag nbt = dimsData.getCompound(i);
         boolean isDel = nbt.getBoolean("deleted");
         ChatFormatting color = isDel ? ChatFormatting.DARK_GRAY : ChatFormatting.GRAY;
         ResourceLocation id = new ResourceLocation(nbt.getString("name"));
         Component key = Component.empty()
                 .append(Component.literal("\"").withStyle(color))
                 .append(Component.translatable(nbt.getString("name")).withStyle(isDel ? ChatFormatting.GRAY : ChatFormatting.RESET))
                 .append(Component.literal("\"").withStyle(color));
         list.add(key);
         data.put(key, id);
         boolean isMC = Level.OVERWORLD.location().equals(id) || Level.NETHER.location().equals(id) || Level.END.location().equals(id);
         Component sfx = Component.empty()
                 .append(Component.literal(isMC ? "MC" : "Mod").withStyle(isMC ? ChatFormatting.AQUA : ChatFormatting.GOLD))
                 .append(Component.literal(".").withStyle(ChatFormatting.GRAY))
                 .append(Component.literal(isDel ? "delete" : nbt.getBoolean("loaded") ? "loaded" : "unloaded")
                         .withStyle(isDel ? ChatFormatting.GRAY : nbt.getBoolean("loaded") ? ChatFormatting.GREEN : ChatFormatting.RED));
         suffixes.add(sfx);
      }
      scroll.setUnsortedList(list)
              .setSuffixes(suffixes);
      init();
   }

   private void tp() {
      if (data.containsKey(scroll.getNormalSelected())) {
         Packets.sendServer(new SPacketDimensionTeleport(data.get(scroll.getNormalSelected())));
         onClose();
      }
   }

}
