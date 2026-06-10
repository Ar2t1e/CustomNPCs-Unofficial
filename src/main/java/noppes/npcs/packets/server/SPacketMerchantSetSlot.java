package noppes.npcs.packets.server;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.village.MerchantRecipeList;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.containers.ContainerMerchantAdd;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.shared.common.util.LogWriter;

import java.util.Collections;
import java.util.List;

public class SPacketMerchantSetSlot extends PacketServerBasic {

    protected static int channelId;
    private MerchantRecipeList list;

    public SPacketMerchantSetSlot() { }

    public SPacketMerchantSetSlot(MerchantRecipeList listIn) { list = listIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.EDIT_VILLAGER); }

    @Override
    public void encode(FriendlyByteBuf buf) { list.writeToBuf(new PacketBuffer(buf)); }

    @Override
    public void decode(FriendlyByteBuf buf) {
        try { list = MerchantRecipeList.readFromBuf(new PacketBuffer(buf)); } catch (Exception e) { LogWriter.error(e); }
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (list != null && player.openContainer instanceof ContainerMerchantAdd) {
            EntityVillager trader = ((ContainerMerchantAdd) player.openContainer).trader;
            if (trader != null) { trader.setRecipes(list); }
        }
        CustomNpcs.debugData.end("Packets");
    }

}