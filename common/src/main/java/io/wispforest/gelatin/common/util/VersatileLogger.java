package io.wispforest.gelatin.common.util;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class VersatileLogger {

    private static final Logger LOGGER = LogUtils.getLogger();

    private long startTime;
    private final String prefix;

    private Supplier<Boolean> oldDebugCheck = () -> true;
    private Supplier<Boolean> debugCheck;

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

    public VersatileLogger setDebugCheck(Supplier<Boolean> debugCheck){
        return setDebugCheck(debugCheck, false);
    }

    public VersatileLogger setDebugCheck(Supplier<Boolean> debugCheck, boolean useForever){
        this.oldDebugCheck = !useForever ? this.debugCheck : null;
        this.debugCheck = debugCheck;

        return this;
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
        this.logMessage(LOGGER::error, message, variables);
    }

    public void warnMessage(String message, Object... variables) {
        this.logMessage(LOGGER::warn, message, variables);
    }

    public void infoMessage(String message, Object... variables) {
        this.logMessage(LOGGER::info, message, variables);
    }

    protected void logMessage(BiConsumer<String, Object[]> logMethod, String message, Object... variables){
        if(!checkDebug()) return;

        logMethod.accept("[" + prefix + "]: " + message, variables);
    }

    private boolean checkDebug(){
        boolean bl = debugCheck.get();

        if(oldDebugCheck != null){
            debugCheck = oldDebugCheck;
            oldDebugCheck = null;
        }

        return bl;
    }
}
