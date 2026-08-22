package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketSpeak extends PacketBasic {

    protected static int channelId;
    private static final int maxChunkSizeInBytes = 32767;

    public String languageKey;
    public String text;
    public float volume;

    public PacketSpeak() { }

    public PacketSpeak(String language, String textIn, float volumeIn) {
        languageKey = language;
        volume = volumeIn;
        text = textIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        StringBuilder textIn = new StringBuilder();
        int textLength = buf.readInt();
        for (int i = 0; i < textLength; i++) { textIn.append(buf.readUtf(maxChunkSizeInBytes)); }
        languageKey = buf.readUtf(maxChunkSizeInBytes);
        text = textIn.toString();
        volume = buf.readFloat();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        List<String> result = new ArrayList<>();
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
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
        buf.writeUtf(languageKey);
        buf.writeFloat(volume);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
