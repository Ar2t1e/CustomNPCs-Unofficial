package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.script.GuiScriptInterface;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.common.PacketBasic;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PacketScriptConsole extends PacketBasic {

    protected static int channelId;
    private static final Map<Integer, Map<Long, String[]>> data = new HashMap<>();

    private int tab;
    private long time;
    private int id;
    private int maxIDs;
    private String part;
    private boolean isSetClient;

    public PacketScriptConsole() { }

    public PacketScriptConsole(int tabIn, long timeIn, int idIn, int maxIDsIn, String partIn, boolean isSetClientIn) {
        tab = tabIn;
        time = timeIn;
        id = idIn;
        maxIDs = maxIDsIn;
        part = partIn;
        isSetClient = isSetClientIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        tab = buf.readInt();
        time = buf.readLong();
        id = buf.readInt();
        maxIDs = buf.readInt();
        part = buf.readUtf();
        isSetClient = buf.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(tab);
        buf.writeLong(time);
        buf.writeInt(id);
        buf.writeInt(maxIDs);
        buf.writeUtf(part);
        buf.writeBoolean(isSetClient);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (!data.containsKey(tab)) { data.put(tab, new LinkedHashMap<>()); }
        if (!data.get(tab).containsKey(time)) { data.get(tab).put(time, new String[maxIDs]); }
        data.get(tab).get(time)[id] = part;
        boolean done = true;
        StringBuilder total = new StringBuilder();
        for (String str : data.get(tab).get(time)) {
            if (str == null) {
                done = false;
                break;
            }
            total.append(str);
        }
        if (done) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiScriptInterface) {
                ((GuiScriptInterface) Minecraft.getMinecraft().currentScreen).setTabConsole(tab, time, total.toString());
            }
            if (isSetClient) {
                ScriptContainer container = ScriptController.Instance.clientScripts.getScripts().get(tab);
                if (container != null) {
                    container.script = total.toString();
                    container.setInit(false);
                    ScriptController.Instance.clientScripts.init();
                }
            }
            data.get(tab).remove(time);
            if (data.get(tab).isEmpty()) { data.remove(tab); }
        }
        CustomNpcs.debugData.end("Packets");
    }

}