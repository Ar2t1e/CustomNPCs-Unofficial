package noppes.npcs.mixin.fml.common.eventhandler;

import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.mixin.fml.common.eventhandler.IEventBusMixin;
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
            if (NPCS$REGISTER_METHOD == null) { NPCS$REGISTER_METHOD = EventBus.class.getDeclaredMethod("register", Class.class, Object.class, Method.class, ModContainer.class); }
            NPCS$REGISTER_METHOD.setAccessible(true);
            NPCS$REGISTER_METHOD.invoke(this, eventType, target, method, CustomNpcs.mod);
        }
        catch (Exception e) { LogWriter.error(e); }
    }
}
