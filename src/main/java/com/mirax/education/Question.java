package com.mirax.education;
import java.util.List;
public record Question(String id,String statement,List<String> alternatives,int correctAnswer,String explanation,
                       SchoolLevel level,int grade,SchoolCourse course,String topic,Difficulty difficulty) {
    public Question {
        if(id==null||id.isBlank()||statement==null||statement.isBlank()||explanation==null||explanation.isBlank()||topic==null||topic.isBlank()||course==null||difficulty==null)throw new IllegalArgumentException("Pregunta incompleta");
        new SchoolGrade(level,grade);
        if(alternatives==null||alternatives.size()!=4||alternatives.stream().anyMatch(s->s==null||s.isBlank())||alternatives.stream().distinct().count()!=4||correctAnswer<0||correctAnswer>3)throw new IllegalArgumentException("Alternativas inválidas: "+id);
        alternatives=List.copyOf(alternatives);
    }
    public boolean matches(SchoolGrade g,SchoolCourse c,Difficulty d){return level==g.level()&&grade==g.number()&&course==c&&difficulty==d;}
}
