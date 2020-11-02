package com.github.steeldev.monstrorvm.util.mobs;

import com.github.steeldev.monstrorvm.util.misc.MVParticle;
import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;
import com.github.steeldev.monstrorvm.util.misc.MVSound;

import java.util.List;

public class MobTargetEffect {
    public int chance;
    public MVParticle targetParticle;
    public MVSound targetSound;
    public List<MVPotionEffect> selfEffects;
    public List<MVPotionEffect> targetEffects;

    public MobTargetEffect(int chance,
                           MVParticle targetParticle,
                           MVSound targetSound,
                           List<MVPotionEffect> selfEffects,
                           List<MVPotionEffect> targetEffects) {
        this.chance = chance;
        this.targetParticle = targetParticle;
        this.targetSound = targetSound;
        this.selfEffects = selfEffects;
        this.targetEffects = targetEffects;
    }
}
