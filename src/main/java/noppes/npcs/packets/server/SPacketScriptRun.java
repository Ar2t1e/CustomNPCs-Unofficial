package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.QuestEvent;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;
import java.util.Objects;

public class SPacketScriptRun extends PacketServerBasic {

    protected static int channelId;
    private EnumScriptType type;
    private int data;

    public SPacketScriptRun() { }

    public SPacketScriptRun(EnumScriptType typeIn, int dataIn) {
        type = typeIn;
        data = dataIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        buf.writeInt(data);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        type = buf.readEnum(EnumScriptType.class);
        data = buf.readInt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        PlayerScriptData handler = PlayerData.get(player).scriptData;
        if (Objects.requireNonNull(type) == EnumScriptType.QUEST_LOG_BUTTON) {
            EventHooks.onEvent(handler, type, new QuestEvent.QuestExtraButtonEvent(handler.getIPlayer(), QuestController.instance.get(data)));
        } // player compass data
        CustomNpcs.debugData.end("Packets");
    }

}
