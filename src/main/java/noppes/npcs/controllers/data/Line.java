package noppes.npcs.controllers.data;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.chat.Component;
import noppes.npcs.api.entity.data.ILine;

public class Line implements ILine {

	public static Line formatTarget(Line lineIn, EntityLivingBase entity) {
		if (entity != null) {
			Line line = lineIn.copy();
			if (entity instanceof EntityPlayer) { line.text = line.text.replace("@target", ((EntityPlayer) entity).getDisplayNameString()); }
			else { line.text = line.text.replace("@target", entity.getName()); }
		}
		return lineIn;
	}

	private boolean showText = true;
	protected String sound = "";
	protected String text = "";

	public Line() { }

	public Line(String textIn) { text = Component.translatable(textIn).getString(); }

	public Line copy() {
		Line line = new Line(text);
		line.sound = sound;
		line.showText = showText;
		return line;
	}

	@Override
	public String getText() { return text; }

	@Override
	public void setText(String textIn) { text = textIn != null ? textIn : ""; }

	@Override
	public String getSound() { return sound; }

	@Override
	public void setSound(String soundIn) { sound = soundIn != null ? soundIn : ""; }

	@Override
	public boolean getShowText() { return showText; }

	@Override
	public void setShowText(boolean show) { showText = show; }

}
