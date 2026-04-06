package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketHudTimerEnd extends PacketServerBasic {

    protected static int channelId;
    private int orientationType;
    private int timerId;

    public SPacketHudTimerEnd(int orientationIn, int timerIn) {
        orientationType = orientationIn;
        timerId = timerIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(orientationType);
        buf.writeInt(timerId);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        orientationType = buf.readInt();
        timerId = buf.readInt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerData data = PlayerData.get(player);
        EventHooks.onPlayerTimer(data, timerId);
        data.overlay.removeComponent(orientationType, timerId);
        data.overlay.update();
        CustomNpcs.debugData.end("Packets");
    }

}
