package com.mirax.game;
import javafx.animation.AnimationTimer;
/** Fixed simulation timestep with a bounded backlog after UI stalls. */
public final class GameLoop extends AnimationTimer {
 private final GameEngine engine;private long previous;private double accumulator;
 public GameLoop(GameEngine engine){this.engine=engine;}
 @Override public void handle(long now){if(previous==0)previous=now;accumulator+=Math.min(.1,(now-previous)/1e9);previous=now;while(accumulator>=1.0/120){engine.update(1.0/120);accumulator-=1.0/120;}engine.render();}
}