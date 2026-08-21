package noppes.npcs.mixin.entity;

import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.util.IntHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityTracker.class, priority = 502)
public interface IEntityTrackerMixin {

    @Accessor IntHashMap<EntityTrackerEntry> getTrackedEntityHashTable();

}
