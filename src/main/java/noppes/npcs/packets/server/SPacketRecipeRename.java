package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketRecipeRename extends PacketServerBasic {

    protected static int channelId;
    private final String oldGroup;
    private final String newGroup;

    public SPacketRecipeRename(String oldGroupIn, String newGroupIn) {
        oldGroup = oldGroupIn;
        newGroup = newGroupIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_RECIPE); }

    public static void encode(SPacketRecipeRename msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.oldGroup);
        buf.writeUtf(msg.newGroup);
    }

    public static SPacketRecipeRename decode(FriendlyByteBuf buf) { return new SPacketRecipeRename(buf.readUtf(), buf.readUtf()); }

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