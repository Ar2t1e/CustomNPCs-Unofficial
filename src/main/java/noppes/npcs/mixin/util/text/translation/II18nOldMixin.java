package noppes.npcs.mixin.util.text.translation;

import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.text.translation.LanguageMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.throwables.MixinException;

@SuppressWarnings("deprecation")
@Mixin(value = I18n.class, priority = 502)
public interface II18nOldMixin {

    @Accessor
    static LanguageMap getLocalizedName() { throw new MixinException("Mixin did not initialize properly."); }

}
