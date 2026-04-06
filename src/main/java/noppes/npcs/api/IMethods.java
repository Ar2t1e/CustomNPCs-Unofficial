package noppes.npcs.api;

import net.minecraft.nbt.Tag;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.util.IRayTraceResults;
import noppes.npcs.api.util.IRayTraceRotate;
import noppes.npcs.api.util.IRayTraceVec;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public interface IMethods {

    String ticksToElapsedTime(@ParamName("ticks") long ticks, @ParamName("isMilliSeconds") boolean isMilliSeconds, @ParamName("colored") boolean colored, @ParamName("upped") boolean upped);

    String deleteColor(@ParamName("str") String str);

    double distanceTo(@ParamName("x0") double x0, @ParamName("y0") double y0, @ParamName("z0") double z0,
                      @ParamName("x1") double x1, @ParamName("y1") double y1, @ParamName("z1") double z1);

    double distanceTo(@ParamName("entity") IEntity<?> entity, @ParamName("target") IEntity<?> target);

    IRayTraceRotate getAngles3D(@ParamName("x0") double x0, @ParamName("y0") double y0, @ParamName("z0") double z0,
                                @ParamName("x1") double x1, @ParamName("y1") double y1, @ParamName("z1") double z1);

    IRayTraceRotate getAngles3D(@ParamName("entity") IEntity<?> entity, @ParamName("target") IEntity<?> target);

    String getJSONStringFromObject(@ParamName("obj") Object obj);

    String getDataFile(@ParamName("fileName") String fileName);

    IRayTraceVec getPosition(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z,
                             @ParamName("yaw") double yaw, @ParamName("pitch") double pitch, @ParamName("radius") double radius);

    IRayTraceVec getPosition(@ParamName("entity") IEntity<?> entity,
                             @ParamName("yaw") double yaw, @ParamName("pitch") double pitch,@ParamName("radius") double radius);

    IRayTraceVec getVector3D(@ParamName("x0") double x0, @ParamName("y0") double y0, @ParamName("z0") double z0,
                             @ParamName("x1") double x1, @ParamName("y1") double y1, @ParamName("z1") double z1);

    IRayTraceVec getVector3D(@ParamName("entity") IEntity<?> entity, @ParamName("target") IEntity<?> target);

    IRayTraceVec getVector3D(@ParamName("entity") IEntity<?> entity, @ParamName("pos") IPos pos);

    IRayTraceResults rayTraceBlocksAndEntitys(@ParamName("entity") IEntity<?> entity, @ParamName("yaw") double yaw, @ParamName("pitch") double pitch, @ParamName("distance") double distance);

    Object readObjectFromNbt(@ParamName("tag") Tag tag);

    @NotNull IEntity<?> transferEntity(@ParamName("entity") IEntity<?> entity, @ParamName("dimensionId") String dimensionId, @ParamName("pos") IPos pos);

    Tag writeObjectToNbt(@ParamName("value") Object value);

    List<File> getFiles(@ParamName("directory") File directory, @ParamName("index") String index);

    String getTextNumberToRoman(@ParamName("value") int value);

    String getTextReducedNumber(@ParamName("value") double value, @ParamName("isInteger") boolean isInteger, @ParamName("color") boolean color, @ParamName("notPfx") boolean notPfx);

    boolean removeFile(@ParamName("directory") File directory);

    String loadFile(@ParamName("file") File file);

    boolean saveFile(@ParamName("file") File file, @ParamName("text") String text);

    boolean saveFile(@ParamName("file") File file, @ParamName("compound") INbt compound);

    String translateGoogle(@ParamName("textLanguageKey") String textLanguageKey, @ParamName("translationLanguageKey") String translationLanguageKey, @ParamName("originalText") String originalText);

}
