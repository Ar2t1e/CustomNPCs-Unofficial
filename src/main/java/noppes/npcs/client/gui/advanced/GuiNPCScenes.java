package noppes.npcs.client.gui.advanced;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.GuiTextAreaScreen;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class GuiNPCScenes extends GuiNPCInterface2 {

   protected final DataScenes scenes;
   protected DataScenes.SceneContainer scene;

   public GuiNPCScenes(EntityNPCInterface npc) {
      super(npc);
      backGui = EnumGuiType.MainMenuAdvanced;

      scenes = npc.advanced.scenes;
   }

   @Override
   public void init() {
      super.init();
      int x0 = guiLeft + 5;
      int x1 = x0 + 82;
      int x2 = x1 + 64;
      int x3 = x2 + 64;
      int x4 = x3 + (CustomNpcs.SceneButtonsEnabled ? 124 : 0);
      int y = guiTop + 5;
      if (CustomNpcs.SceneButtonsEnabled && !scenes.scenes.isEmpty()) {
         addLabel(102, x3 + 2, y, Component.translatable("gui.button").append(":"));
         y += 10;
      }
      for(int i = 0; i < scenes.scenes.size(); ++i) {
         DataScenes.SceneContainer scene = scenes.scenes.get(i);
         addLabel(i * 10, x0, y + 5, scene.name)
                 .setSize(80, 10);
         addButton(1 + i * 10, x1, y, false, scene.enabled ? 1 : 0, "gui.disabled", "gui.enabled")
                 .setSize(60, 20);
         addButton(2 + i * 10, x2, y, "selectServer.edit")
                 .setSize(60, 20);
         addButton(3 + i * 10, x4, y, "X")
                 .setSize(20, 20);
         if (CustomNpcs.SceneButtonsEnabled) {
            addButton(4 + i * 10, x3, y, false, scene.btn,
                    "gui.none", ClientProxy.Scene1.getName(), ClientProxy.Scene2.getName(), ClientProxy.Scene3.getName())
                    .setSize(120, 20);
         }
         y += 22;
      }
      y += 2;
      if (scenes.scenes.size() < 6) {
         addTextField(101, x0 + 1, y + 1, x3 - guiLeft - 11, 18, Component.translatable("scene.name").append(" " + (scenes.scenes.size() + 1)));
         addButton(101, x3, y, "gui.add")
                 .setSize(60, 20);
      }
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      if (button.id < 60) {
         DataScenes.SceneContainer sceneIn = scenes.scenes.get(button.id / 10);
         switch (button.id % 10) {
            case 1: sceneIn.enabled = button.getValue() == 1; break;
            case 2: scene = sceneIn; setSubGui(new GuiTextAreaScreen(0, scene.lines)); break;
            case 3: scenes.scenes.remove(sceneIn); init(); break;
            case 4: sceneIn.btn = button.getValue(); init(); break;
         }
      }
      if (button.id == 101) {
         scenes.addScene(getTextField(101).getValue());
         init();
      }
   }

   @Override
   public void subGuiClosed(Screen gui) {
      if (gui instanceof GuiTextAreaScreen sText && scene != null) {
         scene.lines = sText.text;
         scene = null;
      }
   }

   @Override
   public void save() { Packets.sendServer(new SPacketMenuSave(EnumMenuType.ADVANCED, npc.advanced.save(new CompoundTag()))); }


}
