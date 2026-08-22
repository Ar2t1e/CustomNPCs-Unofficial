package noppes.npcs.client.util;

import noppes.npcs.util.ValueUtil;

public class CrashesData {

	public boolean isActive = false;
	public boolean isFading = true;
	public boolean vector = false;
	public int time = 0;
	public int maxTime = 0;
	public int amplitude = 0;
	public int type = 0;
	public long endTime = 0;

	public float get(long time) {
		if (endTime == 0) {
			time *= -1;
			endTime = time + time;
		}
		if (time <= 0 || maxTime == 0 || amplitude == 0) {
			endTime = 0;
			isActive = false;
			return 0.0f;
		}
		float value;
		if (isFading) {
			value = (float) time * (float) amplitude / (float) maxTime;
		} else {
			value = (float) amplitude;
		}
		value *= vector ? 1.0f : -1.0f;
		vector = !vector;
		if (value == 0.0f) {
			endTime = 0;
			isActive = false;
		}
		return value;
	}

	public void set(int timeIn, int amplitudeIn, int typeIn, boolean isFadingIn) {
		if (timeIn < 0) { timeIn *= -1; }
		if (timeIn > 1200) { timeIn = 1200; }
		time = timeIn;
		maxTime = time;
		amplitude = ValueUtil.onlyPositiveInt(amplitudeIn, 25);
		if (typeIn < 0) { typeIn *= -1; }
		type = typeIn % 6;
		isFading = isFadingIn;
		isActive = true;
		endTime = 0;
	}

}
