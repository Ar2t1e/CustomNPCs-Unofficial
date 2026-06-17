package noppes.npcs.client.resources.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import noppes.npcs.blocks.custom.CustomCauldronBlock;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

// changed only custom liquid color
public class CustomCauldronBakedModel implements BakedModel {

    private final SimpleBakedModel original;
    private final int tintColor;

    public CustomCauldronBakedModel(SimpleBakedModel bakedmodel, CustomCauldronBlock cauldron) {
        original = bakedmodel;
        tintColor = cauldron.getFluid().getFluidType().getTintColor();
    }

    @Override
    public @Nonnull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand) {
        return getQuads(state, side, rand, ModelData.EMPTY, null);
    }

    @Override
    public @Nonnull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand,
                                    @Nonnull ModelData data, @Nullable RenderType renderType) {
        List<BakedQuad> quads = original.getQuads(state, side, rand, data, renderType);
        if (quads.isEmpty()) { return quads; }

        int abgr = Integer.reverseBytes(tintColor) >>> 8 | (tintColor & 0xFF000000);
        List<BakedQuad> result = new ArrayList<>(quads.size());

        for (BakedQuad quad : quads) {
            if (quad.getDirection() == Direction.UP && quad.isTinted()) {
                int[] v = quad.getVertices().clone();
                for (int i = 3; i < v.length; i += 8) v[i] = abgr;
                result.add(new BakedQuad(v, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade()));
            }
            else { result.add(quad); }
        }
        return result;
    }

    @Override public boolean useAmbientOcclusion() { return original.useAmbientOcclusion(); }
    @Override public boolean isGui3d() { return original.isGui3d(); }
    @Override public boolean usesBlockLight() { return original.usesBlockLight(); }
    @Override public boolean isCustomRenderer() { return original.isCustomRenderer(); }
    @Override public @Nonnull TextureAtlasSprite getParticleIcon() { return original.getParticleIcon(); }
    @Override public @Nonnull ItemOverrides getOverrides() { return original.getOverrides(); }
    @SuppressWarnings("deprecation")
    @Override public @Nonnull ItemTransforms getTransforms() { return original.getTransforms(); }

}
