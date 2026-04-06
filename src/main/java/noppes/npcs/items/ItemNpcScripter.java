package noppes.npcs.items;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiOpen;
import noppes.npcs.shared.common.CommonUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemNpcScripter extends Item implements INPCToolItem {

   public ItemNpcScripter() {
      super((new Properties()).stacksTo(1));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level levelIn, @Nonnull List<Component> list, @Nonnull TooltipFlag flagIn) {
      list.add(Component.translatable("info.item.scripter"));
      list.add(Component.translatable("info.item.scripter.0"));
   }

   @Override
   public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player playerIn, @NotNull InteractionHand hand) {
      ItemStack itemstack = playerIn.getItemInHand(hand);
      if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND && playerIn instanceof ServerPlayer player) {
         if (!CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.TOOL_SCRIPTER) ||
                 (CustomNpcs.OpsOnly && !CommonUtil.isOp(player))) {
            player.sendSystemMessage(Component.translatable("availability.permission"));
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
         } else {
            Packets.send(player, new PacketGuiOpen(EnumGuiType.ScriptPlayers, BlockPos.ZERO));
         }
      }
      return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
   }

}
