package noppes.npcs.client.model.part.legs;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.client.model.part.ModelData;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityNPCInterface;

public class ModelSpiderLegs extends ModelRenderer {

	private final ModelBiped base;
	private final ModelRenderer spiderBody;
	private final ModelRenderer spiderLeg1;
	private final ModelRenderer spiderLeg2;
	private final ModelRenderer spiderLeg3;
	private final ModelRenderer spiderLeg4;
	private final ModelRenderer spiderLeg5;
	private final ModelRenderer spiderLeg6;
	private final ModelRenderer spiderLeg7;
	private final ModelRenderer spiderLeg8;
	private final ModelRenderer spiderNeck;

	public ModelSpiderLegs(ModelBiped baseIn) {
		super(baseIn);
		base = baseIn;
		float var1 = 0.0f;
		byte var2 = 15;
		(spiderNeck = new ModelRenderer(baseIn, 0, 0)).addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6, var1);
		spiderNeck.setRotationPoint(0.0f, var2, 2.0f);
		addChild(spiderNeck);
		(spiderBody = new ModelRenderer(baseIn, 0, 12)).addBox(-5.0f, -4.0f, -6.0f, 10, 8, 12, var1);
		spiderBody.setRotationPoint(0.0f, var2, 11.0f);
		addChild(spiderBody);
		(spiderLeg1 = new ModelRenderer(baseIn, 18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, var1);
		spiderLeg1.setRotationPoint(-4.0f, var2, 4.0f);
		addChild(spiderLeg1);
		(spiderLeg2 = new ModelRenderer(baseIn, 18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, var1);
		spiderLeg2.setRotationPoint(4.0f, var2, 4.0f);
		addChild(spiderLeg2);
		(spiderLeg3 = new ModelRenderer(baseIn, 18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, var1);
		spiderLeg3.setRotationPoint(-4.0f, var2, 3.0f);
		addChild(spiderLeg3);
		(spiderLeg4 = new ModelRenderer(baseIn, 18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, var1);
		spiderLeg4.setRotationPoint(4.0f, var2, 3.0f);
		addChild(spiderLeg4);
		(spiderLeg5 = new ModelRenderer(baseIn, 18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, var1);
		spiderLeg5.setRotationPoint(-4.0f, var2, 2.0f);
		addChild(spiderLeg5);
		(spiderLeg6 = new ModelRenderer(baseIn, 18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, var1);
		spiderLeg6.setRotationPoint(4.0f, var2, 2.0f);
		addChild(spiderLeg6);
		(spiderLeg7 = new ModelRenderer(baseIn, 18, 0)).addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, var1);
		spiderLeg7.setRotationPoint(-4.0f, var2, 1.0f);
		addChild(spiderLeg7);
		(spiderLeg8 = new ModelRenderer(baseIn, 18, 0)).addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, var1);
		spiderLeg8.setRotationPoint(4.0f, var2, 1.0f);
		addChild(spiderLeg8);
	}

	public void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(ModelData data, float par1, float par2, float ignoredPar3, float ignoredPar4, float ignoredPar5, float ignoredPar6, Entity entity) {
		rotateAngleX = 0.0f;
		rotationPointY = 0.0f;
		rotationPointZ = 0.0f;
		spiderBody.rotationPointY = 15.0f;
		spiderBody.rotationPointZ = 11.0f;
		spiderNeck.rotateAngleX = 0.0f;
		float var8 = 0.7853982f;
		spiderLeg1.rotateAngleZ = -var8;
		spiderLeg2.rotateAngleZ = var8;
		spiderLeg3.rotateAngleZ = -var8 * 0.74f;
		spiderLeg4.rotateAngleZ = var8 * 0.74f;
		spiderLeg5.rotateAngleZ = -var8 * 0.74f;
		spiderLeg6.rotateAngleZ = var8 * 0.74f;
		spiderLeg7.rotateAngleZ = -var8;
		spiderLeg8.rotateAngleZ = var8;
		float var9 = -0.0f;
		float var10 = 0.3926991f;
		spiderLeg1.rotateAngleY = var10 * 2.0f + var9;
		spiderLeg2.rotateAngleY = -var10 * 2.0f - var9;
		spiderLeg3.rotateAngleY = var10 + var9;
		spiderLeg4.rotateAngleY = -var10 - var9;
		spiderLeg5.rotateAngleY = -var10 + var9;
		spiderLeg6.rotateAngleY = var10 - var9;
		spiderLeg7.rotateAngleY = -var10 * 2.0f + var9;
		spiderLeg8.rotateAngleY = var10 * 2.0f - var9;
		float var11 = -(MathHelper.cos(par1 * 0.6662f * 2.0f + 0.0f) * 0.4f) * par2;
		float var12 = -(MathHelper.cos(par1 * 0.6662f * 2.0f + 3.1415927f) * 0.4f) * par2;
		float var13 = -(MathHelper.cos(par1 * 0.6662f * 2.0f + 1.5707964f) * 0.4f) * par2;
		float var14 = -(MathHelper.cos(par1 * 0.6662f * 2.0f + 4.712389f) * 0.4f) * par2;
		float var15 = Math.abs(MathHelper.sin(par1 * 0.6662f + 0.0f) * 0.4f) * par2;
		float var16 = Math.abs(MathHelper.sin(par1 * 0.6662f + 3.1415927f) * 0.4f) * par2;
		float var17 = Math.abs(MathHelper.sin(par1 * 0.6662f + 1.5707964f) * 0.4f) * par2;
		float var18 = Math.abs(MathHelper.sin(par1 * 0.6662f + 4.712389f) * 0.4f) * par2;
		spiderLeg1.rotateAngleY += var11;
		spiderLeg2.rotateAngleY -= var11;
		spiderLeg3.rotateAngleY += var12;
		spiderLeg4.rotateAngleY -= var12;
		spiderLeg5.rotateAngleY += var13;
		spiderLeg6.rotateAngleY -= var13;
		spiderLeg7.rotateAngleY += var14;
		spiderLeg8.rotateAngleY -= var14;
		spiderLeg1.rotateAngleZ += var15;
		spiderLeg2.rotateAngleZ -= var15;
		spiderLeg3.rotateAngleZ += var16;
		spiderLeg4.rotateAngleZ -= var16;
		spiderLeg5.rotateAngleZ += var17;
		spiderLeg6.rotateAngleZ -= var17;
		spiderLeg7.rotateAngleZ += var18;
		spiderLeg8.rotateAngleZ -= var18;
		if (base.isSneak) {
			rotationPointZ = 5.0f;
			rotationPointY = -1.0f;
			spiderBody.rotationPointY = 16.0f;
			spiderBody.rotationPointZ = 10.0f;
			spiderNeck.rotateAngleX = -0.3926991f;
		}
		if (((EntityNPCInterface) entity).isPlayerSleeping() || ((EntityNPCInterface) entity).currentAnimation == 7) {
			rotationPointY = 12.0f * data.getPartConfig(EnumParts.LEG_LEFT).scaleY;
			rotationPointZ = 15.0f * data.getPartConfig(EnumParts.LEG_LEFT).scaleY;
			rotateAngleX = -1.5707964f;
		}
	}

}
