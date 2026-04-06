package noppes.npcs.shared.client.model.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector4f;

public class CustomRenderStates extends RenderStateShard {
   public static final Vector4f WHITE = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   public static VertexFormat POS_COL_TEX_LIGHT_FADE_NORMAL;
   public static VertexFormat POS_COL_TEX_NORMAL;
   public static final VertexFormat POS_TEX_NORMAL;
   protected static final TransparencyStateShard ADDITIVE_TRANSPARENCY;
   protected static final TransparencyStateShard SUBTRACTIVE_TRANSPARENCY;
   private static final RenderType[] OBJ_RENDER_TYPES;
   public static final RenderType OBJ_OUTLINE_RENDER_TYPE;
   protected static final ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_SHADER;
   public static ShaderInstance posTexNormalShader;
   private static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT;

   public CustomRenderStates(String nameIn, Runnable setupStateIn, Runnable clearStateIn) {
      super(nameIn, setupStateIn, clearStateIn);
   }

   public static RenderType getObjVBORenderType(int blending, boolean glow) {
      return OBJ_RENDER_TYPES[blending << 1 | (glow ? 1 : 0)];
   }

   public static RenderType entityCutout(ResourceLocation location) {
      return ENTITY_CUTOUT.apply(location);
   }

   public static RenderType getObjRenderType(ResourceLocation texture, int blending, boolean glow) {
      if (POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
         Map<String, VertexFormatElement> vertexFormatValues = new HashMap<>();
         vertexFormatValues.put("Position", DefaultVertexFormat.ELEMENT_POSITION);
         vertexFormatValues.put("Color", DefaultVertexFormat.ELEMENT_COLOR);
         vertexFormatValues.put("UV0", DefaultVertexFormat.ELEMENT_UV0);
         vertexFormatValues.put("UV1", DefaultVertexFormat.ELEMENT_UV1);
         vertexFormatValues.put("UV2", DefaultVertexFormat.ELEMENT_UV2);
         vertexFormatValues.put("Normal", DefaultVertexFormat.ELEMENT_NORMAL);
         vertexFormatValues.put("Padding", DefaultVertexFormat.ELEMENT_PADDING);
         POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableMap.copyOf(vertexFormatValues));
      }

      TransparencyStateShard TransparencyStateShard = TRANSLUCENT_TRANSPARENCY;
      if (blending == CustomRenderStates.BLEND.ADD.getValue()) {
         TransparencyStateShard = ADDITIVE_TRANSPARENCY;
      } else if (blending == CustomRenderStates.BLEND.SUB.getValue()) {
         TransparencyStateShard = SUBTRACTIVE_TRANSPARENCY;
      }

      CompositeState renderTypeState = CompositeState.builder().setTextureState(new TextureStateShard(texture, false, false)).setTransparencyState(TransparencyStateShard).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
      return RenderType.create("lm_obj_translucent_no_cull", POS_COL_TEX_LIGHT_FADE_NORMAL, Mode.TRIANGLES, 256, true, false, renderTypeState);
   }

   public static RenderType getObjColorOnlyRenderType(ResourceLocation texture, int blending, boolean glow) {
      if (POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
         Map<String, VertexFormatElement> vertexFormatValues = new HashMap<>();
         vertexFormatValues.put("Position", DefaultVertexFormat.ELEMENT_POSITION);
         vertexFormatValues.put("Color", DefaultVertexFormat.ELEMENT_COLOR);
         vertexFormatValues.put("Normal", DefaultVertexFormat.ELEMENT_NORMAL);
         vertexFormatValues.put("Padding", DefaultVertexFormat.ELEMENT_PADDING);
         POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableMap.copyOf(vertexFormatValues));
      }

      TransparencyStateShard TransparencyStateShard = TRANSLUCENT_TRANSPARENCY;
      if (blending == CustomRenderStates.BLEND.ADD.getValue()) {
         TransparencyStateShard = ADDITIVE_TRANSPARENCY;
      } else if (blending == CustomRenderStates.BLEND.SUB.getValue()) {
         TransparencyStateShard = SUBTRACTIVE_TRANSPARENCY;
      }

      CompositeState renderTypeState = CompositeState.builder().setTextureState(new TextureStateShard(texture, false, false)).setTransparencyState(TransparencyStateShard).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
      return RenderType.create("lm_obj_translucent_no_cull", POS_COL_TEX_LIGHT_FADE_NORMAL, Mode.TRIANGLES, 256, true, false, renderTypeState);
   }

   public static RenderType getObjOutlineRenderType(ResourceLocation texture) {
      if (POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
         Map<String, VertexFormatElement> vertexFormatValues = new HashMap<>();
         vertexFormatValues.put("Position", DefaultVertexFormat.ELEMENT_POSITION);
         vertexFormatValues.put("Color", DefaultVertexFormat.ELEMENT_COLOR);
         vertexFormatValues.put("UV0", DefaultVertexFormat.ELEMENT_UV0);
         vertexFormatValues.put("UV1", DefaultVertexFormat.ELEMENT_UV1);
         vertexFormatValues.put("UV2", DefaultVertexFormat.ELEMENT_UV2);
         vertexFormatValues.put("Normal", DefaultVertexFormat.ELEMENT_NORMAL);
         vertexFormatValues.put("Padding", DefaultVertexFormat.ELEMENT_PADDING);
         POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableMap.copyOf(vertexFormatValues));
      }

      CompositeState renderTypeState = CompositeState.builder().setTextureState(new TextureStateShard(texture, false, false)).setCullState(NO_CULL).setDepthTestState(NO_DEPTH_TEST).setOutputState(OUTLINE_TARGET).createCompositeState(false);
      return RenderType.create("lm_obj_outline_no_cull", POS_COL_TEX_LIGHT_FADE_NORMAL, Mode.TRIANGLES, 256, true, false, renderTypeState);
   }

   public static RenderType getSpriteRenderType(ResourceLocation texture) {
      if (POS_COL_TEX_NORMAL == null) {
         Map<String, VertexFormatElement> vertexFormatValues = new HashMap<>();
         vertexFormatValues.put("Position", DefaultVertexFormat.ELEMENT_POSITION);
         vertexFormatValues.put("Color", DefaultVertexFormat.ELEMENT_COLOR);
         vertexFormatValues.put("UV0", DefaultVertexFormat.ELEMENT_UV0);
         vertexFormatValues.put("Normal", DefaultVertexFormat.ELEMENT_NORMAL);
         vertexFormatValues.put("Padding", DefaultVertexFormat.ELEMENT_PADDING);
         POS_COL_TEX_NORMAL = new VertexFormat(ImmutableMap.copyOf(vertexFormatValues));
      }

      CompositeState renderTypeState = CompositeState.builder().setTextureState(new TextureStateShard(texture, false, false)).createCompositeState(true);
      return RenderType.create("lm_sprite", POS_COL_TEX_NORMAL, Mode.QUADS, 256, true, false, renderTypeState);
   }

   static {
      POS_TEX_NORMAL = new VertexFormat(ImmutableMap.of("Position", DefaultVertexFormat.ELEMENT_POSITION, "UV0", DefaultVertexFormat.ELEMENT_UV0, "Normal", DefaultVertexFormat.ELEMENT_NORMAL, "Padding", DefaultVertexFormat.ELEMENT_PADDING));
      ADDITIVE_TRANSPARENCY = new TransparencyStateShard("lm_additive_transparency", () -> {
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
      }, () -> {
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      });
      SUBTRACTIVE_TRANSPARENCY = new TransparencyStateShard("lm_subtractive_transparency", () -> {
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.DST_COLOR, DestFactor.ONE_MINUS_SRC_ALPHA);
      }, () -> {
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      });
      OBJ_RENDER_TYPES = new RenderType[CustomRenderStates.BLEND.values().length * 2];
       for (BLEND blend : CustomRenderStates.BLEND.values()) {
           for (int glow = 0; glow < 2; ++glow) {
               int var10001 = blend.id * 2 + glow;
               String var10002 = blend.toString();
               OBJ_RENDER_TYPES[var10001] = RenderType.create("lm_obj_" + var10002 + (glow == 1 ? "_glow" : ""), POS_TEX_NORMAL, Mode.TRIANGLES, 256, true, false, CompositeState.builder().setTransparencyState(blend == BLEND.ADD ? ADDITIVE_TRANSPARENCY : (blend == BLEND.SUB ? SUBTRACTIVE_TRANSPARENCY : TRANSLUCENT_TRANSPARENCY)).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
           }
       }

      OBJ_OUTLINE_RENDER_TYPE = RenderType.create("lm_obj_outline_no_cull", POS_TEX_NORMAL, Mode.TRIANGLES, 256, true, false, CompositeState.builder().setDepthTestState(NO_DEPTH_TEST).setCullState(NO_CULL).setOutputState(OUTLINE_TARGET).createCompositeState(false));
      RENDERTYPE_ENTITY_CUTOUT_SHADER = new ShaderStateShard(GameRenderer::getRendertypeEntityCutoutShader);
      posTexNormalShader = null;
      ENTITY_CUTOUT = Util.memoize((p_173202_) -> {
         CompositeState rendertype$compositestate = CompositeState.builder().setShaderState(new ShaderStateShard(() -> posTexNormalShader)).setTextureState(new TextureStateShard(p_173202_, false, false)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
         return RenderType.create("nop_entity_cutout", POS_TEX_NORMAL, Mode.TRIANGLES, 256, true, false, rendertype$compositestate);
      });
   }

   public enum BLEND {
      NORMAL(0),
      ADD(1),
      SUB(2);

      public final int id;

      BLEND(int value) {
         this.id = value;
      }

      public int getValue() {
         return this.id;
      }

      // $FF: synthetic method
      private static CustomRenderStates.BLEND[] $values() {
         return new CustomRenderStates.BLEND[]{NORMAL, ADD, SUB};
      }
   }

}
