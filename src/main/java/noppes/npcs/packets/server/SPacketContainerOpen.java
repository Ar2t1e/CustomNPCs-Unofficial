package noppes.npcs.packets.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;
import java.util.function.Consumer;

public class SPacketContainerOpen extends PacketServerBasic {

    protected static int channelId;
    private final EnumGuiType gui;
    private final FriendlyByteBuf buffer;

    public SPacketContainerOpen(EnumGuiType guiIn, Consumer<FriendlyByteBuf> consumer) {
        gui = guiIn;
        buffer = new FriendlyByteBuf(Unpooled.buffer());
        consumer.accept(buffer);
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketContainerOpen msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.gui);
        buf.writeBytes(msg.buffer.nioBuffer());
    }

    public static SPacketContainerOpen decode(FriendlyByteBuf buf) {
        EnumGuiType gui = buf.readEnum(EnumGuiType.class);
        ByteBuf data = buf.readBytes(buf.readableBytes());
        return new SPacketContainerOpen(gui, (b) -> b.writeBytes(data.nioBuffer()));
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        NoppesUtilServer.openContainerGui(player, gui, b -> b.writeBytes(buffer.nioBuffer()));
        CustomNpcs.debugData.end("Packets");
    }

}
