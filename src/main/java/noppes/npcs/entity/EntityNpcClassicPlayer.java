package noppes.npcs.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomNpcs;

public class EntityNpcClassicPlayer extends EntityCustomNpc {

   public EntityNpcClassicPlayer(EntityType<? extends PathfinderMob> type, Level world) {
      super(type, world);
      display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/humanmale/steve.png");
   }

}
