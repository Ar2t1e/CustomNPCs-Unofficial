package noppes.npcs.mixin.server.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ServerLevel.class, priority = 502)
public interface IServerLevelMixin {

   @Accessor PersistentEntitySectionManager<Entity> getEntityManager();

}
