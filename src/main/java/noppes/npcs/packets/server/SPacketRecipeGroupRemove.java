package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketRecipeGroupRemove extends PacketServerBasic {

    protected static int channelId;
    private boolean isGlobal;
    private String group;

    public SPacketRecipeGroupRemove() { }

    public SPacketRecipeGroupRemove(boolean isGlobalIn, String groupIn) {
        isGlobal = isGlobalIn;
        group = groupIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_RECIPE); }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isGlobal);
        buf.writeUtf(group);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        isGlobal = buf.readBoolean();
        group = buf.readUtf();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        RecipeController.getInstance().deleteGroup(isGlobal, group);
        Packets.sendDelayed(player, new PacketGuiUpdate(), 100);
        CustomNpcs.debugData.end("Packets");
    }

}