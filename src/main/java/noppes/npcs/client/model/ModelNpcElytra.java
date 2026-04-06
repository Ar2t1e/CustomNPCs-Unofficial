package noppes.npcs.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.client.parts.ModelData;
import noppes.npcs.entity.EntityCustomNpc;
import org.jetbrains.annotations.NotNull;

public class ModelNpcElytra<T extends LivingEntity> extends AgeableListModel<T> {

    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public ModelNpcElytra(ModelPart p_170538_) {
        leftWing = p_170538_.getChild("left_wing");
        rightWing = p_170538_.getChild("right_wing");
    }

    protected @NotNull Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    protected @NotNull Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(leftWing, rightWing);
    }

    public void setupAnim(T p_102544_, float p_102545_, float p_102546_, float p_102547_, float p_102548_, float p_102549_) {
        float $$6 = 0.2617994F;
        float $$7 = -0.2617994F;
        float $$8 = 0.0F;
        float $$9 = 0.0F;
        if (p_102544_.isFallFlying()) {
            float $$10 = 1.0F;
            Vec3 $$11 = p_102544_.getDeltaMovement();
            if ($$11.y < 0.0D) {
                Vec3 $$12 = $$11.normalize();
                $$10 = 1.0F - (float)Math.pow(-$$12.y, 1.5D);
            }

            $$6 = $$10 * 0.34906584F + (1.0F - $$10) * $$6;
            $$7 = $$10 * -1.5707964F + (1.0F - $$10) * $$7;
        } else if (p_102544_.isCrouching()) {
            $$6 = 0.6981317F;
            $$7 = -0.7853982F;
            $$8 = 3.0F;
            $$9 = 0.08726646F;
        }

        leftWing.y = $$8;
        if (p_102544_ instanceof EntityCustomNpc) {
            ModelData modelData = ((EntityCustomNpc)p_102544_).modelData;
            modelData.elytraRotX += ($$6 - modelData.elytraRotX) * 0.1F;
            modelData.elytraRotY += ($$9 - modelData.elytraRotY) * 0.1F;
            modelData.elytraRotZ += ($$7 - modelData.elytraRotZ) * 0.1F;
            leftWing.xRot = modelData.elytraRotX;
            leftWing.yRot = modelData.elytraRotY;
            leftWing.zRot = modelData.elytraRotZ;
        } else {
            leftWing.xRot = $$6;
            leftWing.zRot = $$7;
            leftWing.yRot = $$9;
        }

        rightWing.yRot = -leftWing.yRot;
        rightWing.y = leftWing.y;
        rightWing.xRot = leftWing.xRot;
        rightWing.zRot = -leftWing.zRot;
    }
}
