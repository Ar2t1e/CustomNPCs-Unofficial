package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketStopSound extends PacketBasic {

    protected static int channelId;
    private final int category;
    private final ResourceLocation sound;

    public PacketStopSound(ResourceLocation soundIn, int categoryIn) {
        sound = soundIn;
        if (categoryIn < 0) { categoryIn *= -1; }
        category = categoryIn;
    }

    public static void encode(PacketStopSound msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.sound);
        buf.writeInt(msg.category);
    }

    public static PacketStopSound decode(FriendlyByteBuf buf) { return new PacketStopSound(buf.readResourceLocation(), buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MusicController.Instance.stopSound(sound, SoundSource.values()[category % SoundSource.values().length]);
        CustomNpcs.debugData.end("Packets");
    }

}
