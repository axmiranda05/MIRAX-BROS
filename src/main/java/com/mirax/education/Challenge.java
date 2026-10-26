package com.mirax.education;
public enum Challenge {
    COFRE(1,Difficulty.INTERMEDIA), PUERTA(1,Difficulty.INTERMEDIA), RECUPERACION(1,Difficulty.INTERMEDIA),
    PORTAL(2,Difficulty.INTERMEDIA_ALTA), NIVEL(3,Difficulty.INTERMEDIA_ALTA),
    JEFE(3,Difficulty.DESAFIO_FINAL), MUNDO(5,Difficulty.DESAFIO_FINAL);
    public final int count; public final Difficulty difficulty;
    Challenge(int count,Difficulty difficulty){this.count=count;this.difficulty=difficulty;}
    public boolean passes(int correct){return correct >= (this==NIVEL||this==JEFE?2:count);}
}
