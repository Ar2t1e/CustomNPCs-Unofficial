package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketPlayerMailTakeMoney extends PacketServerBasic {

    protected static int channelId;
    private final long id;

    public SPacketPlayerMailTakeMoney(long idIn) { id = idIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketPlayerMailTakeMoney msg, FriendlyByteBuf buf) { buf.writeLong(msg.id); }

    public static SPacketPlayerMailTakeMoney decode(FriendlyByteBuf buf) { return new SPacketPlayerMailTakeMoney(buf.readLong()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData data = PlayerData.get(player);
        PlayerMail mail = data.mailData.get(id);
        CompoundTag compound = new CompoundTag();
        if (mail != null) {
            if (mail.money > 0) {
                data.game.addMoney(mail.money);
                mail.money = 0;
                mail.returned = true;
                compound = mail.save();
                Packets.send(player, new PacketSyncUpdate(0, 12, data.mailData.save(new CompoundTag())));
            }
        }
        Packets.send(player, new PacketGuiData(compound));
        CustomNpcs.debugData.end("Packets");
    }

}