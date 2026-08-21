package noppes.npcs.client.controllers;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.client.TranslateUtil;
import noppes.npcs.mixin.client.audio.ISoundHandlerMixin;
import noppes.npcs.mixin.client.audio.ISoundManagerMixin;
import noppes.npcs.client.ClientTickHandler;
import noppes.npcs.client.util.MusicData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobBard;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.CustomNPCsScheduler;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

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
			for (MusicData music : new ArrayList<>(ClientTickHandler.musics)) {
				if (music.resource.equals(resource) || music.name.equals(resource.toString())) {
					music.setPos(x, y, z);
				}
			}
		}
	}

	public void speak(String languageKey, String text, float volume) {
		CustomNPCsScheduler.runTack(() -> {
			try {
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				if (player == null) { return; }
				URLConnection connection = new URL(String.format(TranslateUtil.AudioUrl, URLEncoder.encode(text, "UTF-8"), languageKey)).openConnection();
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("User-Agent", "Chrome/99.0.4844.51");
				connection.setConnectTimeout(10000);
				InputStream stream = connection.getInputStream();

				// Reading all bytes from a stream
				byte[] audioBytes = IOUtils.toByteArray(stream);
				// Using SourceDataLine to Play Sound
				AudioFormat format = new AudioFormat(
						16000f, // Sampling frequency
						16, // Bit depth
						1, // Number of channels (mono)
						true, // Signed PCM
						false // Little-endian
				);
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
				SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
				sourceDataLine.open(format);
				sourceDataLine.start();

				// Applying Volume and Pitch
				// Adjusting Volume by Changing Signal Amplitude
				float adjustedVolume = Math.min(Math.max(volume, 0f), 1f);  // Limit the volume value within [0, 1]
				for (int i = 0; i < audioBytes.length; i++) {
					// Apply a loudness factor to each sample
					audioBytes[i] = (byte) ((float) audioBytes[i] * adjustedVolume);
				}

				// Let's start playback
				sourceDataLine.write(audioBytes, 0, audioBytes.length);
				sourceDataLine.drain();
				sourceDataLine.stop();
				sourceDataLine.close();

				// Closing Streams
				stream.close();
			} catch (Exception e) {
				LogWriter.error("Error while playing translation \"" + languageKey + "\"", e);
			}
		});
	}

}
