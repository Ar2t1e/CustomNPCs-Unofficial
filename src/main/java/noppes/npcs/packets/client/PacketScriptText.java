package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.script.GuiScriptInterface;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.common.PacketBasic;

import java.util.HashMap;
import java.util.Map;

public class PacketScriptText extends PacketBasic {

    protected static int channelId;
    private static final Map<Integer, String[]> data = new HashMap<>();

    private int tab;
    private int id;
    private int maxIDs;
    private String part;
    private boolean isSetClient;

    public PacketScriptText() { }

    public PacketScriptText(int tabIn, int idIn, int maxIDsIn, String partIn, boolean isSetClientIn) {
        tab = tabIn;
        id = idIn;
        maxIDs = maxIDsIn;
        part = partIn;
        isSetClient = isSetClientIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        tab = buf.readInt();
        id = buf.readInt();
        maxIDs = buf.readInt();
        part = buf.readUtf();
        isSetClient = buf.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(tab);
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
        if (!data.containsKey(tab)) { data.put(tab, new String[maxIDs]); }
        data.get(tab)[id] = part;
        boolean done = true;
        StringBuilder total = new StringBuilder();
        for (String str : data.get(tab)) {
            if (str == null) {
                done = false;
                break;
            }
            total.append(str);
        }
        if (done) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiScriptInterface) {
                ((GuiScriptInterface) Minecraft.getMinecraft().currentScreen).setTabScript(tab, total.toString());
            }
            if (isSetClient) {
                ScriptContainer container = ScriptController.Instance.clientScripts.getScripts().get(tab);
                if (container != null) {
                    container.script = total.toString();
                    container.setInit(false);
                    ScriptController.Instance.clientScripts.init();
                }
            }
            data.remove(tab);
        }
        CustomNpcs.debugData.end("Packets");
    }

}
