package noppes.npcs.mixin.server.packs;

import net.minecraft.server.packs.FilePackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;
import java.util.zip.ZipFile;

@Mixin(value = FilePackResources.class, priority = 502)
public interface IFilePackResourcesMixin {

    @Accessor File getFile();

    @Accessor ZipFile getZipFile();

}
