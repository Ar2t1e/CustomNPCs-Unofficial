package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketBankOpenPlayer extends PacketServerBasic {

    protected static int channelId;
    private final int bankId;
    private final String playerName;

    public SPacketBankOpenPlayer(int bankIdIn, String playerNameIn) {
        bankId = bankIdIn;
        playerName = playerNameIn;
    }

    public static void encode(SPacketBankOpenPlayer msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.bankId);
        buf.writeUtf(msg.playerName);
    }

    public static SPacketBankOpenPlayer decode(FriendlyByteBuf buf) { return new SPacketBankOpenPlayer(buf.readInt(), buf.readUtf()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ContainerNPCBank.editPlayerBankData = playerName;
        PlayerData data = PlayerDataController.instance.getDataFromUsername(CustomNpcs.Server, playerName);
        if (data != null && data.bankData.hasBank(bankId)) {
            data.bankData.get(bankId).openToPlayer(player, 0, 0, 0, 1);
        }
        CustomNpcs.debugData.end("Packets");
    }

}