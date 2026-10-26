package com.mirax.education;
import java.util.*;
public final class QuestionManager {
    private final QuestionBank bank; private final Random random; private final Set<String> used=new HashSet<>();
    public QuestionManager(QuestionBank bank,Random random){this.bank=bank;this.random=random;}
    public void newLevel(){used.clear();}
    public List<Question> select(SchoolGrade grade,SchoolCourse course,Challenge challenge){
        List<Question> pool=new ArrayList<>(bank.find(grade,course,challenge.difficulty));
        pool.removeIf(q->used.contains(q.id()));
        if(pool.size()<challenge.count)throw new IllegalStateException("Preguntas nuevas insuficientes para "+challenge+". Repite el nivel o elige otro curso.");
        Collections.shuffle(pool,random);List<Question> result=List.copyOf(pool.subList(0,challenge.count));result.forEach(q->used.add(q.id()));return result;
    }
}
