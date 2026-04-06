package noppes.npcs.mixin.client.model.obj;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraftforge.client.model.obj.ObjModel;
import noppes.npcs.api.mixin.client.model.obj.IObjModelMixin;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Mixin(value = ObjModel.class, priority = 498, remap = false)
public class ObjModelMixin implements IObjModelMixin {

    @Unique public Vector4f cnpcs$colorMask = new Vector4f(0.0f);

    @Final @Shadow private Map<String, ObjModel.ModelGroup> parts;
    @Mutable @Final @Shadow private List<Vector3f> normals;

    @Inject(
            at = {@At("HEAD")},
            method = {"makeQuad"},
            cancellable = true)
    @SuppressWarnings("unchecked")
    private void cnpcs$makeQuad(int[][] indices, int tintIndex, Vector4f colorTint, Vector4f ambientColor, TextureAtlasSprite texture, Transformation transform, CallbackInfoReturnable<Pair<BakedQuad, Direction>> cir) {
        if (cnpcs$colorMask.w != 0.0f && !cnpcs$colorMask.equals(colorTint)) {
            try {
                Method method = ObjModel.class.getDeclaredMethod("makeQuad", int[][].class, int.class, Vector4f.class, Vector4f.class, TextureAtlasSprite.class, Transformation.class);
                method.trySetAccessible();
                cir.setReturnValue((Pair<BakedQuad, Direction>) method.invoke(this, indices, tintIndex, cnpcs$colorMask, ambientColor, texture, transform));
            }
            catch (Exception ignored) { }
        }
    }

    @Override
    public int cnpcs$getColorMask() { return new Color(cnpcs$colorMask.x, cnpcs$colorMask.y, cnpcs$colorMask.z, cnpcs$colorMask.w).getRGB(); }

    @Override
    public void cnpcs$setColorMask(int newColorMask) {
        cnpcs$colorMask.x = FastColor.ARGB32.red(newColorMask) / 255.0f;
        cnpcs$colorMask.y = FastColor.ARGB32.green(newColorMask) / 255.0f;
        cnpcs$colorMask.z = FastColor.ARGB32.blue(newColorMask) / 255.0f;
        float alpha = FastColor.ARGB32.alpha(newColorMask) / 255.0f;
        if (alpha == 0.0f && cnpcs$colorMask.x != 0.0f && cnpcs$colorMask.y != 0.0f && cnpcs$colorMask.z != 0.0f) { alpha = 1.0f; }
        cnpcs$colorMask.w = alpha;
    }

    @Override
    public Map<String, ObjModel.ModelGroup> cnpcs$getParts() { return parts; }

    @Override
    public List<Vector3f> cnpcs$getNormals() { return normals; }

    @Override
    public void cnpcs$setNormals(List<Vector3f> newNormals) { normals = newNormals; }

}
