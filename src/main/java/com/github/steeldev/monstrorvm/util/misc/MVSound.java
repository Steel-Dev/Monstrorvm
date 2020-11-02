package com.github.steeldev.monstrorvm.util.misc;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public class MVSound {
    public Sound sound;
    public SoundCategory category;
    public float volume;
    public float pitch;

    public MVSound(Sound sound,
                   SoundCategory category,
                   float volume,
                   float pitch) {
        this.sound = sound;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void playSound(Location location) {
        location.getWorld().playSound(location, sound, category, volume, pitch);
    }
}
