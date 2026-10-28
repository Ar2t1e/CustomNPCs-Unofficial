package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.controllers.data.Deal;
import noppes.npcs.controllers.data.Marcet;
import noppes.npcs.controllers.data.MarcetSection;
import noppes.npcs.entity.data.DropSet;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SPacketDealDropSetSave extends PacketServerBasic {

    protected static int channelId;
    private int marcetId;
    private int dealId;
    private NBTTagCompound compound;

    public SPacketDealDropSetSave() { }

    public SPacketDealDropSetSave(int marcetIDIn, int dealIDIn, NBTTagCompound compoundIn) {
        marcetId = marcetIDIn;
        dealId = dealIDIn;
        compound = compoundIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_MARKETS); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(marcetId);
        buf.writeInt(dealId);
        buf.writeNbt(compound);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        marcetId = buf.readInt();
        dealId = buf.readInt();
        compound = buf.readNbt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MarcetController mData = MarcetController.getInstance();
        Deal dealIn = mData.deals.get(dealId);
        if (dealIn != null && dealIn.isCase()) {
            int pos = compound.getInteger("Slot");
            if (pos < 0) { dealIn.addCaseItem(ItemStack.EMPTY, 100.0d).load(compound); }
            else {
                DropSet[] drops = dealIn.getCaseItems();
                if (pos < drops.length) { drops[pos].load(compound); }
            }
            for (Marcet marcet : new ArrayList<>(mData.markets.values())) {
                boolean nasInMarcet = false;
                for (MarcetSection section : new ArrayList<>(marcet.sections.values())) {
                    for (Deal deal : new ArrayList<>(section.deals)) {
                        if (deal.getId() == compound.getInteger("DealID")) {
                            nasInMarcet = true;
                            deal.load(dealIn.saveData());
                        }
                    }
                }
                if (nasInMarcet) { marcet.updateNew(); }
            }
            mData.save();
        }
        CustomNpcs.debugData.end("Packets");
    }

}
