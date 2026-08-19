package io.github.humbleui.jwm.examples;

import io.github.humbleui.jwm.LayerGL;
import org.lwjgl.opengl.GL;

/// LWJGL-specific layer, using OpenGL.
/// 
public class LwjglLayer extends LayerGL {

    private boolean glInitialized = false;

    @Override
    public void frame() {
        // Make Context Current: Ensure the active window framework (JWM) has bound
        // the OpenGL context to the current execution thread.
        makeCurrent();

        if (!glInitialized) {
            // Tells LWJGL to link its pointers to the current OpenGL context
            // managed by JWM
            GL.createCapabilities();
            glInitialized = true;
        }

        _window.accept(new LwjglEventFrame());

        swapBuffers();
    }

}
