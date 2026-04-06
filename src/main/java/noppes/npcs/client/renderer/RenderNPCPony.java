package noppes.npcs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import noppes.npcs.client.model.ModelPony;
import noppes.npcs.client.model.ModelPonyArmor;
import noppes.npcs.entity.EntityNpcPony;
import org.jetbrains.annotations.NotNull;

public class RenderNPCPony<T extends EntityNpcPony, M extends ModelPony<T>> extends RenderNPCInterface<T, M> {

	private final ModelPony<T> modelBipedMain;
	private final ModelPonyArmor<T> modelArmorChestPlate;
	private final ModelPonyArmor<T> modelArmor;

	public RenderNPCPony(Context manager, M model) {
		super(manager, model, 0.5F);
		this.modelBipedMain = model;
		this.modelArmorChestPlate = new ModelPonyArmor<>(1.0F);
		this.modelArmor = new ModelPonyArmor<>(0.5F);
	}

	public @NotNull ResourceLocation getTextureLocation(T pony) {
		boolean check = pony.textureLocation == null || pony.textureLocation != pony.checked;
		ResourceLocation loc = super.getTextureLocation(pony);
		if (check) {
			Resource resource = Minecraft.getInstance().getResourceManager().getResource(loc).orElse(null);
			if (resource != null) {
				try {
					BufferedImage bufferedimage = ImageIO.read(resource.open());
					pony.isPegasus = false;
					pony.isUnicorn = false;
					Color color = new Color(bufferedimage.getRGB(0, 0), true);
					Color color2 = new Color(136, 202, 240, 255);
					Color color3 = new Color(209, 159, 228, 255);
					Color color4 = new Color(254, 249, 252, 255);
					if (color.equals(color2)) {
						pony.isPegasus = true;
					}

					if (color.equals(color3)) {
						pony.isUnicorn = true;
					}

					if (color.equals(color4)) {
						pony.isPegasus = true;
						pony.isUnicorn = true;
					}

					pony.checked = loc;
				} catch (IOException ignored) {}
			}
		}

		return loc;
	}

	public void render(T pony, float entityYaw, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource buffer, int packedLight) {
		this.modelArmorChestPlate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 1;
		this.modelArmorChestPlate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = pony.isCrouching();
		this.modelArmorChestPlate.riding = this.modelArmor.riding = this.modelBipedMain.riding = false;
		this.modelArmorChestPlate.isSleeping = this.modelArmor.isSleeping = this.modelBipedMain.isSleeping = pony.isSleeping();
		this.modelArmorChestPlate.isUnicorn = this.modelArmor.isUnicorn = this.modelBipedMain.isUnicorn = pony.isUnicorn;
		this.modelArmorChestPlate.isPegasus = this.modelArmor.isPegasus = this.modelBipedMain.isPegasus = pony.isPegasus;
		super.render(pony, entityYaw, partialTicks, matrixStack, buffer, packedLight);
		this.modelArmorChestPlate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = false;
		this.modelArmorChestPlate.riding = this.modelArmor.riding = this.modelBipedMain.riding = false;
		this.modelArmorChestPlate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
		this.modelArmorChestPlate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 0;
	}

}
