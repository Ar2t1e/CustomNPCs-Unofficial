package noppes.npcs.api.handler.data;

import net.minecraft.network.chat.Component;
import noppes.npcs.api.IPos;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;

public interface IQuestObjective {

   int getProgress();

   void setProgress(@ParamName("progress") int progress);

   int getMaxProgress();

   boolean isCompleted();

   String getText();

   Component getMCText();

   // New from Unofficial (BetaZavr)
   int getAreaRange();

   String getCompassDimension();

   IPos getCompassPos();

   int getCompassRange();

   int getCompassColor();

   IItemStack getItem();

   String getOrientationEntityName();

   int getTargetID();

   String getTargetName();

   int getType();

   boolean isAndTitle();

   boolean isIgnoreDamage();

   boolean isItemIgnoreNBT();

   boolean isItemLeave();

   boolean isNotShowLogEntity();

   boolean isPartName();

   boolean isSetPointOnMiniMap();

   void setAndTitle(@ParamName("andTitle") boolean andTitle);

   void setAreaRange(@ParamName("range") int range);

   void setCompassDimension(@ParamName("dimensionId") String dimensionId);

   void setCompassPos(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z);

   void setCompassPos(@ParamName("pos") IPos pos);

   void setCompassRange(@ParamName("range") int range);

    void setCompassColor(@ParamName("color") int color);

    void setItem(@ParamName("item") IItemStack item);

   void setItemIgnoreDamage(@ParamName("bo") boolean bo);

   void setItemIgnoreNBT(@ParamName("bo") boolean bo);

   void setItemLeave(@ParamName("bo") boolean bo);

   void setMaxProgress(@ParamName("value") int value);

   void setNotShowLogEntity(@ParamName("notShowLogEntity") boolean notShowLogEntity);

   void setOrientationEntityName(@ParamName("name") String name);

   void setPartName(@ParamName("isPart") boolean isPart);

   void setPointOnMiniMap(@ParamName("bo") boolean bo);

   void setTargetID(@ParamName("id") int id);

   void setTargetName(@ParamName("name") String name);

   void setType(@ParamName("type") int type);

}
