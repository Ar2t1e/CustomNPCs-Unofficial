package noppes.npcs.mixin.entity.player;

import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityPlayerMP.class, priority = 502)
public interface IEntityPlayerMPMixin {

    @Accessor String getLanguage();

}
