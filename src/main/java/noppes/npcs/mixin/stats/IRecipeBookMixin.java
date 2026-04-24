package noppes.npcs.mixin.stats;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.RecipeBookSettings;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = RecipeBook.class, priority = 502)
public interface IRecipeBookMixin {

    @Accessor Set<ResourceLocation> getKnown();

    @Accessor Set<ResourceLocation> getHighlight();

    @Accessor RecipeBookSettings getBookSettings();

}
