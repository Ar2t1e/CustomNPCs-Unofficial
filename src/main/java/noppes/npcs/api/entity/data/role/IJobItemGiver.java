package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;

@SuppressWarnings("unused")
public interface IJobItemGiver {

    IItemStack[] getItemStacks();

    void setItemStacks(@ParamName("stacks") IItemStack[] stacks);

    String[] getLines();

    void setLines(@ParamName("lines") String[] linesIn);

    int getCooldownType();

    void setCooldownType(@ParamName("type") int type);

    int getGivingType();

    void setGivingType(@ParamName("type") int type);

}
