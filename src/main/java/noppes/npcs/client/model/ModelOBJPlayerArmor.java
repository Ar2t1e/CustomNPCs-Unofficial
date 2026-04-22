package noppes.npcs.client.model;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.model.part.ModelOBJPart;
import noppes.npcs.client.renderer.ModelBuffer;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.items.custom.CustomArmor;
import noppes.npcs.reflection.client.renderer.entity.RenderPlayerReflection;

import javax.annotation.Nonnull;

public class ModelOBJPlayerArmor extends ModelBiped {

	public ResourceLocation objModel;
	public ResourceLocation mainTexture;
	public ModelRenderer bipedBelt;
	public ModelRenderer bipedRightFeet;
	public ModelRenderer bipedLeftFeet;
	private ModelOBJPart childRightArm;
	private ModelOBJPart childLeftArm;

	public ModelOBJPlayerArmor(CustomArmor armor) {
		super(0, 0, 128, 128);
		// Clear Basic Armor Pieces
		bipedHeadwear.cubeList.clear();
		bipedHeadwear.showModel = false;
		bipedHeadwear.isHidden = true;
		bipedHead.cubeList.clear();
		bipedBody.cubeList.clear();
		bipedLeftArm.cubeList.clear();
		bipedRightArm.cubeList.clear();
		bipedLeftLeg.cubeList.clear();
		bipedRightLeg.cubeList.clear();

		bipedBelt = new ModelRenderer(this);
		bipedRightFeet = new ModelRenderer(this);
		bipedLeftFeet = new ModelRenderer(this);
		objModel = armor.objModel;
		mainTexture = ModelBuffer.getMainOBJTexture(armor.objModel);

		addLayer(armor);
		setVisible(true);
	}

	public void addLayer(CustomArmor armor) {
		bipedHead.addChild(new ModelOBJPart(this, EnumParts.FEET_LEFT, armor.getMeshNames(EnumParts.HEAD), 0.0f, 1.5f, 0.0f));

		bipedBody.addChild(new ModelOBJPart(this, EnumParts.BODY, armor.getMeshNames(EnumParts.BODY), 0.0f, 1.5f, 0.0f));
		bipedBelt.addChild(new ModelOBJPart(this, EnumParts.BELT, armor.getMeshNames(EnumParts.BELT), 0.0f, 1.5f, 0.0f));

		List<String> listAR = armor.getMeshNames(EnumParts.ARM_RIGHT);
		listAR.addAll(armor.getMeshNames(EnumParts.WRIST_RIGHT));
		childRightArm = new ModelOBJPart(this, EnumParts.ARM_RIGHT, listAR, 0.3175f, 1.375f, 0.0f);
		bipedRightArm.addChild(childRightArm);
		
		List<String> listAL = armor.getMeshNames(EnumParts.ARM_LEFT);
		listAL.addAll(armor.getMeshNames(EnumParts.WRIST_LEFT));
		childLeftArm = new ModelOBJPart(this, EnumParts.ARM_LEFT, listAL, -0.3175f, 1.375f, 0.0f);
		bipedLeftArm.addChild(childLeftArm);

		List<String> listLR = armor.getMeshNames(EnumParts.LEG_RIGHT);
		listLR.addAll(armor.getMeshNames(EnumParts.FEET_RIGHT));
		bipedRightLeg.addChild(new ModelOBJPart(this, EnumParts.LEG_RIGHT, listLR, 0.125f, 0.75f, 0.0f));

		List<String> listLL = armor.getMeshNames(EnumParts.LEG_LEFT);
		listLL.addAll(armor.getMeshNames(EnumParts.FEET_LEFT));
		bipedLeftLeg.addChild(new ModelOBJPart(this, EnumParts.LEG_LEFT, listLL, -0.115f, 0.75f, 0.0f));

		bipedRightFeet.addChild(new ModelOBJPart(this, EnumParts.FEET_RIGHT, armor.getMeshNames(EnumParts.FEET_RIGHT), 0.125f, 0.75f, 0.0f));
		bipedLeftFeet.addChild(new ModelOBJPart(this, EnumParts.FEET_LEFT, armor.getMeshNames(EnumParts.FEET_LEFT), -0.115f, 0.75f, 0.0f));
	}

	public void render(@Nonnull Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
		GlStateManager.pushMatrix();
		if (isChild) {
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
			bipedHead.render(scale);
			GlStateManager.popMatrix();
			
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
		} else {
			if (entityIn.isSneaking()) {
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}
			bipedHead.render(scale);
		}
		bipedBody.render(scale);
		bipedRightArm.render(scale);
		bipedLeftArm.render(scale);
		
		bipedRightLeg.render(scale);
		bipedLeftLeg.render(scale);

		bipedBelt.showModel = bipedRightLeg.showModel;
		bipedBelt.isHidden = bipedRightLeg.isHidden;
		bipedBelt.render(scale);
		bipedRightFeet.render(scale);
		bipedLeftFeet.render(scale);
		GlStateManager.popMatrix();
	}

	public void reset(EntityLivingBase entity) {
		boolean smallArms = false;
		if (entity instanceof EntityPlayerSP) {
			Minecraft mc = Minecraft.getMinecraft();
			Render<?> rp = mc.getRenderManager().getEntityRenderObject(entity);
			if (rp instanceof RenderPlayer) {
				smallArms = RenderPlayerReflection.getSmallArms((RenderPlayer) rp);
			}
		}

		// Initially nothing is visible
		setHidden(true);
		setVisible(false);

		ItemStack headItem = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if (headItem.getItem() instanceof CustomArmor && ((CustomArmor) headItem.getItem()).objModel != null &&
				((CustomArmor) headItem.getItem()).objModel.equals(objModel)) {
			bipedHead.isHidden = false;
			bipedHead.showModel = true;
		}
		ItemStack chestItem = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (chestItem.getItem() instanceof CustomArmor
				&& ((CustomArmor) chestItem.getItem()).objModel.equals(objModel)) {
			bipedBody.isHidden = false;
			bipedLeftArm.isHidden = false;
			bipedRightArm.isHidden = false;
			bipedBody.showModel = true;
			bipedLeftArm.showModel = true;
			bipedRightArm.showModel = true;

			childLeftArm.smallArms = smallArms;
			childRightArm.smallArms = smallArms;
		}

		ItemStack legsItem = entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
		if (legsItem.getItem() instanceof CustomArmor
				&& ((CustomArmor) legsItem.getItem()).objModel.equals(objModel)) {
			bipedBelt.isHidden = false;
			bipedLeftLeg.isHidden = false;
			bipedRightLeg.isHidden = false;
			bipedBelt.showModel = true;
			bipedLeftLeg.showModel = true;
			bipedRightLeg.showModel = true;
		}

		ItemStack feetItem = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		if (feetItem.getItem() instanceof CustomArmor
				&& ((CustomArmor) feetItem.getItem()).objModel.equals(objModel)) {
			bipedLeftFeet.isHidden = false;
			bipedRightFeet.isHidden = false;
			bipedLeftFeet.showModel = true;
			bipedRightFeet.showModel = true;
		}

		isSneak = entity.isSneaking();
		isRiding = entity.isRiding();
		isChild = entity.isChild();

	}

	private void resetPos(ModelRenderer part, ModelRenderer source) {
		part.offsetX = source.offsetX;
		part.offsetY = source.offsetY;
		part.offsetZ = source.offsetZ;
		copyModelAngles(source, part);
	}

	public void setHidden(boolean hidden) {
		bipedHead.isHidden = hidden;
		bipedLeftArm.isHidden = hidden;
		bipedRightArm.isHidden = hidden;
		bipedBody.isHidden = hidden;
		bipedLeftLeg.isHidden = hidden;
		bipedRightLeg.isHidden = hidden;

		bipedBelt.isHidden = hidden;
		bipedLeftFeet.isHidden = hidden;
		bipedRightFeet.isHidden = hidden;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, @Nonnull Entity entityIn) {
		Render<?> re = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entityIn);
		ModelBiped source = null;
		if (re instanceof RenderPlayer) {
			source = ((RenderPlayer) re).getMainModel();
		}
		if (source == null) {
			return;
		}
		reset((EntityLivingBase) entityIn);
		resetPos(bipedHead, source.bipedHead);
		resetPos(bipedBody, source.bipedBody);
		resetPos(bipedBelt, source.bipedBody);
		resetPos(bipedLeftArm, source.bipedLeftArm);
		resetPos(bipedRightArm, source.bipedRightArm);
		resetPos(bipedLeftLeg, source.bipedLeftLeg);
		resetPos(bipedRightLeg, source.bipedRightLeg);

		resetPos(bipedBelt, source.bipedBody);
		resetPos(bipedLeftFeet, source.bipedLeftLeg);
		resetPos(bipedRightFeet, source.bipedRightLeg);
	}

	public void setVisible(boolean visible) {
		bipedHead.showModel = visible;
		bipedBody.showModel = visible;
		bipedRightArm.showModel = visible;
		bipedLeftArm.showModel = visible;
		bipedRightLeg.showModel = visible;
		bipedLeftLeg.showModel = visible;

		bipedBelt.showModel = visible;
		bipedLeftFeet.showModel = visible;
		bipedRightFeet.showModel = visible;
	}

}
