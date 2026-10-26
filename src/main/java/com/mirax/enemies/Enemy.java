package com.mirax.enemies;
import com.mirax.physics.Body;
import com.mirax.player.Player;
public final class Enemy extends Body {
 public enum Kind{PATROL,DRONE,CHASER,TURRET,JUMPER,GUARDIAN,BOSS}
 public enum State{PATROL,CHASE,ATTACK,HURT,DEAD}
 public final Kind kind;public State state=State.PATROL;public int health,direction=1;public double home,baseY,time,cooldown,hurt;
 public Enemy(double x,double y,Kind kind){super(x,y,kind==Kind.BOSS?90:42,kind==Kind.BOSS?100:44);this.kind=kind;home=x;baseY=y;health=kind==Kind.BOSS?400:kind==Kind.GUARDIAN?100:60;}
 public boolean hit(int amount){if(hurt>0||health<=0)return false;health-=amount;hurt=.15;state=health<=0?State.DEAD:State.HURT;return health<=0;}
 public void update(double dt,Player p){time+=dt;cooldown=Math.max(0,cooldown-dt);hurt=Math.max(0,hurt-dt);if(health<=0){state=State.DEAD;return;}if(hurt>0){state=State.HURT;return;}
  double distance=Math.abs(p.x-x);state=distance<440?State.CHASE:State.PATROL;
  if(kind==Kind.TURRET||kind==Kind.BOSS){state=distance<700?State.ATTACK:State.PATROL;return;}
  if(kind==Kind.DRONE)y=baseY+Math.sin(time*2.8)*44;if(kind==Kind.JUMPER)y=baseY-Math.abs(Math.sin(time*2))*95;
  if(kind==Kind.CHASER&&distance<380)direction=p.x>x?1:-1;else if(x>home+110)direction=-1;else if(x<home-110)direction=1;
  x+=direction*(kind==Kind.CHASER?115:65)*dt;x=Math.max(home-115,Math.min(home+115,x));
 }
}