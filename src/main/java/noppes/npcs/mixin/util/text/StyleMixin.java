package noppes.npcs.mixin.util.text;

import net.minecraft.util.text.Style;
import noppes.npcs.api.mixin.util.text.IStyleMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = Style.class, priority = 498)
public class StyleMixin implements IStyleMixin {

    @Unique
    private int npcs$color = 0;

    @Override
    public int npcs$getColor() { return npcs$color; }

    @Override
    public void npcs$setColor(int color) { npcs$color = color & 0x00ffffff; }

    @Override
    public void npcs$clearColor(int color) { npcs$color = 0; }

}
