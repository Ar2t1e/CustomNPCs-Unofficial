package noppes.npcs.api.gui.subgui;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.gui.IButton;
import noppes.npcs.api.gui.IButtonList;
import noppes.npcs.api.gui.IScroll;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.constants.EnumAvailabilityDialog;
import noppes.npcs.constants.EnumAvailabilityQuest;
import noppes.npcs.constants.EnumDayTime;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.Quest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

// Change from Unofficial (BetaZavr)
public class AvailabilityGui {

   public static CustomGuiWrapper open(Availability availability, IPlayer<?> player) {
      CustomGuiWrapper gui = new CustomGuiWrapper(player);
      gui.setBackgroundTexture(CustomNpcs.MODID + ":textures/gui/components.png");
      gui.setSize(280, 214);
      gui.getBackgroundRect().setTextureOffset(0, 0);
      gui.getBackgroundRect().setRepeatingTexture(64, 64, 4);
      IButton b = gui.addTexturedButton(666, "X", 266, -4, 14, 14, CustomNpcs.MODID + ":textures/gui/components.png", 0, 64);
      b.getTextureRect().setRepeatingTexture(64, 22, 3);
      b.setTextureHoverOffset(22).setHoverText("gui.close");
      b.setOnPress((guii, bb) -> guii.close());

      gui.addLabel(0, "availability.available", 0, 4, 280, 8).setCentered(true);
      int x = 6;
      int y = 14;
      int h = 18;
      gui.addButton(0, "availability.selectdialog", x, y, 120, h).setOnPress((gui2, bb) -> gui2.openSubGui(openDialog(availability, player))).setHoverText("availability.hover.selectdialog");
      gui.addButton(1, "availability.selectquest", x, y += h + 2, 120, h).setOnPress((gui2, bb) -> gui2.openSubGui(openQuest(availability, player))).setHoverText("availability.hover.selectquest");
      gui.addButton(2, "availability.selectfaction", x, y += h + 2, 120, h).setOnPress((gui2, bb) -> gui2.openSubGui(openFaction(availability, player))).setHoverText("availability.hover.selectfaction");
      gui.addButton(8, "availability.stack", x, y + h + 2, 120, h).setOnPress((gui2, bb) -> gui2.openSubGui(openStack(availability, player))).setHoverText("availability.hover.stack");
      // colloquium 2
      x += 124;
      y = 14;
      gui.addButton(3, "availability.selectscoreboard", x, y, 120, h).setOnPress((gui2, bb) -> gui2.openSubGui(openScoreboard(availability, player))).setHoverText("availability.hover.selectscoreboard");
      gui.addButton(6, "availability.selectnames", x, y += h + 2, 120, h).setOnPress((gui2, bb) -> gui2.openSubGui(openNames(availability, player))).setHoverText("availability.hover.selectnames");
      gui.addButton(7, "availability.storeddata", x, y += h + 2, 120, h).setOnPress((gui2, bb) -> gui2.openSubGui(openStoredData(availability, player))).setHoverText("availability.hover.storeddata");
      gui.addButton(9, "availability.region", x, y + h + 2, 120, h).setOnPress((gui2, bb) -> gui2.openSubGui(openRegions(availability, player))).setHoverText("availability.hover.region");
      // day type
      gui.addLabel(50, "availability.daytime", 4, 131, 90, 8);
      gui.addButtonList(50, 70, 126, 70, h).setValues("availability.own", "availability.always", "availability.night", "availability.day").setSelected(availability.daytime[0] == availability.daytime[1] ? 1 : availability.daytime[0] == 18 && availability.daytime[1] == 6 ? 2 : availability.daytime[0] == 6 && availability.daytime[1] == 18 ? 3 : 1)
              .setOnPress((gui2, button) -> {
                 if (((IButtonList) button).getSelected() == 0) {
                    gui.getTextField(52).setInteger(availability.daytime[0]);
                    gui.getTextField(53).setInteger(availability.daytime[1]);
                 } else {
                    switch (EnumDayTime.values()[((IButtonList) button).getSelected() - 1]) {
                       case Always: {
                          gui.getTextField(52).setInteger(0);
                          gui.getTextField(53).setInteger(0);
                          break;
                       }
                       case Night: {
                          gui.getTextField(52).setInteger(18);
                          gui.getTextField(53).setInteger(6);
                          break;
                       }
                       case Day: {
                          gui.getTextField(52).setInteger(6);
                          gui.getTextField(53).setInteger(18);
                          break;
                       }
                    }
                 }
                 availability.daytime[0] = gui.getTextField(52).getInteger();
                 availability.daytime[1] = gui.getTextField(53).getInteger();
              }).setHoverText("availability.hover.daytime.0");
      // min player level
      gui.addLabel(51, "availability.minlevel", 4, 153, 90, 8);
      gui.addTextField(51, 70, 149, 70, h - 2).setCharacterType(1).setMinMax(0, Integer.MAX_VALUE).setInteger(availability.minPlayerLevel)
              .setOnFocusLost((gui2, textfield) -> availability.daytime[0] = textfield.getInteger())
              .setHoverText("availability.hover.level");
      // start day time
      gui.addTextField(52, 145, 127, 40, h - 2).setCharacterType(1).setMinMax(0, 23).setInteger(availability.daytime[0])
              .setOnFocusLost((gui2, textfield) -> availability.daytime[0] = textfield.getInteger())
              .setHoverText("availability.hover.daytime.1");
      // next day time
      gui.addTextField(53, 190, 127, 40, h - 2).setCharacterType(1).setMinMax(0, 23).setInteger(availability.daytime[1])
              .setOnFocusLost((gui2, textfield) -> availability.daytime[1] = textfield.getInteger())
              .setHoverText("availability.hover.daytime.2");
      // health
      gui.addLabel(52, "availability.health", 4, 175, 90, 8);
      gui.addButtonList(4, 70, 170, 70, h).setValues("availability.always", "availability.bigger", "availability.smaller").setSelected(availability.daytime[0] == availability.daytime[1] ? 1 : availability.daytime[0] == 18 && availability.daytime[1] == 6 ? 2 : availability.daytime[0] == 6 && availability.daytime[1] == 18 ? 3 : 1)
              .setOnPress((gui2, button) -> {
                 availability.healthType = ((IButtonList) button).getSelected();
                 if (gui.getSlider(5) != null) { gui.getSlider(5).setVisible(availability.healthType != 0); }
              }).setHoverText("availability.hover.health.type");
      gui.addSlider(5, 145, 170, 106, 20, "%s %%").setMin(0).setMax(100).setValue(availability.health)
              .setOnChange((gui2, slider) -> availability.health = (int) slider.getValue());
      return gui;
   }

   // analog of SubGuiNpcAvailabilityDialog
   public static CustomGuiWrapper openDialog(Availability availability, IPlayer<?> player) {
      CustomGuiWrapper gui = new CustomGuiWrapper(player);
      gui.setBackgroundTexture(CustomNpcs.MODID + ":textures/gui/components.png");
      gui.setSize(256, 217);
      gui.getBackgroundRect().setTextureOffset(0, 0);
      gui.getBackgroundRect().setRepeatingTexture(64, 64, 4);
      IButton b = gui.addTexturedButton(666, "X", 308, -4, 14, 14, CustomNpcs.MODID + ":textures/gui/components.png", 0, 64);
      b.getTextureRect().setRepeatingTexture(64, 22, 3);
      b.setTextureHoverOffset(22).setHoverText("gui.close");
      b.setOnPress((guii, bb) -> open(availability, player));

      List<String> ids = new ArrayList<>();
      for (int id : availability.dialogues.keySet()) {
         MutableComponent key = Component.literal("ID:" + id + " - ");
         Dialog dialog = DialogController.instance.dialogs.get(id);
         if (dialog == null) {
            key.append(Component.translatable("quest.notfound").withStyle(ChatFormatting.DARK_RED));
         } else {
            key.append(Component.translatable(dialog.getCategory().getName() + "/").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(dialog.getName()).withStyle(ChatFormatting.RESET))
                    .append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
                    .append(Component.translatable("availability." + availability.dialogues.get(id).name().toLowerCase()).withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
         }
         ids.add(key.getString());
      }
      IScroll scroll = gui.addScroll(6, 6, 14, 244, 151)
              .setOnDoubleClick((gui2, bb) -> gui2.openSubGui(openDialogSelection(availability, player)))
              .setMultiSelect(false)
              .setList(ids.toArray(new String[0]));
      AtomicBoolean isSelect = new AtomicBoolean(scroll.getSelectionList().length != 0);
      // type
      gui.addButtonList(0, 6, 171, 50, 20).setValues("availability.always", "availability.after", "availability.before").setSelected(0)
              .setOnPress((gui2, button) -> {
                 int p = 0;
                 isSelect.set(false);
                 if (scroll.getSelectionList().length != 0) {
                    String select = scroll.getSelectionList()[0];
                    isSelect.set(true);
                    EnumAvailabilityDialog dAvailable = EnumAvailabilityDialog.values()[((IButtonList) button).getSelected()];
                    for (int id : availability.dialogues.keySet()) {
                       MutableComponent key = Component.literal("ID:" + id + " - ");
                       Dialog dialog = DialogController.instance.dialogs.get(id);
                       if (dialog == null) {
                          key.append(Component.translatable("quest.notfound").withStyle(ChatFormatting.DARK_RED));
                       } else {
                          key.append(Component.translatable(dialog.getCategory().getName() + "/").withStyle(ChatFormatting.GRAY))
                                  .append(Component.literal(dialog.getName()).withStyle(ChatFormatting.RESET))
                                  .append(Component.literal(" (").withStyle(ChatFormatting.GRAY))
                                  .append(Component.translatable("availability." + availability.dialogues.get(id).name().toLowerCase()).withStyle(ChatFormatting.BLUE))
                                  .append(Component.literal(")").withStyle(ChatFormatting.GRAY));
                       }
                       if (select.equals(key.getString())) {
                          if (dAvailable == EnumAvailabilityDialog.Always) { availability.dialogues.remove(id); }
                          else { availability.dialogues.put(id, dAvailable); }
                          p = dAvailable.ordinal();
                          break;
                       }
                    }
                 }
                 button.setLabel("" + p);
                 button.setEnabled(isSelect.get());
      }).setHoverText("availability.hover.enum.type");
      // select
      /*addButton(new GuiButtonNop(this, 1, guiLeft + 58, guiTop + imageHeight - 46, 170, 20, "availability.select").setHoverText("availability.hover.dialog"));
      // del
      addButton(new GuiButtonNop(this, 2, guiLeft + 230, guiTop + imageHeight - 46, 20, 20, "X").setHoverText("availability.hover.remove"));
      // extra
      addButton(new GuiButtonNop(this, 3, guiLeft + imageWidth - 76, guiTop + 192, 70, 20, "availability.more").setEnabled(isSelect).setHoverText("availability.hover.more"));
      updateGuiButtons();





      gui.addButtonList(1, 4, 14, 60, 20).setValues("availability.always", "availability.after", "availability.before").setSelected(availability.dialogAvailable.ordinal()).setOnPress((gui2, bb) -> {
         availability.dialogAvailable = EnumAvailabilityDialog.values()[((IButtonList)bb).getSelected()];
         if (availability.dialogAvailable == EnumAvailabilityDialog.Always) {
            availability.dialogId = -1;
         }
         enableDialogButtons(gui, availability.dialogAvailable, availability.dialogId, 2);
      });
      gui.addButton(2, "availability.selectdialog", 66, 14, 228, 20)
              .setOnPress((gui2, bb) -> gui2.openSubGui(SelectorGui.openDialog(availability.dialogId, player, (id) -> {
         availability.dialogId = id;
         enableDialogButtons(gui, availability.dialogAvailable, availability.dialogId, 2);
      })));
      gui.addButton(3, "X", 296, 14, 20, 20).setOnPress((gui2, bb) -> enableDialogButtons(gui, availability.dialogAvailable, availability.dialogId = -1, 2));
      enableDialogButtons(gui, availability.dialogAvailable, availability.dialogId, 2);
      gui.addButtonList(11, 4, 37, 60, 20).setValues("availability.always", "availability.after", "availability.before").setSelected(availability.dialog2Available.ordinal()).setOnPress((gui2, bb) -> {
         availability.dialog2Available = EnumAvailabilityDialog.values()[((IButtonList)bb).getSelected()];
         if (availability.dialog2Available == EnumAvailabilityDialog.Always) {
            availability.dialog2Id = -1;
         }

         enableDialogButtons(gui, availability.dialog2Available, availability.dialog2Id, 12);
      });
      gui.addButton(12, "availability.selectdialog", 66, 37, 228, 20).setOnPress((gui2, bb) -> gui2.openSubGui(SelectorGui.openDialog(availability.dialog2Id, player, (id) -> {
         availability.dialog2Id = id;
         enableDialogButtons(gui, availability.dialog2Available, availability.dialog2Id, 12);
      })));
      gui.addButton(13, "X", 296, 37, 20, 20).setOnPress((gui2, bb) -> enableDialogButtons(gui, availability.dialog2Available, availability.dialog2Id = -1, 12));
      enableDialogButtons(gui, availability.dialog2Available, availability.dialog2Id, 12);
      gui.addButtonList(21, 4, 60, 60, 20).setValues("availability.always", "availability.after", "availability.before").setSelected(availability.dialog3Available.ordinal()).setOnPress((gui2, bb) -> {
         availability.dialog3Available = EnumAvailabilityDialog.values()[((IButtonList)bb).getSelected()];
         if (availability.dialog3Available == EnumAvailabilityDialog.Always) {
            availability.dialog3Id = -1;
         }

         enableDialogButtons(gui, availability.dialog3Available, availability.dialog3Id, 22);
      });
      gui.addButton(22, "availability.selectdialog", 66, 60, 228, 20).setOnPress((gui2, bb) -> gui2.openSubGui(SelectorGui.openDialog(availability.dialog3Id, player, (id) -> {
         availability.dialog3Id = id;
         enableDialogButtons(gui, availability.dialog3Available, availability.dialog3Id, 22);
      })));
      gui.addButton(23, "X", 296, 60, 20, 20).setOnPress((gui2, bb) -> enableDialogButtons(gui, availability.dialog3Available, availability.dialog3Id = -1, 22));
      enableDialogButtons(gui, availability.dialog3Available, availability.dialog3Id, 22);
      gui.addButtonList(31, 4, 83, 60, 20).setValues("availability.always", "availability.after", "availability.before").setSelected(availability.dialog4Available.ordinal()).setOnPress((gui2, bb) -> {
         availability.dialog4Available = EnumAvailabilityDialog.values()[((IButtonList)bb).getSelected()];
         if (availability.dialog4Available == EnumAvailabilityDialog.Always) {
            availability.dialog4Id = -1;
         }

         enableDialogButtons(gui, availability.dialog4Available, availability.dialog4Id, 32);
      });
      gui.addButton(32, "availability.selectdialog", 66, 83, 228, 20).setOnPress((gui2, bb) -> gui2.openSubGui(SelectorGui.openDialog(availability.dialog4Id, player, (id) -> {
         availability.dialog4Id = id;
         enableDialogButtons(gui, availability.dialog4Available, availability.dialog4Id, 32);
      })));
      gui.addButton(33, "X", 296, 83, 20, 20).setOnPress((gui2, bb) -> enableDialogButtons(gui, availability.dialog4Available, availability.dialog4Id = -1, 32));
      enableDialogButtons(gui, availability.dialog4Available, availability.dialog4Id, 32);
      gui.addButton(16, "gui.done", 110, 110, 100, 20).setOnPress((gui2, bb) -> gui2.close());*/
      return gui;
   }

   // analog of SubGuiNpcAvailabilityQuest
   public static CustomGuiWrapper openQuest(Availability availability, IPlayer<?> player) {
      CustomGuiWrapper gui = new CustomGuiWrapper(player);
      gui.setBackgroundTexture(CustomNpcs.MODID + ":textures/gui/components.png");
      gui.setSize(320, 134);
      gui.getBackgroundRect().setTextureOffset(0, 0);
      gui.getBackgroundRect().setRepeatingTexture(64, 64, 4);
      IButton b = gui.addTexturedButton(666, "X", 308, -4, 14, 14, CustomNpcs.MODID + ":textures/gui/components.png", 0, 64);
      b.getTextureRect().setRepeatingTexture(64, 22, 3);
      b.setTextureHoverOffset(22).setHoverText("gui.close");
      b.setOnPress((guii, bb) -> guii.close());
      /*
      gui.addButtonList(1, 4, 14, 60, 20).setValues("availability.always", "availability.after", "availability.before", "availability.whenactive", "availability.whennotactive", "availability.completed", "availability.canStart").setSelected(availability.questAvailable.ordinal()).setOnPress((gui2, bb) -> {
         availability.questAvailable = EnumAvailabilityQuest.values()[((IButtonList)bb).getSelected()];
         if (availability.questAvailable == EnumAvailabilityQuest.Always) {
            availability.questId = -1;
         }
         enableQuestButtons(gui, availability.questAvailable, availability.questId, 2);
      });
      gui.addButton(2, "availability.selectquest", 66, 14, 228, 20).setOnPress((gui2, bb) -> gui2.openSubGui(SelectorGui.openQuest(availability.questId, player, (id) -> {
         availability.questId = id;
         enableQuestButtons(gui, availability.questAvailable, availability.questId, 2);
      })));
      gui.addButton(3, "X", 296, 14, 20, 20).setOnPress((gui2, bb) -> enableQuestButtons(gui, availability.questAvailable, availability.questId = -1, 2));
      enableQuestButtons(gui, availability.questAvailable, availability.questId, 2);
      gui.addButtonList(11, 4, 37, 60, 20).setValues("availability.always", "availability.after", "availability.before", "availability.whenactive", "availability.whennotactive", "availability.completed", "availability.canStart").setSelected(availability.quest2Available.ordinal()).setOnPress((gui2, bb) -> {
         availability.quest2Available = EnumAvailabilityQuest.values()[((IButtonList)bb).getSelected()];
         if (availability.quest2Available == EnumAvailabilityQuest.Always) {
            availability.quest2Id = -1;
         }

         enableQuestButtons(gui, availability.quest2Available, availability.quest2Id, 12);
      });
      gui.addButton(12, "availability.selectquest", 66, 37, 228, 20).setOnPress((gui2, bb) -> gui2.openSubGui(SelectorGui.openQuest(availability.quest2Id, player, (id) -> {
         availability.quest2Id = id;
         enableQuestButtons(gui, availability.quest2Available, availability.quest2Id, 12);
      })));
      gui.addButton(13, "X", 296, 37, 20, 20).setOnPress((gui2, bb) -> enableQuestButtons(gui, availability.quest2Available, availability.quest2Id = -1, 12));
      enableQuestButtons(gui, availability.quest2Available, availability.quest2Id, 12);
      gui.addButtonList(21, 4, 60, 60, 20).setValues("availability.always", "availability.after", "availability.before", "availability.whenactive", "availability.whennotactive", "availability.completed", "availability.canStart").setSelected(availability.quest3Available.ordinal()).setOnPress((gui2, bb) -> {
         availability.quest3Available = EnumAvailabilityQuest.values()[((IButtonList)bb).getSelected()];
         if (availability.quest3Available == EnumAvailabilityQuest.Always) {
            availability.quest3Id = -1;
         }

         enableQuestButtons(gui, availability.quest3Available, availability.quest3Id, 22);
      });
      gui.addButton(22, "availability.selectquest", 66, 60, 228, 20).setOnPress((gui2, bb) -> gui2.openSubGui(SelectorGui.openQuest(availability.quest3Id, player, (id) -> {
         availability.quest3Id = id;
         enableQuestButtons(gui, availability.quest3Available, availability.quest3Id, 22);
      })));
      gui.addButton(23, "X", 296, 60, 20, 20).setOnPress((gui2, bb) -> enableQuestButtons(gui, availability.quest3Available, availability.quest3Id = -1, 22));
      enableQuestButtons(gui, availability.quest3Available, availability.quest3Id, 22);
      gui.addButtonList(31, 4, 83, 60, 20).setValues("availability.always", "availability.after", "availability.before", "availability.whenactive", "availability.whennotactive", "availability.completed", "availability.canStart").setSelected(availability.quest4Available.ordinal()).setOnPress((gui2, bb) -> {
         availability.quest4Available = EnumAvailabilityQuest.values()[((IButtonList)bb).getSelected()];
         if (availability.quest4Available == EnumAvailabilityQuest.Always) {
            availability.quest4Id = -1;
         }

         enableQuestButtons(gui, availability.quest4Available, availability.quest4Id, 32);
      });
      gui.addButton(32, "availability.selectquest", 66, 83, 228, 20).setOnPress((gui2, bb) -> gui2.openSubGui(SelectorGui.openQuest(availability.quest4Id, player, (id) -> {
         availability.quest4Id = id;
         enableQuestButtons(gui, availability.quest4Available, availability.quest4Id, 32);
      })));
      gui.addButton(33, "X", 296, 83, 20, 20).setOnPress((gui2, bb) -> enableQuestButtons(gui, availability.quest4Available, availability.quest4Id = -1, 32));
      enableQuestButtons(gui, availability.quest4Available, availability.quest4Id, 32);
      gui.addButton(16, "gui.done", 110, 110, 100, 20).setOnPress((gui2, bb) -> gui2.close());*/
      return gui;
   }

   // analog of GuiQuestSelection
   private static void enableQuestButtons(CustomGuiWrapper gui, EnumAvailabilityQuest type, int questId, int selectId) {
      String s = "availability.selectquest";
      Quest quest = QuestController.instance.quests.get(questId);
      if (quest != null) {
         s = quest.getName();
      }

      ((IButton)gui.getComponent(selectId)).setLabel(s).setEnabled(type != EnumAvailabilityQuest.Always);
      gui.update();
   }

   // analog of SubGuiNpcAvailabilityScoreboard
   public static CustomGuiWrapper openScoreboard(Availability availability, IPlayer<?> player) {
      CustomGuiWrapper gui = new CustomGuiWrapper(player);
      gui.setBackgroundTexture(CustomNpcs.MODID + ":textures/gui/components.png");
      gui.setSize(316, 134);
      gui.getBackgroundRect().setTextureOffset(0, 0);
      gui.getBackgroundRect().setRepeatingTexture(64, 64, 4);

      /*IButton b = gui.addTexturedButton(666, "X", 308, -4, 14, 14, CustomNpcs.MODID + ":textures/gui/components.png", 0, 64);
      b.getTextureRect().setRepeatingTexture(64, 22, 3);
      b.setTextureHoverOffset(22).setHoverText("gui.close");
      b.setOnPress((guii, bb) -> guii.close());
      gui.addTextField(1, 4, 14, 140, 20).setText(availability.scoreboardObjective).setOnFocusLost((gui2, textfield) -> availability.scoreboardObjective = textfield.getText());
      gui.addButtonList(2, 148, 14, 90, 20).setValues("availability.smaller", "availability.equals", "availability.bigger").setSelected(availability.scoreboardType.ordinal()).setOnPress((gui2, bb) -> availability.scoreboardType = EnumAvailabilityScoreboard.values()[((IButtonList)bb).getSelected()]);
      gui.addTextField(3, 244, 14, 60, 20).setCharacterType(1).setInteger(availability.scoreboardValue).setOnFocusLost((gui2, textfield) -> availability.scoreboardValue = textfield.getInteger());
      gui.addTextField(11, 4, 37, 140, 20).setText(availability.scoreboard2Objective).setOnFocusLost((gui2, textfield) -> availability.scoreboard2Objective = textfield.getText());
      gui.addButtonList(12, 148, 37, 90, 20).setValues("availability.smaller", "availability.equals", "availability.bigger").setSelected(availability.scoreboard2Type.ordinal()).setOnPress((gui2, bb) -> availability.scoreboard2Type = EnumAvailabilityScoreboard.values()[((IButtonList)bb).getSelected()]);
      gui.addTextField(13, 244, 37, 60, 20).setCharacterType(1).setInteger(availability.scoreboard2Value).setOnFocusLost((gui2, textfield) -> availability.scoreboard2Value = textfield.getInteger());
      gui.addButton(16, "gui.done", 108, 110, 100, 20).setOnPress((gui2, bb) -> gui2.close());*/
      return gui;
   }

   // New from Unofficial (BetaZavr)
   // analog of GuiDialogSelection
   public static CustomGuiWrapper openDialogSelection(Availability availability, IPlayer<?> player) {
      CustomGuiWrapper gui = new CustomGuiWrapper(player);
      return gui;
   }

   // analog of SubGuiNpcAvailabilityFaction
   public static CustomGuiWrapper openFaction(Availability availability, IPlayer<?> player) {
      CustomGuiWrapper gui = new CustomGuiWrapper(player);
      return gui;
   }

   // analog of SubGuiNpcAvailabilityItemStacks
   public static CustomGuiWrapper openStack(Availability availability, IPlayer<?> player) {
      CustomGuiWrapper gui = new CustomGuiWrapper(player);
      return gui;
   }

   // analog of SubGuiNpcAvailabilityNames
   public static CustomGuiWrapper openNames(Availability availability, IPlayer<?> player) {
      CustomGuiWrapper gui = new CustomGuiWrapper(player);
      return gui;
   }

   // analog of SubGuiNpcAvailabilityStoredData
   public static CustomGuiWrapper openStoredData(Availability availability, IPlayer<?> player) {
      CustomGuiWrapper gui = new CustomGuiWrapper(player);
      return gui;
   }

   // analog of SubGuiNpcAvailabilityNames
   public static CustomGuiWrapper openRegions(Availability availability, IPlayer<?> player) {
      CustomGuiWrapper gui = new CustomGuiWrapper(player);
      return gui;
   }

}
