package com.mirax.world;
import java.util.*;
import com.mirax.physics.Body;
import com.mirax.enemies.Enemy;
public final class Level {
 public static final String[] WORLDS={"BOSQUE CIBERNÉTICO","CIUDAD NEÓN","VOLCÁN DE FUEGO","GALAXIA PERDIDA","FORTALEZA OSCURA"};
 public final int index,world;public final double width=3600;
 public final List<Body> solids=new ArrayList<>(),coins=new ArrayList<>(),hazards=new ArrayList<>();public final List<Enemy> enemies=new ArrayList<>();
 public final Body portal=new Body(3420,478,76,142),checkpoint=new Body(1730,540,35,80);
 public final Body educationalChest=new Body(1020,580,44,40),educationalDoor=new Body(2230,530,36,90);
 public Level(int index){if(index<0||index>=15)throw new IllegalArgumentException("Nivel fuera de rango");this.index=index;world=index/3;solids.add(new Body(0,620,width,120));
  for(int i=0;i<10;i++){double x=240+i*300,y=490-(i%3)*65;solids.add(new Body(x,y,175,22));for(int j=0;j<3;j++)coins.add(new Body(x+24+j*46,y-38,18,24));}
  for(int i=0;i<24;i++)coins.add(new Body(180+i*135,579,18,24));hazards.add(new Body(1350,603,68,17));hazards.add(new Body(2550,603,68,17));
  Enemy.Kind[] kinds=Enemy.Kind.values();for(int i=0;i<7;i++){Enemy.Kind kind=kinds[(i+index)%6];enemies.add(new Enemy(620+i*375,kind==Enemy.Kind.DRONE?420:576,kind));}
  if(index==14)enemies.add(new Enemy(3190,520,Enemy.Kind.BOSS));
 }
 public double gravity(){return world==3?900:1700;}
}
