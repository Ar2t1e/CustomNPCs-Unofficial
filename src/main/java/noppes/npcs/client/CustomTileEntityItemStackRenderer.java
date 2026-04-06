package noppes.npcs.client;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import noppes.npcs.items.ItemNpcBlock;
import noppes.npcs.shared.common.util.LogWriter;
import org.jetbrains.annotations.NotNull;

public class CustomTileEntityItemStackRenderer extends BlockEntityWithoutLevelRenderer {

   private static CustomTileEntityItemStackRenderer i = null;
   public static IClientItemExtensions itemRenderProperties = new IClientItemExtensions() {
      public BlockEntityWithoutLevelRenderer getCustomRenderer() {
         return CustomTileEntityItemStackRenderer.instance();
      }
   };
   private final HashMap<Block, BlockEntity> data = new HashMap<>();
   private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

   public CustomTileEntityItemStackRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet model) {
      super(dispatcher, model);
      blockEntityRenderDispatcher = dispatcher;
   }

   @Override
   public void renderByItem(ItemStack stack, @NotNull ItemDisplayContext itemDisplayContext, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
      if (stack.getItem() instanceof ItemNpcBlock item) {
         BlockEntity tile = data.get(item.block);
         if (tile == null) {
            data.put(item.block, tile = ((BaseEntityBlock)item.block).newBlockEntity(BlockPos.ZERO, item.block.defaultBlockState()));
         }
         if (tile != null) { blockEntityRenderDispatcher.renderItem(tile, matrixStack, buffer, combinedLight, combinedOverlay); }
      }
   }

   public static CustomTileEntityItemStackRenderer instance() {
       if (i == null) {
           Minecraft mc = Minecraft.getInstance();
           i = new CustomTileEntityItemStackRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
       }
       return i;
   }
}
