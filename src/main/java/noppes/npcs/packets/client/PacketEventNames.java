package noppes.npcs.packets.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.ForgeEventHandler;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.util.Util;

import java.io.File;
import java.util.*;

public class PacketEventNames extends PacketBasic {

    protected static int channelId;
    private final byte type; // 0: client; 1: forge; 2: api
    private final Map<String, String> names;

    public PacketEventNames(Map<String, String> namesIn, byte typeIn) {
        type = typeIn;
        names = namesIn;
    }

    public static void encode(PacketEventNames msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.names.size());
        for (Map.Entry<String, String> entry : msg.names.entrySet()) {
            if (entry.getKey() == null) { buf.writeUtf(""); }
            else { buf.writeUtf(entry.getKey()); }
            buf.writeUtf(entry.getValue());
        }
        buf.writeByte(msg.type);
    }

    public static PacketEventNames decode(FriendlyByteBuf buf) {
        Map<String, String> map = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) { map.put(buf.readUtf(), buf.readUtf()); }
        return new PacketEventNames(map, buf.readByte());
    }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!names.isEmpty()) {
            List<String> list;
            if (type != (byte) 2) {
                for (Map.Entry<String, String> entry : names.entrySet()) {
                    try {
                        Class<?> clazz = Class.forName(entry.getKey());
                        if (type == (byte) 0) { ForgeEventHandler.clientEventNames.put(clazz, entry.getValue()); }
                        else { ForgeEventHandler.eventNames.put(clazz, entry.getValue()); }
                    }
                    catch (Exception ignored) {}
                }
                if (type == (byte) 0) { list = new ArrayList<>(ForgeEventHandler.clientEventNames.values()); }
                else { list = new ArrayList<>(ForgeEventHandler.eventNames.values()); }
            }
            else { list = new ArrayList<>(names.keySet()); }
            Collections.sort(list);
            String pre = "";
            StringBuilder text = new StringBuilder();
            for (String name : list) {
                if (pre.isEmpty()) { pre = "" + name.charAt(0); }
                else if (!pre.equals("" + name.charAt(0))) {
                    text.append(System.lineSeparator());
                    pre = "" + name.charAt(0);
                }
                text.append(name);
                text.append(System.lineSeparator());
            }
            File file = new File(CustomNpcs.Dir.getParentFile().getParentFile().getParentFile(), "all "+(type == (byte) 0 ? "client" : type == (byte) 1 ? "forge" : "api" )+ " event names.txt");
            Util.instance.saveFile(file, text.toString());
            player.sendSystemMessage(Component.literal("CustomNpcs").withStyle(ChatFormatting.DARK_GREEN)
                    .append(Component.literal(": Save event names to file: ").withStyle(ChatFormatting.GRAY))
                    .append(file.getAbsolutePath()));
        }
        CustomNpcs.debugData.end("Packets");
    }

}
