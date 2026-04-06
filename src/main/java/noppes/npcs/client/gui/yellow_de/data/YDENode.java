package noppes.npcs.client.gui.yellow_de.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class YDENode {

    public EnumYDEType type = EnumYDEType.DIALOG;
    public String category = "";
    public int id = -1;
    public int x = 0;
    public int y = 0;
    public int width = 180;
    public int height = 120;
    public boolean isLock = false;
    public Component title = Component.empty();
    public final List<YDELink> links = new ArrayList<>();

    public void load(CompoundTag compound) {
        type = EnumYDEType.values()[ValueUtil.onlyPositiveInt(compound.getInt("Type"), EnumYDEType.values().length)];
        category = compound.getString("Category");
        id = compound.getInt("Id");
        x = compound.getInt("X");
        y = compound.getInt("Y");
        width = compound.getInt("Width");
        height = compound.getInt("Height");
        isLock = compound.getBoolean("IsLock");
        title = Component.Serializer.fromJson(compound.getString("Title"));
        if (title == null) { title = Component.translatable(compound.getString("Title")); }
        links.clear();
        ListTag listLinks = compound.getList("Links", 10);
        for (int i = 0; i < listLinks.size(); i++) {
            YDELink link = new YDELink(0, 0, EnumYDEType.DIALOG);
            link.load(listLinks.getCompound(i));
            links.add(link);
        }
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("Type", type.ordinal());
        compound.putInt("Id", id);
        compound.putInt("X", x);
        compound.putInt("Y", y);
        compound.putInt("Width", width);
        compound.putInt("Height", height);
        compound.putBoolean("IsLock", isLock);
        compound.putString("Category", category);
        compound.putString("Title", Component.Serializer.toJson(title));
        ListTag listLinks = new ListTag();
        for (YDELink link : links) { listLinks.add(link.save());}
        compound.put("Links", listLinks);
        return compound;
    }

}
