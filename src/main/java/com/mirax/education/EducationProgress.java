package com.mirax.education;
import java.util.*;
public final class EducationProgress {
    public String playerName="Jugador";
    public SchoolLevel level=SchoolLevel.PRIMARIA;
    public int grade=1;
    public SchoolCourse course=SchoolCourse.MATEMATICAS;
    public String activeCharacter="MIRAX";
    public int currentLevel,unlocked,answered,correct,incorrect,coins,score;
    public Map<Integer,Integer> stars=new TreeMap<>();
    public Set<Integer> completedLevels=new TreeSet<>();
    public Set<String> badges=new TreeSet<>(),characters=new TreeSet<>(),powers=new TreeSet<>();
    public Set<Integer> worlds=new TreeSet<>(Set.of(0));
    public SchoolGrade schoolGrade(){return new SchoolGrade(level,grade);}
    public double accuracy(){return answered==0?0:100.0*correct/answered;}
    public void answer(boolean right){answered++;if(right){correct++;coins+=10;score+=50;}else incorrect++;reward();}
    public void complete(int sector,int right){
        if(sector<0||sector>14||right<0||right>3)throw new IllegalArgumentException("Resultado de nivel inválido");
        stars.merge(sector,right,Math::max);
        if(right>=2){completedLevels.add(sector);unlocked=Math.max(unlocked,Math.min(14,sector+1));currentLevel=unlocked;worlds.add(unlocked/3);}
        reward();
    }
    private void reward(){
        if(correct>=10){badges.add(switch(course){case MATEMATICAS->"GENIO MATEMÁTICO";case HISTORIA->"MAESTRO DE HISTORIA";case QUIMICA,FISICA,BIOLOGIA->"EXPERTO EN CIENCIAS";case COMUNICACION->"CAMPEÓN DE COMUNICACIÓN";default->"EXPLORADOR DEL CONOCIMIENTO";});powers.add("ENERGÍA ESCOLAR");}
        if(completedLevels.size()>=3){badges.add("EXPLORADOR DEL CONOCIMIENTO");characters.add("MIRAX ESMERALDA");}
    }
    public void validate(){
        schoolGrade();
        if(playerName==null||playerName.isBlank()||course==null||activeCharacter==null||currentLevel<0||currentLevel>14||unlocked<0||unlocked>14||currentLevel>unlocked||answered<0||correct<0||incorrect<0||answered!=correct+incorrect||coins<0||score<0||stars==null||completedLevels==null||badges==null||characters==null||powers==null||worlds==null)throw new IllegalArgumentException("Progreso educativo inválido");
        if(stars.entrySet().stream().anyMatch(e->e.getKey()<0||e.getKey()>14||e.getValue()<0||e.getValue()>3)||completedLevels.stream().anyMatch(n->n<0||n>14)||worlds.stream().anyMatch(n->n<0||n>4))throw new IllegalArgumentException("Desbloqueos inválidos");
    }
}
