package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketUpdateMarcetGui extends PacketBasic {

    protected static int channelId;

    public static void encode(PacketUpdateMarcetGui ignoredMsg, FriendlyByteBuf ignoredBuf) {}

    public static PacketUpdateMarcetGui decode(FriendlyByteBuf ignoredBuf) { return new PacketUpdateMarcetGui(); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (Minecraft.getInstance().screen instanceof IGuiData gui) { gui.setGuiData(new CompoundTag()); }
        CustomNpcs.debugData.end("Packets");
    }

}