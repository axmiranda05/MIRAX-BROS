package com.mirax.education;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;
public final class QuestionBank {
    private final List<Question> questions;
    public QuestionBank(InputStream input)throws IOException {
        if(input==null)throw new IOException("No existe el banco de preguntas");
        try(input){questions=List.of(new ObjectMapper().readValue(input,Question[].class));}
        Set<String> ids=new HashSet<>(),texts=new HashSet<>();
        for(Question q:questions){if(!ids.add(q.id())||!texts.add(q.level()+":"+q.grade()+":"+q.course()+":"+q.statement()))throw new IOException("Pregunta repetida: "+q.id());}
    }
    public static QuestionBank load()throws IOException{return new QuestionBank(QuestionBank.class.getResourceAsStream("/education/questions.json"));}
    public List<Question> find(SchoolGrade grade,SchoolCourse course,Difficulty difficulty){return questions.stream().filter(q->q.matches(grade,course,difficulty)).toList();}
    public int count(SchoolGrade grade,SchoolCourse course){return (int)questions.stream().filter(q->q.level()==grade.level()&&q.grade()==grade.number()&&q.course()==course).count();}
    public boolean playable(SchoolGrade g,SchoolCourse c){return find(g,c,Difficulty.INTERMEDIA).size()>=3&&find(g,c,Difficulty.INTERMEDIA_ALTA).size()>=5&&find(g,c,Difficulty.DESAFIO_FINAL).size()>=5;}
    public String coverage(SchoolGrade g,SchoolCourse c){int n=count(g,c);return n+" / 20 preguntas"+(n<20?" · BANCO INCOMPLETO":"");}
}
