package com.github.steeldev.monstrorvm.util.misc;

import org.bukkit.Location;
import org.bukkit.Particle;

public class MVParticle {
    public Particle particle;
    public int amount;

    public MVParticle(Particle particle,
                      int amount) {
        this.particle = particle;
        this.amount = amount;
    }

    public void spawnParticle(Location location) {
        location.getWorld().spawnParticle(particle, location, amount);
    }
}
