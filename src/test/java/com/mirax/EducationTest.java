package com.mirax;
import com.mirax.education.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import javafx.scene.input.KeyCode;
import java.nio.file.*;
import java.util.*;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
class EducationTest {
    @Test void elevenGradesAndTwelveCourses(){int total=0;for(SchoolLevel level:SchoolLevel.values()){for(int n=1;n<=level.grades;n++){assertEquals(level,new SchoolGrade(level,n).level());total++;}assertThrows(IllegalArgumentException.class,()->new SchoolGrade(level,level.grades+1));}assertEquals(11,total);assertEquals(12,SchoolCourse.values().length);}
    @Test void bankCoverageAndNoCrossGradeFallback()throws Exception{
        QuestionBank bank=QuestionBank.load();int total=0,complete=0;
        for(SchoolLevel level:SchoolLevel.values())for(int n=1;n<=level.grades;n++)for(SchoolCourse course:SchoolCourse.values()){
            SchoolGrade grade=new SchoolGrade(level,n);int count=bank.count(grade,course);total+=count;
            if(count>=20){complete++;assertTrue(bank.playable(grade,course));}else{assertEquals(0,count);assertFalse(bank.playable(grade,course));assertTrue(bank.coverage(grade,course).contains("INCOMPLETO"));}
            for(Difficulty d:Difficulty.values())for(Question q:bank.find(grade,course,d)){assertTrue(q.matches(grade,course,d));assertEquals(4,q.alternatives().stream().distinct().count());}
        }
        assertEquals(440,total);assertEquals(22,complete);
        assertThrows(IllegalStateException.class,()->new QuestionManager(bank,new Random(1)).select(new SchoolGrade(SchoolLevel.SECUNDARIA,5),SchoolCourse.QUIMICA,Challenge.NIVEL));
    }
    @Test void wholeSectorUsesDistinctQuestionsAtExactDifficulty()throws Exception{
        QuestionBank bank=QuestionBank.load();QuestionManager manager=new QuestionManager(bank,new Random(2));SchoolGrade g=new SchoolGrade(SchoolLevel.PRIMARIA,1);Set<String> ids=new HashSet<>();
        for(Challenge c:List.of(Challenge.COFRE,Challenge.PUERTA,Challenge.RECUPERACION,Challenge.RECUPERACION,Challenge.RECUPERACION,Challenge.PORTAL,Challenge.NIVEL,Challenge.JEFE,Challenge.MUNDO)){
            List<Question> questions=manager.select(g,SchoolCourse.MATEMATICAS,c);assertEquals(c.count,questions.size());for(Question q:questions){assertTrue(ids.add(q.id()));assertEquals(c.difficulty,q.difficulty());}
        }
        assertThrows(IllegalStateException.class,()->manager.select(g,SchoolCourse.MATEMATICAS,Challenge.RECUPERACION));manager.newLevel();assertEquals(1,manager.select(g,SchoolCourse.MATEMATICAS,Challenge.RECUPERACION).size());
    }
    @Test void answersTimeoutAndFeedbackAreRecordedOnlyOnce()throws Exception{
        Question q=QuestionBank.load().find(new SchoolGrade(SchoolLevel.PRIMARIA,1),SchoolCourse.MATEMATICAS,Difficulty.INTERMEDIA).get(0);
        QuestionScreen screen=new QuestionScreen(List.of(q,q));assertTrue(screen.answer(q.correctAnswer()));assertNull(screen.answer(q.correctAnswer()));assertFalse(screen.advance());assertTrue(screen.tick(91));assertFalse(screen.answer(-1));assertTrue(screen.feedback());assertEquals(1,screen.correct());assertTrue(screen.advance());
    }
    @Test void progressionRequiresTwoAnswersAndKeepsBestStars(){EducationProgress p=new EducationProgress();p.complete(0,0);assertEquals(0,p.unlocked);p.complete(0,1);assertEquals(0,p.unlocked);p.complete(0,2);assertEquals(1,p.unlocked);p.complete(0,3);p.complete(0,1);assertEquals(3,p.stars.get(0));assertEquals(Set.of(0),p.completedLevels);assertTrue(Challenge.NIVEL.passes(2));assertFalse(Challenge.NIVEL.passes(1));assertFalse(Challenge.MUNDO.passes(4));}
    @Test void rewardsAndStatistics(){EducationProgress p=new EducationProgress();for(int i=0;i<10;i++)p.answer(true);p.answer(false);assertEquals(11,p.answered);assertEquals(10,p.correct);assertEquals(1,p.incorrect);assertEquals(100,p.coins);assertEquals(500,p.score);assertEquals(1000.0/11,p.accuracy(),.001);assertTrue(p.badges.contains("GENIO MATEMÁTICO"));assertTrue(p.powers.contains("ENERGÍA ESCOLAR"));for(int i=0;i<3;i++)p.complete(i,2);assertTrue(p.characters.contains("MIRAX ESMERALDA"));assertTrue(p.worlds.contains(1));}
    @Test void independentProfilesAndCorruptSave(@TempDir Path dir)throws Exception{
        EducationSaveManager saves=new EducationSaveManager(dir);SchoolGrade grade=new SchoolGrade(SchoolLevel.PRIMARIA,1);EducationProgress p=saves.load("Ana",grade,SchoolCourse.MATEMATICAS);p.answer(true);p.complete(0,3);saves.save(p);
        EducationProgress copy=saves.load("Ana",grade,SchoolCourse.MATEMATICAS);assertEquals(1,copy.answered);assertEquals(3,copy.stars.get(0));assertEquals(1,saves.latest().unlocked);
        assertEquals(0,saves.load("Luis",grade,SchoolCourse.MATEMATICAS).answered);assertEquals(0,saves.load("Ana",new SchoolGrade(SchoolLevel.PRIMARIA,2),SchoolCourse.MATEMATICAS).answered);assertEquals(0,saves.load("Ana",grade,SchoolCourse.COMUNICACION).answered);
        Files.writeString(dir.resolve("current.json"),"{broken");assertThrows(IOException.class,saves::latest);
    }
    @Test void malformedQuestionIsRejected(){assertThrows(IllegalArgumentException.class,()->new Question("id","texto",List.of("a","a","b","c"),0,"explicación",SchoolLevel.PRIMARIA,1,SchoolCourse.ARTE,"tema",Difficulty.INTERMEDIA));assertThrows(IOException.class,()->new QuestionBank(new ByteArrayInputStream("{}".getBytes())));}
    @Test void canvasSelectionsRespectBounds(){GradeSelectionScreen g=new GradeSelectionScreen();assertEquals(5,g.hit(200,550,SchoolLevel.PRIMARIA));assertEquals(-1,g.hit(200,550,SchoolLevel.SECUNDARIA));CourseSelectionScreen c=new CourseSelectionScreen();assertEquals(11,c.hit(700,550));assertEquals(-1,c.hit(1250,550));}
    @Test void actualSelectionFlowUsesChosenLevelGradeAndCourse(@TempDir Path dir){
        String previous=System.getProperty("mirax.education.save");System.setProperty("mirax.education.save",dir.toString());
        try{
            for(SchoolLevel level:SchoolLevel.values())for(int grade=1;grade<=level.grades;grade++){
                int[] starts={0};EducationSystem education=new EducationSystem(()->starts[0]++,()->{},(a,b)->{});
                education.newGame();education.typed("Estudiante");education.key(KeyCode.ENTER);if(level==SchoolLevel.SECUNDARIA)education.key(KeyCode.RIGHT);for(int n=1;n<grade;n++)education.key(KeyCode.DOWN);education.key(KeyCode.ENTER);education.key(KeyCode.ENTER);
                assertEquals(1,starts[0]);assertEquals(level,education.progress().level);assertEquals(grade,education.progress().grade);assertEquals(SchoolCourse.MATEMATICAS,education.progress().course);
            }
            int[] starts={0};EducationSystem unavailable=new EducationSystem(()->starts[0]++,()->{},(a,b)->{});unavailable.newGame();unavailable.typed("Otra persona");unavailable.key(KeyCode.ENTER);unavailable.key(KeyCode.RIGHT);unavailable.key(KeyCode.ENTER);unavailable.key(KeyCode.RIGHT);unavailable.key(KeyCode.ENTER);assertEquals(0,starts[0]);assertFalse(unavailable.hasProfile());
            for(SchoolCourse course:SchoolCourse.values()){
                EducationSystem education=new EducationSystem(()->{},()->{},(a,b)->{});education.newGame();education.typed("Todos los cursos");education.key(KeyCode.ENTER);education.key(KeyCode.ENTER);for(int i=0;i<course.ordinal();i++)education.key(KeyCode.RIGHT);education.key(KeyCode.ENTER);assertEquals(course,education.progress().course);
            }
        }finally{if(previous==null)System.clearProperty("mirax.education.save");else System.setProperty("mirax.education.save",previous);}
    }
}
