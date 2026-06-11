package noppes.npcs.api.mixin.fml.common.eventhandler;

import java.lang.reflect.Method;

public interface IEventBusMixin {

    void npcs$Register(Class<?> eventType, Object target, Method method);

}
