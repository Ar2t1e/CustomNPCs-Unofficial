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
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SPacketDealSave extends PacketServerBasic {

    protected static int channelId;
    private NBTTagCompound data;

    public SPacketDealSave() { }

    public SPacketDealSave(NBTTagCompound dataIn) { data = dataIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_MARKETS); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

    @Override
    public void decode(FriendlyByteBuf buf) { data = buf.readAnySizeNbt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MarcetController mData = MarcetController.getInstance();
        if (data.hasKey("DealID", 3)) {
            mData.loadDeal(data);
            for (Marcet marcet : new ArrayList<>(mData.markets.values())) {
                boolean nasInMarcet = false;
                for (MarcetSection section : new ArrayList<>(marcet.sections.values())) {
                    for (Deal deal : new ArrayList<>(section.deals)) {
                        if (deal.getId() == data.getInteger("DealID")) {
                            nasInMarcet = true;
                            deal.load(data);
                        }
                    }
                }
                if (nasInMarcet) { marcet.updateNew(); }
            }
            mData.save();
            mData.sendToAll();
        }
        CustomNpcs.debugData.end("Packets");
    }

}