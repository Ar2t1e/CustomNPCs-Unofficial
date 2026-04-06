package noppes.npcs.client.gui.model;

import noppes.npcs.client.controllers.Preset;
import noppes.npcs.client.controllers.PresetController;
import noppes.npcs.client.parts.ModelData;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class GuiPresetSave extends GuiBasic {

   protected final ModelData data;

   public GuiPresetSave(ModelData dataIn) {
      super();
      data = dataIn;
      imageWidth = 200;
      drawDefaultBackground = true;
   }

   @Override
   public void init() {
      super.init();
      addTextField(0, guiLeft, guiTop + 70, 200, 20, "");
      addButton(0, guiLeft, guiTop + 100, "gui.save")
              .setSize(98, 20);
      addButton(1, guiLeft + 100, guiTop + 100, "gui.cancel")
              .setSize(98, 20);
   }

   @Override
   public void buttonEvent(GuiButtonNop guiButton) {
      if (guiButton.id == 0) {
         String name = getTextField(0).getValue().trim();
         if (name.isEmpty()) { return; }
         Preset preset = new Preset();
         preset.name = name;
         preset.data = data.copy();
         PresetController.instance.addPreset(preset);
      }
      onClose();
   }

}
