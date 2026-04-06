package noppes.npcs.mixin.client.resources;

import net.minecraft.client.resources.Locale;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@SideOnly(Side.CLIENT)
@Mixin(value = Locale.class, priority = 502)
public interface ILocaleMixin {

    @Accessor
    Map<String, String> getProperties();

}
