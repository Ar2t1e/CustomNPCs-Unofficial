package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.interfaces.ParamName;

@SuppressWarnings("unused")
public interface IJobHealer {

    IHealerEffect[] getEffects();

    boolean removeEffect(@ParamName("effectId") int effectId);

    IHealerEffect addEffect(@ParamName("effectId") int effectId,
                            @ParamName("range") int range, @ParamName("speed") int speed,
                            @ParamName("amplifier") int amplifier, @ParamName("type") int type);
}
