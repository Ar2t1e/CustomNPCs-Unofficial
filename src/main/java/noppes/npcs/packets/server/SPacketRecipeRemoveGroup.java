package noppes.npcs.packets.server;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.handler.data.INpcRecipe;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.items.crafting.NpcShapedRecipes;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;
import java.util.Map;

public class SPacketRecipeRemoveGroup extends PacketServerBasic {

    protected static int channelId;
    private int size;
    private String group;

    public SPacketRecipeRemoveGroup() { }

    public SPacketRecipeRemoveGroup(int sizeIn, String groupIn) {
        size = sizeIn;
        group = groupIn;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_RECIPE; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(size);
        buf.writeUtf(group);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        size = buf.readInt();
        group = buf.readUtf();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        RecipeController rData = RecipeController.getInstance();
        Map<String, List<INpcRecipe>> map = size == 3 ? rData.globalRecipes : rData.anvilRecipes;
        if (map.containsKey(group)) {
            RecipeController.Registry.unfreeze();
            for (INpcRecipe rec : map.get(group)) {
                IRecipe r = RecipeController.Registry.getValue(((IRecipe) rec).getRegistryName());
                if (r instanceof INpcRecipe) { RecipeController.Registry.remove(r.getRegistryName()); }
            }
            map.remove(group);
            RecipeController.Registry.freeze();
            CustomNpcs.proxy.updateRecipes(null, true, false, "PacketHandlerServer.RecipeRemoveGroup");
            NoppesUtilServer.sendRecipeData(player, size, "", "");
            NoppesUtilServer.setRecipeGui(player, new NpcShapedRecipes());
        }
        Packets.send(player, new PacketGuiUpdate());
        CustomNpcs.debugData.end("Packets");
    }

}