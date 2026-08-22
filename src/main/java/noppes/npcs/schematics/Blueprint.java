package noppes.npcs.schematics;

import java.util.List;
import java.util.Objects;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.api.IPos;
import noppes.npcs.api.NpcAPI;

public class Blueprint implements ISchematic {

	private String[] architects;
	private String name;
	private final IBlockState[] palette;
	private final short paletteSize;
	private final List<String> requiredMods;
	private final short sizeX;
	private final short sizeY;
	private final short sizeZ;
	private final short[][][] structure;
	private final NBTTagCompound[] tileEntities;

	// New from Unofficial (BetaZavr)
	private final BlockPos offset = BlockPos.ORIGIN;

	public Blueprint(short sizeXIn, short sizeYIn, short sizeZIn, short paletteSizeIn, IBlockState[] paletteIn,
			short[][][] structureIn, NBTTagCompound[] tileEntitiesIn, List<String> requiredModsIn) {
		sizeX = sizeXIn;
		sizeY = sizeYIn;
		sizeZ = sizeZIn;
		paletteSize = paletteSizeIn;
		palette = paletteIn;
		structure = structureIn;
		tileEntities = tileEntitiesIn;
		requiredMods = requiredModsIn;
	}

	public void build(World world, BlockPos pos) {
		IBlockState[] palette = getPalette();
		short[][][] structure = getStructure();
		for (short y = 0; y < getSizeY(); ++y) {
			for (short z = 0; z < getSizeZ(); ++z) {
				for (short x = 0; x < getSizeX(); ++x) {
					IBlockState state = palette[structure[y][z][x] & 0xFFFF];
					if (state.getBlock() != Blocks.STRUCTURE_VOID) {
						if (state.isFullCube()) {
							world.setBlockState(pos.add(x, y, z).add(offset), state, 2);
						}
					}
				}
			}
		}
		for (short y = 0; y < getSizeY(); ++y) {
			for (short z = 0; z < getSizeZ(); ++z) {
				for (short x = 0; x < getSizeX(); ++x) {
					IBlockState state = palette[structure[y][z][x]];
					if (state.getBlock() != Blocks.STRUCTURE_VOID) {
						if (!state.isFullCube()) {
							world.setBlockState(pos.add(x, y, z).add(offset), state, 2);
						}
					}
				}
			}
		}
		if (getTileEntities() != null) {
			for (NBTTagCompound tag : getTileEntities()) {
				TileEntity te = world.getTileEntity(pos.add(tag.getShort("x"), tag.getShort("y"), tag.getShort("z")));
				tag.setInteger("x", pos.getX() + tag.getShort("x"));
				tag.setInteger("y", pos.getY() + tag.getShort("y"));
				tag.setInteger("z", pos.getZ() + tag.getShort("z"));
                assert te != null;
                te.deserializeNBT(tag);
			}
		}
	}

	public String[] getArchitects() { return architects; }

	@Override
	public IBlockState getBlockState(int i) {
		int x = i % getWidth();
		int z = (i - x) / getWidth() % getLength();
		int y = ((i - x) / getWidth() - z) / getLength();
		return getBlockState(x, y, z);
	}

	@Override
	public IBlockState getBlockState(int x, int y, int z) { return palette[structure[y][z][x]]; }

	@Override
	public NBTTagList getEntitys() { return new NBTTagList(); }

	@Override
	public short getHeight() { return getSizeZ(); }

	@Override
	public short getLength() { return getSizeY(); }

	@Override
	public String getName() { return name; }

	@Override
	public NBTTagCompound getNBT() { return BlueprintUtil.writeBlueprintToNBT(this); }

	@Override
	public IPos getOffset() { return Objects.requireNonNull(NpcAPI.Instance()).getIPos(offset.getX(), offset.getY(), offset.getZ()); }

	public IBlockState[] getPalette() { return palette; }

	public short getPaletteSize() { return paletteSize; }

	public List<String> getRequiredMods() { return requiredMods; }

	public short getSizeX() { return sizeX; }

	public short getSizeY() { return sizeY; }

	public short getSizeZ() { return sizeZ; }

	public short[][][] getStructure() { return structure; }

	public NBTTagCompound[] getTileEntities() { return tileEntities; }

	@Override
	public NBTTagCompound getTileEntity(int i) { return tileEntities[i]; }

	@Override
	public int getTileEntitySize() { return tileEntities.length; }

	@Override
	public short getWidth() { return getSizeX(); }

	@Override
	public boolean hasEntitys() { return false; }

	public void setArchitects(String[] architectsIn) { architects = architectsIn; }

	public void setName(String nameIn) { name = nameIn; }

}
