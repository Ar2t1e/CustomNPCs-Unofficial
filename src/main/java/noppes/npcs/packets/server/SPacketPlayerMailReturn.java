package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketPlayerMailReturn extends PacketServerBasic {

    protected static int channelId;
    private long id;

    public SPacketPlayerMailReturn() { }

    public SPacketPlayerMailReturn(long idIn) { id = idIn; }

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
        if (mail != null) {
            PlayerData plData = PlayerDataController.instance.getDataFromUsername(player.getServer(), mail.sender);
            if (plData == null) {
                if (!mail.sender.isEmpty()) { player.sendMessage(Component.translatable("mailbox.error.return.player", mail.sender)); }
            }
            else {
                mail.sender += Component.translatable("mailbox.returned").getFormattedText();
                mail.returned = true;
                mail.ransom = 0;
                mail.beenRead = false;
                plData.mailData.addMail(mail);
                plData.save(false);
            }
            data.mailData.playerMails.removeIf(m -> m.timeWhenReceived == id && m.sender.equals(mail.sender));
            NBTTagCompound compound = data.mailData.save(new NBTTagCompound());
            Packets.send(player, new PacketSyncUpdate(0, 12, compound));
            Packets.send(player, new PacketGuiData(compound));
        }
        CustomNpcs.debugData.end("Packets");
    }

}