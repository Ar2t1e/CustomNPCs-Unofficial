package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.HashMap;
import java.util.Map;

public class SPacketScriptText extends PacketServerBasic {

    protected static int channelId;
    private static final Map<Integer, String[]> data = new HashMap<>();
    public static Map<Integer, IScriptHandler> handlers = new HashMap<>();

    private int type;
    private int tab;
    private int id;
    private int maxIDs;
    private String part;

    public SPacketScriptText() { }

    public SPacketScriptText(int typeIn, int tabIn, int idIn, int maxIDsIn, String partIn) {
        type = typeIn;
        tab = tabIn;
        id = idIn;
        maxIDs = maxIDsIn;
        part = partIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.TOOL_SCRIPTER; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        buf.writeInt(tab);
        buf.writeInt(id);
        buf.writeInt(maxIDs);
        buf.writeUtf(part);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        type = buf.readInt();
        tab = buf.readInt();
        id = buf.readInt();
        maxIDs = buf.readInt();
        part = buf.readUtf();
    }

    @Override
    public int getChannelId() { return channelId; }

    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (handlers.containsKey(type)) {
            if (type == 6 && !CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.EDIT_CLIENT_SCRIPT)) {
                warn(CustomNpcsPermissions.EDIT_CLIENT_SCRIPT);
            } else {
                IScriptHandler handler = handlers.get(type);
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
                    ScriptContainer container = handler.getScripts().get(tab);
                    if (container != null) {
                        container.script = total.toString();
                        handler.init();
                    }
                    data.remove(tab);
                }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}