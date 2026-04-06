package noppes.npcs.shared.common;

import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LogWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PacketServerBasic extends PacketBasic {

   private static final Logger LOGGER = LogManager.getLogger();
   public ServerPlayer player;
   public PlayerWrapper<?> iPlayer;
   public EntityNPCInterface npc;

   public boolean requiresNpc() { return false; }

   public PermissionNode<Boolean> getPermission() { return null; }

   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   public static <MSG> void handle(MSG msg, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> {
         PacketServerBasic parent = (PacketServerBasic) msg;
         if (ctx.get().getSender() == null) {
            LogWriter.error("Error receiving packet \"" + msg.getClass().getSimpleName() + "\" - sender missing");
            return;
         }
         parent.ctx = ctx;
         parent.player = ctx.get().getSender();
         parent.iPlayer = (PlayerWrapper<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(parent.player);
         parent.npc = NoppesUtilServer.getEditingNpc(parent.player);
         try {
            if (!parent.requiresNpc() || parent.npc != null) {
               if (parent.getPermission() == null || CustomNpcsPermissions.hasPermission(parent.player, parent.getPermission())) {
                  if (!parent.toolAllowed(parent.player.getInventory().getSelected())) { parent.warn(parent.getPermission()); }
                  else { parent.handle(); }
               }
               else { parent.permission(parent.getPermission(), parent.getClass().getSimpleName()); }
            }
         } catch (Exception e) { LOGGER.error(e); }
      });
      ctx.get().setPacketHandled(true);
   }

   protected void permission(PermissionNode<Boolean> permission, String className) {
      LOGGER.warn("{}: attempted to use a mechanism that was prohibited to him. Permission: {}", player.getName().getString(), permission.getNodeName());
      player.sendSystemMessage(Component.translatable("availability.permission")
              .append(ChatFormatting.RED + ": " + ChatFormatting.RESET + className));
   }

   protected void warn(PermissionNode<Boolean> permission) {
      LOGGER.warn("{}: tried to use custom npcs without a tool in hand, possibly a hacker - {}, permission - {}",
              player == null ? "NULL" : player.getName().getString(), this,
              permission == null ? "ItemStack" : permission.getNodeName());
      if (player != null)  { player.sendSystemMessage(Component.translatable("availability.permission")); }
   }

}
