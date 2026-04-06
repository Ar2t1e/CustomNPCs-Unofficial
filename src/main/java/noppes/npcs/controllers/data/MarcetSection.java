package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.util.ValueUtil;

public class MarcetSection {

    public static MarcetSection create(CompoundTag compound) {
        MarcetSection ms = new MarcetSection(compound.getInt("ID"));
        ms.name = compound.getString("Name");
        ms.setIcon(compound.getInt("IconId"));
        ListTag list = compound.getList("Deals", 10);
        for (Tag nbt : list) {
            Deal deal = new Deal();
            deal.loadData((CompoundTag) nbt);
            ms.deals.add(deal);
        }
        return ms;
    }

    protected final int id;
    protected int iconId;
    public String name = "market.default.section";
    public List<Deal> deals = new ArrayList<>();

    public MarcetSection(int idIn) { id = idIn; }

    public void addDeal(int dealId) {
        if (hadDeal(dealId)) { return; }
        Deal deal = MarcetController.getInstance().getDeal(dealId);
        if (deal == null || !deal.isValid()) { return; }
        Deal marcetDeal = deal.copy();
        marcetDeal.updateNew();
        deals.add(marcetDeal);
    }

    public int getId() { return id; }

    public int getIcon() { return iconId; }

    public void setIcon(int id) { iconId = ValueUtil.correctInt(id, 0, 29); }

    public Component getName() { return Component.translatable(name); }

    private boolean hadDeal(int dealId) {
        for (Deal deal : deals) {
            if (deal.getId() == dealId) { return true; }
        }
        return false;
    }

    public void removeAllDeals() { deals.clear(); }

    public void removeDeal(int dealId) {
        for (Deal deal : deals) {
            if (deal.getId() == dealId) {
                deals.remove(deal);
                return;
            }
        }
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("ID", id);
        compound.putInt("IconId", iconId);
        compound.putString("Name", name);

        ListTag list = new ListTag();
        for (Deal deal : deals) { list.add(deal.saveData()); }
        compound.put("Deals", list);

        return compound;
    }

}
