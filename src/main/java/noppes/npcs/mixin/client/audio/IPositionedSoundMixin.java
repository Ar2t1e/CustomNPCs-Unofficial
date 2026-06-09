package noppes.npcs.mixin.client.audio;

import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.PositionedSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import javax.annotation.Nonnull;

@Mixin(value = PositionedSound.class, priority = 502)
public interface IPositionedSoundMixin {

    @Accessor
    void setSound(@Nonnull Sound newSound);

    @Accessor float getXPosF();

    @Accessor
    void setXPosF(float newXPosF);

    @Accessor
    float getYPosF();

    @Accessor
    void setYPosF(float newYPosF);

    @Accessor
    float getZPosF();

    @Accessor
    void setZPosF(float newZPosF);

}
