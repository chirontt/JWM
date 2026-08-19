package io.github.humbleui.jwm.examples;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import io.github.humbleui.jwm.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;

import org.lwjgl.BufferUtils;

/// An example of JWM working with LWJGL as graphics-drawing library using OpenGL.
/// 
/// This example shows a rotating square at the center of the main window.
/// 
/// Adapted from
/// [SwtDemo.java](https://github.com/LWJGL/lwjgl3-demos/blob/main/src/org/lwjgl/demo/opengl/swt/SwtDemo.java)
/// of the `lwjgl3-demos` project.
/// 
public class Example implements Consumer<Event> {

    private Window window;
    private int contentWidth, contentHeight;

    public Example(int width, int height) {
        window = App.makeWindow();
        window.setEventListener(this);
        window.setTitle("JWM with LWJGL rendering using OpenGL");

        window.setLayer(new LwjglLayer());

        window.setWindowSize(width, height);
        window.setVisible(true);
    }

    @Override
    public void accept(Event e) {
        if (e instanceof EventWindowCloseRequest) {
            window.close();
            App.terminate();
        }
        else if (e instanceof EventWindowResize er) {
            contentWidth = er.getContentWidth();
            contentHeight = er.getContentHeight();
        }
        else if (e instanceof LwjglEventFrame) {
            drawRotatingSquare(contentWidth, contentHeight);
            window.requestFrame();
            setRotationAngle();
        }
    }

    private boolean resourcesInitialized = false;
    private int programId;
    private int vaoId;
    private float rot;
    private long lastTime = System.nanoTime();
    private int rotLocation;
    private int aspectLocation;

    /// Draw a frame of the rotating square
    /// 
    /// @param width the width of the frame
    /// @param height the height of the frame
    /// 
    private void drawRotatingSquare(int width, int height) {
        if (!resourcesInitialized) {
            initGLResources();
            resourcesInitialized = true;
        }

        // Activate the compiled shader program
        glUseProgram(programId);
        // Bind the VAO (instantly hooks up the VBO, EBO, and layout settings)
        glBindVertexArray(vaoId);

        glClearColor(0.3f, 0.5f, 0.8f, 1.0f); // light blue
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, width, height);

        // Upload new values to 'rot' and 'aspect' uniform locations 
        // inside the compiled shader program
        float aspect = (float) width / height;
        glUniform1f(aspectLocation, aspect);
        glUniform1f(rotLocation, rot);

        // Draw the square (split into two triangles (totaling 6 indices))
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        // Clean up bindings for the next frame
        glBindVertexArray(0);
        glUseProgram(0);
    }

    /// Set the rotation angle of the square for the next frame
    /// 
    private void setRotationAngle() {
        long thisTime = System.nanoTime();
        float delta = (thisTime - lastTime) / 1E9f;
        rot += delta;
        if (rot > 2.0 * Math.PI) {
            rot -= 2.0f * (float) Math.PI;
        }
        lastTime = thisTime;
    }

    /// Initialize the OpenGL resources (shader, VAO, etc.)
    /// 
    private void initGLResources() {
        programId = createShaderProgram();
        glUseProgram(programId);

        // Get the memory locations of 'rot' and 'aspect' uniforms
        // inside the compiled shader program, for later use
        rotLocation = glGetUniformLocation(programId, "rot");
        aspectLocation = glGetUniformLocation(programId, "aspect");

        vaoId = createAndBindVAO();
    }

    /// Create a shader program for the GL context
    /// 
    /// @return the shader program ID
    ///  
    private int createShaderProgram() {
        // Create a simple shader program
        int programId = glCreateProgram();

        // Create and attach a vertex shader to the program
        int vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, """
                #version 330 core
                layout (location = 0) in vec3 vertexPosition; // incoming 3D vertex position
                
                uniform float rot;
                uniform float aspect;
                
                void main(void) {
                  vec4 v = vec4(vertexPosition, 1.0) * 0.5;
                  vec4 v_ = vec4(0.0, 0.0, 0.0, 1.0);
                  v_.x = v.x * cos(rot) - v.y * sin(rot);
                  v_.y = v.y * cos(rot) + v.x * sin(rot);
                  v_.x /= aspect;
                  gl_Position = v_;
                }
                """);
        glCompileShader(vs);
        checkShaderCheckStatus(vs);
        glAttachShader(programId, vs);

        // Create and attach a fragment shader to the program
        int fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, """
                #version 330 core
                layout (location = 0) out vec4 fragColor; // output variable bound to the first color buffer
                
                void main(void) {
                  fragColor = vec4(0.1, 0.3, 0.5, 1.0); // dark blue
                }
                """);
        glCompileShader(fs);
        checkShaderCheckStatus(fs);
        glAttachShader(programId, fs);

        glLinkProgram(programId);
        checkProgramLinkStatus(programId);

        return programId;
    }

    /// Check Vertex/Fragment Shader compilation status
    /// 
    /// @param shaderId the shader ID
    /// 
    /// @throws RuntimeException if the shader compilation failed
    /// 
    private void checkShaderCheckStatus(int shaderId) {
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader compilation failed: " 
                    + glGetShaderInfoLog(shaderId));
        }
    }

    /// Check Shader Program linking status
    /// 
    /// @param programId the program ID
    /// 
    /// @throws RuntimeException if the program linking failed
    /// 
    private void checkProgramLinkStatus(int programId) {
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Program linking failed: " 
                    + glGetProgramInfoLog(programId));
        }
    }

    /// Create and bind a VAO to the GL context
    /// 
    /// @return the VAO ID
    ///  
    private int createAndBindVAO() {
        // Create a simple quad
        float[] vertices = {
                -1.0f, -1.0f, 0.0f, // Index 0 (Bottom-Left)
                 1.0f, -1.0f, 0.0f, // Index 1 (Bottom-Right)
                 1.0f,  1.0f, 0.0f, // Index 2 (Top-Right)
                -1.0f,  1.0f, 0.0f  // Index 3 (Top-Left)
        };
        int[] indices = {
                0, 1, 2, // Triangle 1
                2, 3, 0  // Triangle 2
        };

        // Create and bind the VAO
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create the VBO and upload the vertex coordinates
        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip(); // Crucial: Flip resets buffer position to 0
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Point the VAO to this VBO's data layout.
        // This connects the buffer data to 'layout (location = 0)' in the vertex shader.
        int attributeLocation = 0;
        int coordinateSize = 3; // 3 floats per vertex (X, Y, Z)
        glVertexAttribPointer(attributeLocation, coordinateSize, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(attributeLocation);

        // Create the EBO and upload the drawing indices.
        // Note: When the VAO is bound, it completely memorizes this EBO binding!
        int eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip(); // Crucial: Flip resets buffer position to 0
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        // Unbind the VAO to save the state (optional, but safe practice)
        glBindVertexArray(0);

        // Unbind VBO/EBO *after* unbinding the VAO so no accidental erase of the VAO state
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        return vaoId;
    }

    public static void main(String[] args) {
        App.start(() -> new Example(800, 600));
    }

}
