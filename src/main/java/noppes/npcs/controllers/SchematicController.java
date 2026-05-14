package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.schematics.Blueprint;
import noppes.npcs.schematics.BlueprintUtil;
import noppes.npcs.schematics.ISchematic;
import noppes.npcs.schematics.Schematic;
import noppes.npcs.schematics.SchematicWrapper;
import noppes.npcs.shared.common.CommonUtil;
import noppes.npcs.util.Util;

import javax.annotation.Nullable;

public class SchematicController {

	public static SchematicController Instance = new SchematicController();
	public static long time = 50L;
	public static final List<String> included = Arrays.asList("archery_range.schematic", "bakery.schematic", "barn.schematic",
				"building_site.schematic", "chapel.schematic", "church.schematic", "gate.schematic",
				"glassworks.schematic", "guard_Tower.schematic", "guild_house.schematic", "house.schematic",
				"house_small.schematic", "inn.schematic", "library.schematic", "lighthouse.schematic", "mill.schematic",
				"observatory.schematic", "ship.schematic", "shop.schematic", "stall.schematic", "stall2.schematic",
				"stall3.schematic", "tier_house1.schematic", "tier_house2.schematic", "tier_house3.schematic",
				"tower.schematic", "wall.schematic", "wall_corner.schematic");

	public static void buildBlocks(EntityPlayerMP player, BlockPos pos, SchematicWrapper wrapper) { // Schematic
		if (player != null && pos != null && wrapper != null) {
			long ticks = 3000L + wrapper.size * SchematicController.time + (long) Math.floor((double) wrapper.size / CustomNpcs.MaxBuilderBlocks) * 1000L;
			player.sendMessage(Component.translatable("schematic.info.started", wrapper.schema.getName(),
					"" + pos.getX(), "" + pos.getY(), "" + pos.getZ(), player.world.provider.getDimension(),
					Util.instance.ticksToElapsedTime(ticks, true, true, false)));
			SchematicController.Instance.build(wrapper, player);
		}
	}

	public static @Nullable File getDir() {
		File saveDir = CustomNpcs.getWorldSaveDirectory();
		if (saveDir != null) {
			File schematicDir = new File(saveDir, "schematics");
			while (saveDir.getParentFile() != null) {
				saveDir = saveDir.getParentFile();
				if ((new File(saveDir, "config")).exists()) {
					schematicDir = new File(saveDir, "schematics");
					break;
				}
			}
			if (schematicDir.exists() || schematicDir.mkdir()) { return schematicDir; }
		}
		return null;
	}

	private final List<SchematicWrapper> buildingList = new ArrayList<>();

	public Map<String, SchematicWrapper> map = new HashMap<>();

	public SchematicController() {
	}

	public void build(SchematicWrapper schema, ICommandSender sender) {
		if (schema == null) {
			sendMessage(sender, Component.translatable("schematic.info.notbuild"));
			return;
		}
		if (buildingList.contains(schema)) {
			sendMessage(sender, Component.translatable("schematic.info.already",
					Component.literal(schema.schema.getName()).withStyle(TextFormatting.GRAY),
					Component.literal(schema.getPercentage() + "%").withStyle(TextFormatting.GRAY)));
			if (schema.sender != null) {
				sendMessage(sender, Component.translatable("schematic.info.start.name",
						Component.literal(schema.sender.getDisplayName().getFormattedText()).withStyle(TextFormatting.GRAY)));
			}
			return;
		}
		schema.setBuilder(sender);
		buildingList.add(schema);
	}

	@SuppressWarnings("unused")
	public SchematicWrapper getSchema(String name) {
		if (!map.containsKey(name.toLowerCase())) { load(name.toLowerCase()); }
		return map.get(name.toLowerCase());
	}

	public void info(ICommandSender sender) {
		if (buildingList.isEmpty()) {
			sendMessage(sender, Component.translatable("schematic.info.empty"));
		} else {
			for (SchematicWrapper sm : buildingList) {
				sendMessage(sender, Component.translatable("schematic.info.0",
						Component.literal(sm.schema.getName()).withStyle(TextFormatting.GRAY),
						Component.literal(sm.getPercentage() + "%").withStyle(TextFormatting.GRAY),
						Component.translatable(sm.sender == null ? "" : "schematic.info.1").withStyle(TextFormatting.GRAY)));
			}
		}
	}

	public List<String> list() {
        List<String> list = new ArrayList<>(included);
		File dir = SchematicController.getDir();
		if (dir != null) {
			File[] files = dir.listFiles();
			if (files != null) {
				for (File file : files) {
					String name = file.getName();
					if (name.toLowerCase().endsWith(".schematic") || name.toLowerCase().endsWith(".blueprint")) {
						list.add(name);
					}
				}
			}
		}
		Collections.sort(list);
		return list;
	}

	public SchematicWrapper load(String name) {
		CustomNpcs.debugData.start(null);
		InputStream stream = null;
		if (included.contains(name)) {
			stream = MinecraftServer.class.getResourceAsStream("/assets/" + CustomNpcs.MODID + "/schematics/" + name);
		}
		if (stream == null) {
			File file = new File(SchematicController.getDir(), name);
			if (!file.exists()) {
				File dir = SchematicController.getDir();
				if (dir != null) {
					File[] files = dir.listFiles();
					if (files != null) {
						for (File f : files) {
							if (f.getName().equalsIgnoreCase(name)) {
								file = f;
								break;
							}
						}
					}
				}
			}
			if (!file.exists()) {
				CustomNpcs.debugData.end(null);
				return null;
			}
			try {
				stream = new FileInputStream(file);
			} catch (FileNotFoundException e2) {
				CustomNpcs.debugData.end(null);
				return null;
			}
		}
		SchematicWrapper schemaWr = null;
		try {
			NBTTagCompound compound = CompressedStreamTools.readCompressed(stream);
			stream.close();
			if (name.toLowerCase().endsWith(".blueprint")) {
				Blueprint bp = BlueprintUtil.readBlueprintFromNBT(compound);
				if (bp != null) {
					bp.setName(name);
					schemaWr = new SchematicWrapper(bp);
				}
			}
			if (schemaWr == null) {
				Schematic schema = new Schematic(name);
				schema.load(compound);
				schemaWr = new SchematicWrapper(schema);
			}
		} catch (IOException e) {
			LogWriter.except(e);
		}
		if (schemaWr != null) {
			map.put(name.toLowerCase(), schemaWr);
		}
		CustomNpcs.debugData.end(null);
		return schemaWr;
	}

	public void save(ICommandSender sender, String name, int type, BlockPos pos, short height, short width, short length) {
		CustomNpcs.debugData.start(null);
		name = name.replace(" ", "_");
		if (included.contains(name)) {
			CustomNpcs.debugData.end(null);
			return;
		}
		World world = sender.getEntityWorld();
		File file = null;
		ISchematic schema = null;
		if (type == 0) {
			file = new File(SchematicController.getDir(), name + ".schematic");
			schema = Schematic.create(world, name, pos, height, width, length);
		} else if (type == 1) {
			file = new File(SchematicController.getDir(), name + ".blueprint");
			schema = BlueprintUtil.createBlueprint(world, pos, width, length, height);
		}
		CommonUtil.NotifyOPs(Component.literal("Schematic " + name + " successfully created").withStyle(TextFormatting.GRAY), false);
		try {
			if (schema != null) {
				CompressedStreamTools.writeCompressed(schema.getNBT(), Files.newOutputStream(file.toPath()));
			}
		} catch (Exception e) { LogWriter.error(e); }
		CustomNpcs.debugData.end(null);
	}


	private void sendMessage(ICommandSender sender, Component message) {
		if (sender != null) { sender.sendMessage(message); }
	}

	public void stop(ICommandSender sender) {
		if (buildingList.isEmpty()) {
			sendMessage(sender, Component.translatable("schematic.info.build.empty"));
		} else {
			StringBuilder smts = new StringBuilder();
			for (SchematicWrapper sm : buildingList) {
				if (smts.length() > 0) { smts.append(";" + ((char) 10)); }
				smts.append(((char) 167)).append("7\"").append(sm.schema.getName()).append("\" in [")
						.append(sm.start.getX()).append(", ").append(sm.start.getY()).append(", ").append(sm.start.getZ()).append("]");
			}
			sendMessage(sender, Component.translatable("schematic.info.build.stop", smts.toString()));
			buildingList.clear();
		}
	}

	public void updateBuilding() {
		if (buildingList.isEmpty()) { return; }
		CustomNpcs.debugData.start(null);
		List<SchematicWrapper> del = new ArrayList<>();
		for (SchematicWrapper wrapper : buildingList) {
			wrapper.build();
			if (wrapper.sender != null && wrapper.getPercentage() - wrapper.buildingPercentage >= 10) {
				sendMessage(wrapper.sender, Component.translatable("schematic.info.build.percentage",
						Component.literal(wrapper.schema.getName()).withStyle(TextFormatting.GRAY),
						Component.literal(wrapper.getPercentage() + "%").withStyle(TextFormatting.GRAY)));
				wrapper.buildingPercentage = wrapper.getPercentage();
			}
			if (!wrapper.isBuilding) {
				if (wrapper.sender != null) {
					if (wrapper.schema.hasEntitys()) {
						sendMessage(wrapper.sender, Component.translatable("schematic.info.spawn.entitys",
								Component.literal(wrapper.schema.getName()).withStyle(TextFormatting.GRAY)));
					}
					sendMessage(wrapper.sender, Component.translatable("schematic.info.build.finish",
							Component.literal(wrapper.schema.getName()).withStyle(TextFormatting.GRAY)));
				}
				del.add(wrapper);
			}
		}
		for (SchematicWrapper sm : del) { buildingList.remove(sm); }
		CustomNpcs.debugData.end(null);
	}

}
