package noppes.npcs.mixin.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import noppes.npcs.CustomItems;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ServerboundSetCreativeModeSlotPacket.class, priority = 498)
public abstract class CCreativeInventoryActionPacketMixin implements Packet<ServerGamePacketListener> {

   @Override
   public void write(@NotNull FriendlyByteBuf buffer) {
      ServerboundSetCreativeModeSlotPacket p = (ServerboundSetCreativeModeSlotPacket) (Object) this;
      if (p.getItem().getItem() == CustomItems.scripted_item) {
         buffer.writeShort(p.getSlotNum());
         buffer.writeItemStack(p.getItem(), true);
      } else {
         buffer.writeShort(p.getSlotNum());
         buffer.writeItemStack(p.getItem(), false);
      }
   }

}
