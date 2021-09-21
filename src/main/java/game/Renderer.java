package game;

import engine.Utils;
import engine.Window;
import engine.graph.Mesh;
import engine.graph.ShaderProgram;


import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL30.glEnableVertexAttribArray;

import org.joml.Matrix4f;


public class Renderer {
	
	private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    private Matrix4f projectionMatrix;

    private ShaderProgram shaderProgram;

    public Renderer() {
    }

    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();
        
        float aspectRatio = (float) window.getWidth()/ window.getHeight();
        projectionMatrix = new Matrix4f().setPerspective(Renderer.FOV, aspectRatio, Renderer.Z_NEAR, Renderer.Z_FAR);
        shaderProgram.createUniform("projectionMatrix");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Mesh mesh) {
        clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }
        
        shaderProgram.bind();
        try {
			shaderProgram.createUniform("projectionMatrix");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Bind to the VAO
        glBindVertexArray(mesh.getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the vertices
        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT,0);

        // Restore state
        glBindVertexArray(0);

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}
