package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerOverlayData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketPlayerMousePressed extends PacketServerBasic {

    protected static int channelId;
    private int button;
    private double scrolled;
    private boolean ctrlDown;
    private boolean shiftDown;
    private boolean altDown;
    private boolean metaDown;
    private boolean released;
    private String openGui;

    public SPacketPlayerMousePressed() {}

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

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(button);
        buf.writeBoolean(released);
        buf.writeDouble(scrolled);
        buf.writeBoolean(ctrlDown);
        buf.writeBoolean(shiftDown);
        buf.writeBoolean(altDown);
        buf.writeBoolean(metaDown);
        buf.writeUtf(openGui == null ? "" : openGui);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        button = buf.readInt();
        released = buf.readBoolean();
        scrolled = buf.readDouble();
        ctrlDown = buf.readBoolean();
        shiftDown = buf.readBoolean();
        altDown = buf.readBoolean();
        metaDown = buf.readBoolean();
        openGui = buf.readUtf();
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
            if (data.hasMousePress(button)) { data.mousePress.remove(button); }
        }
        if (CustomNpcs.EnableScripting && !ScriptController.Instance.languages.isEmpty()) {
            EventHooks.onPlayerMouseEvent(player, button, released, scrolled, ctrlDown, shiftDown, altDown, metaDown, openGui);
        }
        CustomNpcs.debugData.end("Packets");
    }

}
