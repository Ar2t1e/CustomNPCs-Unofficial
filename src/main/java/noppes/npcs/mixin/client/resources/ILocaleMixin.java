package noppes.npcs.mixin.client.resources;

import com.google.common.base.Splitter;
import net.minecraft.client.resources.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.regex.Pattern;

@Mixin(value = Locale.class, priority = 502)
public interface ILocaleMixin {

    @Accessor("SPLITTER") Splitter getSplitter();

    @Accessor("PATTERN") Pattern getPattern();

    @Accessor Map<String, String> getProperties();

}
