package com.mirax.education;
public enum SchoolLevel {
    PRIMARIA("Educación primaria",6), SECUNDARIA("Educación secundaria",5);
    public final String label; public final int grades;
    SchoolLevel(String label,int grades){this.label=label;this.grades=grades;}
}
