package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketMarcetRemove extends PacketBasic {

    protected static int channelId;
    private final int marcetID;

    public PacketMarcetRemove(int marcetIDIn) { marcetID = marcetIDIn; }

    public static void encode(PacketMarcetRemove msg, FriendlyByteBuf buf) { buf.writeInt(msg.marcetID); }

    public static PacketMarcetRemove decode(FriendlyByteBuf buf) { return new PacketMarcetRemove(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MarcetController.getInstance().removeMarcet(marcetID);
        if (Minecraft.getInstance().screen instanceof IGuiData gui) { gui.setGuiData(new CompoundTag()); }
        CustomNpcs.debugData.end("Packets");
    }

}
