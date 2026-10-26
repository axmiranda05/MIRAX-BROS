package com.mirax.physics;
import java.util.List;
public final class PhysicsEngine {
 private PhysicsEngine(){}
 /** Substeps prevent tunnelling even during a delayed frame. */
 public static void move(Body b,List<? extends Body> solids,double dt){
  int steps=Math.max(1,(int)Math.ceil(Math.max(Math.abs(b.vx*dt),Math.abs(b.vy*dt))/8));b.grounded=false;
  for(int i=0;i<steps;i++){
   b.x+=b.vx*dt/steps;
   for(Body s:solids)if(b.overlaps(s)){if(b.vx>0)b.x=s.x-b.w;else if(b.vx<0)b.x=s.x+s.w;b.vx=0;}
   b.y+=b.vy*dt/steps;
   for(Body s:solids)if(b.overlaps(s)){if(b.vy>0){b.y=s.y-b.h;b.grounded=true;}else if(b.vy<0)b.y=s.y+s.h;b.vy=0;}
  }
 }
 public static double approach(double value,double target,double step){return value<target?Math.min(target,value+step):Math.max(target,value-step);}
}