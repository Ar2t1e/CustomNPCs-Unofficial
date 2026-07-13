package noppes.npcs.potions;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.CustomPotionEvent;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.util.ValueUtil;

public class CustomPotion extends Potion implements ICustomElement {

	protected final @Nonnull NBTTagCompound nbtData;
	protected final @Nonnull ResourceLocation resource;
	protected final @Nonnull ItemStack cureItem;
	protected final Map<IAttribute, AttributeModifier> attributeModifierMap = new HashMap<>(); // RangedAttribute, AttributeModifier

	public CustomPotion(@Nonnull ResourceLocation name, @Nonnull NBTTagCompound nbtPotion) {
		super(nbtPotion.getBoolean("IsBadEffect"), nbtPotion.getInteger("LiquidColor"));
		nbtData = nbtPotion;
		setRegistryName(name);
		setPotionName("effect." + name.getResourcePath());

		if (nbtPotion.getBoolean("IsBeneficial")) { setBeneficial(); }
		if (nbtPotion.hasKey("CureItem", 10)) { cureItem = new ItemStack(nbtPotion.getCompoundTag("CureItem")); }
		else { cureItem = ItemStack.EMPTY; }
		if (nbtPotion.hasKey("Modifiers", 9)) {
			attributeModifierMap.clear();
			for (int i = 0; i < nbtPotion.getTagList("Modifiers", 10).tagCount(); i++) {
				NBTTagCompound potionModifier = nbtPotion.getTagList("Modifiers", 10).getCompoundTagAt(i);
				try {
					double d = potionModifier.getDouble("AttributeDefValue");
					double m = potionModifier.getDouble("AttributeMinValue");
					double n = potionModifier.getDouble("AttributeMaxValue");
					UUID uuid;
					try {
						uuid = UUID.fromString(potionModifier.getString("UUID"));
					} catch (Exception e) {
						uuid = UUID.randomUUID();
					}
					attributeModifierMap.put(
							new RangedAttribute(null, potionModifier.getString("AttributeName"), ValueUtil.correctDouble(d, m, n), ValueUtil.min(m, n), ValueUtil.max(m, n)),
							new AttributeModifier(uuid, getName(), potionModifier.getDouble("Amount"), potionModifier.getInteger("Operation")));
				}
				catch (Exception e) { LogWriter.error("Error create or added attribute modifier #" + i + " to custom potion: \"" + getCustomName() + "\"", e); }
			}
		}
		resource = new ResourceLocation(CustomNpcs.MODID, "textures/potions/" + name + ".png");
	}

	@Override
	public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource,
							 @Nonnull EntityLivingBase entityLivingBaseIn, int amplifier, double health) {
		EventHooks.onCustomPotionEvent(new CustomPotionEvent.AffectEntity(this, source, indirectSource,
				entityLivingBaseIn, amplifier, health), EnumScriptType.POTION_AFFECT);
	}

	@Override
	public void applyAttributesModifiersToEntity(@Nonnull EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		for (Entry<IAttribute, AttributeModifier> entry : attributeModifierMap.entrySet()) {
			IAttributeInstance iattributeinstance = attributeMapIn.getAttributeInstance(entry.getKey());
            AttributeModifier attributemodifier = entry.getValue();
            iattributeinstance.removeModifier(attributemodifier);
            iattributeinstance.applyModifier(new AttributeModifier(attributemodifier.getID(), getName() + " " + amplifier, getAttributeModifierAmount(amplifier, attributemodifier), attributemodifier.getOperation()));
        }
	}

	@SideOnly(Side.CLIENT)
	public @Nonnull Map<IAttribute, AttributeModifier> getAttributeModifierMap() { return attributeModifierMap; }

	@Override
	public @Nonnull java.util.List<net.minecraft.item.ItemStack> getCurativeItems() {
		List<ItemStack> ret = new ArrayList<>();
		if (!cureItem.isEmpty()) { ret.add(cureItem); }
		else { ret.add(new ItemStack(Items.MILK_BUCKET)); }
		return ret;
	}

	@Override
	public boolean hasStatusIcon() {
		return nbtData.hasKey("hasStatusIcon", 1) && nbtData.getBoolean("hasStatusIcon");
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		int step = nbtData.hasKey("Duration", 3) ? nbtData.getInteger("Duration") : 10;
		boolean isReady = duration % step == 0;
		if (isReady || duration % 10 == 0) {
			CustomPotionEvent.IsReadyEvent event = new CustomPotionEvent.IsReadyEvent(this, isReady, duration, amplifier);
			EventHooks.onCustomPotionEvent(event, EnumScriptType.POTION_IS_READY);
			isReady = event.isReady;
		}
		return isReady;
	}

	@Override
	public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int amplifier) {
		EventHooks.onCustomPotionEvent(new CustomPotionEvent.PerformEffect(this, entityLivingBaseIn, amplifier), EnumScriptType.POTION_PERFORM);
	}

	@Override
	public @Nonnull Potion registerPotionAttributeModifier(@Nonnull IAttribute attribute, @Nonnull String uniqueId, double amount, int operation) {
		AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(uniqueId), getName(), amount, operation);
		attributeModifierMap.put(attribute, attributemodifier);
		return this;
	}

	@Override
	public void removeAttributesModifiersFromEntity(@Nonnull EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		EventHooks.onCustomPotionEvent(new CustomPotionEvent.EndEffect(this, entityLivingBaseIn, amplifier), EnumScriptType.POTION_END);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(@Nonnull PotionEffect effect, @Nonnull Gui gui, int x, int y, float z, float alpha) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
		Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(@Nonnull PotionEffect effect, @Nonnull Gui gui, int x, int y, float z) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
		Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
	}

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName"); }

	@Override
	public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

	@Override
	public int getElementType() {
		if (nbtData.hasKey("ItemType", 1)) { return nbtData.getByte("ItemType"); }
		return 7;
	}

	@Override
	public boolean showInCreative() { return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative"); }

}
