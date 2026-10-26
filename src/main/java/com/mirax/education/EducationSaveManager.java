package com.mirax.education;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
public final class EducationSaveManager {
    private final Path directory;private final ObjectMapper mapper=new ObjectMapper();
    public EducationSaveManager(Path directory){this.directory=directory;}
    private Path path(String name,SchoolGrade grade,SchoolCourse course){String key=name.strip().toLowerCase(java.util.Locale.ROOT)+"/"+grade+"/"+course;return directory.resolve(UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8))+".json");}
    public EducationProgress load(String name,SchoolGrade grade,SchoolCourse course)throws IOException{
        Path p=path(name,grade,course);if(Files.exists(p))return read(p);
        EducationProgress result=new EducationProgress();result.playerName=name.strip();result.level=grade.level();result.grade=grade.number();result.course=course;return result;
    }
    private EducationProgress read(Path p)throws IOException{try{EducationProgress result=mapper.readValue(p.toFile(),EducationProgress.class);result.validate();return result;}catch(RuntimeException e){throw new IOException("Guardado educativo inválido",e);}}
    public EducationProgress latest()throws IOException{return Files.exists(directory.resolve("current.json"))?read(directory.resolve("current.json")):null;}
    public void save(EducationProgress p)throws IOException{p.validate();Files.createDirectories(directory);byte[] json=mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(p);write(path(p.playerName,p.schoolGrade(),p.course),json);write(directory.resolve("current.json"),json);}
    private void write(Path p,byte[] data)throws IOException{Path temp=Files.createTempFile(directory,"education-",".tmp");try{Files.write(temp,data);try{Files.move(temp,p,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}catch(AtomicMoveNotSupportedException e){Files.move(temp,p,StandardCopyOption.REPLACE_EXISTING);}}finally{Files.deleteIfExists(temp);}}
}
