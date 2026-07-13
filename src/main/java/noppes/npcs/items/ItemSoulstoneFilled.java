package noppes.npcs.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;

import javax.annotation.Nonnull;

public class ItemSoulstoneFilled extends Item {

   public ItemSoulstoneFilled() { super((new Properties()).stacksTo(1)); }

   @Override
   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(@Nonnull ItemStack stack, Level level, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
      CompoundTag compound = stack.getTag();
      if (compound != null && compound.contains("Entity", 10)) {
         MutableComponent name = Component.translatable(compound.getString("Name"));
         if (compound.contains("DisplayName")) {
            String key = compound.getString("DisplayName");
            MutableComponent displayName = Component.empty();
            try {
               MutableComponent preName = Component.Serializer.fromJson(key);
               if (preName != null) {
                  preName.withStyle(ChatFormatting.BLUE);
                  displayName.append(preName);
               }
            } catch (Exception ignored) { }
            if (displayName.getString().isEmpty()) { displayName.append(Component.translatable(key).withStyle(ChatFormatting.BLUE)); }
            if (flag == TooltipFlag.ADVANCED) {
               name = displayName.append(Component.literal(" (").withStyle(ChatFormatting.DARK_GRAY))
                       .append(name.withStyle(ChatFormatting.DARK_GRAY))
                       .append(Component.literal(")").withStyle(ChatFormatting.DARK_GRAY));
            }
         }
         list.add(name);
         if (stack.getTag().contains("ExtraText")) {
            MutableComponent text = Component.literal("");
            String[] split = compound.getString("ExtraText").split(",");
            for (String s : split) { text.append(Component.translatable(s)); }
            list.add(text);
         }
      }
      else { list.add(Component.literal(ChatFormatting.RED + "Error")); }
   }

   @Override
   public @Nonnull InteractionResult useOn(UseOnContext context) {
      if (context.getLevel().isClientSide) { return InteractionResult.SUCCESS; }
      ItemStack stack = context.getItemInHand();
      if (Spawn(context.getPlayer(), stack, context.getLevel(), context.getClickedPos()) == null) { return InteractionResult.FAIL; }
      if (context.getPlayer() != null && !context.getPlayer().isCreative()) { stack.split(1); }
      return InteractionResult.SUCCESS;
   }

   public static Entity Spawn(Player player, ItemStack stack, Level level, BlockPos pos) {
      if (!level.isClientSide && stack.getTag() != null && stack.getTag().contains("Entity", 10)) {
         CompoundTag compound = stack.getTag().getCompound("Entity");
         Entity entity = EntityType.create(compound, level).orElse(null);
         if (entity != null) {
            entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D);
            if (entity instanceof EntityNPCInterface npc) {
               npc.ais.setStartPos(pos);
               npc.setHealth(npc.getMaxHealth());
               npc.setPos(pos.getX() + 0.5D, npc.getStartYPos(), pos.getZ() + 0.5D);
               if (npc.role.getType() == 6 && player != null) {
                  PlayerData data = PlayerData.get(player);
                  if (data.hasCompanion()) { return null; }
                  ((RoleCompanion)npc.role).setOwner(player);
                  data.setCompanion(npc);
               }
               if (npc.role.getType() == 2 && player != null) { ((RoleFollower) npc.role).setOwner(player); }
            }
            if (!level.addFreshEntity(entity)) {
               if (player != null) { player.sendSystemMessage(Component.translatable("error.failedToSpawn")); }
               return null;
            }
            return entity;
         }
      }
      return null;
   }

}
