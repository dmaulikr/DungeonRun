package com.clashsoft.dungeonrun.client.engine;

import com.clashsoft.dungeonrun.client.DungeonRunClient;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import java.util.HashMap;
import java.util.Map;

public class SoundEngine
{
	public static class SoundLocation
	{
		public final int	x;
		public final int	y;
		public final int	z;
		
		public SoundLocation(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = y;
		}
	}
	
	public static final SoundLocation DEFAULT_LOCATION = new SoundLocation(0, 0, 0);
	
	public final DungeonRunClient dr;
	
	private Map<String, Sound>	sounds	= new HashMap<String, Sound>();
	private Map<String, Music>	musics	= new HashMap<String, Music>();
	
	public SoundEngine(DungeonRunClient dr)
	{
		this.dr = dr;
	}
	
	public void playSoundEffect(String sound, SoundLocation sl)
	{
		this.playSoundEffect(sound, sl, this.dr.gameSettings.soundVolume);
	}
	
	public void playSoundEffect(String sound, SoundLocation sl, float volume)
	{
		Sound s = this.sounds.get(sound);
		if (s == null)
		{
			final String ref = "resources/audio/" + sound.replace('.', '/') + ".ogg";
			try
			{
				s = new Sound(ref);
				this.sounds.put(sound, s);
			}
			catch (SlickException e)
			{
				return;
			}
		}
		s.playAt(1F, volume, sl.x, sl.y, sl.z);
	}
	
	public void stopSoundEffect(String sound)
	{
		Sound s = this.sounds.get(sound);
		if (s != null)
		{
			s.stop();
		}
	}
	
	public void stopAllSoundEffects()
	{
		for (Sound s : this.sounds.values())
		{
			s.stop();
		}
	}
	
	public void playMusic(String music, boolean repeat)
	{
		this.playMusic(music, repeat, this.dr.gameSettings.musicVolume);
	}
	
	public void playMusic(String music, boolean repeat, float volume)
	{
		Music m = this.musics.get(music);
		if (m == null)
		{
			try
			{
				m = new Music("resources/audio/music/" + music.replace('.', '/') + ".ogg");
				this.musics.put(music, m);
			}
			catch (SlickException e)
			{
				return;
			}
		}
		if (repeat)
		{
			m.loop(1F, volume);
		}
		else
		{
			m.play(1F, volume);
		}
	}
	
	public void stopMusic(String music)
	{
		Music m = this.musics.get(music);
		if (m != null)
		{
			m.stop();
		}
	}
	
	public void stopAllMusics()
	{
		for (Music m : this.musics.values())
		{
			m.stop();
		}
	}
	
	public void stopAllSounds()
	{
		this.stopAllSoundEffects();
		this.stopAllMusics();
	}
}
