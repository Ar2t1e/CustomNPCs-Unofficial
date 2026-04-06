package noppes.npcs.client.renderer.obj;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.mixin.client.model.obj.IObjModelMixin;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.ValueUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Function;

public class ParameterizedModel {

    /** textures */
    private static final ResourceLocation MISSING_TEXTURE_LOCATION = new ResourceLocation(CustomNpcs.MODID, "textures/missingno.png");
    private static Constructor<TextureAtlasSprite> spriteConstructor;
    private static final Map<ResourceLocation, Material> MATERIALS = new HashMap<>();
    /** RenderType as entities */
    private static final Function<ResourceLocation, RenderType> OBJ_SOLID = net.minecraft.Util.memoize((location) -> {
        if (location.getNamespace().equals("minecraft") && location.getPath().equals("textures/missingno.png")) { location = MISSING_TEXTURE_LOCATION; }
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntitySolidShader))
                .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                .setTransparencyState(new RenderStateShard.TransparencyStateShard("has_transparency", RenderSystem::enableBlend, RenderSystem::disableBlend))
                .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                .setOverlayState(new RenderStateShard.OverlayStateShard(true))
                .createCompositeState(true);
        return RenderType.create("obj_solid", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$compositestate);
    });

    protected CompositeRenderable compositeRenderable;

    public ResourceLocation modelLocation;
    public final List<String> visibleMeshes = new ArrayList<>();
    public final Map<String, ResourceLocation> materialTextures = new HashMap<>();
    public final int colorMask;
    public final boolean reverseNormals;
    public ObjModel objModel;

    public ParameterizedModel(ResourceLocation modelLocationIn, List<String> visibleMeshesIn, Map<String, ResourceLocation> materialTexturesIn, boolean reverseNormalsIn,  int colorMaskIn) {
        modelLocation = modelLocationIn;
        colorMask = colorMaskIn;
        reverseNormals = reverseNormalsIn;
        if (visibleMeshesIn != null) { visibleMeshes.addAll(visibleMeshesIn); }
        if (materialTexturesIn != null) { materialTextures.putAll(materialTexturesIn); }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParameterizedModel objPM)) { return false; }
        if (this == objPM) { return true; }
        if (colorMask != objPM.colorMask || !modelLocation.equals(objPM.modelLocation)) { return false; }
        if (visibleMeshes.isEmpty() && materialTextures.isEmpty() && objPM.visibleMeshes.isEmpty() && objPM.materialTextures.isEmpty()) { return true; }
        if (visibleMeshes.size() != objPM.visibleMeshes.size()) { return false; }
        for (String name : visibleMeshes) {
            if (!objPM.visibleMeshes.contains(name)) { return false; }
        }
        if (materialTextures.size() != objPM.materialTextures.size()) { return false; }
        for (String name : materialTextures.keySet()) {
            if (!objPM.materialTextures.containsKey(name) || !objPM.materialTextures.get(name).equals(materialTextures.get(name))) { return false; }
        }
        return true;
    }

    public void load() {
        objModel = ObjLoader.INSTANCE.loadModel(new ObjModel.ModelSettings(
                modelLocation,  // location of .obj file
                true,           // automatically remove invisible edges
                true,           // enable polygon darkening
                true,           // flip texture
                false,          // use radiant light for illumination (not recommended)
                null            // redefine material manually
        ));
        ((IObjModelMixin) objModel).cnpcs$setColorMask(colorMask);
        List<Vector3f> baseNormals = ((IObjModelMixin) objModel).cnpcs$getNormals();
        if (reverseNormals) {
            List<Vector3f> invertNormals = new ArrayList<>();
            for (Vector3f v : baseNormals) { invertNormals.add(new Vector3f(v).mul(-1.0f)); }
            ((IObjModelMixin) objModel).cnpcs$setNormals(invertNormals);
        }
        IGeometryBakingContext configuration = new IGeometryBakingContext() {

            @Override
            public String getModelName() { return modelLocation.toString(); }

            @Override
            public boolean hasMaterial(String name) { return materialTextures.containsKey(name); }

            @Override
            public Material getMaterial(String name) {
                Minecraft mc = Minecraft.getInstance();
                if (!materialTextures.containsKey(name)) {
                    try {
                        if (name.startsWith("#")) { materialTextures.put(name, new ResourceLocation(CustomNpcs.MODID, name.replace("#", ""))); }
                        else if (!name.contains(":")) { materialTextures.put(name, new ResourceLocation(CustomNpcs.MODID, name)); }
                        else { materialTextures.put(name, new ResourceLocation(name)); }
                    }
                    catch (Throwable ignored) {
                        LogWriter.warn("Location \"" + name + "\" is not suitable for creating a ResourceLocation!");
                        materialTextures.put(name, MISSING_TEXTURE_LOCATION);
                    }
                }
                ResourceLocation key = materialTextures.get(name);
                ResourceLocation location = key;
                String path = location.getPath();
                boolean reset= false;
                if (!path.startsWith("textures/")) { path = "textures/" + path; reset = true; }
                if (!path.endsWith(".png")) { path += ".png"; reset = true; }
                if (reset) { location = new ResourceLocation(location.getNamespace(), path); }

                AbstractTexture texture = mc.textureManager.getTexture(location);
                AbstractTexture missing = mc.textureManager.getTexture(MISSING_TEXTURE_LOCATION);
                if (texture != missing) {
                    if (!MATERIALS.containsKey(key)) {
                        if (spriteConstructor == null) {
                            try { spriteConstructor = TextureAtlasSprite.class.getDeclaredConstructor(ResourceLocation.class, SpriteContents.class, int.class, int.class, int.class, int.class); } catch (Exception ignored) { }
                        }
                        if (spriteConstructor != null) {
                            NativeImage image = null;
                            AnimationMetadataSection animation = AnimationMetadataSection.EMPTY;
                            ForgeTextureMetadata forge = ForgeTextureMetadata.EMPTY;
                            try {
                                Resource resource = mc.getResourceManager().getResourceOrThrow(location);
                                InputStream inputStream = resource.open();
                                try { image = NativeImage.read(inputStream); }
                                catch (Throwable t) {
                                    try { inputStream.close(); }
                                    catch (Throwable tc) { t.addSuppressed(tc); }
                                    throw t;
                                }
                                inputStream.close();
                                try {
                                    if (resource.metadata().getSection(ForgeTextureMetadata.SERIALIZER).isPresent()) { forge = resource.metadata().getSection(ForgeTextureMetadata.SERIALIZER).get(); }
                                    if (resource.metadata().getSection(AnimationMetadataSection.SERIALIZER).isPresent()) { animation = resource.metadata().getSection(AnimationMetadataSection.SERIALIZER).get(); }
                                }
                                catch (RuntimeException e) { LogWriter.error("Failed reading metadata of: " + location, e); }
                            }
                            catch (IOException e) { LogWriter.error("Failed load texture of: " + location, e); }
                            if (image != null) {
                                FrameSize frameSize = new FrameSize(image.getWidth(), image.getHeight());
                                SpriteContents spriteContents = new SpriteContents(key, frameSize, image, animation, forge);
                                try {
                                    spriteConstructor.setAccessible(true);
                                    ResourceLocation atlasLocation = new ResourceLocation(CustomNpcs.MODID, "objs");
                                    MATERIALS.put(key, new CustomMaterial(atlasLocation, key, spriteConstructor.newInstance(atlasLocation, spriteContents, image.getWidth(), image.getHeight(), 0, 0)));
                                }
                                catch (Exception ignored) { }
                            }
                        }
                    }
                    if (MATERIALS.containsKey(key)) { return MATERIALS.get(key); }
                }
                try {
                    return new Material(new ResourceLocation("minecraft", "textures/atlas/blocks.png"), location);
                } catch (Exception e) {
                    LogWriter.info("Error create Material: \""+name+"\"");
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isGui3d() { return true; }

            @Override
            public boolean useBlockLight() { return true; }

            @Override
            public boolean useAmbientOcclusion() { return true; }

            @Override
            public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }

            @Override
            public Transformation getRootTransform() { return Transformation.identity(); }

            @Override
            public @Nullable ResourceLocation getRenderTypeHint() { return null; }

            @Override
            public boolean isComponentVisible(String meshName, boolean def) {
                return (visibleMeshes.isEmpty() || visibleMeshes.contains(meshName)) && def;
            }

        };
        var builder = CompositeRenderable.builder();
        for (Map.Entry<String, ObjModel.ModelGroup> entry : ((IObjModelMixin) objModel).cnpcs$getParts().entrySet()) {
            if (configuration.isComponentVisible(entry.getKey(), true)) {
                entry.getValue().bake(builder.child(entry.getKey()), configuration);
            }
        }
        compositeRenderable = builder.get();
        if (reverseNormals) { ((IObjModelMixin) objModel).cnpcs$setNormals(baseNormals); }
        //compositeRenderable = objModel.bakeRenderable(configuration);
    }

    // overlay <- OverlayTexture
    public void render(PoseStack matrixStack, MultiBufferSource bufferSource, int lightMap, int overlay, float partialTick, CompositeRenderable.Transforms transforms) {
        if (compositeRenderable != null) {
            compositeRenderable.render(matrixStack, bufferSource, OBJ_SOLID::apply,
                    Math.max(0, ValueUtil.correctInt(lightMap, 0, LightTexture.FULL_BRIGHT)),
                    overlay, partialTick, transforms != null ? transforms : CompositeRenderable.Transforms.EMPTY);
        }
    }

}
