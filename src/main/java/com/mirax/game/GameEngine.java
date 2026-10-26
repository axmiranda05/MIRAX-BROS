package com.mirax.game;
import com.mirax.player.Player;
import com.mirax.education.*;
import com.mirax.physics.*;
import com.mirax.enemies.Enemy;
import com.mirax.world.Level;
import com.mirax.save.SaveManager;
import com.mirax.ui.Art;
import javafx.scene.canvas.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.util.*;
public final class GameEngine {
 private enum Screen{MENU,GAME,PAUSE,CONTROLS,SETTINGS,CREDITS,SELECT,WIN,LOSE}
 private final EducationSystem education;private int recoveryAttempts;private boolean portalPassed,bossPassed,worldPassed,chestOpened,doorOpened;
 private final Stage stage;private final GraphicsContext g;private final GameLoop loop;
 private final Set<KeyCode> keys=EnumSet.noneOf(KeyCode.class),pressed=EnumSet.noneOf(KeyCode.class);
 private final SaveManager saves=new SaveManager(Path.of(System.getProperty("mirax.save","saves/mirax-java.json")));private SaveManager.Progress progress=new SaveManager.Progress(0,0,0,0);
 private Screen screen=Screen.MENU;private Player player;private Level level;private int selected,unlocked;private long frames;private double time,camera,shake,checkpointX=80;private boolean checkpoint,closed;private String notice="";
 private final List<Bolt> bolts=new ArrayList<>();private final List<Particle> particles=new ArrayList<>();private final Random random=new Random(41);
 private static final String[] MENU={"NUEVA PARTIDA","CONTINUAR","SELECCIONAR MUNDO","CONFIGURACIÓN","CONTROLES","CRÉDITOS","SALIR"};
 private static final class Bolt extends Body{boolean hostile;double life=2;Bolt(double x,double y,double vx,boolean hostile){super(x,y,16,8);this.vx=vx;this.hostile=hostile;}}
 private static final class Particle{double x,y,vx,vy,life=.6;Color color;Particle(double x,double y,double vx,double vy,Color c){this.x=x;this.y=y;this.vx=vx;this.vy=vy;color=c;}}
 public GameEngine(Canvas canvas,Stage stage){this.stage=stage;g=canvas.getGraphicsContext2D();loop=new GameLoop(this);education=new EducationSystem(this::startEducation,()->screen=Screen.MENU,this::educationFinished);try{progress=saves.load();unlocked=progress.unlocked();}catch(Exception e){notice="No se pudo leer el guardado: "+e.getMessage();}}
 /** Drives the actual input/simulation path; used only by the opt-in smoke test. */
 public String verifyFirstLevel(){
  load(0,false);
  for(int i=0;i<120;i++)update(1.0/120);
  press(KeyCode.SPACE);update(1.0/120);release(KeyCode.SPACE);
  for(int i=0;i<15;i++)update(1.0/120);
  press(KeyCode.SPACE);update(1.0/120);release(KeyCode.SPACE);
  if(player.jumps!=2)throw new IllegalStateException("Doble salto no registrado");
  for(int i=0;i<200;i++)update(1.0/120);
  if(!player.grounded)throw new IllegalStateException("No aterriza sobre el suelo");
  press(KeyCode.ESCAPE);update(1.0/120);release(KeyCode.ESCAPE);
  if(screen!=Screen.PAUSE)throw new IllegalStateException("Pausa no registrada");
  double pausedX=player.x;update(.1);if(player.x!=pausedX)throw new IllegalStateException("Movimiento durante pausa");
  press(KeyCode.ENTER);update(1.0/120);release(KeyCode.ENTER);
  press(KeyCode.D);press(KeyCode.SHIFT);
  for(int i=0;i<5000&&screen==Screen.GAME;i++){
   if(i%36==0)press(KeyCode.K);if(i%36==1)release(KeyCode.K);
   if(i%45==0)press(KeyCode.J);if(i%45==1)release(KeyCode.J);
   if(player.x>level.portal.x-20)press(KeyCode.E);
   update(1.0/120);
  }
  keys.clear();pressed.clear();
  if(screen!=Screen.WIN)throw new IllegalStateException("No llega a victoria: "+screen+" x="+player.x+" vidas="+player.lives);
  if(player.coins<1||player.score<500)throw new IllegalStateException("Recompensas no registradas");
  return "victoria=true coins="+player.coins+" score="+player.score+" lives="+player.lives;
 }
 /** Exercises the educational UI and real death/portal hooks on the JavaFX thread. */
 public String verifyEducationFlow()throws java.io.IOException{
  if(!Boolean.getBoolean("mirax.smoke"))throw new IllegalStateException("Solo prueba de humo");
  education.newGame();education.typed("Prueba "+System.nanoTime());education.key(KeyCode.ENTER);
  education.key(KeyCode.RIGHT);education.key(KeyCode.DOWN);capture(Path.of("target/smoke-secondary.png"));
  education.key(KeyCode.LEFT);education.key(KeyCode.UP);education.key(KeyCode.ENTER);capture(Path.of("target/smoke-courses.png"));education.key(KeyCode.ENTER);
  if(!education.hasProfile()||education.progress().grade!=1||education.progress().level!=SchoolLevel.PRIMARIA)throw new IllegalStateException("Selección educativa incorrecta");
  for(int i=0;i<3;i++){
   if(i==0){player.lives=1;player.health=0;update(.008);capture(Path.of("target/smoke-question.png"));}
   education.smokeAnswer(false);education.key(KeyCode.ENTER);
  }
  if(screen!=Screen.LOSE||education.active()||recoveryAttempts!=3)throw new IllegalStateException("No limita los tres intentos incorrectos");
  load(0,false);checkpoint=true;checkpointX=1690;
  for(int i=0;i<3;i++){player.lives=1;player.health=0;update(.008);education.smokeAnswer(true);education.key(KeyCode.ENTER);if(screen!=Screen.GAME||player.lives!=1||player.health!=100||player.x!=1690)throw new IllegalStateException("Recuperación incorrecta");}
  player.health=0;update(.008);if(education.active()||screen!=Screen.LOSE)throw new IllegalStateException("Permite más de tres recuperaciones");
  load(0,false);level.enemies.forEach(e->e.health=0);player.x=level.portal.x;player.y=550;
  press(KeyCode.E);update(.008);release(KeyCode.E);
  education.smokeAnswer(true);education.smokeAnswer(true);education.key(KeyCode.ENTER);
  if(education.progress().unlocked!=0)throw new IllegalStateException("El portal desbloquea el nivel sin evaluación final");
  press(KeyCode.E);update(.008);release(KeyCode.E);
  education.smokeAnswer(true);education.smokeAnswer(false);education.smokeAnswer(false);capture(Path.of("target/smoke-statistics.png"));education.key(KeyCode.ENTER);
  if(screen!=Screen.LOSE||education.progress().unlocked!=0)throw new IllegalStateException("Una respuesta permite avanzar");
  load(0,false);level.enemies.forEach(e->e.health=0);player.x=level.portal.x;player.y=550;
  press(KeyCode.E);update(.008);release(KeyCode.E);education.smokeAnswer(true);education.smokeAnswer(true);education.key(KeyCode.ENTER);
  press(KeyCode.E);update(.008);release(KeyCode.E);education.smokeAnswer(true);education.smokeAnswer(true);education.smokeAnswer(false);education.key(KeyCode.ENTER);
  if(screen!=Screen.WIN||education.progress().unlocked!=1||education.progress().stars.get(0)!=2)throw new IllegalStateException("No desbloquea con dos aciertos");
  education.save();EducationProgress saved=new EducationSaveManager(Path.of(System.getProperty("mirax.education.save"))).latest();
  if(saved.unlocked!=1||saved.answered!=16)throw new IllegalStateException("No conserva estadísticas educativas: "+saved.answered);
  load(0,false);player.x=level.educationalChest.x;press(KeyCode.E);update(.008);release(KeyCode.E);education.smokeAnswer(true);education.key(KeyCode.ENTER);
  if(!chestOpened)throw new IllegalStateException("No abre el cofre");
  player.x=level.educationalDoor.x;press(KeyCode.E);update(.008);release(KeyCode.E);education.smokeAnswer(true);education.key(KeyCode.ENTER);
  if(!doorOpened||player.shield<=0)throw new IllegalStateException("No activa puerta/poder");
  education.progress().unlocked=2;load(2,false);level.enemies.forEach(e->e.health=0);player.x=level.portal.x;player.y=550;
  press(KeyCode.E);update(.008);release(KeyCode.E);for(int i=0;i<2;i++)education.smokeAnswer(true);education.key(KeyCode.ENTER);
  press(KeyCode.E);update(.008);release(KeyCode.E);for(int i=0;i<5;i++)education.smokeAnswer(true);education.key(KeyCode.ENTER);
  if(!worldPassed||education.progress().worlds.contains(1))throw new IllegalStateException("Mundo desbloqueado antes de completar el sector");
  press(KeyCode.E);update(.008);release(KeyCode.E);for(int i=0;i<3;i++)education.smokeAnswer(true);education.key(KeyCode.ENTER);
  if(education.progress().unlocked!=3||!education.progress().worlds.contains(1))throw new IllegalStateException("No desbloquea el mundo");
  education.progress().unlocked=14;load(14,false);level.enemies.forEach(e->e.health=0);player.x=level.portal.x;player.y=550;
  press(KeyCode.E);update(.008);release(KeyCode.E);for(int i=0;i<3;i++)education.smokeAnswer(true);education.key(KeyCode.ENTER);
  if(!bossPassed)throw new IllegalStateException("No evalúa al jefe");
  press(KeyCode.E);update(.008);release(KeyCode.E);for(int i=0;i<2;i++)education.smokeAnswer(true);education.key(KeyCode.ENTER);
  press(KeyCode.E);update(.008);release(KeyCode.E);for(int i=0;i<3;i++)education.smokeAnswer(true);capture(Path.of("target/smoke-rewards.png"));education.key(KeyCode.ENTER);
  if(!education.progress().characters.contains("MIRAX ESMERALDA")||education.progress().powers.isEmpty())throw new IllegalStateException("No entrega recompensas");
  return "educacion=true recuperaciones=3 estrellas=2 cofre=true puerta=true mundo=true jefe=true recompensas=true respuestas="+education.progress().answered;
 }
 public void capture(Path path)throws java.io.IOException{
  render();javafx.scene.image.WritableImage image=g.getCanvas().snapshot(null,null);
  java.awt.image.BufferedImage png=new java.awt.image.BufferedImage(1280,720,java.awt.image.BufferedImage.TYPE_INT_ARGB);
  for(int y=0;y<720;y++)for(int x=0;x<1280;x++)png.setRGB(x,y,image.getPixelReader().getArgb(x,y));
  java.nio.file.Files.createDirectories(path.toAbsolutePath().getParent());javax.imageio.ImageIO.write(png,"png",path.toFile());
 }
 public void start(){loop.start();}public long frames(){return frames;}public double playerY(){return player==null?-1:player.y;}
 public void typed(String text){education.typed(text);}
 public void press(KeyCode code){if(education.active()){education.key(code);keys.clear();pressed.clear();return;}if(keys.add(code))pressed.add(code);}public void release(KeyCode code){keys.remove(code);}
 public void focusLost(){keys.clear();pressed.clear();if(screen==Screen.GAME)screen=Screen.PAUSE;}
 public void mouse(double x,double y){if(!education.active()&&screen==Screen.MENU&&x>=90&&x<=470&&y>=255&&y<612)selected=(int)((y-255)/51);}
 public void click(double x,double y){if(education.active()){education.click(x,y);return;}mouse(x,y);if(!education.active()&&screen==Screen.MENU&&x>=90&&x<=470&&y>=255&&y<612)activate();else if(screen==Screen.SELECT){int col=(int)((x-110)/220),row=(int)((y-220)/100);if(x>=110&&col<5&&row>=0&&row<3&&y>=220){int index=col*3+row;if(index<=unlocked)load(index,false);}}}
 private void load(int index,boolean carry){education.newLevel();recoveryAttempts=0;portalPassed=bossPassed=worldPassed=chestOpened=doorOpened=false;if(education.hasProfile()){education.progress().currentLevel=index;education.save();}int score=carry&&player!=null?player.score:0,coins=carry&&player!=null?player.coins:0,lives=carry&&player!=null?player.lives:3;level=new Level(index);player=new Player();player.score=score;player.coins=coins;player.lives=lives;if(education.hasProfile()){player.score=education.progress().score;player.coins=education.progress().coins;if(education.progress().powers.contains("ENERGÍA ESCOLAR"))player.shield=8;}camera=0;checkpoint=false;checkpointX=80;bolts.clear();particles.clear();screen=Screen.GAME;keys.clear();pressed.clear();}
 private void activate(){switch(selected){case 0->education.newGame();case 1->education.continueGame();case 2->{if(!education.hasProfile())education.continueGame();if(!education.active()&&education.hasProfile()){unlocked=education.progress().unlocked;screen=Screen.SELECT;}}case 3->screen=Screen.SETTINGS;case 4->screen=Screen.CONTROLS;case 5->screen=Screen.CREDITS;case 6->{close();stage.close();}}}
 public void update(double dt){time+=dt;if(education.active()){education.update(dt);pressed.clear();keys.clear();return;}
  if(screen==Screen.MENU){if(pressed.contains(KeyCode.DOWN)||pressed.contains(KeyCode.S))selected=(selected+1)%7;if(pressed.contains(KeyCode.UP)||pressed.contains(KeyCode.W))selected=(selected+6)%7;if(pressed.contains(KeyCode.ENTER))activate();}
  else if(screen==Screen.GAME){if(pressed.contains(KeyCode.ESCAPE)){screen=Screen.PAUSE;keys.clear();}else if(pressed.contains(KeyCode.R))load(level.index,false);else simulate(dt);}
  else if(screen==Screen.PAUSE){if(pressed.contains(KeyCode.ESCAPE)||pressed.contains(KeyCode.ENTER))screen=Screen.GAME;if(pressed.contains(KeyCode.M)){save();screen=Screen.MENU;}}
  else if(screen==Screen.WIN){if(pressed.contains(KeyCode.ENTER)){if(level.index<14)load(level.index+1,true);else screen=Screen.MENU;}if(pressed.contains(KeyCode.ESCAPE))screen=Screen.MENU;}
  else if(screen==Screen.LOSE){if(pressed.contains(KeyCode.ENTER)||pressed.contains(KeyCode.R))load(level.index,false);if(pressed.contains(KeyCode.ESCAPE))screen=Screen.MENU;}
  else{if(pressed.contains(KeyCode.ESCAPE)||pressed.contains(KeyCode.ENTER))screen=Screen.MENU;if(screen==Screen.SETTINGS&&pressed.contains(KeyCode.F11))stage.setFullScreen(!stage.isFullScreen());}
  pressed.clear();
 }
 private void simulate(double dt){
  if(education.hasProfile()&&pressed.contains(KeyCode.C)&&education.progress().characters.contains("MIRAX ESMERALDA")){education.progress().activeCharacter=education.progress().activeCharacter.equals("MIRAX")?"MIRAX ESMERALDA":"MIRAX";education.save();}
  if(education.hasProfile()&&pressed.contains(KeyCode.E)){if(!chestOpened&&Math.abs(player.x-level.educationalChest.x)<85){education.begin(Challenge.COFRE);return;}if(!doorOpened&&Math.abs(player.x-level.educationalDoor.x)<85){education.begin(Challenge.PUERTA);return;}}
  boolean jumped=pressed.contains(KeyCode.SPACE);double oldY=player.y;player.update(dt,keys,pressed,level.solids,level.gravity());player.x=Math.max(0,Math.min(level.width-player.w,player.x));if(jumped&&player.vy<0)burst(player.x+18,player.y+60,Art.CYAN);
  if(pressed.contains(KeyCode.J)&&player.attack==0){player.attack=.28;Body hit=new Body(player.facing>0?player.x+25:player.x-65,player.y-6,76,72);for(Enemy e:level.enemies)if(e.health>0&&hit.overlaps(e))damage(e,40);}
  if(pressed.contains(KeyCode.K)&&player.energy>=12&&player.shootCooldown==0){player.energy-=12;player.shootCooldown=.22;bolts.add(new Bolt(player.x+18+player.facing*22,player.y+23,player.facing*800,false));}
  if(pressed.contains(KeyCode.T)&&player.energy>=25&&player.dashCooldown==0){double original=player.x;player.vx=player.facing*1000;PhysicsEngine.move(player,level.solids,.12);player.x=Math.max(0,Math.min(level.width-player.w,player.x));player.energy-=25;player.dashCooldown=.8;burst(original,player.y,Art.CYAN);}
  for(Iterator<Body> i=level.coins.iterator();i.hasNext();){Body c=i.next();if(player.overlaps(c)){i.remove();player.coins++;player.score+=25;if(education.hasProfile()){education.progress().coins++;education.progress().score+=25;}burst(c.x,c.y,Art.GOLD);}}
  if(!checkpoint&&player.overlaps(level.checkpoint)){checkpoint=true;checkpointX=1690;player.health=100;burst(player.x,player.y,Art.CYAN);save();}
  for(Body h:level.hazards)if(player.overlaps(h))hurt(22);
  for(Enemy e:level.enemies){e.update(dt,player);if(e.health<=0)continue;
   if((e.kind==Enemy.Kind.TURRET||e.kind==Enemy.Kind.BOSS)&&e.state==Enemy.State.ATTACK&&e.cooldown==0){bolts.add(new Bolt(e.x,e.y+20,player.x<e.x?-300:300,true));e.cooldown=e.kind==Enemy.Kind.BOSS?.65:1.5;}
   if(player.overlaps(e)){if(player.vy>80&&oldY+player.h<=e.y+14){damage(e,60);player.vy=-430;}else hurt(16);}
  }
  for(Iterator<Bolt> i=bolts.iterator();i.hasNext();){Bolt b=i.next();b.x+=b.vx*dt;b.life-=dt;boolean remove=b.life<=0;for(Body s:level.solids)if(b.overlaps(s))remove=true;
   if(!remove&&b.hostile&&b.overlaps(player)){hurt(14);remove=true;}if(!remove&&!b.hostile)for(Enemy e:level.enemies)if(e.health>0&&b.overlaps(e)){damage(e,35);remove=true;break;}if(remove)i.remove();
  }
  for(Iterator<Particle> i=particles.iterator();i.hasNext();){Particle p=i.next();p.life-=dt;p.x+=p.vx*dt;p.y+=p.vy*dt;p.vy+=300*dt;if(p.life<=0)i.remove();}
  if(player.y>800)player.health=0;
  if(player.health<=0){player.lives--;burst(player.x,player.y,Color.SALMON);if(player.lives<=0){screen=Screen.LOSE;if(education.hasProfile()&&recoveryAttempts<3){recoveryAttempts++;education.begin(Challenge.RECUPERACION);}}else{player.x=checkpointX;player.y=530;player.vx=player.vy=0;player.health=100;player.energy=100;player.invulnerable=2;bolts.clear();}}
  boolean bossAlive=level.enemies.stream().anyMatch(e->e.kind==Enemy.Kind.BOSS&&e.health>0);
  if(screen==Screen.GAME&&player.overlaps(level.portal)&&!bossAlive&&pressed.contains(KeyCode.E)){if(education.hasProfile()){if(!bossPassed&&level.enemies.stream().anyMatch(e->e.kind==Enemy.Kind.BOSS))education.begin(Challenge.JEFE);else if(!portalPassed)education.begin(Challenge.PORTAL);else if(level.index%3==2&&level.index<14&&!worldPassed)education.begin(Challenge.MUNDO);else education.begin(Challenge.NIVEL);}else{unlocked=Math.max(unlocked,Math.min(14,level.index+1));player.score+=500;saveCompleted();screen=Screen.WIN;}burst(player.x,player.y,Art.GOLD);}
  camera=PhysicsEngine.approach(camera,Math.max(0,Math.min(level.width-1280,player.x-430)),1000*dt);shake=Math.max(0,shake-dt);
 }
 private void startEducation(){unlocked=education.progress().unlocked;load(education.progress().currentLevel,false);}
 private void educationFinished(Challenge type,boolean passed){
  keys.clear();pressed.clear();player.score=education.progress().score;player.coins=education.progress().coins;
  if(type==Challenge.RECUPERACION){if(passed){player.lives=1;player.health=100;player.energy=100;player.x=checkpointX;player.y=530;player.vx=player.vy=0;player.invulnerable=2;bolts.clear();screen=Screen.GAME;}else{if(recoveryAttempts<3){recoveryAttempts++;education.begin(Challenge.RECUPERACION);}else screen=Screen.LOSE;}return;}
  if(!passed){if(type!=Challenge.COFRE&&type!=Challenge.PUERTA)screen=Screen.LOSE;return;}
  if(type==Challenge.COFRE){chestOpened=true;player.coins+=50;education.progress().coins+=50;education.save();}
  if(type==Challenge.PUERTA){doorOpened=true;player.shield=10;}
  if(type==Challenge.PORTAL)portalPassed=true;
  else if(type==Challenge.JEFE)bossPassed=true;
  else if(type==Challenge.MUNDO)worldPassed=true;
  else if(type==Challenge.NIVEL){unlocked=education.progress().unlocked;player.score+=500;education.progress().score+=500;education.save();saveCompleted();screen=Screen.WIN;}
 }
 private void hurt(int amount){if(player.damage(amount)){shake=.22;burst(player.x+18,player.y+25,Color.SALMON);}}
 private void damage(Enemy enemy,int amount){if(enemy.hit(amount)){player.score+=150;if(education.hasProfile())education.progress().score+=150;player.energy=Math.min(100,player.energy+15);}burst(enemy.x+20,enemy.y+20,Art.GOLD);}
 private void burst(double x,double y,Color color){for(int i=0;i<12&&particles.size()<350;i++)particles.add(new Particle(x,y,(random.nextDouble()-.5)*260,(random.nextDouble()-.7)*260,color));}
 private void saveCompleted(){progress=new SaveManager.Progress(Math.min(14,level.index+1),unlocked,player.score,player.coins);writeSave();}
 private void save(){education.save();if(level!=null&&player!=null){progress=new SaveManager.Progress(level.index,unlocked,player.score,player.coins);writeSave();}}
 private void writeSave(){try{saves.save(progress);notice="";}catch(Exception e){notice="Guardado no disponible: "+e.getMessage();}}
 public void close(){if(closed)return;closed=true;if(screen==Screen.GAME||screen==Screen.PAUSE)save();education.save();loop.stop();}
 public void render(){if(education.active()){frames++;education.draw(g);return;}frames++;g.setGlobalAlpha(1);g.setLineWidth(1);Art.background(g,camera,time,level!=null&&screen!=Screen.MENU?level.world:0);if(screen==Screen.GAME||screen==Screen.PAUSE||screen==Screen.WIN||screen==Screen.LOSE)drawGame();else drawMenu();if(!notice.isEmpty())Art.text(g,notice,35,700,14,Color.SALMON);}
 private void drawGame(){
  g.save();g.translate(-camera+(shake>0?Math.sin(time*110)*5:0),0);
  for(Body s:level.solids){if(s.x+s.w<camera||s.x>camera+1280)continue;g.setFill(Color.web("#142c3e"));g.fillRoundRect(s.x,s.y,s.w,s.h,6,6);g.setFill(Color.web("#285363"));g.fillRect(s.x,s.y+5,s.w,6);g.setFill(Art.CYAN);g.fillRect(s.x,s.y,s.w,3);g.setStroke(Color.web("#356375",.35));for(double x=s.x;x<Math.min(s.x+s.w,camera+1320);x+=48)if(x>camera-60){g.strokeLine(x,s.y+15,x+24,s.y+35);g.strokeLine(x+24,s.y+35,x+24,s.y+70);}}
  for(Body h:level.hazards){g.setFill(Color.web("#fc7288"));for(double x=h.x;x<h.x+h.w;x+=17)g.fillPolygon(new double[]{x,x+8,x+16},new double[]{620,600,620},3);}
  for(Body c:level.coins){if(c.x<camera-20||c.x>camera+1300)continue;double bob=Math.sin(time*4+c.x)*3;g.setStroke(Art.GOLD);g.setLineWidth(2);g.strokeOval(c.x,c.y+bob,18,24);g.setFill(Art.GOLD);g.fillPolygon(new double[]{c.x+9,c.x+14,c.x+9,c.x+4},new double[]{c.y+4+bob,c.y+12+bob,c.y+20+bob,c.y+12+bob},4);}
  if(education.hasProfile()){Body chest=level.educationalChest;g.setFill(chestOpened?Color.web("#235d46"):Art.GOLD);g.fillRoundRect(chest.x,chest.y,chest.w,chest.h,8,8);Art.text(g,"E · COFRE",chest.x-20,chest.y-15,14,Art.GOLD);Body door=level.educationalDoor;g.setStroke(doorOpened?Color.LIMEGREEN:Art.CYAN);g.setLineWidth(4);g.strokeRoundRect(door.x,door.y,door.w,door.h,8,8);Art.text(g,"E · PUERTA DE ENERGÍA",door.x-65,door.y-15,14,Art.CYAN);}
  Body cp=level.checkpoint;g.setFill(Color.web("#37536b"));g.fillRect(cp.x,cp.y,5,80);g.setFill(checkpoint?Art.CYAN:Color.web("#657889"));g.fillPolygon(new double[]{cp.x+5,cp.x+38,cp.x+5},new double[]{cp.y,cp.y+13,cp.y+26},3);
  Body p=level.portal;g.setStroke(Color.web("#2c6570"));g.setLineWidth(13);g.strokeOval(p.x,p.y,p.w,p.h);g.setStroke(Art.CYAN);g.setLineWidth(3);g.strokeOval(p.x+5,p.y+5,p.w-10,p.h-10);g.setFill(Color.web("#67f9de",.10+.06*Math.sin(time*3)));g.fillOval(p.x+8,p.y+8,p.w-16,p.h-16);Art.text(g,"E · PORTAL",p.x-15,p.y-18,16,Art.CYAN);
  for(Enemy e:level.enemies)if(e.health>0&&e.x>camera-100&&e.x<camera+1400)drawEnemy(e);
  for(Bolt b:bolts){g.setFill(b.hostile?Color.SALMON:Art.CYAN);g.fillRoundRect(b.x,b.y,b.w,b.h,8,8);g.setFill(Color.WHITE);g.fillRect(b.x+4,b.y+2,8,3);}
  for(Particle q:particles){g.setGlobalAlpha(Math.max(0,q.life/.6));g.setFill(q.color);g.fillRect(q.x,q.y,4,4);}g.setGlobalAlpha(1);
  if(player.invulnerable<=0||(int)(time*14)%2==0){g.save();if(education.hasProfile()&&"MIRAX ESMERALDA".equals(education.progress().activeCharacter))g.setEffect(new javafx.scene.effect.ColorAdjust(.35,.2,.1,0));Art.hero(g,player.x,player.y,player.facing,time,Math.abs(player.vx)>20,player.shield>0);g.restore();}
  if(player.attack>0){g.setStroke(Art.CYAN);g.setLineWidth(5);g.strokeArc(player.x+(player.facing>0?0:-50),player.y-12,90,80,player.facing>0?-70:110,140,javafx.scene.shape.ArcType.OPEN);}g.restore();hud();if(education.hasProfile())Art.text(g,education.progress().schoolGrade().label()+" · "+education.progress().course.label+" · Recuperaciones "+recoveryAttempts+"/3 · C cambiar personaje desbloqueado",30,647,16,Art.CYAN);
  if(screen==Screen.PAUSE)overlay("SISTEMA EN PAUSA","ENTER / ESC · continuar     M · guardar y volver al menú");
  if(screen==Screen.WIN)overlay(level.index==14?"VICTORIA · MIRAX BROS":"SECTOR COMPLETADO","+500 puntos · Progreso guardado     ENTER · continuar     ESC · menú");
  if(screen==Screen.LOSE)overlay("SEÑAL PERDIDA","ENTER / R · reintentar     ESC · menú");
 }
 private void drawEnemy(Enemy e){
  g.save();g.translate(e.x,e.y);g.setFill(Color.web("#000000",.2));g.fillOval(-4,e.h-4,e.w+8,12);Color armor=e.hurt>0?Color.WHITE:e.kind==Enemy.Kind.BOSS?Color.web("#c45ca1"):Color.web("#9a5d7e");
  if(e.kind==Enemy.Kind.DRONE){g.setStroke(Color.web("#ff92b7"));g.setLineWidth(3);g.strokeLine(-15,9,e.w+15,9);g.strokeOval(-22,3,26,9);g.strokeOval(e.w-4,3,26,9);}
  g.setFill(Color.web("#273247"));g.fillRoundRect(2,e.h-14,13,15,5,5);g.fillRoundRect(e.w-15,e.h-14,13,15,5,5);g.setFill(armor);g.fillPolygon(new double[]{0,8,e.w-8,e.w,e.w-5,5},new double[]{8,0,0,8,e.h-9,e.h-9},6);g.setFill(Color.web("#211c35"));g.fillRoundRect(5,10,e.w-10,14,4,4);g.setFill(Color.web("#ffb482"));g.fillRect(10,14,e.w-20,4);
  if(e.kind==Enemy.Kind.TURRET){g.setFill(Color.web("#788199"));g.fillRect(-12,22,30,9);}if(e.kind==Enemy.Kind.GUARDIAN){g.setStroke(Color.web("#d290e8"));g.setLineWidth(3);g.strokeArc(-12,-8,e.w+24,e.h+16,80,190,javafx.scene.shape.ArcType.OPEN);}
  g.setFill(Color.web("#211c35"));g.fillRect(0,-12,e.w,4);g.setFill(Color.SALMON);g.fillRect(0,-12,e.w*Math.max(0,e.health)/(e.kind==Enemy.Kind.BOSS?400:e.kind==Enemy.Kind.GUARDIAN?100:60),4);g.restore();
 }
 private void hud(){g.setFill(Color.web("#071120",.92));g.fillRoundRect(24,20,1232,88,16,16);Art.text(g,"MIRAX",44,47,18,Art.CYAN);bar(44,58,180,10,player.health/100.0,Art.CYAN);bar(44,78,180,6,player.energy/100,Color.web("#7aaaff"));Art.text(g,"VIDAS  "+player.lives,253,57,17,Color.WHITE);Art.text(g,"NIVEL  "+(level.index/3+1)+" - "+(level.index%3+1),410,48,17,Art.CYAN);Art.text(g,Level.WORLDS[level.world],410,77,17,Color.WHITE);Art.text(g,"◈  "+player.coins,945,54,22,Art.GOLD);Art.text(g,String.format("%07d",player.score),1090,54,22,Color.WHITE);Art.text(g,"PUNTOS",1112,79,12,Color.web("#819aac"));g.setFill(Color.web("#081426",.9));g.fillRoundRect(24,665,1232,36,8,8);Art.text(g,"A / D  MOVER     SHIFT  CORRER     ESPACIO  DOBLE SALTO     J  ATAQUE     K  ENERGÍA     F  ESCUDO     Q  IMPULSO     T  TELEPORT     ESC  PAUSA",42,689,13,Color.web("#9ebecb"));}
 private void bar(double x,double y,double w,double h,double ratio,Color color){g.setFill(Color.web("#26364b"));g.fillRoundRect(x,y,w,h,h,h);g.setFill(color);g.fillRoundRect(x,y,w*Math.max(0,ratio),h,h,h);}
 private void overlay(String title,String subtitle){g.setFill(Color.web("#030818",.82));g.fillRect(0,0,1280,720);Art.text(g,title,180,330,46,Art.CYAN);Art.text(g,subtitle,180,390,19,Color.WHITE);}
 private void drawMenu(){
  if(screen==Screen.MENU){Art.text(g,"M I R A X   /   E X P E D I T I O N   0 1",92,72,15,Art.CYAN);Art.text(g,"MIRAX",86,158,88,Color.web("#e5fcff"));Art.text(g,"BROS",405,158,88,Art.CYAN);Art.text(g,"U L T I M A T E   A D V E N T U R E",93,199,19,Color.web("#9ebdce"));g.setFill(Art.CYAN);g.fillRect(92,221,65,3);
   for(int i=0;i<7;i++){double y=255+i*51;boolean on=i==selected;g.setFill(Color.web(on?"#20525b":"#0b1c2c",on?.95:.8));g.fillRoundRect(90,y,380,42,8,8);if(on){g.setFill(Art.CYAN);g.fillRoundRect(90,y,4,42,3,3);}Art.text(g,String.format("%02d",i+1),110,y+27,13,on?Art.CYAN:Color.web("#506d80"));Art.text(g,MENU[i],150,y+28,18,on?Color.WHITE:Color.web("#8eafbf"));if(on)Art.text(g,"›",438,y+29,26,Art.CYAN);}
   g.save();g.translate(850,360+Math.sin(time*1.8)*10);g.scale(3.6,3.6);Art.hero(g,0,0,1,time,false,true);g.restore();Art.text(g,"EL BOSQUE ESTÁ DESPERTANDO.",727,585,18,Art.CYAN);Art.text(g,"Recupera la energía. Abre el portal.",727,615,17,Color.web("#9abacb"));Art.text(g,"↑ ↓  SELECCIONAR    ENTER  CONFIRMAR    /    RATÓN",92,675,13,Color.web("#6f93a7"));return;
  }
  Art.text(g,"MIRAX / SISTEMAS",90,90,24,Art.CYAN);Art.text(g,"ESC / ENTER · volver al menú",90,665,17,Color.web("#a6bbcb"));
  if(screen==Screen.SELECT){Art.text(g,"SELECCIONAR SECTOR",90,160,40,Color.WHITE);for(int w=0;w<5;w++){Art.text(g,"MUNDO 0"+(w+1),110+w*220,205,18,Art.CYAN);for(int l=0;l<3;l++){int id=w*3+l;double x=110+w*220,y=220+l*100;g.setFill(Color.web(id<=unlocked?"#20525b":"#182334"));g.fillRoundRect(x,y,195,80,10,10);Art.text(g,id<=unlocked?"NIVEL "+(l+1):"BLOQUEADO",x+20,y+46,18,id<=unlocked?Color.WHITE:Color.web("#627384"));}}}
  if(screen==Screen.CONTROLS){Art.text(g,"CONTROLES",90,165,40,Color.WHITE);String[] lines={"A / D / FLECHAS · Movimiento     SHIFT · Correr","ESPACIO · Saltar / Doble salto (soltar entre saltos)","J · Ataque de pulso     K · Proyectil de energía","L · Deslizar     F · Escudo     Q · Impulso aéreo","T · Teleportación corta con colisiones","E · Activar portal     R · Reiniciar nivel","ESC · Pausa     M desde pausa · Guardar y volver"};for(int i=0;i<lines.length;i++)Art.text(g,lines[i],95,240+i*48,23,Color.web("#c3dce6"));}
  if(screen==Screen.SETTINGS){Art.text(g,"CONFIGURACIÓN",90,165,40,Color.WHITE);Art.text(g,"F11 · Alternar pantalla completa",95,260,25,Color.WHITE);Art.text(g,"Resolución interna 1280 × 720 · Escala proporcional",95,320,23,Color.web("#9dbcca"));Art.text(g,"Simulación 120 Hz · Renderizado sincronizado con JavaFX",95,375,23,Color.web("#9dbcca"));Art.text(g,"Audio: pendiente de implementación",95,435,23,Color.web("#9dbcca"));}
  if(screen==Screen.CREDITS){Art.text(g,"UN UNIVERSO ORIGINAL",90,170,40,Color.WHITE);Art.text(g,"MIRAX BROS — Ultimate Adventure",95,270,28,Art.CYAN);Art.text(g,"Personajes, escenarios y efectos dibujados mediante JavaFX Canvas.",95,335,22,Color.WHITE);Art.text(g,"Sin imágenes externas ni recursos gráficos de terceros.",95,390,22,Color.web("#9dbcca"));}
 }
}
