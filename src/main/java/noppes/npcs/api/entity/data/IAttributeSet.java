package noppes.npcs.api.entity.data;

import net.minecraft.world.entity.ai.attributes.Attribute;
import noppes.npcs.api.interfaces.ParamName;

@SuppressWarnings("all")
public interface IAttributeSet {

    String getAttribute();

    double getChance();

    double getMaxValue();

    double getMinValue();

    int getSlot();

    void remove();

    void setAttribute(@ParamName("attribute") Attribute attribute);

    void setAttribute(@ParamName("name") String name);

    void setChance(@ParamName("chance") double chance);

    void setSlot(@ParamName("slot") int slot);

    void setValues(@ParamName("min") double min, @ParamName("max") double max);

}
