package noppes.npcs.client.renderer.obj;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class CustomMaterial extends Material {

    private final TextureAtlasSprite textureAtlasSprite;

    public CustomMaterial(ResourceLocation atlasLocationIn, ResourceLocation textureIn, TextureAtlasSprite textureAtlasSpriteIn) {
        super(atlasLocationIn, textureIn);
        textureAtlasSprite = textureAtlasSpriteIn;
    }

    public @NotNull TextureAtlasSprite sprite() { return textureAtlasSprite; }

    public @NotNull VertexConsumer buffer(MultiBufferSource buffer, @NotNull Function<ResourceLocation, RenderType> renderTypeGetter) { return textureAtlasSprite.wrap(buffer.getBuffer(renderType(renderTypeGetter))); }

    public @NotNull VertexConsumer buffer(@NotNull MultiBufferSource buffer, @NotNull Function<ResourceLocation, RenderType> renderTypeGetter, boolean isMultiConsumer) { return textureAtlasSprite.wrap(ItemRenderer.getFoilBufferDirect(buffer, renderType(renderTypeGetter), true, isMultiConsumer)); }

}
