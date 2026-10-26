package com.mirax.save;
import java.nio.file.*;
import java.io.IOException;
import java.util.regex.*;
public final class SaveManager {
 public record Progress(int level,int unlocked,int score,int coins){}
 private final Path path;
 public SaveManager(Path path){this.path=path;}
 public Progress load()throws IOException{
  if(!Files.exists(path))return new Progress(0,0,0,0);
  String json=Files.readString(path);
  return new Progress(Math.min(14,number(json,"level")),Math.min(14,number(json,"unlocked")),number(json,"score"),number(json,"coins"));
 }
 private int number(String json,String key)throws IOException{
  Matcher m=Pattern.compile(Pattern.quote((char)34+key+Character.toString(34))+"[ \\t\\r\\n]*:[ \\t\\r\\n]*([0-9]+)").matcher(json);
  if(!m.find())throw new IOException("Guardado incompleto: "+key);
  try{return Integer.parseInt(m.group(1));}catch(NumberFormatException e){throw new IOException("Valor invalido",e);}
 }
 public void save(Progress p)throws IOException{
  Path parent=path.toAbsolutePath().getParent();Files.createDirectories(parent);
  Path temp=Files.createTempFile(parent,"mirax-",".tmp");
  String json=String.format("{\n  \"level\": %d,\n  \"unlocked\": %d,\n  \"score\": %d,\n  \"coins\": %d\n}\n",p.level(),p.unlocked(),p.score(),p.coins());
  try{
   Files.writeString(temp,json);
   try{Files.move(temp,path,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}catch(AtomicMoveNotSupportedException e){Files.move(temp,path,StandardCopyOption.REPLACE_EXISTING);}
  }finally{Files.deleteIfExists(temp);}
 }
}