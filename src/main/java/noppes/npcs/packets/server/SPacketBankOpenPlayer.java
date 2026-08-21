package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketBankOpenPlayer extends PacketServerBasic {

    protected static int channelId;
    private int bankId;
    private String playerName;

    public SPacketBankOpenPlayer() { }

    public SPacketBankOpenPlayer(int bankIdIn, String playerNameIn) {
        bankId = bankIdIn;
        playerName = playerNameIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(bankId);
        buf.writeUtf(playerName);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        bankId = buf.readInt();
        playerName = buf.readUtf();
    }

    @Override
    public int getChannelId() { return channelId; }

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