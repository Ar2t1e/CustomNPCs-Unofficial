package noppes.npcs.mixin.nbt;

import java.util.List;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ListTag.class, priority = 502)
public interface IListTagMixin {

   @Accessor List<Tag> getList();

}
