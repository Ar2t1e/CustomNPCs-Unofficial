package noppes.npcs.shared.common;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.CustomNPCsScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PacketServerBasic extends PacketBasic {

   private static final Logger LOGGER = LogManager.getLogger();
   public ServerPlayer player;
   public PlayerWrapper<?> iPlayer;
   public EntityNPCInterface npc;

   public abstract boolean requiresNpc();

   public abstract List<PermissionNode<Boolean>>  getPermission();

   public abstract boolean toolAllowed(ItemStack item);

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
               List<PermissionNode<Boolean>> permissions = parent.getPermission();
               StringBuilder prs = new StringBuilder();
               if (permissions != null) {
                  boolean isAccess = permissions.isEmpty();
                  for (PermissionNode<Boolean> permission : permissions) {
                     if (!prs.isEmpty()) { prs.append(", "); }
                     prs.append(permission.getNodeName());
                     if (CustomNpcsPermissions.hasPermission(parent.player, permission)) { isAccess = true; }
                  }
                  if (!isAccess) { parent.permission(prs.toString()); }
               }
               if (prs.isEmpty()) {
                  CustomNPCsScheduler.runTack(()-> {
                     if (!parent.toolAllowed(parent.player.getInventory().getSelected())) { parent.warn(prs.toString()); }
                     else { parent.handle(); }
                  });
               }
            }
         } catch (Exception e) { LOGGER.error(e); }
      });
      ctx.get().setPacketHandled(true);
   }

   protected void permission(String permissions) {
      LOGGER.warn("Player: \"{}\" attempted to use a mechanism that was prohibited to him. Packet: \"{}\". Permissions: [{}]",
              player == null ? "NULL" : player.getName().getString(),
              getClass().getSimpleName(),
              permissions == null || permissions.isEmpty() ? "NULL" : permissions);
      sendNotAccess();
   }

   protected void warn(String permissions) {
      LOGGER.warn("Player: \"{}\" tried to use custom npcs without a tool in hand, possibly a hacker. Packet: \"{}\". Permission: [{}]",
              player == null ? "NULL" : player.getName().getString(),
              getClass().getSimpleName(),
              permissions == null || permissions.isEmpty() ? "NULL" : permissions);
      sendNotAccess();
   }

   private void sendNotAccess() {
      if (player != null) {
         player.sendSystemMessage(Component.translatable("availability.permission")
                 .append(ChatFormatting.RED + ": " + ChatFormatting.RESET + getClass().getSimpleName()));
      }
   }

}
