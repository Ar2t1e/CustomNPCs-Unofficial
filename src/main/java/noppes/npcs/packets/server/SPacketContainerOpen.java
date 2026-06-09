package noppes.npcs.packets.server;

import io.netty.buffer.Unpooled;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.function.Consumer;

public class SPacketContainerOpen extends PacketServerBasic {

    protected static int channelId;
    private EnumGuiType gui;
    private FriendlyByteBuf buffer;

    public SPacketContainerOpen() { }

    public SPacketContainerOpen(EnumGuiType guiIn, Consumer<FriendlyByteBuf> consumer) {
        gui = guiIn;
        buffer = new FriendlyByteBuf(Unpooled.buffer());
        consumer.accept(buffer);
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(gui);
        buf.writeBytes(buffer.nioBuffer());
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        gui = buf.readEnum(EnumGuiType.class);
        buffer = new FriendlyByteBuf(buf.readBytes(buf.readableBytes()));
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