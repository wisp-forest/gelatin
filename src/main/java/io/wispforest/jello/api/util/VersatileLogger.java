package io.wispforest.jello.api.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class VersatileLogger {

    private static final Logger LOGGER = LogManager.getLogger(VersatileLogger.class);

    private long startTime;
    private final String prefix;

    public final Supplier<Boolean> debugCheck;

    public VersatileLogger(String prefix) {
        this.startTime = System.currentTimeMillis();
        this.prefix = prefix;

        this.debugCheck = () -> false;
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

    public void failMessage(String message) {
        LOGGER.error("[" + prefix + "]: " + message);
    }

    public void infoMessage(String message) {
        LOGGER.info("[" + prefix + "]: " + message);
    }

}
