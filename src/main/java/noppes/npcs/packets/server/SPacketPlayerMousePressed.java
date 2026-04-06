package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerOverlayData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketPlayerMousePressed extends PacketServerBasic {

    protected static int channelId;
    private final int button;
    private final double scrolled;
    private final boolean ctrlDown;
    private final boolean shiftDown;
    private final boolean altDown;
    private final boolean metaDown;
    private final boolean released;
    private final String openGui;

    public SPacketPlayerMousePressed(int buttonIn, boolean releasedIn, double scrolledIn,
                                     boolean ctrlDownIn, boolean shiftDownIn, boolean altDownIn, boolean metaDownIn,
                                     String openGuiIn) {
        button = buttonIn;
        released = releasedIn;
        scrolled = scrolledIn;
        ctrlDown = ctrlDownIn;
        shiftDown = shiftDownIn;
        altDown = altDownIn;
        metaDown = metaDownIn;
        openGui = openGuiIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketPlayerMousePressed msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.button);
        buf.writeBoolean(msg.released);
        buf.writeDouble(msg.scrolled);
        buf.writeBoolean(msg.ctrlDown);
        buf.writeBoolean(msg.shiftDown);
        buf.writeBoolean(msg.altDown);
        buf.writeBoolean(msg.metaDown);
        buf.writeUtf(msg.openGui == null ? "" : msg.openGui);
    }

    public static SPacketPlayerMousePressed decode(FriendlyByteBuf buf) {
        return new SPacketPlayerMousePressed(buf.readInt(), buf.readBoolean(), buf.readDouble(),
                buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(),
                buf.readUtf());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerOverlayData data = PlayerData.get(player).overlay;
        if (button < 0) {
            for (int k : data.mousePress) {
                EventHooks.onPlayerMouseEvent(player, k, false, 0.0d,
                        false, false, false, false, openGui);
            }
            data.mousePress.clear();
            return;
        }
        if (released) { data.mousePress.add(button); }
        else {
            if (data.hasMousePress(button)) { data.mousePress.remove((Integer) button); }
        }
        if (CustomNpcs.EnableScripting && !ScriptController.Instance.languages.isEmpty()) {
            EventHooks.onPlayerMouseEvent(player, button, released, scrolled, ctrlDown, shiftDown, altDown, metaDown, openGui);
        }
        CustomNpcs.debugData.end("Packets");
    }

}
