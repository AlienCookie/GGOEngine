package Gameplay;

import Editor.EditorObjectsCreator;
import Editor.GLEditorObject;
import GUI.InputManager;

import java.io.IOException;
import java.util.*;

/**
 * Created by andri on 28-Aug-16.
 */
public class ObjectsManager {
    private class Scene{
        List<GLEditorObject> gameItem = new ArrayList<>();
        float shift = 0;
        public Scene(List<GLEditorObject> objects){
            gameItem = objects;
        }

        public void addObjects(List<GLEditorObject> objects){
            gameItem.addAll(objects);
        }

        public void translateScene(float[] translationVector){
            shift += translationVector[0];
            for(GLEditorObject object : gameItem){
                float[] position = {
                        translationVector[0],
                        translationVector[1],
                        translationVector[2],
                };
                object.move(position);
            }
            gameItem.get(1).move(translationVector);
        }

        public void draw(){
            for(GLEditorObject object : gameItem)
                object.drawAsArray();
        }


        public void updateProjectMatrix(){
            for(GLEditorObject object : gameItem) {
                //object;
                object.updateProjectMatrix();
            }
        }

        public List<GLEditorObject> getGameItem(){
            return gameItem;
        }
    }
    List<Scene> gameScenes = new ArrayList<>();
    List<GLEditorObject> gameBonuses = new ArrayList<>();
    GLEditorObject player;
    GLEditorObject effect;
    GLEditorObject gameOver;
    GLEditorObject background;

    private float shift = 0;
    private boolean up  = false;
    private boolean down  = false;
    private float flour = 0.0f;
    private float jumpVelocity = 0.12f;
    private float gravity = -0.007f;
    private float playerUpSpeed = 0.0f;
    private float effectUpPos;
    private float effectUpSpeed = 0.0f;

    public ObjectsManager(){
        try {
            player = EditorObjectsCreator
                    .getGLObjectsFromSource("/res/raw/player.ggo").get(0);
            effect = EditorObjectsCreator
                    .getGLObjectsFromSource("/res/raw/effect.ggo").get(0);

            background = EditorObjectsCreator
                    .getGLObjectsFromSource("/res/raw/backgroundobj.ggo").get(0);

            flour = player.getPosition()[1];
            effectUpPos = flour;
            gameOver = EditorObjectsCreator
                    .getGLObjectsFromSource("/res/raw/endgame.ggo").get(0);


        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner objectScanner = new Scanner(this.getClass().getResourceAsStream("/res/raw/scenePack.txt"));
        while (objectScanner.hasNextLine()) {
            try {
                    gameScenes.add(new Scene(EditorObjectsCreator
                            .getGLObjectsFromSource(objectScanner.nextLine())));

                    float[] translationVector = new float[3];
                    translationVector[0] = 4 * (gameScenes.size() - 1);
                    gameScenes.get(gameScenes.size() - 1).translateScene(translationVector);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        objectScanner = new Scanner(this.getClass().getResourceAsStream("/res/raw/bonusPack.txt"));
        while (objectScanner.hasNextLine()) {
            try {
                gameBonuses.addAll(EditorObjectsCreator
                        .getGLObjectsFromSource(objectScanner.nextLine()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (GLEditorObject bonus : gameBonuses)
            bonus.getPosition()[0] = (float)Math.random() * 3.55554f - 1.777777f;
    }

    public void nextFrame(){
        float[] translationVector = {-0.04f, 0.0f, 0.0f};
        for (Scene scene : gameScenes) {
            scene.translateScene(translationVector);
            if(scene.shift < -3.55555f) {
                float[] translation = {7.6211f, 0.0f, 0.0f};
                scene.translateScene(translation);
            }
            //scene.updateProjectMatrix();
        }
        for (GLEditorObject bonus : gameBonuses) {
            bonus.move(new float[] {translationVector[0], 0.f, 0.f});
            bonus.updateProjectMatrix();
        }
    }

    public void bonusesManager(){
        float[] playerVertices = player.getActiveBox().getUpdatedVertices();
        for (GLEditorObject bonus : gameBonuses){
            if(bonus.getPosition()[0] < - 1.77777f ||
                    FigureIntersection.cubeIntersection(playerVertices,
                            bonus.getActiveBox().getUpdatedVertices()))
                bonus.translate(new float[]{(float)Math.random() * 3.55554f + 1.97777f, 0.f, 0.f});
        }
    }

    public void playerPhysic() {
        if (player.getPosition()[1] <= flour && InputManager.onSpace && !down) {
            playerUpSpeed += jumpVelocity;
            player.setCurrentTexturePack(1);
            up = true;
        }

        if (player.getPosition()[1] <= flour && InputManager.onDown && !up && !down) {
            player.setCurrentTexturePack(2);
            player.setActiveBox(1);
            down = true;
        }

        playerUpSpeed += gravity;
        effectUpSpeed = -0.017f;
        if(player.getPosition()[1] > effectUpPos)
            effectUpSpeed = (player.getPosition()[1] - effectUpPos) * 0.38f;
        effectUpPos += effectUpSpeed;

        player.move(new float[]{0.f, playerUpSpeed, 0.f});
        if (effectUpPos < flour){
            effectUpSpeed = 0.0f;
            effectUpPos = flour;
        }
        if (player.getPosition()[1] < flour) {
            player.translate(new float[]{0.f, flour, 0.f});
            playerUpSpeed = 0.0f;
            if(up)
                player.setCurrentTexturePack(0);
            else if (down && player.isAnimationEnd()){
                player.setCurrentTexturePack(0);
                player.setActiveBox(0);
                down = false;
            }
            up = false;
        }
       // player.updateProjectMatrix();
    }


    private boolean onIntersection(){
        //int index = (int)(Math.abs(shift - 1.77777f))/4;
        //if(index == gameScenes.size()) index = gameScenes.size()-1;
        List<GLEditorObject> sceneItems = gameScenes.get(0).getGameItem();
        float[] playerVertices = player.getActiveBox().getUpdatedVertices();
        for (GLEditorObject object : sceneItems){
            if(FigureIntersection.cubeIntersection(playerVertices,
                    object.getActiveBox().getUpdatedVertices()))
                return true;
        }
        sceneItems = gameScenes.get(1).getGameItem();
        for (GLEditorObject object : sceneItems){
            if(FigureIntersection.cubeIntersection(playerVertices,
                    object.getActiveBox().getUpdatedVertices()))
                return true;
        }
        return false;
    }

    public void draw(){
        background.updateProjectMatrix();
        background.drawAsArray();
        for (Scene scene : gameScenes) {
            scene.updateProjectMatrix();
            scene.draw();
        }
        for (GLEditorObject bonus : gameBonuses) {
            bonus.updateProjectMatrix();
            bonus.drawAsArray();
        }

        player.updateProjectMatrix();
        player.drawAsArray();
        effect.setUnifom((float)(System.currentTimeMillis()%10000)/400f, "time");

        effect.setUnifom(effectUpPos, "posY");

        effect.drawAsArray();

        if(onIntersection())
            gameOver.drawAsArray();
        else
           nextFrame();
        playerPhysic();
        bonusesManager();
    }
}
