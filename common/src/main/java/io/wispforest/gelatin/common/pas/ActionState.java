package io.wispforest.gelatin.common.pas;

import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Supplier;

public class ActionState implements Supplier<Boolean> {

    private OnStateChangeEvent onStateChangeEvent = (player, newState) -> {};

    private boolean state;

    public ActionState(){
        this(false);
    }

    public ActionState(boolean state){
        this.state = state;
    }

    public ActionState onStateChangeEvent(OnStateChangeEvent event){
        this.onStateChangeEvent = event;

        return this;
    }

    @Override
    public Boolean get() {
        return this.state;
    }

    public void setState(boolean state){
        this.state = state;
    }

    public void onChange(PlayerEntity entity){
        this.onStateChangeEvent.onStateChange(entity, this.state);
    }

    public interface OnStateChangeEvent {
        void onStateChange(PlayerEntity player, boolean newState);
    }


}
