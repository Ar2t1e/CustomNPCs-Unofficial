package noppes.npcs.mixin.client.multiplayer;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClientLevel.class, priority = 502)
public interface IClientLevelMixin {

    @Accessor TransientEntitySectionManager<Entity> getEntityStorage();

}
