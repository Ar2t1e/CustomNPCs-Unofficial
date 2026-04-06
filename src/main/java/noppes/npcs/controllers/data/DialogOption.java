package noppes.npcs.controllers.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.handler.data.IDialogOption;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.db.DatabaseColumn;

import java.util.ArrayList;
import java.util.List;

public class DialogOption implements IDialogOption {

   public static class OptionDialogID {

      public int dialogId;
      public Availability availability;

      public OptionDialogID(int id) {
         dialogId = id;
         availability = new Availability();
      }

      public OptionDialogID(CompoundTag compound) {
         dialogId = compound.getInt("DialogId");
         availability = new Availability();
         availability.load(compound);
      }

      public CompoundTag getNBT() {
         CompoundTag compound = new CompoundTag();
         compound.putInt("DialogId", dialogId);
         availability.save(compound);
         return compound;
      }

      public String toString() { return "OptionDialogID: " + dialogId + "; " + availability.toString(); }

   }

   @DatabaseColumn(name = "text", type = DatabaseColumn.Type.TEXT)
   public String title = "Talk";
   @DatabaseColumn(name = "color", type = DatabaseColumn.Type.SMALLINT)
   public int optionColor = 0xE0E0E0;
   @DatabaseColumn(name = "command", type = DatabaseColumn.Type.TEXT)
   public String command = "";
   @DatabaseColumn(name = "order", type = DatabaseColumn.Type.SMALLINT)
   public int slot = -1;

   /* OLD
   @DatabaseColumn(name = "id", type = DatabaseColumn.Type.INT)
   public int id = -1;
   @DatabaseColumn(name = "option", type = DatabaseColumn.Type.VARCHAR)
   public String option = "Talk";*/

   // New from Unofficial (BetaZavr)
   public int iconId = 0;
   public OptionType optionType = OptionType.DIALOG_OPTION;
   public final List<OptionDialogID> dialogs = new ArrayList<>();

   public void load(CompoundTag compound) {
      if (compound != null) {
         title = compound.getString("Title");
         optionColor = compound.getInt("DialogColor");
         command = compound.getString("DialogCommand");
         if (optionColor == 0) { optionColor = 0xE0E0E0; }

         // New from Unofficial (BetaZavr)
         optionType = OptionType.get(compound.getInt("OptionType"));
         iconId = compound.getInt("IconId");
         dialogs.clear();
         if (compound.contains("Dialog", 3)) { // OLD
            dialogs.add(new OptionDialogID(compound.getInt("Dialog")));
         } else if (compound.contains("Dialogs", 9)) {
            for (int i = 0; i < compound.getList("Dialogs", 10).size(); i++) {
               dialogs.add(new OptionDialogID(compound.getList("Dialogs", 10).getCompound(i)));
            }
         }
      }
   }

   public CompoundTag save() {
      CompoundTag compound = new CompoundTag();
      compound.putString("Title", title);
      compound.putInt("DialogColor", optionColor);
      compound.putString("DialogCommand", command);

      // New from Unofficial (BetaZavr)
      compound.putInt("OptionType", optionType.get());
      compound.putInt("IconId", iconId);
      ListTag list = new ListTag();
      for (OptionDialogID od : dialogs) { list.add(od.getNBT()); }
      compound.put("Dialogs", list);

      return compound;
   }

   public boolean hasDialogs() { return !dialogs.isEmpty() && optionType == OptionType.DIALOG_OPTION; }

   public Dialog getDialog(Player player) {
      if (!hasDialogs() || player == null) { return null; }
      DialogController dData = DialogController.instance;
      for (OptionDialogID od : dialogs) {
         if (!dData.hasDialog(od.dialogId)) { continue; }
         if (od.availability.isAvailable(player)) { return dData.get(od.dialogId); }
      }
      return null;
   }

   public boolean isAvailable(Player player) {
      if (optionType == OptionType.DISABLED) { return false; }
      if (optionType != OptionType.DIALOG_OPTION) { return true; }
      Dialog dialog = getDialog(player);
      return dialog != null && dialog.availability.isAvailable(player);
   }

   @Override
   public int getSlot() { return slot; }

   @Override
   public String getName() { return title; }

   @Override
   public int getType() { return optionType.get(); }

   // New from Unofficial (BetaZavr)
   public void replaceDialogIDs(int oldId, int newId) {
      List<OptionDialogID> newDialogs = new ArrayList<>();
      boolean added = false;
      for (OptionDialogID od : dialogs) {
         if (od.dialogId == oldId) {
            od.dialogId = newId;
            added = true;
         }
         newDialogs.add(od);
      }
      if (added) {
         dialogs.clear();
         dialogs.addAll(newDialogs);
      }
   }

   public void upPos(int dialogId) {
      List<OptionDialogID> newDialogs = new ArrayList<>();
      boolean added = false;
      OptionDialogID old = null;
      for (OptionDialogID od : dialogs) {
         if (od.dialogId == dialogId && old != null) {
            newDialogs.remove(old);
            newDialogs.add(od);
            newDialogs.add(old);
            added = true;
            continue;
         }
         old = od;
         newDialogs.add(od);
      }
      if (added) {
         dialogs.clear();
         dialogs.addAll(newDialogs);
      }
   }

   public void downPos(int dialogId) {
      List<OptionDialogID> newDialogs = new ArrayList<>();
      boolean added = false;
      OptionDialogID found = null;
      for (OptionDialogID od : dialogs) {
         if (od.dialogId == dialogId && found == null) {
            found = od;
            continue;
         }
         newDialogs.add(od);
         if (found != null && !added) {
            newDialogs.add(found);
            added = true;
         }
      }
      if (found != null && !added) {
         newDialogs.add(found);
         added = true;
      }
      if (added) {
         dialogs.clear();
         dialogs.addAll(newDialogs);
      }
   }

   public void addDialog(int dialogId) {
      OptionDialogID od = new OptionDialogID(dialogId);
      dialogs.add(od);
   }

   public DialogOption copy() {
      DialogOption newDO = new DialogOption();
      newDO.load(save());
      return newDO;
   }

}
