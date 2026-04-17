package noppes.npcs.client.controllers;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.mixin.client.audio.IPositionedSoundMixin;
import noppes.npcs.mixin.client.audio.ISoundHandlerMixin;
import noppes.npcs.mixin.client.audio.ISoundManagerMixin;
import noppes.npcs.mixin.client.audio.ISoundSystemMixin;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.client.ClientTickHandler;
import noppes.npcs.client.util.MusicData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobBard;
import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.Source;

import javax.annotation.Nullable;

public class MusicController {

	public static MusicController Instance;

	// New Unofficial (Goodbird)
	ISound dialogSound = null;

	// New from Unofficial (BetaZavr)
	public @Nullable ResourceLocation song;
	public @Nullable ResourceLocation music;
	public @Nullable Entity songBard;
	public @Nullable Entity musicBard;
	public @Nullable ISound playingSong;
	public @Nullable ISound playingMusic;
	public boolean unloadSongBard = false;
	public boolean unloadMusicBard = false;

	public MusicController() { MusicController.Instance = this; }

	public boolean isPlaying(ResourceLocation resource) {
		SoundManager handler = ((ISoundHandlerMixin) Minecraft.getMinecraft().getSoundHandler()).getSndManager();
		Map<String, ISound> playingSounds = ((ISoundManagerMixin) handler).getPlayingSounds();
		if (playingSounds != null) {
			for (ISound sound : playingSounds.values()) {
				if (sound.getSound().getSoundLocation().equals(resource) || sound.getSoundLocation().equals(resource)) {
					return true;
				}
			}
		}
		return false;
	}

	public void playSound(SoundCategory category, String sound, double x, double y, double z, float volume, float pitch) {
		if (category == null || sound == null || sound.isEmpty()) { return; }
		ResourceLocation res = new ResourceLocation(sound);
		if (category == SoundCategory.MUSIC && isPlaying(res)) { return; }
		ISound.AttenuationType aType = ISound.AttenuationType.LINEAR;
		Minecraft mc = Minecraft.getMinecraft();
		if (category == SoundCategory.MUSIC) {
			Minecraft.getMinecraft().getSoundHandler().stop(sound, SoundCategory.MUSIC);
			aType = ISound.AttenuationType.NONE;
			x = mc.player != null ? (float) mc.player.posX : 0.0f;
			y = mc.player != null ? (float) mc.player.posY + 0.5f : 0.0f;
			z = mc.player != null ? (float) mc.player.posZ : 0.0f;
		}
		mc.getSoundHandler().playSound(new PositionedSoundRecord(res, category, volume, pitch,
				false, 0, aType, (float) x, (float) y, (float) z));
	}

	public void stopSound(@Nullable ResourceLocation sound, SoundCategory category) {
		Minecraft.getMinecraft().getSoundHandler().stop(sound == null ? "" : sound.toString(), category);
		if (category == SoundCategory.AMBIENT && sound == null || Objects.equals(sound, song)) {
			song = null;
			songBard = null;
			playingSong = null;
		}
		else if (category == SoundCategory.MUSIC && sound == null || Objects.equals(sound, music)) {
			music = null;
			musicBard = null;
			playingMusic = null;
		}
	}

	public void stopSounds() {
		Minecraft.getMinecraft().getSoundHandler().stopSounds();
		song = null;
		music = null;
		songBard = null;
		musicBard = null;
		playingSong = null;
		playingMusic = null;
	}

	public void playStreaming(ResourceLocation sound, Entity entity, boolean looping) {
		if (!isPlaying(sound)) {
			stopSounds();
			song = sound;
			if (song != null) {
				songBard = entity;
				Minecraft.getMinecraft().getSoundHandler().playSound(playingMusic = new PositionedSoundRecord(sound, SoundCategory.RECORDS,
						4.0f, 1.0f, looping, 0, ISound.AttenuationType.NONE,
						(float) entity.posX, (float) entity.posY, (float) entity.posZ));
			}
		}
	}

	public void playMusic(ResourceLocation sound, Entity entity, boolean isLooping) {
		if (!isPlaying(sound)) {
			stopMusics();
			music = sound;
			if (music != null) {
				musicBard = entity;
				Minecraft.getMinecraft().getSoundHandler().playSound(playingMusic = new PositionedSoundRecord(music, SoundCategory.MUSIC,
						1.0f, 1.0f, isLooping, 0, ISound.AttenuationType.NONE,
						0, 0, 0));
			}
		}
	}

	// New from Unofficial (Goodbird)
	public void playSoundDialog(SoundCategory category, ResourceLocation sound, BlockPos pos, float volume, float pitch) {
		if (dialogSound != null) { Minecraft.getMinecraft().getSoundHandler().stopSound(dialogSound); }
		Minecraft.getMinecraft().getSoundHandler().playSound(dialogSound = new PositionedSoundRecord(sound,
				category, volume, pitch,
				false, 0, ISound.AttenuationType.LINEAR,
				(float) pos.getX() + 0.5F, pos.getY(), (float) pos.getZ() + 0.5F));
	}

	// New from Unofficial (BetaZavr)
	public void bardPlaySound(ResourceLocation sound, boolean isStreamer, EntityNPCInterface npc) {
		stopSound(sound, isStreamer ? SoundCategory.AMBIENT : SoundCategory.MUSIC);
		ISound.AttenuationType aType = ISound.AttenuationType.LINEAR;
		float x = (float) npc.posX;
		float y = (float) npc.posY;
		float z = (float) npc.posZ;
		if (isStreamer) {
			song = sound;
			songBard = npc;
		} else {
			music = sound;
			musicBard = npc;
			aType = ISound.AttenuationType.NONE;
			x = 0.0f;
			y = 0.0f;
			z = 0.0f;
			for (MusicData md : ClientTickHandler.musics) {
				if (!md.name.isEmpty() && md.name.indexOf("minecraft") == 0) {
					Minecraft.getMinecraft().getSoundHandler().stop(md.name, SoundCategory.MUSIC);
				}
			}
		}
		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(sound,
				isStreamer ? SoundCategory.AMBIENT : SoundCategory.MUSIC, 1.0f, 1.0f, false, 0, aType, x, y, z));
	}

	public boolean isBardPlaying(ResourceLocation soundIn, boolean isStreamer) { // check Any Bards
		return isPlaying(soundIn) || (isStreamer ? song != null && isPlaying(song) : music != null && isPlaying(music));
	}

	public void checkBards(EntityPlayer player) {
		if (music == null) {
			if (musicBard != null) { musicBard = null; }
			if (playingMusic != null) { playingMusic = null; }
		}
		else {
			if (!(musicBard instanceof EntityNPCInterface) || !(((EntityNPCInterface) musicBard).job instanceof JobBard)) {
				stopSound(music, SoundCategory.MUSIC);
			}
			else {
				Entity entity = player.world.getEntityByID(musicBard.getEntityId());
				if (entity == null) {
					unloadMusicBard = true;
					JobBard job = (JobBard) ((EntityNPCInterface) musicBard).job;
					if (job.hasOffRange) {
						int x = job.range[1], y = job.range[1], z = job.range[1];
						if (!job.isRange) {
							x = job.maxPos[0];
							y = job.maxPos[1];
							z = job.maxPos[2];
						}
						int xD = (int) Math.abs(player.posX - musicBard.posX);
						int yD = (int) Math.abs(player.posY - musicBard.posY);
						int zD = (int) Math.abs(player.posZ - musicBard.posZ);
						if (xD > x || yD > y || zD > z) {
							stopSound(song, SoundCategory.MUSIC);
						}
					}
				}
			}
		}
		if (song == null) {
			if (songBard != null) { songBard = null; }
			if (playingSong != null) { playingSong = null; }
		}
		else {
			if (!(songBard instanceof EntityNPCInterface) || !(((EntityNPCInterface) songBard).job instanceof JobBard)) {
				stopSound(song, SoundCategory.AMBIENT);
			} else {
				Entity entity = player.world.getEntityByID(songBard.getEntityId());
				if (entity == null) {
					unloadSongBard = true;
					JobBard job = (JobBard) ((EntityNPCInterface) songBard).job;
					if (job != null && job.hasOffRange) {
						int x = job.range[1], y = job.range[1], z = job.range[1];
						if (!job.isRange) {
							x = job.maxPos[0];
							y = job.maxPos[1];
							z = job.maxPos[2];
						}
						int xD = (int) Math.abs(player.posX - songBard.posX);
						int yD = (int) Math.abs(player.posY - songBard.posY);
						int zD = (int) Math.abs(player.posZ - songBard.posZ);
						if (xD > x || yD > y || zD > z) {
							stopSound(song, SoundCategory.AMBIENT);
						}
					}
				}
			}
		}
	}

	public void stopMusics() {
		Minecraft.getMinecraft().getSoundHandler().stop("", SoundCategory.MUSIC);
		music = null;
		musicBard = null;
		playingMusic = null;
	}

	public void setNewPosSong(ResourceLocation resource, float x, float y, float z) {
		if (resource != null) {
			SoundManager sm = ((ISoundHandlerMixin) Minecraft.getMinecraft().getSoundHandler()).getSndManager();
			Map<String, ISound> playingSounds = ((ISoundManagerMixin) sm).getPlayingSounds();
			if (playingSounds == null) { return; }
			String uuid = null;
			for (String id : playingSounds.keySet()) {
				ISound sound = playingSounds.get(id);
				if (sound.getSound().getSoundLocation().equals(resource)
						|| sound.getSoundLocation().equals(resource) && sound instanceof PositionedSound) {
					((IPositionedSoundMixin) sound).getXPosF(x);
					((IPositionedSoundMixin) sound).setYPosF(y);
					((IPositionedSoundMixin) sound).setZPosF(z);
					uuid = id;
					break;
				}
			}
			System.out.println("New pos song uuid: \"" + uuid + "\" to [" + (int) x + ", " + (int) y + ", " + (int) z + "]");
			if (uuid != null) {
				SoundSystem sndSystem = null;
				for (Field f : sm.getClass().getDeclaredFields()) {
					if (f.getType().getName().contains("SoundSystem")) {
						try {
							f.setAccessible(true);
							sndSystem = (SoundSystem) f.get(sm);
						}
						catch (IllegalAccessException e) { LogWriter.debug(e.toString()); }
						break;
					}
				}
				if (sndSystem == null) { return; }
				Library soundLibrary = ((ISoundSystemMixin) sndSystem).getSoundLibrary();
				if (soundLibrary == null) { return; }
				Source source = soundLibrary.getSources().get(uuid);
				if (source != null && source.position != null) {
					source.position.x = x;
					source.position.y = y;
					source.position.z = z;
				}
			}
		}
	}

}
