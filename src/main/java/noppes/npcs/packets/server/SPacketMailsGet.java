package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketMailsGet extends PacketServerBasic {

    protected static int channelId;

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_MAIL); }

    public static void encode(SPacketMailsGet ignoredMsg, FriendlyByteBuf ignoredBuf) { }

    public static SPacketMailsGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketMailsGet(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        CompoundTag compound = new CompoundTag();
        compound.putInt("LettersBeDeleted", CustomNpcs.MailTimeWhenLettersWillBeDeleted);
        compound.putIntArray("LettersBeReceived", CustomNpcs.MailTimeWhenLettersWillBeReceived);
        compound.putIntArray("CostSendingLetter", CustomNpcs.MailCostSendingLetter);
        compound.putBoolean("SendToYourself", CustomNpcs.MailSendToYourself);
        Packets.send(player, new PacketGuiData(compound));
        CustomNpcs.debugData.end("Packets");
    }

}
