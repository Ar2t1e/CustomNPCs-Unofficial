package noppes.npcs.api.entity;

import noppes.npcs.api.ITimers;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.data.INPCAdvanced;
import noppes.npcs.api.entity.data.INPCAi;
import noppes.npcs.api.entity.data.INPCDisplay;
import noppes.npcs.api.entity.data.INPCInventory;
import noppes.npcs.api.entity.data.INPCJob;
import noppes.npcs.api.entity.data.INPCRole;
import noppes.npcs.api.entity.data.INPCStats;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.handler.data.IFaction;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.entity.EntityNPCInterface;

@SuppressWarnings("unused")
public interface ICustomNpc<T extends EntityNPCInterface> extends IMob<T> {

   INPCDisplay getDisplay();

   INPCInventory getInventory();

   INPCStats getStats();

   INPCAi getAi();

   INPCAdvanced getAdvanced();

   IFaction getFaction();

   void setFaction(@ParamName("id") int id);

   INPCRole getRole();

   INPCJob getJob();

   ITimers getTimers();

   int getHomeX();

   int getHomeY();

   int getHomeZ();

   IEntityLiving<?> getOwner();

   void setHome(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z);

   void reset();

   void say(@ParamName("message") String message);

   void sayTo(@ParamName("player") IPlayer<?> player, @ParamName("message") String message);

   IProjectile<?> shootItem(@ParamName("target") IEntityLiving<?> target, @ParamName("item") IItemStack item, @ParamName("accuracy") int accuracy);

   IProjectile<?> shootItem(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z,
                            @ParamName("item") IItemStack item, @ParamName("accuracy") int accuracy);

   void giveItem(@ParamName("player") IPlayer<?> player, @ParamName("item") IItemStack item);

   void setDialog(@ParamName("slot") int slot, @ParamName("dialog") IDialog dialog);

   IDialog getDialog(@ParamName("slot") int slot);

   void updateClient();

   String executeCommand(@ParamName("command") String command);

   void trigger(@ParamName("id") int id, @ParamName("arguments") Object... arguments);

}
