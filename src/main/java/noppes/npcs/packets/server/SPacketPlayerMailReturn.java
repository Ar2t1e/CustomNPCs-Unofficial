package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.Util;

public class SPacketPlayerMailReturn extends PacketServerBasic {

    protected static int channelId;
    private final long id;

    public SPacketPlayerMailReturn(long idIn) { id = idIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketPlayerMailReturn msg, FriendlyByteBuf buf) { buf.writeLong(msg.id); }

    public static SPacketPlayerMailReturn decode(FriendlyByteBuf buf) { return new SPacketPlayerMailReturn(buf.readLong()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData data = PlayerData.get(player);
        PlayerMail mail = data.mailData.get(id);
        if (mail != null) {
            PlayerData plData = PlayerDataController.instance.getDataFromUsername(player.getServer(), mail.sender);
            if (plData == null) {
                if (!mail.sender.isEmpty()) { player.sendSystemMessage(Component.translatable("mailbox.error.return.player", mail.sender)); }
            }
            else {
                mail.sender += Util.instance.getOldFormattedText(Component.translatable("mailbox.returned"));
                mail.returned = true;
                mail.ransom = 0;
                mail.beenRead = false;
                plData.mailData.addMail(mail);
                plData.save(false);
            }
            data.mailData.playerMails.removeIf(m -> m.timeWhenReceived == id && m.sender.equals(mail.sender));
            CompoundTag compound = data.mailData.save(new CompoundTag());
            Packets.send(player, new PacketSyncUpdate(0, 12, compound));
            Packets.send(player, new PacketGuiData(compound));
        }
        CustomNpcs.debugData.end("Packets");
    }

}