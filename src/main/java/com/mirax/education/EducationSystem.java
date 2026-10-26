package com.mirax.education;
import com.mirax.ui.Art;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import java.nio.file.Path;
import java.util.Random;
import java.util.function.*;
public final class EducationSystem {
    private enum View{NONE,NAME,GRADE,COURSE,QUESTION,RESULT,MESSAGE}
    private View view=View.NONE;private SchoolLevel school=SchoolLevel.PRIMARIA;private int gradeIndex,courseIndex;
    private String name="",message="";private final GradeSelectionScreen grades=new GradeSelectionScreen();private final CourseSelectionScreen courses=new CourseSelectionScreen();
    private final EducationSaveManager saves;private QuestionBank bank;private QuestionManager manager;private QuestionScreen quiz;
    private EducationProgress progress;private Challenge challenge;private final Runnable start,menu;private final BiConsumer<Challenge,Boolean> finish;
    public EducationSystem(Runnable start,Runnable menu,BiConsumer<Challenge,Boolean> finish){
        this.start=start;this.menu=menu;this.finish=finish;saves=new EducationSaveManager(Path.of(System.getProperty("mirax.education.save","saves/education")));
        try{bank=QuestionBank.load();manager=new QuestionManager(bank,new Random());}catch(Exception e){message="Banco no disponible: "+e.getMessage();}
    }
    public EducationProgress progress(){return progress;}
    public int lastCorrect(){return quiz==null?0:quiz.correct();}
    /** Only used by the explicitly requested JavaFX smoke run. */
    public void smokeAnswer(boolean right){if(!Boolean.getBoolean("mirax.smoke"))throw new IllegalStateException("Solo prueba de humo");int choice=quiz.current().correctAnswer();answer(right?choice:(choice+1)%4);key(KeyCode.ENTER);}
    public boolean active(){return view!=View.NONE;}
    public boolean hasProfile(){return progress!=null;}
    public void newGame(){name="";view=View.NAME;}
    public void continueGame(){try{progress=saves.latest();if(progress==null){newGame();return;}school=progress.level;gradeIndex=progress.grade-1;courseIndex=progress.course.ordinal();if(bank==null||!bank.playable(progress.schoolGrade(),progress.course)){view=View.COURSE;return;}view=View.NONE;start.run();}catch(Exception e){show("No se pudo cargar el progreso: "+e.getMessage());}}
    public void newLevel(){if(manager!=null)manager.newLevel();}
    public void typed(String text){if(view==View.NAME&&name.length()<32)for(char c:text.toCharArray())if(!Character.isISOControl(c)&&name.length()<32)name+=c;}
    public void save(){if(progress!=null)try{saves.save(progress);}catch(Exception e){show("Error al guardar: "+e.getMessage()+". ENTER vuelve al menú.");}}
    public void show(String text){message=text;view=View.MESSAGE;}
    public boolean begin(Challenge type){
        if(progress==null){newGame();return false;}
        try{quiz=new QuestionScreen(manager.select(progress.schoolGrade(),progress.course,type));challenge=type;view=View.QUESTION;return true;}
        catch(Exception e){show(e.getMessage());return false;}
    }
    private void chooseCourse(){
        SchoolGrade grade=new SchoolGrade(school,gradeIndex+1);SchoolCourse course=SchoolCourse.values()[courseIndex];
        if(bank==null||!bank.playable(grade,course))return;
        try{progress=saves.load(name,grade,course);saves.save(progress);view=View.NONE;start.run();}catch(Exception e){show("No se pudo abrir el perfil: "+e.getMessage());}
    }
    private void answer(int choice){Boolean right=quiz.answer(choice);if(right!=null){progress.answer(right);save();}}
    private void next(){if(quiz.advance()){if(challenge==Challenge.NIVEL)progress.complete(progress.currentLevel,quiz.correct());save();if(view!=View.MESSAGE)view=View.RESULT;}}
    public void key(KeyCode code){
        switch(view){
            case NAME->{if(code==KeyCode.BACK_SPACE&&!name.isEmpty())name=name.substring(0,name.length()-1);if(code==KeyCode.ENTER&&!name.isBlank())view=View.GRADE;if(code==KeyCode.ESCAPE){view=View.NONE;menu.run();}}
            case GRADE->{if(code==KeyCode.LEFT||code==KeyCode.RIGHT){school=SchoolLevel.values()[1-school.ordinal()];gradeIndex=Math.min(gradeIndex,school.grades-1);}if(code==KeyCode.DOWN)gradeIndex=(gradeIndex+1)%school.grades;if(code==KeyCode.UP)gradeIndex=(gradeIndex+school.grades-1)%school.grades;if(code==KeyCode.ENTER)view=View.COURSE;if(code==KeyCode.ESCAPE)view=View.NAME;}
            case COURSE->{if(code==KeyCode.RIGHT||code==KeyCode.DOWN)courseIndex=(courseIndex+1)%12;if(code==KeyCode.LEFT||code==KeyCode.UP)courseIndex=(courseIndex+11)%12;if(code==KeyCode.ENTER)chooseCourse();if(code==KeyCode.ESCAPE)view=View.GRADE;}
            case QUESTION->{if(code==KeyCode.ENTER&&quiz.feedback())next();else if(!quiz.feedback()){int n=switch(code){case DIGIT1,NUMPAD1->0;case DIGIT2,NUMPAD2->1;case DIGIT3,NUMPAD3->2;case DIGIT4,NUMPAD4->3;default->-2;};if(n>=0)answer(n);}}
            case RESULT->{if(code==KeyCode.ENTER){view=View.NONE;finish.accept(challenge,challenge.passes(quiz.correct()));}}
            case MESSAGE->{if(code==KeyCode.ENTER||code==KeyCode.ESCAPE){view=View.NONE;menu.run();}}
            default->{}
        }
    }
    public void click(double x,double y){switch(view){
        case GRADE->{if(y>=120&&y<=185&&x>=80&&x<=1200){school=SchoolLevel.values()[x<650?0:1];gradeIndex=Math.min(gradeIndex,school.grades-1);}else{int i=grades.hit(x,y,school);if(i>=0){gradeIndex=i;view=View.COURSE;}}}
        case COURSE->{int i=courses.hit(x,y);if(i>=0){courseIndex=i;chooseCourse();}}
        case QUESTION->{if(quiz.feedback()){if(y>=640)next();}else{int i=quiz.hit(x,y);if(i>=0)answer(i);}}
        case RESULT,MESSAGE->{if(y>=630)key(KeyCode.ENTER);}
        default->{}
    }}
    public void update(double dt){if(view==View.QUESTION&&quiz.tick(dt))answer(-1);}
    public void draw(GraphicsContext g){
        g.setGlobalAlpha(1);g.setEffect(null);g.setFill(Color.web("#061322"));g.fillRect(0,0,1280,720);
        switch(view){
            case NAME->{Art.text(g,"MIRAX / TU AVENTURA EDUCATIVA",80,150,34,Art.CYAN);Art.text(g,"Escribe tu nombre y presiona ENTER",80,260,26,Color.WHITE);Art.text(g,name+"_",80,330,36,Color.ORANGE);Art.text(g,"El progreso se guarda por nombre, grado y curso. ESC volver.",80,450,20,Color.WHITE);}
            case GRADE->grades.draw(g,school,gradeIndex);
            case COURSE->{SchoolGrade grade=new SchoolGrade(school,gradeIndex+1);courses.draw(g,grade,courseIndex);String coverage=bank==null?message:bank.coverage(grade,SchoolCourse.values()[courseIndex]);Art.text(g,coverage,70,615,20,Color.ORANGE);if(bank!=null&&!bank.playable(grade,SchoolCourse.values()[courseIndex]))Art.text(g,"Curso pendiente: faltan preguntas verificadas de este grado. Elige otro curso.",70,646,18,Color.WHITE);}
            case QUESTION->quiz.draw(g,progress,challenge);
            case RESULT->{boolean pass=challenge.passes(quiz.correct());Art.text(g,pass?"¡DESAFÍO SUPERADO!":"SIGUE APRENDIENDO",80,100,40,Art.CYAN);Art.text(g,"Aciertos: "+quiz.correct()+" / "+quiz.size()+(challenge==Challenge.NIVEL?" · Estrellas: "+quiz.correct():""),80,170,26,Color.ORANGE);Art.text(g,"Total: "+progress.answered+" respuestas · "+progress.correct+" correctas · "+progress.incorrect+" incorrectas",80,240,23,Color.WHITE);Art.text(g,String.format(java.util.Locale.ROOT,"Precisión: %.1f %% · Monedas totales: %d · Puntos: %d",progress.accuracy(),progress.coins,progress.score),80,290,23,Color.WHITE);QuestionScreen.wrap(g,"Insignias: "+progress.badges,80,360,90,28,21,Art.CYAN);QuestionScreen.wrap(g,"Personajes: "+progress.characters+" · Poderes: "+progress.powers+" · Mundos: "+progress.worlds.size(),80,450,90,28,20,Color.WHITE);Art.text(g,challenge==Challenge.NIVEL&&!pass?"Repite el sector: necesitas al menos 2 respuestas correctas.":"El progreso educativo ha sido guardado.",80,590,22,Color.ORANGE);Art.text(g,"ENTER o clic aquí para continuar",80,675,24,Art.CYAN);}
            case MESSAGE->{QuestionScreen.wrap(g,message,80,210,85,35,26,Color.ORANGE);Art.text(g,"ENTER / ESC · volver al menú",80,675,24,Art.CYAN);}
            default->{}
        }
    }
}
