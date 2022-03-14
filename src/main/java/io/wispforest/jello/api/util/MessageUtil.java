package io.wispforest.jello.api.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageUtil {
    private static final Logger LOGGER = LogManager.getLogger(MessageUtil.class);

    private long startTime;

    public MessageUtil(){
        this.startTime = System.currentTimeMillis();
    }

    //-------------------------------------------------------------

    public void stopTimerPrint(String outputName, String message){
        stopTimerPrint("[" + outputName + "]: " + message);
    }

    private void stopTimerPrint(String message){
        long endTime = System.currentTimeMillis();

        LOGGER.info(message + (endTime - this.startTime) / 1000F + " secounds");
    }

    public void restartTimer(){
        this.startTime = System.currentTimeMillis();
    }

    //-------------------------------------------------------------

    public void failMessage(String outputName, String message){
        failMessage("[" + outputName + "]: " + message);
    }

    private void failMessage(String message){
        LOGGER.error(message);
    }

    //-------------------------------------------------------------

    public void infoMessage(String outputName, String message){
        infoMessage("[" + outputName + "]: " + message);
    }

    private void infoMessage(String message){
        LOGGER.info(message);
    }

    //-------------------------------------------------------------


}
