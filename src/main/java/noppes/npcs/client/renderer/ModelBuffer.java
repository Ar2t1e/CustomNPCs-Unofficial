package noppes.npcs.client.renderer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

import net.minecraft.client.renderer.texture.*;
import org.apache.commons.io.IOUtils;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.client.model.ModelOBJPlayerArmor;
import noppes.npcs.client.renderer.obj.CustomOBJState;
import noppes.npcs.client.renderer.obj.ParameterizedModel;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.items.custom.CustomArmor;

public class ModelBuffer {

	// rendered models
	protected static final List<ParameterizedModel> MODELS = new ArrayList<>(); // list of parameterized
	public static List<ResourceLocation> NOT_FOUND = new ArrayList<>();	// list of missing models
	// so as not to freeze
	// the client
	private static ModelOBJPlayerArmor objModel;

	/**
	 * Attempting to display an OBJ model
	 * With all model settings checked
	 *
	 * @param modelLocation - resource for the location of the OBJ model
	 * @param visibleMeshes - list of names of meshes/grids that need to be displayed from model
	 * @param materialTextures - texture replacement map. Key is a resource for a texture from a material, Value is a new resource texture
	 * @param colorMask - color of the mask applied to the model
	 * @param isDynamic - measurement of overlay lighting is displayed on the model
	 */
	public static void render(ResourceLocation modelLocation,
							  List<String> visibleMeshes,
							  Map<String, ResourceLocation> materialTextures, int colorMask, boolean isDynamic) {
		render(getParameterizedModel(modelLocation, visibleMeshes, materialTextures, false, colorMask, isDynamic));
	}

	/**
	 * Trying to quickly display a finished OBJ model
	 *
	 * @param model - generated custom model
	 */
	public static void render(ParameterizedModel model) {
		if (ModelBuffer.MODELS.size() > 500) { clear(); }
		if (model != null) {
			try { model.render(); }
			catch (Exception e) {
				LogWriter.error("Error render OBJ model \"" + model.modelLocation + "\"", e);
				NOT_FOUND.add(model.modelLocation);
			}
		}
	}

	/**
	 * formation of the 3B model
	 *
	 * @param modelLocation - resource for the location of the OBJ model
	 * @param visibleMeshes - list of names of meshes/grids that need to be displayed from model
	 * @param materialTextures - texture replacement map. Key is a resource for a texture from a material, Value is a new resource texture
	 * @param colorMask - color of the mask applied to the model
	 * @return generated custom model
	 */
	public static ParameterizedModel getParameterizedModel(ResourceLocation modelLocation,
														   List<String> visibleMeshes,
														   Map<String, ResourceLocation> materialTextures,
														   boolean reverseNormals, int colorMask, boolean isDynamic) {
		if (NOT_FOUND.contains(modelLocation)) { return null; }
		ParameterizedModel model = new ParameterizedModel(modelLocation, visibleMeshes, materialTextures, reverseNormals, colorMask, isDynamic);
		boolean found = false;
		for (ParameterizedModel pm : ModelBuffer.MODELS) {
			if (pm.equals(model)) {
				model = pm;
				found = true;
				break;
			}
		}
		if (model.objModel == null) { model.load(); }
		if (model.objModel == null) { NOT_FOUND.add(modelLocation); return null; }
		if (!found) { ModelBuffer.MODELS.add(model); }
		return model;
	}

	public static void clear() { ModelBuffer.MODELS.clear(); }

	// Armor
	public static ResourceLocation getMainOBJTexture(ResourceLocation objModel) {
		try {
			ResourceLocation location = new ResourceLocation(objModel.getResourceDomain(), objModel.getResourcePath().replace(".obj", ".mtl"));
			IResource res = Minecraft.getMinecraft().getResourceManager().getResource(location);
			String mat_lib = IOUtils.toString(res.getInputStream(), StandardCharsets.UTF_8);
			if (mat_lib.contains("map_Kd")) {
				int endIndex = mat_lib.indexOf("" + ((char) 10), mat_lib.indexOf("map_Kd"));
				if (endIndex == -1) {
					endIndex = mat_lib.length();
				}
				String txtr = mat_lib.substring(mat_lib.indexOf(" ", mat_lib.indexOf("map_Kd")) + 1, endIndex);
				String domain = "", path;
				if (!txtr.contains(":")) {
					path = txtr;
				} else {
					domain = txtr.substring(0, txtr.indexOf(":"));
					path = txtr.substring(txtr.indexOf(":") + 1);
				}
				return new ResourceLocation(domain, path);
			}
		} catch (Exception e) { LogWriter.error(e); }
		return null;
	}

	public static ModelBiped getOBJModelBiped(CustomArmor armor, EntityLivingBase entity, ModelBiped defModel) {
		if (!(entity instanceof EntityPlayer) && !(entity instanceof EntityNPCInterface)) {
			return null;
		}
		if (entity instanceof EntityNPCInterface) { return defModel; }
		if (ModelBuffer.objModel == null) {
			ModelBuffer.objModel = new ModelOBJPlayerArmor(armor);
		}
		return ModelBuffer.objModel;
	}

	public static IBakedModel getIBakedModel(CustomArmor armor) {
		if (armor.objModel == null) { return null; }
		List<String> visibleMeshes = new ArrayList<>();
		if (armor.getEquipmentSlot() == EntityEquipmentSlot.HEAD) {
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.HEAD));
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.MOHAWK));
		} else if (armor.getEquipmentSlot() == EntityEquipmentSlot.CHEST) {
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.BODY));
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.ARM_LEFT));
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.ARM_RIGHT));
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.WRIST_LEFT));
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.WRIST_RIGHT));
		} else if (armor.getEquipmentSlot() == EntityEquipmentSlot.LEGS) {
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.BELT));
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.LEG_LEFT));
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.LEG_RIGHT));
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.FEET_LEFT));
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.FEET_RIGHT));
		} else if (armor.getEquipmentSlot() == EntityEquipmentSlot.FEET) {
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.FEET_LEFT));
			visibleMeshes.addAll(armor.getMeshNames(EnumParts.FEET_RIGHT));
		} else { return null; }
		try {
			OBJModel iModel = (OBJModel) OBJLoader.INSTANCE.loadModel(armor.objModel);
			Function<ResourceLocation, TextureAtlasSprite> spriteFunction = location -> {
				if (location.toString().equals("minecraft:missingno") || location.toString().equals("minecraft:builtin/white")) {
					return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
				}
				TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
				if (sprite == Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite()) {
					LogWriter.debug("Not load or found texture sprite: " + location + " to " + objModel);
				}
				return sprite;
			};
			return iModel.bake(new CustomOBJState(ImmutableList.copyOf(visibleMeshes), true, armor), DefaultVertexFormats.ITEM, spriteFunction);
		} catch (Exception e) { LogWriter.error(e); }
		return null;
	}

}
