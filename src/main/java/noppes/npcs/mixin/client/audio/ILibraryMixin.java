package noppes.npcs.mixin.client.audio;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import paulscode.sound.Library;
import paulscode.sound.Source;

import java.util.HashMap;

@Mixin(value = Library.class, remap = false, priority = 502)
public interface ILibraryMixin {

    @Accessor HashMap<String, Source> getSourceMap();

}
