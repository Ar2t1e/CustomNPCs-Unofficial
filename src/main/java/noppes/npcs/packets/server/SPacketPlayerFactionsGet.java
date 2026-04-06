package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerFactionData;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketPlayerFactionsGet extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketPlayerFactionsGet ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static SPacketPlayerFactionsGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketPlayerFactionsGet(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerFactionData data = CustomNpcs.proxy.getPlayerData(player).factionData;
        Packets.send(player, new PacketGuiData(data.getPlayerGuiData(player)));
        CustomNpcs.debugData.end("Packets");
    }

}