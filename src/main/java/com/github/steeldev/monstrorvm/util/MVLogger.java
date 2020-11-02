package com.github.steeldev.monstrorvm.util;

import java.util.logging.Logger;

public class MVLogger extends Logger {
    protected MVLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    public static MVLogger getLogger() {
        return new MVLogger("", null);
    }

    @Override
    public void info(String msg) {
        String prefix = msg.replace("[NBTAPI]", Util.getNbtapiPrefix());
        Util.log(prefix);
    }
}
