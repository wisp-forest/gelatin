package io.wispforest.gelatin.common.pas;

public enum ActionObservationType {
    PRESSED,
    TOGGLED,
    DISABLED;

    public static ActionObservationType of(boolean value){
        return value ? ActionObservationType.TOGGLED : ActionObservationType.PRESSED;
    }

    public boolean isToggled(){
        return this == TOGGLED;
    }
}
