package com.mirax.education;
public record SchoolGrade(SchoolLevel level,int number) {
    public SchoolGrade { if(level==null||number<1||number>level.grades)throw new IllegalArgumentException("Grado inválido"); }
    public String label(){return number+".° de "+level.name().toLowerCase(java.util.Locale.ROOT);}
}
