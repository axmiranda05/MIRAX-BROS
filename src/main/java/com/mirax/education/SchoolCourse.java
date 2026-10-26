package com.mirax.education;
public enum SchoolCourse {
    MATEMATICAS("Matemáticas"), COMUNICACION("Comunicación"), HISTORIA("Historia"),
    QUIMICA("Química"), FISICA("Física"), GEOGRAFIA("Geografía"), BIOLOGIA("Biología"),
    INGLES("Inglés"), COMPUTACION("Computación"), CIVICA("Educación cívica"),
    FINANZAS("Educación financiera"), ARTE("Arte y cultura");
    public final String label; SchoolCourse(String label){this.label=label;}
    public String label(SchoolGrade grade){return grade.level()==SchoolLevel.PRIMARIA && (this==QUIMICA||this==FISICA||this==BIOLOGIA)?"Ciencia y Tecnología · "+label:label;}
}
