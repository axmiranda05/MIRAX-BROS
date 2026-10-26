package com.mirax.education;
import com.mirax.ui.Art;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
public final class CourseSelectionScreen {
    public void draw(GraphicsContext g,SchoolGrade grade,int selected){
        Art.text(g,"ELIGE TU CURSO / "+grade.label(),70,80,30,Art.CYAN);
        for(int i=0;i<SchoolCourse.values().length;i++){double x=70+(i%2)*580,y=120+(i/2)*78;g.setFill(Color.web(i==selected?"#236655":"#102a40"));g.fillRoundRect(x,y,555,65,12,12);Art.text(g,SchoolCourse.values()[i].label(grade),x+15,y+40,19,Color.WHITE);}
        Art.text(g,"Flechas seleccionar   ENTER comenzar   ESC volver",70,680,18,Art.CYAN);
    }
    public int hit(double x,double y){if(x<70||x>1205||y<120||y>=588)return -1;int c=(int)((x-70)/580),r=(int)((y-120)/78);return (x-70)%580<555&&(y-120)%78<65?r*2+c:-1;}
}
