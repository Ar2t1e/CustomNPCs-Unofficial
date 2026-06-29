package noppes.npcs.mixin.client.settings;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

@Mixin(value = KeyBinding.class, priority = 502)
public interface IKeyBindingMixin {

    @Accessor String getKeyDescription();

    @Accessor int getKeyCode();

    @Accessor("KEYBIND_ARRAY") Map<String, KeyBinding> getAll();

    @Accessor("HASH") KeyBindingMap getMap();

    @Accessor("KEYBIND_SET") Set<String> getCategories();

    @Accessor void setKeyCode(int newKeyCode);

    @Mutable @Accessor void setKeyCodeDefault(int newKeyCodeDefault);

    @Mutable @Accessor void setKeyDescription(String newKeyDescription);

    @Mutable @Accessor void setKeyCategory(String newKeyCategory);

    @SuppressWarnings("unused")
    @Accessor(remap = false) void setKeyModifier(KeyModifier newKeyModifier);

}
