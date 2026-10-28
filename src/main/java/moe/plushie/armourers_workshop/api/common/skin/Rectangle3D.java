package moe.plushie.armourers_workshop.api.common.skin;

import moe.plushie.armourers_workshop.api.common.IRectangle3D;

@SuppressWarnings("unused")
public class Rectangle3D implements IRectangle3D {

	private int x;
	private int y;
	private int z;
	private int width;
	private int height;
	private int depth;

	public Rectangle3D(int xIn, int yIn, int zIn, int widthIn, int heightIn, int depthIn) {
		x = xIn;
		y = yIn;
		z = zIn;
		width = widthIn;
		height = heightIn;
		depth = depthIn;
	}

	@Override
	public int getDepth() { return depth; }

	@Override
	public int getHeight() { return height; }

	@Override
	public int getWidth() { return width; }

	@Override
	public int getX() { return x; }

	@Override
	public int getY() { return y; }

	@Override
	public int getZ() { return z; }

	public void setDepth(int depthIn) { depth = depthIn; }

	public void setHeight(int heightIn) { height = heightIn; }

	public void setWidth(int widthIn) { width = widthIn; }

	public void setX(int xIn) { x = xIn; }

	public void setY(int yIn) { y = yIn; }

	public void setZ(int zIn) { z = zIn; }

	@Override
	public String toString() {
		return "Rectangle3D [x=" + x + ", y=" + y + ", z=" + z + ", width=" + width + ", height=" + height + ", depth=" + depth + "]";
	}

}
