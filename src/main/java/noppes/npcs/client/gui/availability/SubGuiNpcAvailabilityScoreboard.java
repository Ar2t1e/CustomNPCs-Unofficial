package noppes.npcs.client.gui.availability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumAvailabilityScoreboard;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.AvailabilityScoreboardData;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Change from Unofficial (BetaZavr)
public class SubGuiNpcAvailabilityScoreboard
        extends GuiNPCInterface
        implements ICustomScrollListener, ITextfieldListener {

   protected final Availability availability;
   protected final Map<Component, String> dataNames = new HashMap<>();
   protected final Map<Component, AvailabilityScoreboardData> dataSets = new HashMap<>();
   protected GuiCustomScrollNop scroll;
   protected Component select = Component.empty();

   public SubGuiNpcAvailabilityScoreboard(Availability availabilityIn) {
      super();
      setBackground("menubg.png");
      imageWidth = 316;
      imageHeight = 217;

      availability = availabilityIn;
   }

   @Override
   public void init() {
      super.init();
      boolean isSelect = !select.getString().isEmpty();
      // title
      addLabel(1, guiLeft + 6, guiTop + 4, "availability.available.6")
              .setSize(imageWidth - 12, 12)
              .setCenter(imageWidth - 12);
      // exit
      addButton(66, guiLeft + 6, guiTop + 192, "gui.done")
              .setSize(70, 20)
              .setHoverTexts("hover.back");
      // data
      if (scroll == null) { scroll = addScroll(6).setSize(imageWidth - 12, imageHeight - 66); }
      dataNames.clear();
      dataSets.clear();
      for (String objectiveName : availability.scoreboards.keySet()) {
         AvailabilityScoreboardData asd = availability.scoreboards.get(objectiveName);
         MutableComponent key = Component.literal(objectiveName + " - ")
                 .append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
                 .append(Component.translatable("availability." + asd.scoreboardType.name().toLowerCase()).withStyle(ChatFormatting.DARK_AQUA))
                 .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                 .append(Component.translatable("" + asd.scoreboardValue).withStyle(ChatFormatting.BLUE))
                 .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
         dataNames.put(key, objectiveName);
         dataSets.put(key, asd);
      }
      if (isSelect) {
         boolean found = false;
         for (Component line : dataSets.keySet()) {
            if (line.getString().equals(select.getString())) {
               found= true;
               break;
            }
         }
         if (!found) {
            select = Component.empty();
            isSelect = false;
         }
      }
      scroll.setNormalList(new ArrayList<>(dataNames.keySet()));
      if (isSelect) { scroll.setSelected(select); }
      add(scroll.setPos(guiLeft + 6, guiTop + 14));
      // type
      int p = 0;
      if (isSelect) { p = dataSets.get(select).scoreboardType.ordinal(); }
      addButton(0, guiLeft + 6, guiTop + imageHeight - 46, false, p,
              "availability.smaller", "availability.equals", "availability.bigger")
              .setSize(50, 20)
              .setIsEnabled(isSelect)
              .setHoverTexts("availability.hover.enum.type");
      // name
      addTextField(0, guiLeft + 59, guiTop + imageHeight - 46, 189, 20, isSelect ? dataNames.get(select) : "")
              .setHoverTexts("availability.hover.scoreboard.name");
      // value
      addTextField(1, guiLeft + 252, guiTop + imageHeight - 46, 36, 20, isSelect ? dataSets.get(select).scoreboardValue : "")
              .setMinMaxDefault(Integer.MIN_VALUE, Integer.MAX_VALUE, 0)
              .setHoverTexts("availability.hover.scoreboard.value");
      addButton(2, guiLeft + 290, guiTop + imageHeight - 46, "X")
              .setSize(20, 20)
              .setIsEnabled(isSelect)
              .setHoverTexts("availability.hover.remove");
      // extra
      addButton(3, guiLeft + imageWidth - 76, guiTop + 192, "availability.more")
              .setSize(70, 20)
              .setIsEnabled(isSelect)
              .setHoverTexts("availability.hover.more");
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 0 : {
            if (!dataNames.containsKey(select)) { return; }
            String objectiveName = dataNames.get(select);
            AvailabilityScoreboardData asd = availability.scoreboards.get(objectiveName);
            asd.scoreboardType = EnumAvailabilityScoreboard.values()[button.getValue()];
            availability.scoreboards.put(objectiveName, asd);
            select = Component.literal(objectiveName + " - ")
                    .append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
                    .append(Component.translatable("availability." + asd.scoreboardType.name().toLowerCase()).withStyle(ChatFormatting.DARK_AQUA))
                    .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                    .append(Component.translatable("" + asd.scoreboardValue).withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
            init();
            break;
         }
         case 2 : {
            availability.scoreboards.remove(dataNames.get(select));
            select = Component.empty();
            init();
            break;
         }
         case 3 : { // More
            save();
            init();
            break;
         }
         case 66 : onClose(); break;
      }
   }

   @Override
   public void unFocused(GuiTextFieldNop textfield) {
      if (textfield.isEmpty()) { return; }
      String objectiveName = "";
      AvailabilityScoreboardData asd = null;
      int value = NoppesStringUtils.parseInt(getTextField(1).getValue(), 0);
      if (dataNames.containsKey(select)) {
         objectiveName = dataNames.get(select);
         asd = availability.scoreboards.get(objectiveName);
      }
      if (textfield.id == 0) {
         if (objectiveName == null || objectiveName.isEmpty() || asd == null) {
            objectiveName = textfield.getValue();
            asd = new AvailabilityScoreboardData(EnumAvailabilityScoreboard.SMALLER, value);
         } else {
            if (objectiveName.equals(textfield.getValue())) { return; }
            objectiveName = textfield.getValue();
            availability.scoreboards.remove(dataNames.get(select));
         }
      }
      else if (textfield.id == 1) {
         if (asd == null || asd.scoreboardValue == value) { return; }
         asd.scoreboardValue = value;
      }
      if (asd != null) {
         availability.scoreboards.put(objectiveName, asd);
         select = Component.literal(objectiveName + " - ")
                 .append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
                 .append(Component.translatable("availability." + asd.scoreboardType.name().toLowerCase()).withStyle(ChatFormatting.DARK_AQUA))
                 .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                 .append(Component.translatable("" + asd.scoreboardValue).withStyle(ChatFormatting.BLUE))
                 .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
      }
      init();
   }

   // New from Unofficial (BetaZavr)
   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) {
      select = scroll.getNormalSelected();
      init();
   }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

   @Override
   public void save() {
      if (!dataNames.containsKey(select)) { return; }
      EnumAvailabilityScoreboard eas = EnumAvailabilityScoreboard.values()[getButton(0).getValue()];
      int value = NoppesStringUtils.parseInt(getTextField(1).getValue(), 0);
      String objectiveName = dataNames.get(select);
      availability.scoreboards.put(objectiveName, new AvailabilityScoreboardData(eas, value));
      select = Component.empty();
   }

}
