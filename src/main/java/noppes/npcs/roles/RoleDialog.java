package noppes.npcs.roles;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.OptionType;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.data.role.IRoleDialog;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;

public class RoleDialog extends RoleInterface implements IRoleDialog {

   public String dialog = "";
   public int questId = -1;
   public HashMap<Integer, String> options = new HashMap<>();
   public HashMap<Integer, String> optionsTexts = new HashMap<>();

   public RoleDialog(EntityNPCInterface npc) {
      super(npc);
      type = RoleType.DIALOG;
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      compound.putInt("RoleQuestId", questId);
      compound.putString("RoleDialog", dialog);
      compound.put("RoleOptions", NBTTags.nbtIntegerStringMap(options));
      compound.put("RoleOptionTexts", NBTTags.nbtIntegerStringMap(optionsTexts));
      return compound;
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = RoleType.DIALOG;
      questId = compound.getInt("RoleQuestId");
      dialog = compound.getString("RoleDialog");
      options = NBTTags.getIntegerStringMap(compound.getList("RoleOptions", 10));
      optionsTexts = NBTTags.getIntegerStringMap(compound.getList("RoleOptionTexts", 10));
   }

   @Override
   public void interact(Player player) {
      if (dialog.isEmpty()) { npc.say(player, npc.advanced.getInteractLine()); }
      else {
         Dialog d = new Dialog(null);
         d.text = dialog;
         for (Map.Entry<Integer, String> entry : options.entrySet()) {
            if (!entry.getValue().isEmpty()) {
               DialogOption option = new DialogOption();
               String text = optionsTexts.get(entry.getKey());
               if (text != null && !text.isEmpty()) { option.optionType = OptionType.ROLE_OPTION; }
               else { option.optionType = OptionType.QUIT_OPTION; }
               option.title = entry.getValue();
               d.options.put(entry.getKey(), option);
            }
         }
         NoppesUtilServer.openDialog(player, npc, d);
      }
      Quest quest = QuestController.instance.quests.get(questId);
      if (quest != null) { PlayerQuestController.addActiveQuest(quest, player, false); }
   }

   @Override
   public String getDialog() { return dialog; }

   @Override  public String getOption(int option) { return options.get(option); }

   @Override
   public String getOptionDialog(int option) { return optionsTexts.get(option); }

   @Override
   public void setDialog(String text) { dialog = text == null ? "" : text; }

   @Override
   public void setOption(int option, String text) {
      if (option < 1 || option > 6) {
         throw new CustomNPCsException("Wrong dialog option slot given (1<>6): " + option);
      }
      options.put(option, text);
   }

   @Override
   public void setOptionDialog(int option, String text) {
      if (option < 1 || option > 6) {
         throw new CustomNPCsException("Wrong dialog option slot given (1<>6): " + option);
      }
      optionsTexts.put(option, text);
   }

}
