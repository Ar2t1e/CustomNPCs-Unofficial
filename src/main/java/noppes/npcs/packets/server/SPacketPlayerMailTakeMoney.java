package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketPlayerMailTakeMoney extends PacketServerBasic {

    protected static int channelId;
    private long id;

    public SPacketPlayerMailTakeMoney() { }

    public SPacketPlayerMailTakeMoney(long idIn) { id = idIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeLong(id); }

    @Override
    public void decode(FriendlyByteBuf buf) { id = buf.readLong(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData data = PlayerData.get(player);
        PlayerMail mail = data.mailData.get(id);
        if (mail != null && mail.money > 0) {
            data.game.addMoney(mail.money);
            mail.money = 0;
            Packets.send(player, new PacketSyncUpdate(0, 12, data.mailData.save(new NBTTagCompound())));
        }
        CustomNpcs.debugData.end("Packets");
    }

}