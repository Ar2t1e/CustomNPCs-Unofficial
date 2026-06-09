package noppes.npcs.client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.blocks.custom.*;
import noppes.npcs.client.particles.CustomParticleSettings;
import noppes.npcs.items.custom.*;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketGuiOpen;
import noppes.npcs.potions.PotionData;
import noppes.npcs.shared.common.util.LogWriter;
import org.lwjgl.Sys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.Util;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumGuiType;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

public class NoppesUtil {


	private static final Random rnd = new Random();
	public static final Map<String, String> jsonMap = new TreeMap<>();

	public static void requestOpenGUI(EnumGuiType gui) { requestOpenGUI(gui, BlockPos.ORIGIN); }

	public static void requestOpenGUI(EnumGuiType gui, BlockPos pos) { Packets.sendServer(new SPacketGuiOpen(gui, pos)); }

	public static void openGUI(EntityPlayer player, Object guiscreen) { CustomNpcs.proxy.openGui(player, guiscreen); }

	public static void openFolder(File dir) {
		String s = dir.getAbsolutePath();
		Label_0072: {
			if (Util.getOSType() == Util.EnumOS.OSX) {
				try {
					Runtime.getRuntime().exec(new String[] { "/usr/bin/open", s });
					return;
				} catch (IOException ex) {
					break Label_0072;
				}
			}
			if (Util.getOSType() == Util.EnumOS.WINDOWS) {
				String s2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", s);
				try {
					Runtime.getRuntime().exec(s2);
					return;
				} catch (IOException e) { LogWriter.error(e); }
			}
		}
		boolean flag = false;
		try {
			Class<?> oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
			oclass.getMethod("browse", URI.class).invoke(object, dir.toURI());
		}
		catch (Throwable throwable) { flag = true; }
		if (flag) { Sys.openURL("file://" + s); }
	}

	public static void clickSound() { Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f)); }

	// New from Unofficial (BetaZavr)
	private static void setLocalization(String key, String value) {
		File langDir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/lang");
		if (!langDir.exists() && !langDir.mkdirs()) { return; }
		boolean isExample = key.contains("example") && value.contains("Example");
		boolean isTranslate = false;
		String currentLanguage = "en_en";
		LanguageManager lm = Minecraft.getMinecraft().getLanguageManager();
		if (lm != null) { currentLanguage = lm.getCurrentLanguage().getLanguageCode(); }
		else {
			File dir = CustomNpcs.getWorldSaveDirectory();
			if (dir != null) {
				try {
					File options = new File(dir, "options.txt");
					if (options.exists()) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(options.toPath()), StandardCharsets.UTF_8));
						String line;
						while ((line = reader.readLine()) != null) {
							if (line.startsWith("lang:")) {
								currentLanguage = line.replace("lang:", "");
								break;
							}
						}
						reader.close();
					}
				}
				catch (Exception ignored) {}
			}
		}
		String translateValue = value;
		if (!currentLanguage.equals("en_us")) {
			String language = currentLanguage;
			if (currentLanguage.contains("_")) {
				if (currentLanguage.equals("zh_cn")) { language = "zh_CN"; }
				else if (currentLanguage.equals("zh_tw")) { language = "zh_TW"; }
				else { language = currentLanguage.substring(0, currentLanguage.indexOf("_")); }
			}
			if (isExample) {
				translateValue = noppes.npcs.util.Util.instance.translateGoogle("en", language, value);
				if (translateValue.equals(value)) { return; }
				isTranslate = true;
			}
			else { value = noppes.npcs.util.Util.instance.translateGoogle(language, "en", translateValue); }
		}
		boolean write = false;
		for (int i = 0; i < 2; i++) {
			if (i == 1 && currentLanguage.equals("en_us")) { break; }
			File lang = new File(langDir, (i == 0 ? "en_us" : currentLanguage) + ".json");
			if (jsonMap.isEmpty()) {
				if (lang.exists()) {
					try (BufferedReader reader = Files.newBufferedReader(lang.toPath())) {
						String line;
						while ((line = reader.readLine()) != null) {
							if (line.contains("=")) {
								String[] vk = line.split("=");
								jsonMap.put(vk[0], vk[1]);
							}
						}
					}
					catch (Exception e) { LogWriter.error(e); }
				}
			}
			if (!jsonMap.containsKey(key) || isExample || isTranslate) { jsonMap.put(key, (i == 0 ? value : translateValue)); }
            try (BufferedWriter writer = Files.newBufferedWriter(lang.toPath())) {
                StringBuilder jsonStr = new StringBuilder();
                String str = "";
                for (String k : jsonMap.keySet()) {
                    String pre = k.contains(".") ? k.substring(0, k.indexOf(".")) : k;
                    if (!str.isEmpty() && !str.equals(pre)) { jsonStr.append((char) 10); }
                    str = pre;
                    jsonStr.append(k).append("=").append(jsonMap.get(k)).append((char) 10);
                }
                writer.write(jsonStr.toString());
                writer.close();
                write = true;
            } catch (Exception e) { LogWriter.error(e); }
        }
		if (write) { LogWriter.debug("Create Default Localization key \"" + key + "\""); }
	}

	private static @Nonnull BufferedImage getBufferImageOffset(@Nonnull BufferedImage bufferedImage, float hueShift) {
		try {
			for (int u = 0; u < bufferedImage.getWidth(); u++) {
				for (int v = 0; v < bufferedImage.getHeight(); v++) {
					int i = bufferedImage.getRGB(u, v);
					if (i == 16777215 || i == -1) { continue; }
					Color c = new Color(bufferedImage.getRGB(u, v));
					float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
					float hue = hsb[0] + hueShift;
					while (hue > 1.0f) { hue -= 1.0f; }
					while (hue < 0.0f) { hue += 1.0f; }
					bufferedImage.setRGB(u, v, Color.HSBtoRGB(hue, hsb[1], hsb[2])); // HSBtoRGB(hue, saturation, brightness)
				}
			}
		} catch (Exception e) { LogWriter.error(e); }
		return bufferedImage;
	}

	private static BufferedImage getBufferedImage(String name, int width, int height) {
		try { return ImageIO.read(noppes.npcs.util.Util.instance.getModInputStream(name)); }
		catch (Exception e) { LogWriter.error("Not found file data \"" + name +"\"", e); }
		return new BufferedImage(width, height, 6);
	}

	public static void createItemFiles(ICustomElement customitem) {
		String name = customitem.getCustomName();
		String fileName = "custom_" + customitem.getCustomName();
		NBTTagCompound nbtData = customitem.getCustomNbt().getMCNBT();

		// localization name
		String n = "Item " + name;
		boolean isExample = name.contains("example");
		if (isExample) {
			String t = name.replace("example", "");
			n = "Example Custom " + t.toUpperCase().charAt(0) + t.substring(1);
		}
		if (customitem instanceof CustomArmor) {
			String t = ((CustomArmor) customitem).getEquipmentSlot().getName();
			n += " (" + t.toUpperCase().charAt(0) + t.substring(1) + ")";
		}
		while (n.indexOf('_') != -1) { n = n.replace('_', ' '); }
		setLocalization("item." + fileName + ".name", n);


		// directories
		File texturesDir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/textures/items");
		File armorDir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/textures/models/armor");
		File trimsItemsDir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/trims/items");
		File textEntityDir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/textures/entity");
		if ((texturesDir.exists() || texturesDir.mkdirs()) &&
				(armorDir.exists() || armorDir.mkdirs()) &&
				(trimsItemsDir.exists() || trimsItemsDir.mkdirs()) &&
				(textEntityDir.exists() || textEntityDir.mkdirs())) {
			Map<File, BufferedImage> textures = new HashMap<>();
			File texture = new File(texturesDir, name + ".png");
			float offsetColor = rnd.nextFloat();
			if (customitem.getCustomNbt().getBoolean("IsOBJModel")) {
				if (!texture.exists()) {
					textures.put(texture, getBufferImageOffset(getBufferedImage("ba.png", 16, 16), offsetColor));
				}
			}
			else {
				switch (customitem.getElementType()) {
					case (byte) 1: {
						if (!texture.exists()) { textures.put(texture, getBufferImageOffset(getBufferedImage("sw.png", 16, 16), offsetColor)); }
						break;
					} // Weapon
					case (byte) 2: {
						if (!texture.exists()) {
							String parentName;
							if (customitem instanceof CustomAxe) { parentName = "axe.png"; }
							else if (customitem instanceof CustomHoe) { parentName = "he.png"; }
							else if (customitem instanceof CustomShovel) { parentName = "sl.png"; }
							else { parentName = "pa.png"; }
							textures.put(texture, getBufferImageOffset(getBufferedImage(parentName, 16, 16), offsetColor));
						}
						break;
					} // Tool
					case (byte) 3: {
						String slot = "helmet";
						if (customitem instanceof CustomArmor) { slot = ((CustomArmor) customitem).getEquipmentSlot().getName().toLowerCase(); }
						// Models
						if (nbtData.hasKey("OBJData", 10)) {
							File armorObjFile = new File(armorDir, name + ".png");
							if (!armorObjFile.exists()) {
								textures.put(armorObjFile, getBufferImageOffset(getBufferedImage("am_i.png", 256, 256), offsetColor));
							}
						}
						else {
							File layer_0_File = new File(armorDir, name + "_layer_0.png");
							File layer_1_File = new File(armorDir, name + "_layer_1.png");
							if (!layer_0_File.exists() || !layer_1_File.exists()) {
								textures.put(layer_0_File, getBufferImageOffset(getBufferedImage("ail0.png", 64, 32), offsetColor));
								textures.put(layer_1_File, getBufferImageOffset(getBufferedImage("ail1.png", 64, 32), offsetColor));
							}
						}
						if (customitem instanceof CustomArmor) {
							String part;
							switch (((CustomArmor) customitem).getEquipmentSlot()) {
								case HEAD: part = "ah"; break;
								case CHEST: part = "ac"; break;
								case LEGS: part = "al"; break;
								default: part = "ab"; break;
							}
							File slotFile = new File(texturesDir, name + "_" + slot + ".png");
							File slotTrimFile = new File(trimsItemsDir, name + "_" + slot + "_trim.png");
							if (!slotFile.exists() || !slotTrimFile.exists()) {
								textures.put(slotFile, getBufferImageOffset(getBufferedImage(part + ".png", 16, 16), offsetColor));
								textures.put(slotTrimFile, getBufferImageOffset(getBufferedImage(part + "t.png", 16, 16), offsetColor));
							}
						}
						break;
					} // Armor
					case (byte) 4: {
						if (!texture.exists()) { textures.put(texture, getBufferImageOffset(getBufferedImage("sh.png", 16, 16), offsetColor)); }
						break;
					} // Shield
					case (byte) 5: {
						File pulling_0_File = new File(texturesDir, name + "_pulling_0.png");
						File pulling_1_File = new File(texturesDir, name + "_pulling_1.png");
						File pulling_2_File = new File(texturesDir, name + "_pulling_2.png");
						if (!texture.exists() || !pulling_0_File.exists() ||
								!pulling_1_File.exists() || !pulling_2_File.exists()) {
							textures.put(texture, getBufferImageOffset(getBufferedImage("b_0.png", 16, 16), offsetColor));
							textures.put(pulling_0_File, getBufferImageOffset(getBufferedImage("b_1.png", 16, 16), offsetColor));
							textures.put(pulling_1_File, getBufferImageOffset(getBufferedImage("b_2.png", 16, 16), offsetColor));
							textures.put(pulling_2_File, getBufferImageOffset(getBufferedImage("b_3.png", 16, 16), offsetColor));
						}
						break;
					} // Bow
					case (byte) 6: {
						if (!texture.exists()) { textures.put(texture, getBufferImageOffset(getBufferedImage("sc.png", 16, 16), offsetColor)); }
						break;
					} // Food
					case (byte) 7: {
						break;
					} // Potion
					case (byte) 8: {
						File castFile = new File(texturesDir, name + "_cast.png");
						if (!texture.exists() || !castFile.exists()) {
							textures.put(texture, getBufferImageOffset(getBufferedImage("fr_0.png", 16, 16), offsetColor));
							textures.put(castFile, getBufferImageOffset(getBufferedImage("fr_1.png", 16, 16), offsetColor));
						}
						if (isExample || nbtData.hasKey("FishingHookTexture", 8)) {
							File hookTextureFile = new File(textEntityDir, nbtData.getString("FishingHookTexture") + ".png");
							if (!hookTextureFile.exists()) {
								textures.put(hookTextureFile, getBufferImageOffset(getBufferedImage("frt.png", 16, 16), offsetColor));
							}
						}
						break;
					} // Fishing Rod
					default: {
						if (!texture.exists()) { textures.put(texture, getBufferImageOffset(getBufferedImage("si.png", 16, 16), offsetColor)); }
						break;
					} // 0: Simple
				}
			}
			// Write
			for (Map.Entry<File, BufferedImage> entry: textures.entrySet()) {
				try {
					if (ImageIO.write(entry.getValue(), "png", entry.getKey())) { LogWriter.debug("Create Default Texture for \"" + name + "\" item. File: " + entry.getKey().getName()); }
				}
				catch (Exception e) { LogWriter.error("Error create default texture for \"" + name + "\" item", e); }
			}
		}
	}

	public static void createBlockFiles(ICustomElement customblock) {
		String name = customblock.getCustomName().toLowerCase();
		String fileName = Objects.requireNonNull(((Block) customblock).getRegistryName()).getResourcePath().toLowerCase();

		// localization name
		String n = "Block " + name;
		boolean isExample = name.contains("example");
		if (isExample) {
			String t = name.replace("example", "");
			n = "Example Custom " + t.toUpperCase().charAt(0) + t.substring(1);
		}
		while (n.indexOf('_') != -1) { n = n.replace('_', ' '); }
		setLocalization("tile." + fileName + ".name", n);
		if (customblock instanceof CustomChest) {
			setLocalization("custom.chest." + name, "Custom " + (((CustomChest) customblock).isChest ? "Chest" : "Container") + ": " + n);
		}
		if (customblock instanceof CustomLiquid) {
			setLocalization("item." + CustomNpcs.MODID + "." + fileName + "_bucket.name", n);
			setLocalization("fluid." + fileName, n);
		}

		// textures
		File textBlocksDir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/textures/blocks");
		File textEntityDir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/textures/entity");
		File textChestDir = new File(textEntityDir, "chest");
		File textItemDir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/textures/item");
		File textEnvironmentDir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/textures/environment");
		if ((textBlocksDir.exists() || textBlocksDir.mkdirs()) &&
				(textEntityDir.exists() || textEntityDir.mkdirs()) &&
				(textChestDir.exists() || textChestDir.mkdirs()) &&
				(textEnvironmentDir.exists() || textEnvironmentDir.mkdirs()) &&
				(textItemDir.exists() || textItemDir.mkdirs())) {
			Map<File, BufferedImage> textures = new HashMap<>();
			BufferedImage bb = getBufferedImage("bb.png", 16, 16);
			float offsetColor = rnd.nextFloat();
			if (customblock.getCustomNbt().getBoolean("IsOBJModel")) {
				File textureFile = new File(textBlocksDir, name + ".png");
				File topFile = new File(textBlocksDir, name + "_top.png");
				if (!textureFile.exists() || !topFile.exists()) {
					textures.put(textureFile, getBufferImageOffset(getBufferedImage("hs.png", 16, 16), offsetColor));
					textures.put(topFile, getBufferImageOffset(getBufferedImage("ht.png", 16, 16), offsetColor));
				}
			}
			else {
				switch (customblock.getElementType()) {
					case 1: {
						File stillMCmetaFile = new File(textBlocksDir, name + "_still.png.mcmeta");
						File flowMCmetaFile = new File(textBlocksDir, name + "_flow.png.mcmeta");
						File textureFile = new File(textBlocksDir, name + "_overlay.png");
						File flowFile = new File(textBlocksDir, name + "_flow.png");
						File stillFile = new File(textBlocksDir, name + "_still.png");
						File bucketFile = new File(textItemDir, name + "_bucket.png");
						if (!stillMCmetaFile.exists() || !flowMCmetaFile.exists() ||
								!textureFile.exists() || !flowFile.exists() || !stillFile.exists() || !bucketFile.exists()) {
							// mc_metas
							noppes.npcs.util.Util.instance.saveFile(stillMCmetaFile, NoppesUtilServer.getDataFile("wms.dat", fileName, name));
							noppes.npcs.util.Util.instance.saveFile(flowMCmetaFile, NoppesUtilServer.getDataFile("wmf.dat", fileName, name));
							// images
							textures.put(textureFile, getBufferImageOffset(getBufferedImage("wo.png", 8, 8), offsetColor));
							textures.put(flowFile, getBufferImageOffset(getBufferedImage("wf.png", 32, 512), offsetColor));
							textures.put(stillFile, getBufferImageOffset(getBufferedImage("ws.png", 16, 320), offsetColor));
							// bucket
							textures.put(bucketFile, getBufferImageOffset(getBufferedImage("wb.png", 16, 16), offsetColor));
						}
						break;
					} // Liquid
					case 2: {
						if (customblock instanceof CustomChest && ((CustomChest) customblock).isChest) {
							File textureFile = new File(textBlocksDir, name + ".png");
							File chestFile = new File(textChestDir, name + ".png");
							if (!textureFile.exists() || !chestFile.exists()) {
								textures.put(textureFile, getBufferImageOffset(getBufferedImage("ht.png", 16, 16), offsetColor));
								textures.put(chestFile, getBufferImageOffset(getBufferedImage("hc.png", 64, 64), offsetColor));
							}
						} // chest
						else {
							File textureFile = new File(textBlocksDir, name + ".png");
							if (!textureFile.exists()) {
								textures.put(textureFile, getBufferImageOffset(getBufferedImage("ht.png", 16, 16), offsetColor));
							}
						} // container
						break;
					} // Chest
					case 3: // Stairs and
					case 4: {
						File topFile = new File(textBlocksDir, name + "_top.png");
						File bottomFile = new File(textBlocksDir, name + "_bottom.png");
						File sideFile = new File(textBlocksDir, name + "_side.png");
						if (!topFile.exists() || !bottomFile.exists() || !sideFile.exists()) {
							boolean isSlab = customblock.getElementType() == 4;
							textures.put(topFile, getBufferImageOffset(getBufferedImage("b" + (isSlab ? "l" : "s") + "_top.png", 16, 16), offsetColor));
							textures.put(bottomFile, getBufferImageOffset(getBufferedImage("b" + (isSlab ? "l" : "s") + "_bottom.png", 16, 16), offsetColor));
							textures.put(sideFile, getBufferImageOffset(getBufferedImage("b" + (isSlab ? "l" : "s") + "_side.png", 16, 16), offsetColor));
						}
						break;
					} // Slab
					case 5: {
						File textureFile = new File(textBlocksDir, name + ".png");
						File skyFile = new File(textEnvironmentDir, name + "_sky.png");
						File portalFile = new File(textEntityDir, name + "_portal.png");
						if (!textureFile.exists() || !skyFile.exists() || !portalFile.exists()) {
							textures.put(textureFile, getBufferImageOffset(bb, offsetColor));
							textures.put(skyFile, getBufferImageOffset(getBufferedImage("es.png", 256, 256), offsetColor));
							textures.put(portalFile, getBufferImageOffset(getBufferedImage("ep.png", 256, 256), offsetColor));
						}
						break;
					} // Portal
					case 6: {
						File textureFile = new File(textItemDir, name + ".png");
						File bottomFile = new File(textBlocksDir, name + "_bottom.png");
						File topFile = new File(textBlocksDir, name + "_top.png");
						if (!textureFile.exists() || !bottomFile.exists() || !topFile.exists()) {
							textures.put(textureFile, getBufferImageOffset(getBufferedImage("dw.png", 16, 16), offsetColor));
							textures.put(bottomFile, getBufferImageOffset(getBufferedImage("dwl.png", 16, 16), offsetColor));
							textures.put(topFile, getBufferImageOffset(getBufferedImage("dwu.png", 16, 16), offsetColor));
						}
						break;
					} // Door
					default: {
						if (customblock instanceof CustomBlock && ((CustomBlock) customblock).hasProperty()) {
							CustomBlock block = (CustomBlock) customblock;
							if (block.BO != null) {
								File trueFile = new File(textBlocksDir, name + "_true.png");
								File falseFile = new File(textBlocksDir, name + "_false.png");
								if (!trueFile.exists() || !falseFile.exists()) {
									textures.put(trueFile, getBufferImageOffset(bb, offsetColor));
									textures.put(falseFile, getBufferImageOffset(bb, offsetColor));
								}
							}
							else if (block.INT != null) {
								NBTTagCompound data = block.getCustomNbt().getMCNBT().getCompoundTag("Property");
								for (int i = data.getInteger("Min"); i <= data.getInteger("Max"); i++) {
									File textureFile = new File(textBlocksDir, name + "_" + i + ".png");
									if (!textureFile.exists()) { textures.put(textureFile, getBufferImageOffset(bb, 0.15f + 0.85f * rnd.nextFloat())); }
								}
							}
							else if (block.FACING != null) {
								File frontFile = new File(textBlocksDir, name + "_front.png");
								File bottomFile = new File(textBlocksDir, name + "_bottom.png");
								File topFile = new File(textBlocksDir, name + "_top.png");
								File rightFile = new File(textBlocksDir, name + "_right.png");
								File backFile = new File(textBlocksDir, name + "_back.png");
								File leftFile = new File(textBlocksDir, name + "_left.png");
								if (!frontFile.exists() || !bottomFile.exists() ||
										!topFile.exists() || !rightFile.exists() ||
										!backFile.exists() || !leftFile.exists()) {
									textures.put(frontFile, getBufferImageOffset(getBufferedImage("bp_front.png", 16, 16), offsetColor));
									textures.put(bottomFile, getBufferImageOffset(getBufferedImage("bp_bottom.png", 16, 16), offsetColor));
									textures.put(topFile, getBufferImageOffset(getBufferedImage("bp_top.png", 16, 16), offsetColor));
									textures.put(rightFile, getBufferImageOffset(getBufferedImage("bp_right.png", 16, 16), offsetColor));
									textures.put(backFile, getBufferImageOffset(getBufferedImage("bp_back.png", 16, 16), offsetColor));
									textures.put(leftFile, getBufferImageOffset(getBufferedImage("bp_left.png", 16, 16), offsetColor));
								}
							}
						}
						else {
							File textureFile = new File(textBlocksDir, name + ".png");
							if (!textureFile.exists()) { textures.put(textureFile, getBufferImageOffset(bb, offsetColor)); }
						}
						break;
					}
				}
			}
			for (Map.Entry<File, BufferedImage> entry : textures.entrySet()) {
				try {
					if (ImageIO.write(entry.getValue(), "png", entry.getKey())) { LogWriter.debug("Create default texture for \"" + name + "\" block"); }
				}
				catch (Exception e) { LogWriter.error("Error create default texture for \"" + name + "\" block", e); }
			}
		}
	}

	public static void createParticleFiles(CustomParticleSettings customparticle) {
		String name = customparticle.getCustomName();
		String fileName = "custom_" + name.toLowerCase();
		String n = name;
		boolean isExample = name.toLowerCase().contains("example");
		if (isExample) {
			n = "Example Custom " + (name.toLowerCase().contains("_obj_") ? "OBJ" : "") + "Particle";
		}
		while (n.indexOf('_') != -1) { n = n.replace('_', ' '); }
		setLocalization("particle." + name, n);

		File modelDir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/models/particle");
		File texturesDir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/textures/particle");
		if ((texturesDir.exists() || texturesDir.mkdirs()) && (modelDir.exists() || modelDir.mkdirs())) {
			if (customparticle.nbtData.hasKey("OBJModel", 8)) {
				name = customparticle.nbtData.getString("OBJModel");
				File modelFile = new File(modelDir, name + ".obj");
				File mtlFile = new File(modelDir, name + ".mtl");
				if (!modelFile.exists() || !mtlFile.exists()) {
					if (noppes.npcs.util.Util.instance.saveFile(modelFile, NoppesUtilServer.getDataFile("pe_o.dat", fileName, name)) &&
							noppes.npcs.util.Util.instance.saveFile(mtlFile, NoppesUtilServer.getDataFile("pe_m.dat", fileName, name))) {
						LogWriter.debug("Create Default OBJ Model for \"" + name + "\" particle");
					}
				}
			}
			else {
				File texture = new File(texturesDir, name.toLowerCase() + ".png");
				if (!texture.exists()) {
					try {
						if (ImageIO.write(getBufferImageOffset(getBufferedImage("pl.png", 64, 64), rnd.nextFloat()), "png", texture)) {
							LogWriter.debug("Create default texture for \"" + name + "\" particle");
						}
					}
					catch (Exception e) { LogWriter.error("Error create default texture for \"" + name + "\" particle", e); }
				}
			}
		}
	}

	public static void createAllPotionFiles(PotionData custompotion) {
		String name = custompotion.getCustomName();
		String n = name;
		boolean isExample = name.contains("example");
		if (isExample) {
			String t = name.replace("example", "");
			n = "Example Custom " + t.toUpperCase().charAt(0) + t.substring(1);
		}
		while (n.indexOf('_') != -1) { n = n.replace('_', ' '); }

		setLocalization("effect." + name, n);
		String effectPart = ".effect." + name;
		setLocalization("potion" + effectPart, n);
		setLocalization("splash_potion" + effectPart, name.equals("potionexample") ? "Example Custom Splash Potion" : n + " Splash");
		setLocalization("lingering_potion" + effectPart, name.equals("potionexample") ? "Example Custom Lingering Potion" : n + " Lingering");
		setLocalization("tipped_arrow" + effectPart, name.equals("potionexample") ? "Example Custom Arrow Potion" : n + " Arrow");

		File texturesDir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/textures/potions");
		if (texturesDir.exists() || texturesDir.mkdirs()) {
			float offsetColor = rnd.nextFloat();
			File texture = new File(texturesDir, name + ".png");
			if (!isExample || !texture.exists()) {
				try {
					if (ImageIO.write(getBufferImageOffset(getBufferedImage("pi.png", 18, 18), offsetColor), "png", texture)) {
						LogWriter.debug("Create default texture for \"" + name + "\" potion");
					}
				}
				catch (Exception e) { LogWriter.error("Error create default texture for \"" + name + "\" potion", e); }
			}
		}
	}

}
