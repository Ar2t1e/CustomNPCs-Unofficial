package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncRecipeRemove extends PacketBasic {

    protected static int channelId;
    private ResourceLocation id;
    private int type;

    public PacketSyncRecipeRemove() { }

    public PacketSyncRecipeRemove(ResourceLocation idIn, int typeIn) {
        id = idIn;
        type = typeIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        id = buf.readResourceLocation();
        type = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        buf.writeInt(type);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        /*if (type == 6) {
            RecipeController.instance.globalRecipes.remove(id);
            RecipeController.instance.reloadGlobalRecipes();
        }
        else if (type == 7) {
            RecipeController.instance.anvilRecipes.remove(id);
        }*/
        CustomNpcs.debugData.end("Packets");
    }

}
