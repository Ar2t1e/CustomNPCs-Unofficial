package noppes.npcs.api.wrapper;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.IInventory;
import net.minecraft.village.MerchantRecipeList;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.IVillager;
import noppes.npcs.mixin.entity.passive.IEntityVillagerMixin;

public class VillagerWrapper<T extends EntityVillager> extends EntityLivingWrapper<T> implements IVillager<T> {

	public VillagerWrapper(T entity) { super(entity); }

	@Override
	public String getCareer() { return entity.getProfessionForge().getCareer(((IEntityVillagerMixin) entity).getCareerId()).getName(); }

	@Override
	@SuppressWarnings("deprecation")
	public int getProfession() { return entity.getProfession(); }

	@Override
	public MerchantRecipeList getRecipes(IPlayer<?> player) { return entity.getRecipes(player.getMCEntity()); }

	@Override
	public int getType() { return EntityType.VILLAGER.get(); }

	@Override
	public IInventory getVillagerInventory() { return entity.getVillagerInventory(); }

	@Override
	public boolean typeOf(int type) { return type == EntityType.VILLAGER.get() || super.typeOf(type); }

}
