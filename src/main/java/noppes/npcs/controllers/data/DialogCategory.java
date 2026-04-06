package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IDialogCategory;

public class DialogCategory implements IDialogCategory {

   public final Map<Integer, Dialog> dialogs = new TreeMap<>();
   public int id = -1;
   public String title = "";

   public void load(CompoundTag compound) {
      id = compound.getInt("Slot");
      title = compound.getString("Title");
      ListTag dialogsList = compound.getList("Dialogs", 10);
      for(int i = 0; i < dialogsList.size(); ++i) {
         Dialog dialog = new Dialog(this);
         CompoundTag comp = dialogsList.getCompound(i);
         dialog.load(comp);
         dialog.id = comp.getInt("DialogId");
         dialogs.put(dialog.id, dialog);
      }
   }

   public CompoundTag save(CompoundTag compound) {
      compound.putInt("Slot", id);
      compound.putString("Title", title);
      ListTag list = new ListTag();
      for (Dialog dialog : dialogs.values()) { list.add(dialog.save(new CompoundTag())); }
      compound.put("Dialogs", list);
      return compound;
   }

   @Override
   public List<IDialog> dialogs() { return new ArrayList<>(dialogs.values()); }

   @Override
   public String getName() { return title; }

   @Override
   public IDialog create() { return new Dialog(this); }

   // New from Unofficial (BetaZavr)
   public DialogCategory copy() {
      DialogCategory newCat = new DialogCategory();
      newCat.load(save(new CompoundTag()));
      return newCat;
   }

}
