package noppes.npcs.mixin.nbt;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = NBTTagList.class, priority = 502)
public interface INBTTagListMixin {

    @Accessor List<NBTBase> getTagList();

}
