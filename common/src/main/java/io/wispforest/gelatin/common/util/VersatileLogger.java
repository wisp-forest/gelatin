package io.wispforest.gelatin.common.util;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class VersatileLogger {

    private static final Logger LOGGER = LogUtils.getLogger();

    private long startTime;
    private final String prefix;

    public final Supplier<Boolean> debugCheck;

    public VersatileLogger(String prefix) {
        this.startTime = System.currentTimeMillis();
        this.prefix = prefix;

        this.debugCheck = () -> true;
    }

    public VersatileLogger(String prefix, Supplier<Boolean> debugCheck) {
        this.startTime = System.currentTimeMillis();
        this.prefix = prefix;

        this.debugCheck = debugCheck;
    }

    //-------------------------------------------------------------

    public void stopTimerPrint(String message) {
        long endTime = System.currentTimeMillis();
        LOGGER.info("[{}]: {} {} seconds", prefix, message, (endTime - this.startTime) / 1000F);
    }

    public void restartTimer() {
        this.startTime = System.currentTimeMillis();
    }

    //-------------------------------------------------------------

    public void failMessage(String message, Object... variables) {
        if(!debugCheck.get()) return;

        LOGGER.error("[" + prefix + "]: " + message, variables);
    }

    public void infoMessage(String message, Object... variables) {
        if(!debugCheck.get()) return;

        LOGGER.info("[" + prefix + "]: " + message, variables);
    }

}
