package noppes.npcs.packets.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketMailsSave extends PacketServerBasic {

    protected static int channelId;
    private NBTTagCompound compound;

    public SPacketMailsSave() { }

    public SPacketMailsSave(NBTTagCompound compoundIn) { compound = compoundIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_MAIL); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(compound); }

    @Override
    public void decode(FriendlyByteBuf buf) { compound = buf.readAnySizeNbt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        CustomNpcs.MailTimeWhenLettersWillBeDeleted = compound.getInteger("LettersBeDeleted");
        int[] vs = compound.getIntArray("LettersBeReceived");
        System.arraycopy(vs, 0, CustomNpcs.MailTimeWhenLettersWillBeReceived, 0, vs.length);
        vs = compound.getIntArray("CostSendingLetter");
        System.arraycopy(vs, 0, CustomNpcs.MailCostSendingLetter, 0, vs.length);
        CustomNpcs.MailSendToYourself = compound.getBoolean("SendToYourself");
        compound.setInteger("LettersBeDeleted", CustomNpcs.MailTimeWhenLettersWillBeDeleted);
        compound.setIntArray("LettersBeReceived", CustomNpcs.MailTimeWhenLettersWillBeReceived);
        compound.setIntArray("CostSendingLetter", CustomNpcs.MailCostSendingLetter);
        compound.setBoolean("SendToYourself", CustomNpcs.MailSendToYourself);
        MinecraftServer server = player.world.getMinecraftServer();
        if (server != null && server.getPlayerList().getPlayers().size() > 1) {
            for (EntityPlayerMP pl : server.getPlayerList().getPlayers()) {
                Packets.send(pl, new PacketSyncUpdate(0, 12, compound));
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}