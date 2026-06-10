package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketPlayerMailRansom extends PacketServerBasic {

    protected static int channelId;
    private final long id;

    public SPacketPlayerMailRansom(long idIn) { id = idIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketPlayerMailRansom msg, FriendlyByteBuf buf) { buf.writeLong(msg.id); }

    public static SPacketPlayerMailRansom decode(FriendlyByteBuf buf) { return new SPacketPlayerMailRansom(buf.readLong()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData data = PlayerData.get(player);
        PlayerMail mail = data.mailData.get(id);
        if (mail != null && mail.ransom > 0) {
            if (!player.isCreative() && data.game.getMoney() < mail.ransom) { NoppesUtilServer.sendGuiError(player, 3); }
            else {
                data.game.addMoney(mail.ransom * -1L);
                mail.ransom = 0;
                Packets.send(player, new PacketSyncUpdate(0, 12, data.mailData.save(new CompoundTag())));
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}
