package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketStopSound extends PacketBasic {

    protected static int channelId;
    private int category;
    private ResourceLocation sound;

    public PacketStopSound() { }

    public PacketStopSound(ResourceLocation soundIn, int categoryIn) {
        sound = soundIn;
        category = categoryIn;
        if (category < 0) { category *= -1; }
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        sound = buf.readResourceLocation();
        category = buf.readInt();
        if (category < 0) { category *= -1; }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(sound);
        buf.writeInt(category);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MusicController.Instance.stopSound(sound, SoundCategory.values()[category % SoundCategory.values().length]);
        CustomNpcs.debugData.end("Packets");
    }

}