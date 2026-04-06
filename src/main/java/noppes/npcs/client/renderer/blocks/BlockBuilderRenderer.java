package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.schematics.Schematic;
import noppes.npcs.schematics.SchematicWrapper;
import org.jetbrains.annotations.NotNull;

public class BlockBuilderRenderer<T extends TileBuilder> extends BlockRendererInterface<T> {

    public static Schematic schematic = null;
    public static BlockPos pos = null;

    public BlockBuilderRenderer(Context dispatcher) { super(dispatcher); }

    public void render(@NotNull T tile, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource buffer, int light, int overlay) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isCreative()) { return; }
        matrixStack.pushPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        if (tile.getSchematic() == null) {
            drawSelectionBox(matrixStack, buffer, new BlockPos(2, 2, 2));
        }
        else {
            SchematicWrapper schem = tile.getSchematic();
            if (tile.rotation % 2 == 0) {
                drawSelectionBox(matrixStack, buffer, new BlockPos(schem.schema.getWidth(), schem.schema.getHeight(), schem.schema.getLength()));
            } else {
                drawSelectionBox(matrixStack, buffer, new BlockPos(schem.schema.getLength(), schem.schema.getHeight(), schem.schema.getWidth()));
            }
        }
        matrixStack.popPose();
    }

    private void drawSelectionBox(@NotNull PoseStack matrixStack, @NotNull MultiBufferSource buffer, BlockPos blockPos) {
        AABB bb = new AABB(BlockPos.ZERO, blockPos);
        matrixStack.translate(1.0F, 0.0F, 1.0F);
        LevelRenderer.renderLineBox(matrixStack, buffer.getBuffer(RenderType.lines()), bb, 1.0F, 0.0F, 0.0F, 1.0F);
    }

}
