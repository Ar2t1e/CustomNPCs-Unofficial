package noppes.npcs.items;

import java.util.function.Consumer;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import noppes.npcs.CustomBlocks;
import noppes.npcs.client.CustomTileEntityItemStackRenderer;

import javax.annotation.Nonnull;

public class ItemNpcBlock extends BlockItem {

   public final Block block;

   public ItemNpcBlock(Block blockIn, Properties builder) {
      super(blockIn, builder);
      block = blockIn;
   }

   @Override
   public void initializeClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
      if (CustomBlocks.registryNbt != null && CustomBlocks.registryNbt.getByte("BlockType") == (byte) 5) {
         consumer.accept(CustomTileEntityItemStackRenderer.itemPortalRenderProperties);
      }
      else { consumer.accept(CustomTileEntityItemStackRenderer.itemRenderProperties); }
   }

}
