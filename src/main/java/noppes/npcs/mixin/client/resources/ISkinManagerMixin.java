package noppes.npcs.mixin.client.resources;

import net.minecraft.client.resources.SkinManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(value = SkinManager.class, priority = 502)
public interface ISkinManagerMixin {

    @Accessor File getSkinsDirectory();

}
