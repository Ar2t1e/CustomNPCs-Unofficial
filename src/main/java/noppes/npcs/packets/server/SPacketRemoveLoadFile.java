package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketRemoveLoadFile extends PacketServerBasic {

    protected static int channelId;
    private String name;

    public SPacketRemoveLoadFile() { }

    public SPacketRemoveLoadFile(String nameIn) { name = nameIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeUtf(name); }

    @Override
    public void decode(FriendlyByteBuf buf) { name = buf.readUtf(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData.get(player).clientScriptFiles.remove(name);
        CustomNpcs.debugData.end("Packets");
    }

}