package com.company;

import java.nio.IntBuffer;

import Editor.GameEditor;

import org.lwjgl.glfw.*;
import org.lwjgl.nuklear.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.Callback;
import org.lwjgl.system.Platform;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBDebugOutput.*;
import static org.lwjgl.opengl.GL43.*;

import static org.lwjgl.system.MemoryUtil.*;


public class DisplayManager {

    private static final NkAllocator ALLOCATOR;
    Callback debugProc;
    static {
        ALLOCATOR = NkAllocator.create();
        ALLOCATOR.alloc((handle, old, size) -> {
            long mem = nmemAlloc(size);
            if ( mem == NULL )
                throw new OutOfMemoryError();

            return mem;

        });
        ALLOCATOR.mfree((handle, ptr) -> nmemFree(ptr));
    }


    private long win;

    // private Renderer renderer;
    private GameEditor editor;


    public DisplayManager() {

        // Init qlfw
        GLFWErrorCallback.createPrint().set();
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize glfw");


        // Adding window hints
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        if ( Platform.get() == Platform.MACOSX )
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);


        // Window size
        int WINDOW_WIDTH = 1740;
        int WINDOW_HEIGHT = 900;

        // Creating window
        win = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "GGO Engine", NULL, NULL);
        if ( win == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Getting OpenGL contest
        glfwMakeContextCurrent(win);
        GLCapabilities caps = GL.createCapabilities();

        debugProc = GLUtil.setupDebugMessageCallback();

        if ( caps.OpenGL43 )
            glDebugMessageControl(
                    GL_DEBUG_SOURCE_API,
                    GL_DEBUG_TYPE_OTHER,
                    GL_DEBUG_SEVERITY_NOTIFICATION,
                    (IntBuffer)null, false);

        else if ( caps.GL_KHR_debug ) {
            KHRDebug.glDebugMessageControl(
                    KHRDebug.GL_DEBUG_SOURCE_API,
                    KHRDebug.GL_DEBUG_TYPE_OTHER,
                    KHRDebug.GL_DEBUG_SEVERITY_NOTIFICATION,
                    (IntBuffer)null,
                    false
            );
        }
        else if ( caps.GL_ARB_debug_output )
            glDebugMessageControlARB(
                    GL_DEBUG_SOURCE_API_ARB,
                    GL_DEBUG_TYPE_OTHER_ARB,
                    GL_DEBUG_SEVERITY_LOW_ARB,
                    (IntBuffer)null, false);

        // Init Editor
        editor = new GameEditor(win);
    }

    protected void run() {

        // Set synchronization
        glfwSwapInterval(1);

        // Show window
        glfwShowWindow(win);

        // Window live cycle
        while ( !glfwWindowShouldClose(win) ) {

            // Input handle
            editor.prepareInput();
            glfwPollEvents();
            editor.inputManaging();

            // Drawing scene
            editor.draw();

            // Swap buffers
            glfwSwapBuffers(win);
        }

        // Free callbacks
        glfwFreeCallbacks(win);
        if ( debugProc != null )
            debugProc.free();

        // Closing window
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}