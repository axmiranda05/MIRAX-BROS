package com.mirax;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.util.*;
import com.mirax.physics.*;
import com.mirax.player.Player;
import com.mirax.world.Level;
import com.mirax.enemies.Enemy;
import com.mirax.save.SaveManager;
import javafx.scene.input.KeyCode;
class GameTest {
 @Test void landsWithoutTunnelling(){Body p=new Body(30,0,30,50);p.vy=900;PhysicsEngine.move(p,List.of(new Body(0,200,500,10)),.5);assertEquals(150,p.y,.001);assertTrue(p.grounded);}
 @Test void wallsAndCeilingsBlock(){Body p=new Body(0,50,30,50);p.vx=1200;PhysicsEngine.move(p,List.of(new Body(100,0,10,300)),.4);assertEquals(70,p.x,.001);p.vy=-900;PhysicsEngine.move(p,List.of(new Body(0,0,300,10)),.3);assertEquals(10,p.y,.001);}
 @Test void doubleJumpHasLimit(){Player p=new Player();p.grounded=true;var space=Set.of(KeyCode.SPACE);p.update(.008,space,space,List.of(),1700);assertEquals(1,p.jumps);p.update(.008,Set.of(),Set.of(),List.of(),1700);p.update(.008,space,space,List.of(),1700);assertEquals(2,p.jumps);double velocity=p.vy;p.update(.008,space,space,List.of(),1700);assertEquals(2,p.jumps);assertTrue(p.vy>velocity);}
 @Test void firstLevelCanBeTraversed(){Level l=new Level(0);Player p=new Player();var right=Set.of(KeyCode.D);for(int i=0;i<1800;i++){p.update(1.0/120,right,Set.of(),l.solids,l.gravity());assertTrue(p.y<=620);if(p.x>l.portal.x)break;}assertTrue(p.x>=l.portal.x);}
 @Test void campaignHasFifteenValidLevels(){for(int i=0;i<15;i++){Level l=new Level(i);assertFalse(l.coins.isEmpty());assertFalse(l.enemies.isEmpty());assertTrue(l.portal.x+l.portal.w<l.width);for(Body c:l.coins)assertTrue(c.x<l.width);}}
 @Test void damageRespectsInvulnerability(){Player p=new Player();assertTrue(p.damage(20));assertFalse(p.damage(20));assertEquals(80,p.health);p.invulnerable=0;p.shield=1;assertFalse(p.damage(20));}
 @Test void enemyStateMachineAndDeath(){Enemy e=new Enemy(600,576,Enemy.Kind.CHASER);Player p=new Player();p.x=700;e.update(.1,p);assertEquals(Enemy.State.CHASE,e.state);assertTrue(e.x>600);assertTrue(e.hit(100));e.update(.1,p);assertEquals(Enemy.State.DEAD,e.state);}
 @Test void saveRoundTrip(@TempDir Path dir)throws Exception{SaveManager s=new SaveManager(dir.resolve("save.json"));var p=new SaveManager.Progress(3,5,1200,42);s.save(p);assertEquals(p,s.load());}
 @Test void corruptSaveIsReported(@TempDir Path dir)throws Exception{Path f=dir.resolve("save.json");Files.writeString(f,"broken");assertThrows(java.io.IOException.class,()->new SaveManager(f).load());}
}