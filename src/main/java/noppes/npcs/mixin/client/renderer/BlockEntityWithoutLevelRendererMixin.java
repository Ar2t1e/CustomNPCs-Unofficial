package noppes.npcs.mixin.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.model.custom.CustomCube;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashMap;

@Mixin(value = BlockEntityWithoutLevelRenderer.class, priority = 498)
public class BlockEntityWithoutLevelRendererMixin {

    @Shadow private ShieldModel shieldModel;

    @Unique private static final ModelPart npcs$faction_flag = new ModelPart(Collections.singletonList(CustomCube.createShieldFlag()), new HashMap<>());

    /**
     * @author BetaZavr
     * @reason Added faction ID for flag display
     */
    @Inject(at = {@At("HEAD")}, method = {"renderByItem"}, cancellable = true)
    public void npcs$renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, CallbackInfo ci) {
        CompoundTag compound = BlockItem.getBlockEntityData(stack);
        if (stack.is(Items.SHIELD) && compound != null) {
            ResourceLocation resource = null;
            if (compound.contains("FlagResource", 8)) { resource = new ResourceLocation(NoppesUtilServer.validLocation(compound.getString("FlagResource"))); }
            else if (compound.contains("FactionID", 3)) {
                Faction faction = FactionController.instance.getFaction(compound.getInt("FactionID"));
                if (faction != null) { resource = faction.flag; }
            }
            if (resource != null) {
                ci.cancel();
                poseStack.pushPose();
                poseStack.scale(1.0F, -1.0F, -1.0F);
                shieldModel.plate().render(poseStack,
                        ModelBakery.NO_PATTERN_SHIELD.sprite().wrap(ItemRenderer.getFoilBufferDirect(buffer, shieldModel.renderType(ModelBakery.NO_PATTERN_SHIELD.atlasLocation()), true, stack.hasFoil())),
                        packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                npcs$faction_flag.render(poseStack, buffer.getBuffer(RenderType.entityCutout(resource)), packedLight, packedOverlay);
                poseStack.popPose();
            }
        }
    }

}
