package noppes.npcs.client.layer;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.client.model.part.ModelPartConfig;
import noppes.npcs.client.parts.ModelPartData;
import noppes.npcs.client.model.part.legs.ModelDigitigradeLegs;
import noppes.npcs.client.model.part.legs.ModelHorseLegs;
import noppes.npcs.client.model.part.legs.ModelMermaidLegs;
import noppes.npcs.client.model.part.legs.ModelNagaLegs;
import noppes.npcs.client.model.part.legs.ModelSpiderLegs;
import noppes.npcs.client.model.part.tails.ModelCanineTail;
import noppes.npcs.client.model.part.tails.ModelDragonTail;
import noppes.npcs.client.model.part.tails.ModelFeatherTail;
import noppes.npcs.client.model.part.tails.ModelRodentTail;
import noppes.npcs.client.model.part.tails.ModelSquirrelTail;
import noppes.npcs.client.model.part.tails.ModelTailFin;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;

public class LayerLegs<T extends EntityLivingBase> extends LayerInterface<T> implements LayerPreRender {

	private ModelDigitigradeLegs digitigrade;
	private ModelRenderer dragon;
	private ModelRenderer feathers;
	private ModelRenderer fin;
	private ModelCanineTail fox;
	private ModelRenderer horse;
	private ModelHorseLegs horseLegs;
	private ModelMermaidLegs mermaid;
	private ModelNagaLegs naga;
	private ModelRenderer rodent;
	float rotationPointY;
	float rotationPointZ;
	private ModelSpiderLegs spiderLegs;
	private ModelRenderer squirrel;
	private ModelRenderer tail;

	public LayerLegs(RenderLiving<?> render) {
		super(render);
		createParts();
	}

	private void createParts() {
		spiderLegs = new ModelSpiderLegs(model);
		horseLegs = new ModelHorseLegs(model);
		naga = new ModelNagaLegs(model);
		mermaid = new ModelMermaidLegs(model);
		digitigrade = new ModelDigitigradeLegs(model);
		fox = new ModelCanineTail(model);
		(tail = new ModelRenderer(model, 56, 21)).addBox(-1.0f, 0.0f, 0.0f, 2, 9, 2);
		tail.setRotationPoint(0.0f, 0.0f, 1.0f);
		setRotation(tail, 0.8714253f, 0.0f, 0.0f);
		(horse = new ModelRenderer(model)).setTextureSize(32, 32);
		horse.setRotationPoint(0.0f, -1.0f, 1.0f);
		ModelRenderer tailBase = new ModelRenderer(model, 0, 26);
		tailBase.setTextureSize(32, 32);
		tailBase.addBox(-1.0f, -1.0f, 0.0f, 2, 2, 3);
		setRotation(tailBase, -1.134464f, 0.0f, 0.0f);
		horse.addChild(tailBase);
		ModelRenderer tailMiddle = new ModelRenderer(model, 0, 13);
		tailMiddle.setTextureSize(32, 32);
		tailMiddle.addBox(-1.5f, -2.0f, 3.0f, 3, 4, 7);
		setRotation(tailMiddle, -1.134464f, 0.0f, 0.0f);
		horse.addChild(tailMiddle);
		ModelRenderer tailTip = new ModelRenderer(model, 0, 0);
		tailTip.setTextureSize(32, 32);
		tailTip.addBox(-1.5f, -4.5f, 9.0f, 3, 4, 7);
		setRotation(tailTip, -1.40215f, 0.0f, 0.0f);
		horse.addChild(tailTip);
		horse.rotateAngleX = 0.5f;
		dragon = new ModelDragonTail(model);
		squirrel = new ModelSquirrelTail(model);
		fin = new ModelTailFin(model);
		rodent = new ModelRodentTail(model);
		feathers = new ModelFeatherTail(model);
	}

	@Override
	public void preRender(EntityCustomNpc player) {
		npc = player;
		playerdata = player.modelData;
		ModelPartData data = playerdata.getPartData(EnumParts.LEGS);
		ModelRenderer bipedLeftLeg = model.bipedLeftLeg;
		ModelRenderer bipedRightLeg = model.bipedRightLeg;
		boolean b = data == null || data.type != 0;
		bipedRightLeg.isHidden = b;
		bipedLeftLeg.isHidden = b;
	}

	@Override
	public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
		renderLegs(par7);
		if (!npc.animation.showParts.get(EnumParts.BODY)) { return; }
		renderTails(par7);
	}

	private void renderLegs(float par7) {
		ModelPartData data = playerdata.getPartData(EnumParts.LEGS);
		if (data.type <= 0) {
			return;
		}
		GlStateManager.pushMatrix();
		ModelPartConfig config = playerdata.getPartConfig(EnumParts.LEG_LEFT);
		preRender(data);
		if (data.type == 1) {
			GlStateManager.translate(0.0f, config.transY * 2.0f, config.transZ * par7 + 0.04f);
			GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
			naga.render(par7);
		} else if (data.type == 2) {
			GlStateManager.translate(0.0, config.transY * 1.76f - 0.1 * config.scaleY,
					(config.transZ * par7));
			GlStateManager.scale(1.06f, 1.06f, 1.06f);
			GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
			spiderLegs.render(par7);
		} else if (data.type == 3) {
			if (config.scaleY >= 1.0f) {
				GlStateManager.translate(0.0f, config.transY * 1.76f, config.transZ * par7);
			} else {
				GlStateManager.translate(0.0f, config.transY * 1.86f, config.transZ * par7);
			}
			GlStateManager.scale(0.79f, 0.9f - config.scaleY / 10.0f, 0.79f);
			GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
			horseLegs.render(par7);
		} else if (data.type == 4) {
			GlStateManager.translate(0.0f, config.transY * 1.86f, config.transZ * par7);
			GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
			mermaid.render(par7);
		} else if (data.type == 5) {
			GlStateManager.translate(0.0f, config.transY * 1.86f, config.transZ * par7);
			GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
			digitigrade.render(par7);
		}
		GlStateManager.popMatrix();
	}

	private void renderTails(float par7) {
		ModelPartData data = playerdata.getPartData(EnumParts.TAIL);
		if (data == null) {
			return;
		}
		GlStateManager.pushMatrix();
		ModelPartConfig config = playerdata.getPartConfig(EnumParts.LEG_LEFT);
		GlStateManager.translate(config.transX * par7, config.transY + rotationPointY * par7, config.transZ * par7 + rotationPointZ * par7);
		GlStateManager.translate(0.0f, 0.0f, (config.scaleZ - 1.0f) * 5.0f * par7);
		GlStateManager.scale(config.scaleX, config.scaleY, config.scaleZ);
		preRender(data);
		switch (data.type) {
			case 0:
				if (data.pattern == 1) {
					tail.rotationPointX = -0.5f;
					tail.rotateAngleY -= 0.2f;
					tail.render(par7);
					++tail.rotationPointX;
					tail.rotateAngleY += 0.4f;
					tail.render(par7);
					tail.rotationPointX = 0.0f;
				}
				else { tail.render(par7); }
				break;
			case 1: dragon.render(par7); break;
			case 2: horse.render(par7); break;
			case 3: squirrel.render(par7); break;
			case 4: fin.render(par7); break;
			case 5: rodent.render(par7); break;
			case 6: feathers.render(par7); break;
			case 7: fox.render(par7); break;
		}
		GlStateManager.popMatrix();
	}

	@Override
	public void rotate(float par1, float par2, float par3, float par4, float par5, float par6) {
		rotateLegs(par1, par2, par3, par4, par5, par6);
		rotateTail(par1, par2, par3, par4, par5, par6);
	}

	public void rotateLegs(float par1, float par2, float par3, float par4, float par5, float par6) {
		ModelPartData part = playerdata.getPartData(EnumParts.LEGS);
		if (part.type == 2) {
			spiderLegs.setRotationAngles(playerdata, par1, par2, par3, par4, par5, par6, npc);
		} else if (part.type == 3) {
			horseLegs.setRotationAngles(playerdata, par1, par2, par3, par4, par5, par6, npc);
		} else if (part.type == 1) {
			naga.isRiding = model.isRiding;
			naga.isSleeping = npc.isPlayerSleeping();
			naga.isCrawling = (npc.currentAnimation == 7);
			naga.isSneaking = model.isSneak;
			naga.setRotationAngles(par1, par2, par3, par4, par5, par6, npc);
		} else if (part.type == 4) {
			mermaid.setRotationAngles(par1, par2, par3, par4, par5, par6, npc);
		} else if (part.type == 5) {
			digitigrade.setRotationAngles(par1, par2, par3, par4, par5, par6, npc);
		}
	}

	public void rotateTail(float par1, float par2, float par3, float par4, float par5, float par6) {
		ModelPartData part = playerdata.getPartData(EnumParts.LEGS);
		ModelPartData partTail = playerdata.getPartData(EnumParts.TAIL);
		ModelPartConfig config = playerdata.getPartConfig(EnumParts.LEG_LEFT);
		float rotateAngleY = MathHelper.cos(par1 * 0.6662f) * 0.2f * par2;
		float rotateAngleX = MathHelper.sin(par3 * 0.067f) * 0.05f;
        rotationPointY = 11.0f;
		if (part.type == 2) {
			rotationPointY = 12.0f + (config.scaleY - 1.0f) * 3.0f;
			rotationPointZ = 15.0f + (config.scaleZ - 1.0f) * 10.0f;
			if (npc.isPlayerSleeping() || npc.currentAnimation == 7) {
				rotationPointY = 12.0f + 16.0f * config.scaleZ;
				rotationPointZ = config.scaleY;
				rotateAngleX = -0.7853982f;
			}
		} else if (part.type == 3) {
			rotationPointY = 10.0f;
			rotationPointZ = 16.0f + (config.scaleZ - 1.0f) * 12.0f;
		} else {
			rotationPointZ = 1.0f - config.scaleZ;
		}
		if (partTail != null) {
			if (partTail.type == 2) { rotateAngleX += 0.5f; }
			if (partTail.type == 0) { rotateAngleX += 0.87f; }
			if (partTail.type == 7) { fox.setRotationAngles(par1, par2, par3, par4, par5, par6, npc); }
		}
		rotationPointZ += model.bipedRightLeg.rotationPointZ + 0.5f;
		rodent.rotateAngleX = rotateAngleX;
		fin.rotateAngleX = rotateAngleX;
		horse.rotateAngleX = rotateAngleX;
		squirrel.rotateAngleX = rotateAngleX;
		dragon.rotateAngleX = rotateAngleX;
		feathers.rotateAngleX = rotateAngleX;
		tail.rotateAngleX = rotateAngleX;
		fox.rotateAngleX = rotateAngleX;
		rodent.rotateAngleY = rotateAngleY;
		fin.rotateAngleY = rotateAngleY;
		horse.rotateAngleY = rotateAngleY;
		squirrel.rotateAngleY = rotateAngleY;
		dragon.rotateAngleY = rotateAngleY;
		feathers.rotateAngleY = rotateAngleY;
		tail.rotateAngleY = rotateAngleY;
		fox.rotateAngleY = rotateAngleY;
	}

	@Override
	public boolean shouldCombineTextures() { return true; }

}
