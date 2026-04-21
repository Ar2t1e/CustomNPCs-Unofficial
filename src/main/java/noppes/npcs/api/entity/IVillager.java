package noppes.npcs.api.entity;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.IInventory;
import net.minecraft.village.MerchantRecipeList;
import noppes.npcs.api.interfaces.ParamName;

public interface IVillager<T extends EntityVillager> extends IEntityLiving<T> {

	MerchantRecipeList getRecipes(@ParamName("player") IPlayer<?> player);

	IInventory getVillagerInventory();

	@SuppressWarnings("unused")
	String getCareer();

	int getProfession();

}
