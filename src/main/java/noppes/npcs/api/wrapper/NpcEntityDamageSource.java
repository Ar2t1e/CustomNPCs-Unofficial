package noppes.npcs.api.wrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.IEntityDamageSource;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;

import java.util.Objects;

public class NpcEntityDamageSource extends EntityDamageSource implements IEntityDamageSource {

	public Entity indirectEntity;
	public boolean projectile = false;
	public boolean explosion = false;
	public String deadMessage = "";

	public NpcEntityDamageSource(String damageTypeIn, IEntity<?> damageSourceEntityIn) {
		super(damageTypeIn, damageSourceEntityIn.getMCEntity());
		if (damageTypeIn.isEmpty()) { damageTypeIn = CustomNpcs.MODID + ":npc"; }
		damageType = damageTypeIn;
	}

	@Override
	public String getDeadMessage() { return deadMessage; }

	@Nonnull
	public ITextComponent getDeathMessage(@Nonnull EntityLivingBase entity) {
		ITextComponent entitySourceName = new TextComponentString("Empty");
		ItemStack stack = ItemStack.EMPTY;
		if (indirectEntity instanceof EntityLivingBase) {
			entitySourceName = indirectEntity.getDisplayName();
			stack = ((EntityLivingBase) indirectEntity).getHeldItemMainhand();
		} else if (damageSourceEntity instanceof EntityLivingBase) {
			entitySourceName = damageSourceEntity.getDisplayName();
			stack = ((EntityLivingBase) damageSourceEntity).getHeldItemMainhand();
		}
		if (!deadMessage.isEmpty()) {
			return new TextComponentTranslation(deadMessage,
                    entity.getDisplayName(), entitySourceName,
                    new TextComponentTranslation(damageType).getFormattedText(),
                    stack.getTextComponent());
		}
		String s = "death.attack." + damageType;
		String s1 = s + ".item";
		ITextComponent ts1 = new TextComponentTranslation(s1, entity.getDisplayName(), entitySourceName, stack.getTextComponent());
		return !stack.isEmpty() && stack.hasDisplayName() && ts1.getFormattedText().equals(s1) ? ts1
				: new TextComponentTranslation(s, entity.getDisplayName(), entitySourceName);
	}

	@Override
	public IEntity<?> getIImmediateSource() {
		return this.indirectEntity == null ? null : Objects.requireNonNull(NpcAPI.Instance()).getIEntity(this.indirectEntity);
	}

	@Nullable
	public Entity getImmediateSource() { return this.indirectEntity; }

	@Override
	public IEntity<?> getITrueSource() {
		return this.damageSourceEntity == null ? null : Objects.requireNonNull(NpcAPI.Instance()).getIEntity(this.damageSourceEntity);
	}

	@Override
	public Entity getTrueSource() {
		return this.damageSourceEntity;
	}

	@Override
	public String getType() {
		return this.damageType;
	}

	@Override
	public void setDeadMessage(String message) {
		if (message == null) { message = ""; }
		deadMessage = message;
	}

	@Override
	public void setImmediateSource(IEntity<?> entity) {
		indirectEntity = entity == null ? null : entity.getMCEntity();
	}

	@Override
	public void setTrueSource(IEntity<?> entity) {
		damageSourceEntity = entity == null ? null : entity.getMCEntity();
	}

	@Override
	public void setType(String damageTypeIn) {
		if (damageTypeIn == null || damageTypeIn.isEmpty()) { damageTypeIn = CustomNpcs.MODID + ":npc"; }
		damageType = damageTypeIn;
	}

}
