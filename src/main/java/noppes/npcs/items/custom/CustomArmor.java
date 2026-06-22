package noppes.npcs.items.custom;

import java.util.*;

import noppes.npcs.CustomTabs;
import noppes.npcs.mixin.item.IItemArmorMixin;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.client.renderer.obj.ModelBuffer;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;

public class CustomArmor extends ItemArmor implements ICustomElement {

	public static @Nonnull ArmorMaterial getMaterialArmor(@Nonnull String materialName) {
		switch (materialName.toLowerCase()) {
			case "diamond": return ArmorMaterial.DIAMOND;
			case "chain": return ArmorMaterial.CHAIN;
			case "iron": return ArmorMaterial.IRON;
			case "gold": return ArmorMaterial.GOLD;
			default: return ArmorMaterial.LEATHER;
		}
	}
	
	public static @Nonnull EntityEquipmentSlot getSlotEquipment(@Nonnull String slotName) {
		switch (slotName.toLowerCase()) {
			case "head": return EntityEquipmentSlot.HEAD;
			case "chest": return EntityEquipmentSlot.CHEST;
			case "legs": return EntityEquipmentSlot.LEGS;
			default: return EntityEquipmentSlot.FEET;
		}
	}

	protected final Map<EnumParts, List<String>> parts = new HashMap<>();
	protected final Map<TransformType, Optional<TRSRTransformation>> cameraData = new HashMap<>();
	protected final @Nonnull NBTTagCompound nbtData;
	protected final ItemStack repairItemStack;
	protected final int enchantability;
	public ResourceLocation objModel = null;

	public CustomArmor(@Nonnull ArmorMaterial materialIn, int renderIndexIn, @Nonnull EntityEquipmentSlot equipmentSlotIn,
					   int maxStDam, int damReAmt, int enchantabilityIn, float tough, @Nonnull NBTTagCompound nbtItem) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
		nbtData = nbtItem;
		String name = "custom_" + nbtItem.getString("RegistryName") + "_" + equipmentSlotIn.name().toLowerCase();
		setRegistryName(CustomNpcs.MODID, name);
		setUnlocalizedName(name);
		setCreativeTab(CustomTabs.ITEMS);
		if (nbtItem.hasKey("IsFull3D", 1) && nbtItem.getBoolean("IsFull3D")) { setFull3D(); }
		if (maxStDam > 1) { setMaxDamage(maxStDam); }
		if (damReAmt > 0) { ((IItemArmorMixin) this).setDefense(damReAmt); }
		if (tough > 0.0f) { ((IItemArmorMixin) this).setToughness(tough); }
		if (nbtItem.hasKey("RepairItem", 10)) { repairItemStack = new ItemStack(nbtItem.getCompoundTag("RepairItem")); }
		else { repairItemStack = materialIn.getRepairItemStack(); }
		if (enchantabilityIn > 0) { enchantability = enchantabilityIn; }
		else { enchantability = materialIn.getEnchantability(); }
		if (nbtData.hasKey("OBJData", 10)) {
			NBTTagCompound data = nbtData.getCompoundTag("OBJData");
			NBTTagList tagList = data.getTagList("Head Mesh Names", 8);
			List<String> listHead = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listHead.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.HEAD, listHead);
			parts.put(EnumParts.MOHAWK, listHead);
			tagList = data.getTagList("Body Mesh Names", 8);
			List<String> listBody = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listBody.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.BODY, listBody);
			tagList = data.getTagList("Arm Right Mesh Names", 8);
			List<String> listArmRight = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listArmRight.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.ARM_RIGHT, listArmRight);
			tagList = data.getTagList("Wrist Right Mesh Names", 8);
			List<String> listWristRight = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listWristRight.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.WRIST_RIGHT, listWristRight);
			tagList = data.getTagList("Arm Left Mesh Names", 8);
			List<String> listArmLeft = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listArmLeft.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.ARM_LEFT, listArmLeft);
			tagList = data.getTagList("Wrist Left Mesh Names", 8);
			List<String> listWristLeft = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listWristLeft.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.WRIST_LEFT, listWristLeft);
			tagList = data.getTagList("Belt Mesh Names", 8);
			List<String> listBelt = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listBelt.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.BELT, listBelt);
			tagList = data.getTagList("Leg Right Mesh Names", 8);
			List<String> listLegRight = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listLegRight.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.LEG_RIGHT, listLegRight);
			tagList = data.getTagList("Foot Right Mesh Names", 8);
			List<String> listFootRight = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listFootRight.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.FOOT_RIGHT, listFootRight);
			tagList = data.getTagList("Leg Left Mesh Names", 8);
			List<String> listLegLeft = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listLegLeft.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.LEG_LEFT, listLegLeft);
			tagList = data.getTagList("Foot Left Mesh Names", 8);
			List<String> listFootLeft = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listFootLeft.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.FOOT_LEFT, listFootLeft);
			tagList = data.getTagList("Boot Right Mesh Names", 8);
			List<String> listBootRight = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listBootRight.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.FEET_RIGHT, listBootRight);
			tagList = data.getTagList("Boot Left Mesh Names", 8);
			List<String> listBootLeft = new ArrayList<>();
			for (int i = 0; i < tagList.tagCount(); i++) { listBootLeft.add(tagList.getStringTagAt(i)); }
			parts.put(EnumParts.FEET_LEFT, listBootLeft);
			objModel = new ResourceLocation(CustomNpcs.MODID, "models/armor/" + nbtItem.getString("RegistryName").toLowerCase() + ".obj");
			if (Util.instance.getSide() == Side.CLIENT) { createCameraData(); }
		}
		else if (nbtData.hasKey("OBJData", 9)) { // OLD
			NBTTagList data = nbtData.getTagList("OBJData", 10);
			for (EnumParts part : EnumParts.values()) {
				NBTTagCompound nbt = null;
				switch (part) {
					case HEAD:
                    case MOHAWK: {
						nbt = data.getCompoundTagAt(0);
						break;
					}
                    case BODY: {
						nbt = data.getCompoundTagAt(1);
						break;
					}
					case ARM_RIGHT: {
						nbt = data.getCompoundTagAt(2);
						break;
					}
					case ARM_LEFT: {
						nbt = data.getCompoundTagAt(3);
						break;
					}
					case BELT: {
						nbt = data.getCompoundTagAt(4);
						break;
					}
					case LEG_RIGHT: {
						nbt = data.getCompoundTagAt(5);
						break;
					}
					case LEG_LEFT: {
						nbt = data.getCompoundTagAt(6);
						break;
					}
					case FEET_RIGHT: {
						nbt = data.getCompoundTagAt(7);
						break;
					}
					case FEET_LEFT: {
						nbt = data.getCompoundTagAt(8);
						break;
					}
					case WRIST_RIGHT: {
						nbt = data.getCompoundTagAt(9);
						break;
					}
					case WRIST_LEFT: {
						nbt = data.getCompoundTagAt(10);
						break;
					}
					case FOOT_RIGHT: {
						nbt = data.getCompoundTagAt(11);
						break;
					}
					case FOOT_LEFT: {
						nbt = data.getCompoundTagAt(12);
						break;
					}
					default: {
						break;
					}
				}
				if (nbt == null) { continue; }
				List<String> list = new ArrayList<>();
                for (int i = 0; i < nbt.getTagList("meshes", 8).tagCount(); i++) {
                    list.add(nbt.getTagList("meshes", 8).getStringTagAt(i));
                }
                parts.put(part, list);
			}
			objModel = new ResourceLocation(CustomNpcs.MODID, "models/armor/" + nbtItem.getString("RegistryName").toLowerCase() + ".obj");
			if (Util.instance.getSide() == Side.CLIENT) { createCameraData(); }
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(@Nonnull EntityLivingBase entity, @Nonnull ItemStack itemStack, @Nonnull EntityEquipmentSlot slot, @Nonnull ModelBiped defModel) {
		if (objModel != null) {
			return ModelBuffer.getOBJModelBiped(this, entity, defModel);
		}
		return super.getArmorModel(entity, itemStack, slot, defModel);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getArmorTexture(@Nonnull ItemStack stack, @Nonnull Entity entity, @Nonnull EntityEquipmentSlot slot, @Nonnull String type) {
		if (objModel != null) {
			return CustomNpcs.MODID + ":textures/items/null.png";
		}
		return CustomNpcs.MODID + ":textures/models/armor/" + nbtData.getString("RegistryName") + "_layer_"
				+ (slot == EntityEquipmentSlot.LEGS ? "2" : "1") + ".png";
	}

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName"); }

	@Override
	public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

	@Override
	public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
		ItemStack mat = repairItemStack;
		if (repairItemStack.isEmpty()) {
			mat = getArmorMaterial().getRepairItemStack();
		}
		if (!mat.isEmpty() && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) {
			return true;
		}
		return super.getIsRepairable(toRepair, repair);
	}

	@Override
	public int getItemEnchantability() {
		if (enchantability > 0) { return enchantability; }
		return super.getItemEnchantability();
	}

	public List<String> getMeshNames(EnumParts slot) {
		if (parts.containsKey(slot)) { return parts.get(slot); }
		return new ArrayList<>();
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
		if (showInCreative() && (tab == CustomTabs.ITEMS || tab == CreativeTabs.SEARCH)) {
			items.add(new ItemStack(this));
			if (tab == CustomTabs.ITEMS) { Util.instance.sort(items); }
		}
	}

	@SuppressWarnings("deprecation")
	private void createCameraData() {
		cameraData.clear();
		NBTTagCompound display = nbtData.hasKey("Display", 10) ? nbtData.getCompoundTag("Display") : new NBTTagCompound();

		NBTTagCompound head = display.hasKey("HEAD", 10) ? nbtData.getCompoundTag("HEAD") : new NBTTagCompound();
		NBTTagCompound chest = display.hasKey("CHEST", 10) ? nbtData.getCompoundTag("CHEST") : new NBTTagCompound();
		NBTTagCompound legs = display.hasKey("LEGS", 10) ? nbtData.getCompoundTag("LEGS") : new NBTTagCompound();
		NBTTagCompound feet = display.hasKey("FEET", 10) ? nbtData.getCompoundTag("FEET") : new NBTTagCompound();

		for (TransformType transformType : TransformType.values()) {
			Vector3f rotation = new Vector3f();
			Vector3f translation = new Vector3f();
			Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
			switch(transformType) {
				case THIRD_PERSON_LEFT_HAND: {
					switch(getEquipmentSlot()) {
						case CHEST: {
							if (!chest.hasKey("thirdperson_lefthand", 10)) {
								translation.z = 0.5f;
								scale.x = 0.5f;
								scale.y = 0.5f;
								scale.z = 0.5f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, chest.getCompoundTag("thirdperson_lefthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case LEGS: {
							if (!legs.hasKey("thirdperson_lefthand", 10)) {
								translation.x = -0.15f;
								translation.y = 0.35f;
								translation.z = 0.5f;
								scale.x = 0.65f;
								scale.y = 0.65f;
								scale.z = 0.65f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, legs.getCompoundTag("thirdperson_lefthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case FEET: {
							if (!feet.hasKey("thirdperson_lefthand", 10)) {
								rotation.x = 90.0f;
								rotation.y = 180.0f;
								translation.x = 1.15f;
								translation.y = 0.5f;
								translation.z = 0.5f;
								scale.x = 0.65f;
								scale.y = 0.65f;
								scale.z = 0.65f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, feet.getCompoundTag("thirdperson_lefthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						default: {
							if (!head.hasKey("thirdperson_lefthand", 10)) {
								rotation.y = 180.0f;
								translation.x = 1.0f;
								translation.y = -0.375f;
								translation.z = 0.5f;
								scale.x = 0.5f;
								scale.y = 0.5f;
								scale.z = 0.5f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, head.getCompoundTag("thirdperson_lefthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
					}
					break;
				}
				case THIRD_PERSON_RIGHT_HAND: {
					switch(getEquipmentSlot()) {
						case CHEST: {
							if (!chest.hasKey("thirdperson_righthand", 10)) {
								translation.x = 0.5f;
								translation.z = 0.5f;
								scale.x = 0.5f;
								scale.y = 0.5f;
								scale.z = 0.5f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, chest.getCompoundTag("thirdperson_righthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case LEGS: {
							if (!legs.hasKey("thirdperson_righthand", 10)) {
								translation.x = 0.5f;
								translation.y = 0.35f;
								translation.z = 0.5f;
								scale.x = 0.65f;
								scale.y = 0.65f;
								scale.z = 0.65f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, legs.getCompoundTag("thirdperson_righthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case FEET: {
							if (!feet.hasKey("firstperson_righthand", 10)) {
								rotation.x = 90.0f;
								rotation.y = 180.0f;
								translation.x = 0.5f;
								translation.y = 0.5f;
								translation.z = 0.5f;
								scale.x = 0.65f;
								scale.y = 0.65f;
								scale.z = 0.65f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, feet.getCompoundTag("firstperson_righthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						default: {
							if (!head.hasKey("thirdperson_righthand", 10)) {
								rotation.y = 180.0f;
								translation.x = 0.5f;
								translation.y = -0.375f;
								translation.z = 0.5f;
								scale.x = 0.5f;
								scale.y = 0.5f;
								scale.z = 0.5f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, head.getCompoundTag("thirdperson_righthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
					}
					break;
				}
				case FIRST_PERSON_LEFT_HAND: {
					switch(getEquipmentSlot()) {
						case CHEST: {
							if (!chest.hasKey("firstperson_lefthand", 10)) {
								rotation.y = 280.0f;
								translation.x = 0.57f;
								translation.y = 0.1f;
								translation.z = -0.085f;
								scale.x = 0.5f;
								scale.y = 0.5f;
								scale.z = 0.5f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, chest.getCompoundTag("firstperson_lefthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case LEGS: {
							if (!legs.hasKey("firstperson_lefthand", 10)) {
								rotation.y = 280.0f;
								translation.x = 0.65f;
								translation.y = 0.4f;
								translation.z = -0.085f;
								scale.x = 0.5f;
								scale.y = 0.5f;
								scale.z = 0.5f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, legs.getCompoundTag("firstperson_lefthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case FEET: {
							if (!feet.hasKey("firstperson_lefthand", 10)) {
								rotation.y = 280.0f;
								translation.x = 0.72f;
								translation.y = 0.435f;
								translation.z = -0.585f;
								scale.x = 0.85f;
								scale.y = 0.85f;
								scale.z = 0.85f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, feet.getCompoundTag("firstperson_lefthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						default: {
							if (!head.hasKey("firstperson_lefthand", 10)) {
								rotation.y = 280.0f;
								translation.x = 0.57f;
								translation.y = -0.225f;
								translation.z = -0.085f;
								scale.x = 0.5f;
								scale.y = 0.5f;
								scale.z = 0.5f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, head.getCompoundTag("firstperson_lefthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
					}
					break;
				}
				case FIRST_PERSON_RIGHT_HAND: {
					switch(getEquipmentSlot()) {
						case CHEST: {
							if (!chest.hasKey("firstperson_righthand", 10)) {
								rotation.y = 280.0f;
								translation.x = 0.85f;
								translation.y = -0.1f;
								translation.z = 0.2f;
								scale.x = 0.6f;
								scale.y = 0.6f;
								scale.z = 0.6f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, chest.getCompoundTag("firstperson_righthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case LEGS: {
							if (!legs.hasKey("firstperson_righthand", 10)) {
								rotation.y = 280.0f;
								translation.x = 0.95f;
								translation.y = 0.25f;
								translation.z = 0.2f;
								scale.x = 0.6f;
								scale.y = 0.6f;
								scale.z = 0.6f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, legs.getCompoundTag("firstperson_righthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case FEET: {
							if (!feet.hasKey("firstperson_righthand", 10)) {
								rotation.y = 280.0f;
								translation.x = 0.95f;
								translation.y = 0.4f;
								translation.z = 0.2f;
								scale.x = 0.85f;
								scale.y = 0.85f;
								scale.z = 0.85f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, feet.getCompoundTag("firstperson_righthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						default: {
							if (!head.hasKey("firstperson_righthand", 10)) {
								rotation.y = 280.0f;
								translation.x = 0.85f;
								translation.y = -0.5f;
								translation.z = 0.2f;
								scale.x = 0.6f;
								scale.y = 0.6f;
								scale.z = 0.6f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, head.getCompoundTag("firstperson_righthand"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
					}
					break;
				}
				case HEAD: {
					switch(getEquipmentSlot()) {
						case CHEST: {
							if (!chest.hasKey("head", 10)) {
								rotation.x = 270.0f;
								translation.x = 0.5f;
								translation.y = 1.0f;
								translation.z = 1.65f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, chest.getCompoundTag("head"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case LEGS: {
							if (!legs.hasKey("head", 10)) {
								rotation.x = 270.0f;
								translation.x = 0.5f;
								translation.y = 1.0f;
								translation.z = 1.0f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, legs.getCompoundTag("head"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case FEET: {
							if (!feet.hasKey("head", 10)) {
								rotation.y = 180.0f;
								translation.x = 0.5f;
								translation.y = 0.925f;
								translation.z = 0.4f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, feet.getCompoundTag("head"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						default: { break; }
					}
					break;
				}
				case GUI: {
					switch(getEquipmentSlot()) {
						case CHEST: {
							if (!chest.hasKey("gui", 10)) {
								rotation.x = 30.0f;
								rotation.y = 45.0f;
								translation.x = 0.49f;
								translation.y = -0.41f;
								scale.x = 0.9f;
								scale.y = 0.9f;
								scale.z = 0.9f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, chest.getCompoundTag("gui"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case LEGS: {
							if (!legs.hasKey("gui", 10)) {
								rotation.x = 30.0f;
								rotation.y = 45.0f;
								translation.x = 0.5f;
								translation.y = 0.05f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, legs.getCompoundTag("gui"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case FEET: {
							if (!feet.hasKey("gui", 10)) {
								rotation.x = 30.0f;
								rotation.y = 45.0f;
								translation.x = 0.5f;
								translation.y = 0.3f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, feet.getCompoundTag("gui"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						default: {
							if (!head.hasKey("gui", 10)) {
								rotation.x = 30.0f;
								rotation.y = 45.0f;
								translation.x = 0.5f;
								translation.y = -1.0f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, head.getCompoundTag("gui"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
					}
					break;
				}
				case GROUND: {
					switch(getEquipmentSlot()) {
						case CHEST: {
							if (!chest.hasKey("ground", 10)) {
								translation.x = 0.5f;
								translation.z = 0.5f;
								scale.x = 0.5f;
								scale.y = 0.5f;
								scale.z = 0.5f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, chest.getCompoundTag("ground"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case LEGS: {
							if (!legs.hasKey("ground", 10)) {
								translation.x = 0.5f;
								translation.y = 0.25f;
								translation.z = 0.5f;
								scale.x = 0.6f;
								scale.y = 0.6f;
								scale.z = 0.6f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, legs.getCompoundTag("ground"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case FEET: {
							if (!feet.hasKey("ground", 10)) {
								translation.x = 0.5f;
								translation.y = 0.35f;
								translation.z = 0.5f;
								scale.x = 0.65f;
								scale.y = 0.65f;
								scale.z = 0.65f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, feet.getCompoundTag("ground"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						default: {
							if (!head.hasKey("ground", 10)) {
								translation.x = 0.5f;
								translation.y = -0.375f;
								translation.z = 0.5f;
								scale.x = 0.5f;
								scale.y = 0.5f;
								scale.z = 0.5f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, head.getCompoundTag("ground"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
					}
					break;
				}
				case FIXED: {
					switch(getEquipmentSlot()) {
						case CHEST: {
							if (!chest.hasKey("fixed", 10)) {
								rotation.y = 180.0f;
								translation.x = 0.5f;
								translation.y = -0.65f;
								translation.z = 0.45f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, chest.getCompoundTag("fixed"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case LEGS: {
							if (!legs.hasKey("fixed", 10)) {
								rotation.y = 180.0f;
								translation.x = 0.5f;
								translation.y = 0.05f;
								translation.z = 0.475f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, legs.getCompoundTag("fixed"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						case FEET: {
							if (!feet.hasKey("fixed", 10)) {
								rotation.y = 180.0f;
								translation.x = 0.5f;
								translation.y = 0.2f;
								translation.z = 0.475f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, feet.getCompoundTag("fixed"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
						default: {
							if (!head.hasKey("fixed", 10)) {
								rotation.y = 180.0f;
								translation.x = 0.5f;
								translation.y = -0.85f;
								translation.z = 0.4f;
								scale.x = 0.75f;
								scale.y = 0.75f;
								scale.z = 0.75f;
							} else {
								Vector3f[] data = setOptional(rotation, translation, scale, head.getCompoundTag("fixed"));
								rotation = data[0];
								translation = data[1];
								scale = data[2];
							}
							break;
						}
					}
					break;
				}
				default: { break; } // NONE
			}
			cameraData.put(transformType, Optional.of(TRSRTransformation.from(new ItemTransformVec3f(rotation, translation, scale))));
		}
	}

	private Vector3f[] setOptional(Vector3f rotation, Vector3f translation, Vector3f scale, NBTTagCompound compound) {
		if (compound.hasKey("rotation", 9)) {
			NBTTagList list = compound.getTagList("rotation", 5);
			if (list.tagCount() > 0) { rotation.x = list.getFloatAt(0); }
			if (list.tagCount() > 1) { rotation.y = list.getFloatAt(1); }
			if (list.tagCount() > 2) { rotation.z = list.getFloatAt(2); }
		}
		if (compound.hasKey("translation", 9)) {
			NBTTagList list = compound.getTagList("translation", 5);
			if (list.tagCount() > 0) { translation.x = list.getFloatAt(0); }
			if (list.tagCount() > 1) { translation.y = list.getFloatAt(1); }
			if (list.tagCount() > 2) { translation.z = list.getFloatAt(2); }
		}
		if (compound.hasKey("scale", 9)) {
			NBTTagList list = compound.getTagList("scale", 5);
			if (list.tagCount() > 0) { scale.x = list.getFloatAt(0); }
			if (list.tagCount() > 1) { scale.y = list.getFloatAt(1); }
			if (list.tagCount() > 2) { scale.z = list.getFloatAt(2); }
		}
		return new Vector3f[] { rotation, translation, scale };
	}

	public Optional<TRSRTransformation> getOptional(TransformType transformType) { return cameraData.get(transformType); }
	
	@Override
	public int getElementType() {
		if (nbtData.hasKey("ItemType", 1)) { return nbtData.getByte("ItemType"); }
		return 3;
	}

	@Override
	public boolean showInCreative() {
		return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative");
	}

}
