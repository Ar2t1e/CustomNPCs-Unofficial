package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.shared.common.PacketBasic;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketSpeak extends PacketBasic {

    private static final int maxChunkSizeInBytes = 32767;
    protected static int channelId;

    private final String languageKey;
    private final String text;
    private final float volume;

    public PacketSpeak(String language, String textIn, float volumeIn) {
        languageKey = language;
        volume = volumeIn;
        text = textIn;
    }

    public static void encode(PacketSpeak msg, FriendlyByteBuf buf) {
        List<String> result = new ArrayList<>();
        byte[] bytes = msg.text.getBytes(StandardCharsets.UTF_8);
        int startIndex = 0;
        while (startIndex < bytes.length) {
            int endIndex = Math.min(startIndex + maxChunkSizeInBytes, bytes.length);
            byte[] chunkBytes = Arrays.copyOfRange(bytes, startIndex, endIndex);
            String chunk = new String(chunkBytes, StandardCharsets.UTF_8);
            result.add(chunk);
            startIndex += maxChunkSizeInBytes;
        }
        buf.writeInt(result.size());
        for (String str : result) { buf.writeUtf(str); }
        buf.writeUtf(msg.languageKey);
        buf.writeFloat(msg.volume);
    }

    public static PacketSpeak decode(FriendlyByteBuf buf) {
        StringBuilder textIn = new StringBuilder();
        int textLength = buf.readInt();
        for (int i = 0; i < textLength; i++) { textIn.append(buf.readUtf(maxChunkSizeInBytes)); }
        return new PacketSpeak(buf.readUtf(maxChunkSizeInBytes), textIn.toString(), buf.readFloat());
    }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        MusicController.Instance.speak(languageKey, text, volume);
        CustomNpcs.debugData.end("Packets");
    }

}
