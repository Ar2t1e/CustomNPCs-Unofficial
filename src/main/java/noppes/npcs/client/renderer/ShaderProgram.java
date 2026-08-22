package noppes.npcs.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

public class ShaderProgram {

    private final int programId;

    public ShaderProgram(ResourceLocation vertex, ResourceLocation fragment) throws Exception {
        int v = 0, f = 0;
        try {
            v = load(vertex, GL20.GL_VERTEX_SHADER);
            f = load(fragment, GL20.GL_FRAGMENT_SHADER);
            programId = GL20.glCreateProgram();
            GL20.glAttachShader(programId, v);
            GL20.glAttachShader(programId, f);
            GL20.glLinkProgram(programId);
            if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                throw new RuntimeException(GL20.glGetProgramInfoLog(programId, 1024));
            }
            GL20.glValidateProgram(programId);
        } finally {
            if (v != 0) GL20.glDeleteShader(v);
            if (f != 0) GL20.glDeleteShader(f);
        }
    }

    private int load(ResourceLocation rl, int type) throws Exception {
        int id = GL20.glCreateShader(type);
        InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream();
        String src = IOUtils.toString(is, StandardCharsets.UTF_8);
        is.close();
        GL20.glShaderSource(id, src);
        GL20.glCompileShader(id);
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
            throw new RuntimeException(rl + ": " + GL20.glGetShaderInfoLog(id, 1024));
        return id;
    }

    public void use()   { GL20.glUseProgram(programId); }
    public void stop()  { GL20.glUseProgram(0); }

    public int u(String n){ return GL20.glGetUniformLocation(programId, n); }

    public void setMat4(String n, FloatBuffer b)                { GL20.glUniformMatrix4(u(n), false, b); }
    public void set1i(String n, int v)                          { GL20.glUniform1i(u(n), v); }
    public void set2i(String n, int v1, int v2)                 { GL20.glUniform2i(u(n), v1, v2); }
    public void set3i(String n, int v1, int v2, int v3)         { GL20.glUniform3i(u(n), v1, v2, v3); }
    public void set1f(String n, float v)                        { GL20.glUniform1f(u(n), v); }
    public void set2f(String n, float v1, float v2)             { GL20.glUniform2f(u(n), v1, v2); }
    public void set3f(String n, float v1, float v2, float v3)   { GL20.glUniform3f(u(n), v1, v2, v3); }

    public static void texUnit(int unit) { GL13.glActiveTexture(unit); }

    public void delete() {
        if (programId != 0) { GL20.glDeleteProgram(programId); }
    }

}