package noppes.npcs.ai.movement;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.AnimationKind;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAINpcPanic extends EntityAIBase {

    private final EntityNPCInterface npc;
    private final float speed;
    private double randPosX;
    private double randPosY;
    private double randPosZ;

    public EntityAINpcPanic(EntityNPCInterface npcIn, float runSpeed) {
        npc = npcIn;
        speed = runSpeed;
        setMutexBits(AiMutex.PASSIVE);
    }

    @Override
    public boolean shouldExecute() {
        if ((npc.getAttackTarget() != null || npc.isBurning()) &&
                (!CustomNpcs.ShowCustomAnimation ||
                        !npc.animation.isAnimated(AnimationKind.ATTACKING, AnimationKind.INIT, AnimationKind.INTERACT, AnimationKind.DIES))) {
            Vec3d vec = RandomPositionGenerator.findRandomTarget(npc, 5, 4);
            if (vec != null) {
                randPosX = vec.x;
                randPosY = vec.y;
                randPosZ = vec.z;
                return true;
            }
        }
        return false;
    }

    @Override
    public void startExecuting() { npc.getNavigator().tryMoveToXYZ(randPosX, randPosY, randPosZ, speed); }

    @Override
    public boolean shouldContinueExecuting() { return npc.getAttackTarget() != null && (npc.getNavigator().noPath() || !npc.isMoving()); }

}
