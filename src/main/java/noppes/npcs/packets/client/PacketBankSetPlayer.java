package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.shared.common.PacketBasic;

import javax.annotation.Nonnull;

public class PacketBankSetPlayer extends PacketBasic {

    protected static int channelId;
    private final String name;

    public PacketBankSetPlayer(@Nonnull String nameIn) { name = nameIn; }

    public static void encode(PacketBankSetPlayer msg, FriendlyByteBuf buf) { buf.writeUtf(msg.name); }

    public static PacketBankSetPlayer decode(FriendlyByteBuf buf) { return new PacketBankSetPlayer(buf.readUtf()); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        ContainerNPCBank.editPlayerBankData = name.isEmpty() ? null : name;
        CustomNpcs.debugData.end("Packets");
    }

}
