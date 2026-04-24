package noppes.npcs.api.handler.data;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.api.INbt;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.WrapperRecipe;

@SuppressWarnings("all")
public interface INpcRecipe {

	String getName();

	boolean isGlobal();

	@SuppressWarnings("unused")
	void setIsGlobal(@ParamName("bo") boolean bo);

	boolean getIgnoreNBT();

	void setIgnoreNBT(@ParamName("bo") boolean bo);

	boolean getIgnoreDamage();

	void setIgnoreDamage(@ParamName("bo") boolean bo);

	int getWidth();

	int getHeight();

	IItemStack getResult();

	IItemStack[][] getRecipe();

	@SuppressWarnings("unused")
	void saves(@ParamName("bo") boolean bo);

	@SuppressWarnings("unused")
	boolean saves();

	void save();

	void delete();

	boolean isShaped();

	@SuppressWarnings("unused")
	void setIsShaped(boolean isShapedIn);

	// New from Unofficial (BetaZavr)
	boolean isValid();

	boolean isKnown();

	@SuppressWarnings("unused")
	void setIsKnown(boolean isKnown);

	@SuppressWarnings("unused")
	boolean showInRecipeBook();

	@SuppressWarnings("unused")
	void setShowInRecipeBook(boolean showInRecipeBook);

	IAvailability getAvailability();

	ResourceLocation getMCId();

	INbt getNbt();

	String getNpcGroup();

	void setNbt(@ParamName("nbt") INbt nbt);

	boolean isRecipeItemsEmpty();

	WrapperRecipe getWrapperRecipe();

    void setResult(@ParamName("item") ItemStack item);

}
