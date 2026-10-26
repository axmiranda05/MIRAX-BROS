package com.mirax.physics;
public class Body {
 public double x,y,w,h,vx,vy; public boolean grounded;
 public Body(double x,double y,double w,double h){this.x=x;this.y=y;this.w=w;this.h=h;}
 public boolean overlaps(Body b){return x<b.x+b.w&&x+w>b.x&&y<b.y+b.h&&y+h>b.y;}
}