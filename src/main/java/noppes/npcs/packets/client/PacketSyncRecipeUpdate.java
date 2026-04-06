package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncRecipeUpdate extends PacketBasic {

    protected static int channelId;
    private ResourceLocation id;
    private int type;
    private NBTTagCompound data;

    public PacketSyncRecipeUpdate() { }

    public PacketSyncRecipeUpdate(ResourceLocation idIn, int typeIn, NBTTagCompound dataIn) {
        id = idIn;
        type = typeIn;
        data = dataIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        id = buf.readResourceLocation();
        type = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        buf.writeInt(type);
        buf.writeNbt(data);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        /*RecipeCarpentry recipe;
        if (type == 6) {
            recipe = RecipeCarpentry.load(data);
            RecipeController.instance.globalRecipes.put(recipe.getId(), recipe);
            RecipeController.instance.reloadGlobalRecipes();
        }
        else if (type == 7) {
            recipe = RecipeCarpentry.load(data);
            RecipeController.instance.anvilRecipes.put(recipe.getId(), recipe);
        }*/
        CustomNpcs.debugData.end("Packets");
    }

}
