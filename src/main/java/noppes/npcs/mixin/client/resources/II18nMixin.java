package noppes.npcs.mixin.client.resources;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Locale;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SideOnly(Side.CLIENT)
@Mixin(value = I18n.class, priority = 502)
public interface II18nMixin {

    @Accessor
    static Locale getI18nLocale() { throw new IllegalStateException("Mixin did not initialize properly."); }

}
