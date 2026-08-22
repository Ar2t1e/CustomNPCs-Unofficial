package noppes.npcs.api.handler.data;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.storage.WorldInfo;
import noppes.npcs.api.INbt;

@SuppressWarnings("unused")
public interface IWorldProvider {

    WorldType getMCTerrainType();

    WorldInfo getMCWorldInfo();

    DimensionType getMCDimensionType();

    INbt getData();

    String getGeneratorSettings();

    BiomeProvider getMCBiomeProvider();

    boolean isDoesWaterVaporize();

    float[] getMCLightBrightnessTable();

    float[] getMCColorsSunriseSunset();

}
