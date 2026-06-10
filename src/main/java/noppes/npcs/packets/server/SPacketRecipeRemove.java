package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketRecipeRemove extends PacketServerBasic {

   protected static int channelId;
   private final ResourceLocation recipeId;

   public SPacketRecipeRemove(ResourceLocation recipeIn) { recipeId = recipeIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_RECIPE); }

   public static void encode(SPacketRecipeRemove msg, FriendlyByteBuf buf) { buf.writeResourceLocation(msg.recipeId); }

   public static SPacketRecipeRemove decode(FriendlyByteBuf buf) { return new SPacketRecipeRemove(buf.readResourceLocation()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      RecipeController.getInstance().delete(recipeId);
      Packets.sendDelayed(player, new PacketGuiUpdate(), 100);
      CustomNpcs.debugData.end("Packets");
   }

}
