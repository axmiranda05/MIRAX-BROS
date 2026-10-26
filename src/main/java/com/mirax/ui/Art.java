package com.mirax.ui;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.text.*;
public final class Art {
 public static final Color CYAN=Color.web("#56f4e0"),INK=Color.web("#081426"),GOLD=Color.web("#ffcf75");private Art(){}
 public static void text(GraphicsContext g,String s,double x,double y,int size,Color c){g.setFont(Font.font("Segoe UI",FontWeight.BOLD,size));g.setFill(c);g.fillText(s,x,y);}
 public static void background(GraphicsContext g,double camera,double time,int world){
  Color[] skies={Color.web("#102c39"),Color.web("#1b153d"),Color.web("#351c31"),Color.web("#111535"),Color.web("#21152e")};
  g.setFill(new LinearGradient(0,0,0,720,false,CycleMethod.NO_CYCLE,new Stop(0,Color.web("#050c1b")),new Stop(1,skies[world])));g.fillRect(0,0,1280,720);
  g.setFill(Color.web("#98d7e9"));for(int i=0;i<85;i++){double x=((i*173.7-camera*.08)%1320+1320)%1320;g.setGlobalAlpha(.3+.3*Math.sin(time+i));g.fillOval(x,(i*97)%480,2,2);}g.setGlobalAlpha(1);
  g.setFill(Color.web("#74c9d3",.08));g.fillOval(950-camera*.035,65,210,210);g.setStroke(Color.web("#78f7dc",.15));g.setLineWidth(1);g.strokeOval(930-camera*.035,45,250,250);
  for(int layer=0;layer<3;layer++)for(int i=-1;i<17;i++){
   double x=i*135-((camera*(.12+layer*.14))%135),base=590+layer*16,height=140+Math.floorMod(i*59+layer*43,180);
   g.setFill(Color.web(layer==0?"#0d2433":layer==1?"#123744":"#194553"));
   if(world==0){g.fillRect(x+44,base-height,13,height);g.fillPolygon(new double[]{x-8,x+50,x+112},new double[]{base-height*.4,base-height-70,base-height*.4},3);g.fillPolygon(new double[]{x+3,x+50,x+99},new double[]{base-height*.7,base-height-100,base-height*.7},3);g.setStroke(Color.web("#63dfc4",.2));g.strokeLine(x+50,base-height,x+50,base);}
   else if(world==2){g.fillPolygon(new double[]{x-100,x+55,x+180},new double[]{base,base-height,base},3);}
   else{g.fillRect(x,base-height,105,height);g.setFill(Color.web(world==4?"#f56ca9":"#55d8e8",.25));for(int j=0;j<6;j++)g.fillRect(x+12+(j%3)*28,base-height+20+(j/3)*38,12,18);}
  }
  g.setFill(Color.web("#52e7cc",.15));for(int i=0;i<24;i++)g.fillOval((i*99+Math.sin(time*.3+i)*25-camera*.2+4000)%1350,150+(i*79)%430,3,3);
 }
 public static void hero(GraphicsContext g,double x,double y,int facing,double time,boolean moving,boolean shield){
  g.save();g.translate(x+18,y+30);g.scale(facing,1);g.setFill(Color.web("#000000",.25));g.fillOval(-25,27,56,10);double stride=moving?Math.sin(time*17)*8:0;
  g.setStroke(Color.web("#58ebdf",.22));g.setLineWidth(5);if(moving)g.strokeLine(-12,9,-36,14);
  g.setFill(Color.web("#314a68"));g.fillRoundRect(-13,9+stride,11,21,5,5);g.fillRoundRect(5,9-stride,11,21,5,5);g.setFill(Color.web("#78b6c9"));g.fillRoundRect(-16,24+stride,17,7,3,3);g.fillRoundRect(3,24-stride,20,7,3,3);
  g.setFill(Color.web("#253c58"));g.fillPolygon(new double[]{-18,-9,13,21,14,-11},new double[]{-13,-21,-21,-9,13,13},6);g.setFill(Color.web("#72cbd4"));g.fillPolygon(new double[]{-12,-6,9,14,8,-8},new double[]{-13,-17,-17,-6,5,5},6);g.setFill(CYAN);g.fillPolygon(new double[]{-4,4,7,0},new double[]{-9,-9,-3,2},4);
  g.setFill(Color.web("#244059"));g.fillRoundRect(-18,-32,35,24,9,9);g.setFill(Color.web("#8ed5df"));g.fillPolygon(new double[]{-18,-10,15,19,10,-17},new double[]{-26,-36,-34,-21,-12,-13},6);g.setFill(INK);g.fillRoundRect(-5,-28,26,11,4,4);g.setFill(CYAN);g.fillRect(0,-25,19,4);
  g.setFill(Color.web("#426680"));g.fillRoundRect(-22,-13,11,21,5,5);g.fillRoundRect(12,-11,11,23,5,5);g.setFill(CYAN);g.fillRect(15,2,7,4);
  if(shield){g.setStroke(Color.web("#7bf5ff",.8));g.setLineWidth(2);g.strokeOval(-37,-46,76,91);g.setFill(Color.web("#6ce7ff",.08));g.fillOval(-37,-46,76,91);}g.restore();
 }
}