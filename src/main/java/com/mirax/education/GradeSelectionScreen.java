package com.mirax.education;
import com.mirax.ui.Art;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
public final class GradeSelectionScreen {
    public void draw(GraphicsContext g,SchoolLevel level,int selected){
        Art.text(g,"AVENTURA ESCOLAR / ELIGE TU GRADO",80,85,32,Art.CYAN);
        for(int i=0;i<2;i++){g.setFill(Color.web(level.ordinal()==i?"#235e70":"#102a40"));g.fillRoundRect(80+i*570,120,550,65,12,12);Art.text(g,SchoolLevel.values()[i].label,100+i*570,162,25,Color.WHITE);}
        for(int i=0;i<level.grades;i++){double y=215+i*65;g.setFill(Color.web(i==selected?"#236655":"#102a40"));g.fillRoundRect(100,y,1050,52,10,10);Art.text(g,new SchoolGrade(level,i+1).label(),125,y+34,24,Color.WHITE);}
        Art.text(g,"← → nivel educativo   ↑ ↓ grado   ENTER elegir   ESC menú",80,680,18,Art.CYAN);
    }
    public int hit(double x,double y,SchoolLevel level){int i=(int)((y-215)/65);return x>=100&&x<=1150&&y>=215&&i<level.grades&&(y-215)%65<52?i:-1;}
}
