package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncRecipeUpdate extends PacketBasic {

    protected static int channelId;
    private final ResourceLocation id;
    private final boolean isGlobal;
    private final CompoundTag data;

    public PacketSyncRecipeUpdate(ResourceLocation idIn, boolean isGlobalIn, CompoundTag dataIn) {
        id = idIn;
        isGlobal = isGlobalIn;
        data = dataIn;
    }

    public static void encode(PacketSyncRecipeUpdate msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.id);
        buf.writeBoolean(msg.isGlobal);
        buf.writeNbt(msg.data);
    }

    public static PacketSyncRecipeUpdate decode(FriendlyByteBuf buf) {
        return new PacketSyncRecipeUpdate(buf.readResourceLocation(), buf.readBoolean(), buf.readNbt());
    }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        RecipeCarpentry recipe = RecipeCarpentry.load(data);
        if (isGlobal) { RecipeController.getInstance().putGlobalRecipes(recipe.getId(), recipe); }
        else { RecipeController.getInstance().putAnvilRecipes(recipe.getId(), recipe); }
        CustomNpcs.debugData.end("Packets");
    }

}
