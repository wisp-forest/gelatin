package com.dragon.jello.mixin.ducks;

public interface ConstantColorEntity {

    default int getConstantColor(){
        return -1;
    }

    default boolean isConstantColor(){
        return getConstantColor() != -1;
    }

    /**
     * A method used to allow for Color Blending with the Entity's base Texture's color
     */
    boolean trueColorOverride();

}
