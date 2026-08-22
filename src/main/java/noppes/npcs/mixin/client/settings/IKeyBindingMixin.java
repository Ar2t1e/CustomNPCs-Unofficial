package noppes.npcs.mixin.client.settings;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.throwables.MixinException;

import java.util.Map;
import java.util.Set;

@Mixin(value = KeyBinding.class, priority = 502)
public interface IKeyBindingMixin {

    @Accessor("KEYBIND_ARRAY") static Map<String, KeyBinding> getAll() { throw new MixinException("Mixin did not initialize properly."); }

    @Accessor("HASH") static KeyBindingMap getMap() { throw new MixinException("Mixin did not initialize properly."); }

    @Accessor("KEYBIND_SET") static Set<String> getCategories() { throw new MixinException("Mixin did not initialize properly."); }

    @Accessor String getKeyDescription();

    @Accessor int getKeyCode();

    @Accessor void setKeyCode(int newKeyCode);

    @Mutable @Accessor void setKeyCodeDefault(int newKeyCodeDefault);

    @Mutable @Accessor void setKeyDescription(String newKeyDescription);

    @Mutable @Accessor void setKeyCategory(String newKeyCategory);

    @SuppressWarnings("unused")
    @Accessor(remap = false) void setKeyModifier(KeyModifier newKeyModifier);

}
