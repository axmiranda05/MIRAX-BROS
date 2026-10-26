package com.mirax.player;
import com.mirax.physics.*;
import javafx.scene.input.KeyCode;
import java.util.*;
public final class Player extends Body {
 public int facing=1,jumps,health=100,coins,score,lives=3;
 public double energy=100,invulnerable,attack,shootCooldown,slide,shield,dashCooldown,coyote,jumpBuffer;
 public Player(){super(80,530,36,60);}
 public void update(double dt,Set<KeyCode> keys,Set<KeyCode> pressed,List<? extends Body> solids,double gravity){
  invulnerable=Math.max(0,invulnerable-dt);attack=Math.max(0,attack-dt);shootCooldown=Math.max(0,shootCooldown-dt);dashCooldown=Math.max(0,dashCooldown-dt);slide=Math.max(0,slide-dt);shield=Math.max(0,shield-dt);
  coyote=grounded?.10:Math.max(0,coyote-dt);if(grounded)jumps=0;jumpBuffer=pressed.contains(KeyCode.SPACE)?.12:Math.max(0,jumpBuffer-dt);
  int direction=(keys.contains(KeyCode.D)||keys.contains(KeyCode.RIGHT)?1:0)-(keys.contains(KeyCode.A)||keys.contains(KeyCode.LEFT)?1:0);if(direction!=0)facing=direction;
  double target=direction*(keys.contains(KeyCode.SHIFT)?430:285);
  if(pressed.contains(KeyCode.L)&&grounded&&energy>=10){slide=.28;energy-=10;}
  vx=slide>0?facing*540:PhysicsEngine.approach(vx,target,(grounded?2400:1600)*dt);
  if(jumpBuffer>0&&(coyote>0||jumps<2)){vy=jumps==0?-650:-590;jumps++;coyote=0;jumpBuffer=0;grounded=false;}
  if(pressed.contains(KeyCode.Q)&&energy>=20&&dashCooldown==0){vx=facing*950;vy=Math.min(0,vy);energy-=20;dashCooldown=.65;}
  if(pressed.contains(KeyCode.F)&&energy>=20){shield=.8;energy-=20;}
  vy=Math.min(950,vy+gravity*dt);PhysicsEngine.move(this,solids,dt);energy=Math.min(100,energy+13*dt);
 }
 public boolean damage(int amount){if(invulnerable>0||shield>0)return false;health-=amount;invulnerable=1.1;return true;}
}