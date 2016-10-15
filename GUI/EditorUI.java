package GUI;

import Editor.Camera;
import Editor.GLEditorObject;
import Editor.GameEditor;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.nuklear.Nuklear.nk_combo_end;
import static org.lwjgl.nuklear.Nuklear.nk_end;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by andri on 01-Aug-16.
 */


public class EditorUI {

    // Current operation on object
    enum Operation {
        TRANSLATION, ROTATION, SCALING
    }

    private class Item {
        String name;
        boolean selectedObject;

        Item(String _name, boolean _selected) {
            name = _name;
            selectedObject = _selected;
        }
    }

    private NkContext ctx;
    private List<GLEditorObject> gameObject = new ArrayList<>();
    private List<Item> gameItem = new ArrayList<>();
    private List<GLEditorObject> activeObject = new ArrayList<>();
    private Operation operation;
    private GameEditor gEditor;
    private boolean wasClick = false;
    private String fileNme = "";
    private boolean gameMode = false;
    private boolean cubeEdit = false;

    private List<NkImage> objectImage = new ArrayList<>();

    public EditorUI(NkContext _ctx, GameEditor _editor) {
        Scanner objectScanner = new Scanner(this.getClass().getResourceAsStream("/res/raw/objectPackList.txt"));
        while (objectScanner.hasNextLine()) {
            gameItem.add(new Item(objectScanner.nextLine(), false));
        }
        this.ctx = _ctx;
        gEditor = _editor;
        gameObject = gEditor.getGameObject();
    }


    public List<GLEditorObject> getActiveObject() {
        return activeObject;
    }

    public boolean getMode(){return gameMode;}

    public void mouseRotate() {
        if (operation == Operation.ROTATION && InputManager.onClick && activeObject != null && InputManager.onMove) {
            if (InputManager.clickPosition[0] > 30 && InputManager.clickPosition[0] < 883
                    && InputManager.clickPosition[1] > 30 && InputManager.clickPosition[1] < 520) {

                float[] angle = {InputManager.shift[1] / 1f, InputManager.shift[0] / 1f, 0.f};

                if(!cubeEdit)
                    activeObject.get(0).rotateXYZ(angle);
                else
                    activeObject.get(0).getActiveBox().rotateXYZ(angle);

                InputManager.onMove = false;
            }
        }
    }

    public void mouseTranslate() {
        if (operation == Operation.TRANSLATION && InputManager.onClick
                && activeObject != null && InputManager.onMove) {
            if (InputManager.clickPosition[0] > 30 && InputManager.clickPosition[0] < 883
                    && InputManager.clickPosition[1] > 30 && InputManager.clickPosition[1] < 520) {

                float[] position = {InputManager.shift[0] / 230f, -InputManager.shift[1] / 230f, 0.f};

                if(!cubeEdit)
                    activeObject.get(0).move(position);
                else
                    activeObject.get(0).getActiveBox().move(position);
                InputManager.onMove = false;
            }
        }

    }

    public void mouseScale() {
        if (operation == Operation.SCALING && InputManager.onClick && activeObject != null && InputManager.onMove) {
            if (InputManager.clickPosition[0] > 30 && InputManager.clickPosition[0] < 883
                    && InputManager.clickPosition[1] > 30 && InputManager.clickPosition[1] < 520) {

                float[] scale = {1.f + InputManager.shift[0] / 160f, 1.f + InputManager.shift[1] / 160f, 1.f};
                if(!cubeEdit)
                    activeObject.get(0).scale(scale);
                else
                    activeObject.get(0).getActiveBox().scale(scale);
                InputManager.onMove = false;
            }
        }
    }


    public void layout(int x, int y) {
        try (MemoryStack stack = stackPush()) {
            NkPanel layout = NkPanel.mallocStack(stack);

            NkRect rect = NkRect.mallocStack(stack);
            NkRect rect2 = NkRect.mallocStack(stack);

            if (nk_begin(
                    ctx,
                    layout,
                    "Selected Items",
                    nk_rect(x, y, 210, 400, rect),
                    NK_WINDOW_BORDER | NK_WINDOW_MINIMIZABLE | NK_WINDOW_MOVABLE
            )) {
                if (!gameObject.isEmpty()) {
                    nk_layout_row_dynamic(ctx, 25, 2);
                    {
                        for (GLEditorObject gOb : gameObject) {
                            if (gOb.getSelected() != nk_select_label(ctx, gOb.getObjectName(),
                                    NK_TEXT_ALIGN_LEFT, gOb.getSelected())) {
                                gOb.setSelected(!gOb.getSelected());
                                if (gOb.getSelected()) {
                                    if (!activeObject.isEmpty() && !InputManager.onShift) {
                                        for (GLEditorObject aOb : activeObject)
                                            aOb.setSelected(false);
                                        activeObject.clear();
                                    }
                                    activeObject.add(gOb);
                                } else activeObject.remove(gOb);
                            }
                        }
                    }
                }
                nk_layout_row_static(ctx, 25, 80, 2);
                if (!activeObject.isEmpty()) {
                    activeObject.get(0).setUnifom((float)(System.currentTimeMillis()%10000)/400f, "time");
                    if (!wasClick) {
                        if (nk_button_label(ctx, "Delete")) {
                            for(GLEditorObject ob : activeObject)
                                gameObject.remove(ob);
                            activeObject.clear();
                        }

                        if (nk_button_label(ctx, "Save"))
                            wasClick = true;

                        if (nk_button_label(ctx, "Next Animation"))
                            for(GLEditorObject ob : activeObject)
                                ob.nextTexturePack();

                        if (nk_button_label(ctx, "Next Box"))
                            for(GLEditorObject ob : activeObject)
                                ob.nextBox();

                    } else {
                        nk_layout_row_dynamic(ctx, 25, 1);
                        NkFilterCallback stringFilter = NkFilterCallback.create(Nuklear::nnk_filter_default);

                        ByteBuffer buffer = stack.calloc(256);
                        int length = memASCII(fileNme, false, buffer);

                        IntBuffer len = stack.ints(length);
                        nk_edit_string(ctx, NK_EDIT_SIMPLE, buffer, len, 255, stringFilter);

                        fileNme = (memASCII(buffer, len.get(0)));

                        if (nk_button_label(ctx, "Save selected items")){
                            gEditor.saveObject(fileNme);
                            gameItem.add(new Item("/res/"+fileNme+".ggo", false));
                        }

                        if (nk_button_label(ctx, "Cancel")) {
                            fileNme = "";
                            wasClick = false;
                        }
                    }

                }
            }
            nk_end(ctx);

            if (nk_begin(
                    ctx,
                    layout,
                    "Item List",
                    nk_rect(x + 215, y, 320, 400, rect2),
                    NK_WINDOW_BORDER | NK_WINDOW_MINIMIZABLE | NK_WINDOW_MOVABLE
            )) {

                nk_layout_row_dynamic(ctx, 20, 1);
                gameMode = nk_check_label(ctx, "Game Mode", gameMode);

                NkPanel combo1 = NkPanel.mallocStack(stack);

                if (nk_combo_begin_label(ctx, combo1, "Item List",
                        (gameItem.size() < 10) ? gameItem.size() * 22 + 35 : 250)) {

                    nk_layout_row_dynamic(ctx, 20, 1);
                    {
                        for (Item item : gameItem)
                            if (nk_select_label(ctx, item.name, NK_TEXT_ALIGN_LEFT, item.selectedObject)) {
                                gEditor.addObject(item.name);
                            }
                    }
                }
                nk_combo_end(ctx);

                if (activeObject.size() == 1) {

                    cubeEdit = nk_check_label(ctx, "Edit Box", cubeEdit);

                    nk_layout_row_dynamic(ctx, 25, 1);
                    if (nk_select_label(ctx, "Position:", NK_TEXT_ALIGN_LEFT, operation == Operation.TRANSLATION)
                            || operation == Operation.TRANSLATION)
                        operation = Operation.TRANSLATION;

                    float[] position = cubeEdit ? activeObject.get(0).getActiveBox().getPosition() :
                            activeObject.get(0).getPosition();

                    nk_layout_row_dynamic(ctx, 25, 3);
                    position[0] = nk_propertyf(ctx, "X:", -2f, position[0], 2f, 0.01f, 0.01f);
                    position[1] = nk_propertyf(ctx, "Y:", -2f, position[1], 2f, 0.01f, 0.01f);
                    position[2] = nk_propertyf(ctx, "Z:", -2f, position[2], 2f, 0.01f, 0.01f);

                    if(!cubeEdit)
                        for(GLEditorObject currObj : activeObject)
                            currObj.translate(position);
                    else
                        for(GLEditorObject currObj : activeObject)
                            currObj.getActiveBox().translate(position);

                    nk_layout_row_dynamic(ctx, 25, 1);
                    if (nk_select_label(ctx, "Scale:", NK_TEXT_ALIGN_LEFT, operation == Operation.SCALING)
                            || operation == Operation.SCALING)
                        operation = Operation.SCALING;

                    float[] scale = cubeEdit ? activeObject.get(0).getActiveBox().getScale() :
                                                activeObject.get(0).getScale();

                    nk_layout_row_dynamic(ctx, 25, 3);
                    scale[0] = nk_propertyf(ctx, "SX:", 0f, scale[0], 20f, 0.01f, 0.01f);
                    scale[1] = nk_propertyf(ctx, "SY:", 0f, scale[1], 20f, 0.01f, 0.01f);
                    scale[2] = nk_propertyf(ctx, "SZ:", 0f, scale[2], 20f, 0.01f, 0.01f);

                    if(!cubeEdit)
                        for(GLEditorObject currObj : activeObject)
                            currObj.setScale(scale);
                    else
                        for(GLEditorObject currObj : activeObject)
                            currObj.getActiveBox().setScale(scale);

                    nk_layout_row_dynamic(ctx, 25, 1);
                    if (nk_select_label(ctx, "Rotation:", NK_TEXT_ALIGN_LEFT, operation == Operation.ROTATION)
                            || operation == Operation.ROTATION)
                        operation = Operation.ROTATION;

                    float[] angle = cubeEdit ? activeObject.get(0).getActiveBox().getRotationAngles() :
                            activeObject.get(0).getRotationAngles();

                    nk_layout_row_dynamic(ctx, 25, 3);
                    angle[0] = nk_propertyf(ctx, "RX:", -360f, angle[0] / Camera.RADIAN_COEFFICIENT, 360f, 0.01f, 0.01f);
                    angle[1] = nk_propertyf(ctx, "RY:", -360f, angle[1] / Camera.RADIAN_COEFFICIENT, 360f, 0.01f, 0.01f);
                    angle[2] = nk_propertyf(ctx, "RZ:", -360f, angle[2] / Camera.RADIAN_COEFFICIENT, 360f, 0.01f, 0.01f);

                    if(!cubeEdit)
                        for(GLEditorObject currObj : activeObject)
                            currObj.setRotationXYZ(angle);
                    else
                        for(GLEditorObject currObj : activeObject)
                            currObj.getActiveBox().setRotationXYZ(angle);

                    nk_layout_row_static(ctx, 20, 290, 1);
                    if (nk_combo_begin_label(ctx, combo1, "Shaders", 115)) {
                        nk_layout_row_static(ctx, 25, 220, 1);
                        nk_label(ctx, "vShader: " + activeObject.get(0).getVShaderSource(), NK_TEXT_ALIGN_LEFT);
                        nk_label(ctx, "fShader: " + activeObject.get(0).getFShaderSource(), NK_TEXT_ALIGN_LEFT);

                        nk_layout_row_dynamic(ctx, 25, 2);
                        nk_button_label(ctx, "Load");
                        nk_button_label(ctx, "Cancel");
                    }
                    nk_combo_end(ctx);

                    nk_layout_row_static(ctx, 20, 290, 1);
                    if (nk_combo_begin_label(ctx, combo1, "Textures", 250)) {
                        nk_layout_row_static(ctx, 25, 220, 1);
                        for (String texture : activeObject.get(0).getTextureSource())
                            nk_label(ctx, "texture: " + texture, NK_TEXT_ALIGN_LEFT);
                    }
                    nk_combo_end(ctx);

                }
            }
            nk_end(ctx);

            if (activeObject.size() == 1) {
                mouseRotate();
                mouseTranslate();
                mouseScale();
            }
        }
    }
}
