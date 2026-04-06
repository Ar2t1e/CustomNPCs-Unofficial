package noppes.npcs.api.entity;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.trading.MerchantOffers;

public interface IVillager<T extends Mob> extends IMob<T> {

    MerchantOffers getMCRecipes();

    Container getMCVillagerContainer();

    String getProfession();

    String villagerType();

}
