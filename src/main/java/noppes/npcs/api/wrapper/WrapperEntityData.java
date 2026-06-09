package noppes.npcs.api.wrapper;

import java.lang.reflect.Field;
import java.util.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.handler.capability.IWrapperEntityDataHandler;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.mixin.entity.IEntityMixin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WrapperEntityData implements IWrapperEntityDataHandler, ICapabilityProvider {

	@CapabilityInject(IWrapperEntityDataHandler.class)
	public static Capability<IWrapperEntityDataHandler> ENTITYDATA_CAPABILITY;
	private static final ResourceLocation key = new ResourceLocation(CustomNpcs.MODID, "entitydata");

	public static IEntity<?> get(Entity entity) {
		if (entity == null || entity.world == null) {
			return null;
		}
		WrapperEntityData data = (WrapperEntityData) entity.getCapability(ENTITYDATA_CAPABILITY, null);
		if (data == null || data.base == null) {
			if (entity instanceof EntityPlayer) {
				PlayerData playerData = PlayerData.get((EntityPlayer) entity);
				if (playerData == null || playerData.scriptData == null) {
					if (ClientProxy.iPlayer == null) { ClientProxy.iPlayer = new PlayerWrapper<>((EntityPlayer) entity); }
					if (data != null && data.base == null) { data.base = ClientProxy.iPlayer; }
					return ClientProxy.iPlayer;
				}
				else {
					if (data != null && data.base == null) { data.base = playerData.scriptData.getIPlayer(); }
					return playerData.scriptData.getIPlayer();
				}
			}
			LogWriter.warn("Unable to get EntityData for " + entity);
			WrapperEntityData ret = WrapperEntityData.getData(entity);
			CapabilityDispatcher capabilities = ((IEntityMixin) entity).getCapabilities();
			if (capabilities != null) {
				// "capabilities" does not want to be converted to the created mixin interface under any circumstances
				Field fieldCaps = null;
				for (Field f : capabilities.getClass().getDeclaredFields()) {
					if (f.getName().equals("caps")) {
						fieldCaps = f;
						break;
					}
				}
				if (fieldCaps != null) {
					try {
						fieldCaps.setAccessible(true);
						ICapabilityProvider[] caps = (ICapabilityProvider[]) fieldCaps.get(capabilities);
						if (caps != null) {
							List<ICapabilityProvider> list = new ArrayList<>();
							Collections.addAll(list, caps);
							list.add(ret);
							fieldCaps.set(capabilities, list.toArray(new ICapabilityProvider[0]));
						}
					}
					catch (Exception e) { LogWriter.error(e); }
				}
			}
			else {
				Map<ResourceLocation, ICapabilityProvider> m = new HashMap<>();
				m.put(WrapperEntityData.key, ret);
				((IEntityMixin) entity).setCapabilities(new CapabilityDispatcher(m, null));
			}
			return ret.base;
		}
		return data.base;
	}

	public static WrapperEntityData getData(Entity entity) {
		if (entity == null) {
			return null;
		}
		if (entity instanceof EntityPlayer) {
			return new WrapperEntityData(new PlayerWrapper<>((EntityPlayer) entity));
		}
		if (PixelmonHelper.isPixelmon(entity)) {
			return new WrapperEntityData(new PixelmonWrapper<>((EntityTameable) entity));
		}
		if (entity instanceof EntityVillager) {
			return new WrapperEntityData(new VillagerWrapper<>((EntityVillager) entity));
		}
		if (entity instanceof EntityAnimal) {
			return new WrapperEntityData(new AnimalWrapper<>((EntityAnimal) entity));
		}
		if (entity instanceof EntityMob) {
			return new WrapperEntityData(new MonsterWrapper<>((EntityMob) entity));
		}
		if (entity instanceof EntityLiving) {
			return new WrapperEntityData(new EntityLivingWrapper<>((EntityLiving) entity));
		}
		if (entity instanceof EntityLivingBase) {
			return new WrapperEntityData(new EntityLivingBaseWrapper<>((EntityLivingBase) entity));
		}
		if (entity instanceof EntityItem) {
			return new WrapperEntityData(new EntityItemWrapper<>((EntityItem) entity));
		}
		if (entity instanceof EntityProjectile) {
			return new WrapperEntityData(new ProjectileWrapper<>((EntityProjectile) entity));
		}
		if (entity instanceof EntityThrowable) {
			return new WrapperEntityData(new ThrowableWrapper<>((EntityThrowable) entity));
		}
		if (entity instanceof EntityArrow) {
			return new WrapperEntityData(new ArrowWrapper<>((EntityArrow) entity));
		}
		return new WrapperEntityData(new EntityWrapper<>(entity));
	}

	public static void register(AttachCapabilitiesEvent<Entity> event) {
		if (CustomNpcs.EnableScripting) { event.addCapability(WrapperEntityData.key, getData(event.getObject())); }
	}

	private static void setTempData(WrapperEntityData oldData, WrapperEntityData newData) {
		if (oldData == null || newData == null || oldData.base == null || newData.base == null) {
			return;
		}
        oldData.base.getTempdata().getKeys();
        for (String key : oldData.base.getTempdata().getKeys()) {
            try { newData.base.getTempdata().put(key, oldData.base.getTempdata().get(key)); }
			catch (Exception e) { LogWriter.error(e); }
        }
    }

	public IEntity<?> base;

	public WrapperEntityData() {
	}

	public WrapperEntityData(IEntity<?> base) {
		this.base = base;
	}

	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		if (hasCapability(capability, facing)) {return (T) this;}
		return null;
	}

	@Override
	public NBTTagCompound getNBT() {
		return new NBTTagCompound();
	}

	public boolean hasCapability(@Nullable Capability<?> capability, EnumFacing facing) {
		return capability != null && capability == ENTITYDATA_CAPABILITY;
	}

	@Override
	public void setNBT(NBTTagCompound compound) {
	}

}
