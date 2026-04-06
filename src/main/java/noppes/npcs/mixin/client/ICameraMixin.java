package noppes.npcs.mixin.client;

import net.minecraft.client.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Camera.class, priority = 502)
public interface ICameraMixin {

    @Accessor float getYRot();

    @Accessor void setYRot(float newYRot);

    @Accessor float getXRot();

    @Accessor void setXRot(float newXRot);

    @Accessor Quaternionf getRotation();

    @Accessor Vector3f getForwards();

    @Accessor Vector3f getUp();

    @Accessor Vector3f getLeft();

}
