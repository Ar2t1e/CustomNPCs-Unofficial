package noppes.npcs.shared.common;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public abstract class PacketBasic {

   public Player player;
   public Supplier<Context> ctx;

   public static <MSG> void handle(MSG msg, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> {
         PacketBasic parent = (PacketBasic) msg;
         parent.ctx = ctx;
         DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> parent::handleClient);
      });
      ctx.get().setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private void handleClient() {
      player = Minecraft.getInstance().player;
      handle();
   }

   public abstract int getChannelId();

   protected abstract void handle();

}
