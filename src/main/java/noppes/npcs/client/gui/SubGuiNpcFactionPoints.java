package noppes.npcs.client.gui;

import net.minecraft.network.chat.Component;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.util.Util;

public class SubGuiNpcFactionPoints extends GuiBasic implements ITextfieldListener {

   protected final Faction faction;

   public SubGuiNpcFactionPoints(Faction factionIn) {
      super();
      setBackground("menubg.png");
      imageWidth = 256;
      imageHeight = 216;

      faction = factionIn;
   }

   @Override
   public void init() {
      super.init();
      // default Points
      addLabel(2, guiLeft + 4, guiTop + 33, "faction.default");
      addTextField(2, guiLeft + 8 + font.width(getLabel(2).getMessage()), guiTop + 28, 70, 20, faction.defaultPoints)
              .setMinMaxDefault(0, Integer.MAX_VALUE, faction.defaultPoints)
              .setHoverTexts("faction.hover.point.def")
              .setMaxLength(6);
      // unfriendly -> neutral
      Component title = Component.translatable("faction.unfriendly").append("<->").append(Component.translatable("faction.neutral"));
      addLabel(3, guiLeft + 4, guiTop + 80, title);
      GuiTextFieldNop textField3 = addTextField(3, guiLeft + 8 + font.width(title), guiTop + 75, 70, 20, faction.neutralPoints)
              .setMinMaxDefault(0, Integer.MAX_VALUE, faction.neutralPoints)
              .setHoverTexts("faction.hover.point.unfr");
      title = Component.translatable("faction.neutral").append("<->").append(Component.translatable("faction.friendly"));
      addLabel(4, guiLeft + 4, guiTop + 105, title);
      GuiTextFieldNop textField4 = addTextField(4, guiLeft + 8 + font.width(title), guiTop + 100, 70, 20, faction.friendlyPoints)
              .setMinMaxDefault(0, Integer.MAX_VALUE, faction.friendlyPoints);
      if (textField3.getX() > textField4.getX()) { textField4.setX(textField3.getX()); }
      else { textField3.setX(textField4.getX()); }
      addButton(66, guiLeft + 20, guiTop + 192, "gui.done")
              .setSize(90, 20)
              .setHoverTexts("hover.back");
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      if (button.id == 66) { onClose(); }
   }

   @Override
   public void unFocused(GuiTextFieldNop textField) {
      switch (textField.id) {
         case 2: faction.defaultPoints = textField.getInteger(); break;
         case 3: faction.neutralPoints = textField.getInteger(); break;
         case 4: faction.friendlyPoints = textField.getInteger(); break;
      }
   }

}
