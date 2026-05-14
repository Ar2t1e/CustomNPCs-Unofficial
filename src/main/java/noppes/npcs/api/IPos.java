package noppes.npcs.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.api.interfaces.ParamName;

public interface IPos {

	double getX();

	double getY();

	double getZ();

	IPos up();

	IPos up(@ParamName("n") double n);

	IPos down();

	IPos down(@ParamName("n") double n);

	IPos north();

	IPos north(@ParamName("n") double n);

	IPos east();

	IPos east(@ParamName("n") double n);

	IPos south();

	IPos south(@ParamName("n") double n);

	IPos west();

	IPos west(@ParamName("n") double n);

	IPos add(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z);

	IPos add(@ParamName("pos") IPos pos);

	IPos subtract(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z);

	IPos subtract(@ParamName("pos") IPos pos);

	double[] normalize();

	BlockPos getMCBlockPos();

	IPos offset(@ParamName("direction") int direction);

	IPos offset(@ParamName("direction") int direction, @ParamName("n") double n);

	double distanceTo(@ParamName("pos") IPos pos);

	// New from Unofficial (BetaZavr)
	double distanceTo(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z);

	Vec3d getMCVec3();

	IPos rotate(int rotation);

	IPos offset(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z);

}
