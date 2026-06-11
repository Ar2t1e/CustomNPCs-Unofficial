package noppes.npcs.mixin.minecraftforge.eventbus;

import net.minecraftforge.eventbus.EventBus;
import noppes.npcs.api.mixin.minecraftforge.eventbus.IEventBusMixin;
import noppes.npcs.shared.common.util.LogWriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.lang.reflect.Method;

@Mixin(value = EventBus.class, priority = 502)
public class EventBusMixin implements IEventBusMixin {

    @Unique
    private static Method NPCS$REGISTER_METHOD;

    @Override
    public void npcs$Register(Class<?> eventType, Object target, Method method) {
        try {
            if (NPCS$REGISTER_METHOD == null) { NPCS$REGISTER_METHOD = EventBus.class.getDeclaredMethod("register", Class.class, Object.class, Method.class); }
            NPCS$REGISTER_METHOD.setAccessible(true);
            NPCS$REGISTER_METHOD.invoke(this, eventType, target, method);
        }
        catch (Exception e) { LogWriter.error(e); }
    }
}