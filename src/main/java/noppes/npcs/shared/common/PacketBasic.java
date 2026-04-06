package noppes.npcs.shared.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class PacketBasic implements IMessage {

    public MessageContext ctx;
    public EntityPlayer player;
    public EntityNPCInterface npc;

    @SideOnly(Side.CLIENT)
    public void handleClient() {
        player = Minecraft.getMinecraft().player;
        npc = NoppesUtilServer.getEditingNpc(player);
        handle();
    }

    @Override
    public void fromBytes(ByteBuf buf) { decode(new FriendlyByteBuf(buf)); }

    @Override
    public void toBytes(ByteBuf buf) { encode(new FriendlyByteBuf(buf)); }

    public abstract int getChannelId();

    protected abstract void handle();

    public abstract void decode(FriendlyByteBuf buf);

    public abstract void encode(FriendlyByteBuf buf);

}