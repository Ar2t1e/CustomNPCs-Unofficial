package noppes.npcs.mixin.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Deque;

@Mixin(value = PoseStack.class, priority = 502)
public interface IPoseStackMixin {

    @Accessor Deque<PoseStack.Pose> getPoseStack();

}
