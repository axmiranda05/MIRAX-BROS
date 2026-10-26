package com.mirax.education;
import com.mirax.ui.Art;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.*;
public final class QuestionScreen {
    private final List<Question> questions;private int index,correct,selected=-1;private double remaining=90;private boolean feedback;
    public QuestionScreen(List<Question> questions){if(questions.isEmpty())throw new IllegalArgumentException();this.questions=List.copyOf(questions);}
    public Question current(){return questions.get(index);}
    public int correct(){return correct;}
    public int size(){return questions.size();}
    public boolean feedback(){return feedback;}
    public double remaining(){return remaining;}
    public Boolean answer(int choice){if(feedback||choice< -1||choice>3)return null;selected=choice;feedback=true;boolean right=choice==current().correctAnswer();if(right)correct++;return right;}
    public boolean advance(){if(!feedback)return false;if(index+1==questions.size())return true;index++;feedback=false;selected=-1;remaining=90;return false;}
    public boolean tick(double dt){if(!feedback){remaining=Math.max(0,remaining-dt);return remaining==0;}return false;}
    public int hit(double x,double y){if(x<90||x>1190||y<275||y>=515)return -1;int i=(int)((y-275)/60);return (y-275)%60<50?i:-1;}
    public void draw(GraphicsContext g,EducationProgress p,Challenge challenge){
        Art.text(g,p.course.label(p.schoolGrade())+" / "+p.schoolGrade().label(),65,65,24,Art.CYAN);
        Art.text(g,challenge==Challenge.NIVEL?"¡FELICIDADES! SUPERASTE EL NIVEL":challenge.name().replace('_',' '),65,105,22,Color.ORANGE);
        Art.text(g,challenge.difficulty.label+" · Pregunta "+(index+1)+" / "+size()+" · "+(int)Math.ceil(remaining)+" s · Puntos "+p.score,65,145,19,Color.WHITE);
        g.setFill(Color.web("#15364b"));g.fillRect(65,165,1150,8);g.setFill(Art.CYAN);g.fillRect(65,165,1150*(index+(feedback?1:0))/size(),8);
        wrap(g,current().statement(),80,215,88,25,26,Color.WHITE);
        for(int i=0;i<4;i++){boolean right=feedback&&i==current().correctAnswer();g.setFill(Color.web(right?"#216b4b":feedback&&i==selected?"#8b3931":"#15364b"));g.fillRoundRect(90,275+i*60,1100,50,10,10);wrap(g,(i+1)+". "+current().alternatives().get(i),110,307+i*60,95,22,20,Color.WHITE);}
        if(feedback){Art.text(g,selected==current().correctAnswer()?"¡Correcto! +50 puntos · +10 monedas":"Respuesta correcta: "+(current().correctAnswer()+1),85,550,23,Color.ORANGE);wrap(g,current().explanation(),85,585,105,24,19,Color.WHITE);Art.text(g,"ENTER o clic abajo para continuar",85,680,20,Art.CYAN);}
        else Art.text(g,"Elige una alternativa: teclas 1–4 o ratón. El juego está pausado.",85,675,20,Art.CYAN);
    }
    public static void wrap(GraphicsContext g,String text,double x,double y,int width,int lineHeight,int size,Color color){StringBuilder line=new StringBuilder();int row=0;for(String word:text.split("\\s+")){if(line.length()+word.length()>width){Art.text(g,line.toString(),x,y+row++*lineHeight,size,color);line.setLength(0);}line.append(word).append(' ');}Art.text(g,line.toString(),x,y+row*lineHeight,size,color);}
}
