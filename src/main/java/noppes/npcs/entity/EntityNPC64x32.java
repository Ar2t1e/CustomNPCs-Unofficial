package noppes.npcs.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomNpcs;

public class EntityNPC64x32 extends EntityCustomNpc {

   public EntityNPC64x32(EntityType<? extends PathfinderMob> type, Level world) {
      super(type, world);
      this.display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/humanmale/steve64x32.png");
   }

}
