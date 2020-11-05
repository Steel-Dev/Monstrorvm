package com.github.steeldev.monstrorvm.skript.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.World;

public class ClassInfos {
    static {
        Classes.registerClass(new ClassInfo<>(MVMob.class, "mvmob")
                .name("MVMob")
                .description("Represents a custom Monstrorvm Mob")
                .examples("set {_mob} to mvmob of type zombie with key \"example_mob\" named \"&6Mob\" with a spawn chance of 10 percent")
                .defaultExpression(new EventValueExpression<>(MVMob.class))
                .parser(new Parser<MVMob>() {
                    @Override
                    public String toString(MVMob mvMob, int i) {
                        return toVariableNameString(mvMob);
                    }

                    @Override
                    public String toVariableNameString(MVMob mvMob) {
                        return "Custom MV mob of type '" + mvMob.baseEntity + "' with the key '" + mvMob.key + "'";
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return null;
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }
                }));
    }
}
