package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncRecipeRemove extends PacketBasic {

    protected static int channelId;
    private final ResourceLocation id;

    public PacketSyncRecipeRemove(ResourceLocation idIn) { id = idIn; }

    public static void encode(PacketSyncRecipeRemove msg, FriendlyByteBuf buf) { buf.writeResourceLocation(msg.id); }

    public static PacketSyncRecipeRemove decode(FriendlyByteBuf buf) { return new PacketSyncRecipeRemove(buf.readResourceLocation()); }

    @Override
    public int getChannelId() { return channelId; }

    @OnlyIn(Dist.CLIENT)
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        RecipeController.getInstance().delete(id);
        CustomNpcs.debugData.end("Packets");
    }

}
