package noppes.npcs.constants;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public enum EnumAnimationType {
    NONE, PUPPET, CUSTOM;

    public static Object[] getNames() {
        List<Component> list = new ArrayList<>();
        for (EnumAnimationType eat : EnumAnimationType.values()) { list.add(Component.translatable("animation.type." + eat.name().toLowerCase())); }
        return list.toArray(new Component[0]);
    }
}
