package noppes.npcs.items;

import java.util.function.Consumer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.client.CustomTileEntityItemStackRenderer;

public class ItemNpcBlock extends BlockItem implements INPCToolItem {

   public final Block block;

   public ItemNpcBlock(Block blockIn, Properties builder) {
      super(blockIn, builder);
      block = blockIn;
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(CustomTileEntityItemStackRenderer.itemRenderProperties);
   }

}
