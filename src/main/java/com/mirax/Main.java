package com.mirax;
import com.mirax.game.GameEngine;
import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;
public final class Main extends Application {
 @Override public void start(Stage stage){
  Canvas canvas=new Canvas(1280,720);StackPane root=new StackPane(canvas);root.setStyle("-fx-background-color:#050b1a;");Scene scene=new Scene(root,1280,720);
  canvas.scaleXProperty().bind(javafx.beans.binding.Bindings.min(root.widthProperty().divide(1280),root.heightProperty().divide(720)));canvas.scaleYProperty().bind(canvas.scaleXProperty());
  if(getParameters().getRaw().contains("--smoke-test")){System.setProperty("mirax.smoke","true");System.setProperty("mirax.save","target/smoke-save.json");System.setProperty("mirax.education.save","target/smoke-education");}
  GameEngine engine=new GameEngine(canvas,stage);
  scene.setOnKeyTyped(e->engine.typed(e.getCharacter()));scene.setOnKeyPressed(e->engine.press(e.getCode()));scene.setOnKeyReleased(e->engine.release(e.getCode()));canvas.setOnMouseMoved(e->engine.mouse(e.getX(),e.getY()));canvas.setOnMouseClicked(e->engine.click(e.getX(),e.getY()));
  stage.focusedProperty().addListener((o,a,b)->{if(!b)engine.focusLost();});stage.setTitle("MIRAX BROS — ULTIMATE ADVENTURE");stage.setScene(scene);stage.setMinWidth(800);stage.setMinHeight(480);stage.show();engine.start();stage.setOnCloseRequest(e->engine.close());
  if(getParameters().getRaw().contains("--smoke-test")){PauseTransition stop=new PauseTransition(Duration.seconds(4));stop.setOnFinished(e->{try{String result=engine.verifyFirstLevel();engine.capture(java.nio.file.Path.of("target","smoke-level.png"));String education=engine.verifyEducationFlow();System.out.println("MIRAX_SMOKE_OK frames="+engine.frames()+" "+result+" "+education);engine.close();Platform.exit();}catch(Exception failure){failure.printStackTrace();engine.close();Platform.exit();System.exit(1);}});stop.play();}
 }
 public static void main(String[] args){launch(args);}
}
