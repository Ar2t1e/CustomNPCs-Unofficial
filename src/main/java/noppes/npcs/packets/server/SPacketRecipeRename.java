package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketRecipeRename extends PacketServerBasic {

    protected static int channelId;
    private String oldGroup;
    private String newGroup;

    public SPacketRecipeRename() { }

    public SPacketRecipeRename(String oldGroupIn, String newGroupIn) {
        oldGroup = oldGroupIn;
        newGroup = newGroupIn;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_RECIPE; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(oldGroup);
        buf.writeUtf(newGroup);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        oldGroup = buf.readUtf();
        newGroup = buf.readUtf();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        RecipeController.getInstance().renameRecipe(oldGroup, newGroup);
        Packets.sendDelayed(player, new PacketGuiUpdate(), 100);
        CustomNpcs.debugData.end("Packets");
    }

}