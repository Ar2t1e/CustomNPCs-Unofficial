package noppes.npcs.mixin.world.entity.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.phys.AABB;
import noppes.npcs.api.mixin.world.entity.decoration.IHangingEntityMixin;
import org.apache.commons.lang3.Validate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = HangingEntity.class, priority = 499)
public class HangingEntityMixin implements IHangingEntityMixin {

    @Shadow protected BlockPos pos;
    @Shadow protected Direction direction = Direction.SOUTH;

    @Override
    public void cnpcs$getDirection(Direction newDirection) {
        HangingEntity parent = (HangingEntity) (Object) this;
        Validate.notNull(newDirection);
        Validate.isTrue(newDirection.getAxis().isHorizontal());
        direction = newDirection;
        parent.setYRot((float) (direction.get2DDataValue() * 90));
        parent.yRotO = parent.getYRot();
        if (direction != null) {
            double d0 = (double) pos.getX() + 0.5D;
            double d1 = (double) pos.getY() + 0.5D;
            double d2 = (double) pos.getZ() + 0.5D;
            double d3 = 0.46875D;
            double d4 = parent.getWidth() % 32 == 0 ? 0.5D : 0.0D;
            double d5 = parent.getHeight() % 32 == 0 ? 0.5D : 0.0D;
            d0 -= (double) direction.getStepX() * d3;
            d2 -= (double) direction.getStepZ() * d3;
            d1 += d5;
            Direction clockWiseDirection = direction.getCounterClockWise();
            d0 += d4 * (double) clockWiseDirection.getStepX();
            d2 += d4 * (double) clockWiseDirection.getStepZ();
            parent.setPosRaw(d0, d1, d2);
            double d6 = parent.getWidth();
            double d7 = parent.getHeight();
            double d8 = parent.getWidth();
            if (direction.getAxis() == Direction.Axis.Z) { d8 = 1.0D; }
            else { d6 = 1.0D; }
            d6 /= 32.0D;
            d7 /= 32.0D;
            d8 /= 32.0D;
            parent.setBoundingBox(new AABB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
        }
    }

}
