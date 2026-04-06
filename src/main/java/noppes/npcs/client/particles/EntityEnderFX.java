package noppes.npcs.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.PortalParticle;
import noppes.npcs.client.parts.ModelPartData;
import noppes.npcs.entity.EntityCustomNpc;

public class EntityEnderFX extends PortalParticle {

    private boolean move;
    private final double xStart;
    private final double yStart;
    private final double zStart;

    public EntityEnderFX(EntityCustomNpc npc, double par2, double par4, double par6, double par8, double par10, double par12, ModelPartData data) {
        super((ClientLevel) npc.level(), par2, par4, par6, par8, par10, par12);
        move = true;
        quadSize = random.nextFloat() * 0.2f + 0.5f;
        rCol = (data.color >> 16 & 0xFF) / 255.0f;
        gCol = (data.color >> 8 & 0xFF) / 255.0f;
        bCol = (data.color & 0xFF) / 255.0f;
        if (npc.getRandom().nextInt(3) == 1) {
            move = false;
            xStart = npc.getX();
            yStart = npc.getY();
            zStart = npc.getZ();
        } else {
            xStart = 0.0f;
            yStart = 0.0f;
            zStart = 0.0f;
        }
    }

    public void move(double x, double y, double z) {
        if (move) {
            setBoundingBox(getBoundingBox().move(x, y, z));
            setLocationFromBoundingbox();
        }
    }


    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
        } else if (move) {
            float f = (float)age / (float)lifetime;
            float f1 = -f + f * f * 2.0F;
            float f2 = 1.0F - f1;
            x = xStart + xd * (double)f2;
            y = yStart + yd * (double)f2 + (double)(1.0F - f);
            z = zStart + zd * (double)f2;
            setPos(x, y, z);
        }
    }

}
