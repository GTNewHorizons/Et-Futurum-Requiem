package ganymedes01.etfuturum.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class OpenGLHelper {

    public static void enableTexture2D() {
        enable(GL11.GL_TEXTURE_2D);
    }

    public static void disableTexture2D() {
        disable(GL11.GL_TEXTURE_2D);
    }

    public static void pushAttrib() {
        GL11.glPushAttrib(8256);
    }

    public static void popAttrib() {
        GL11.glPopAttrib();
    }

    public static void loadIdentity() {
        GL11.glLoadIdentity();
    }

    private static void disable(int cap) {
        GL11.glDisable(cap);
    }

    private static void enable(int cap) {
        GL11.glEnable(cap);
    }

    public static void disableAlpha() {
        disable(GL11.GL_ALPHA_TEST);
    }

    public static void enableAlpha() {
        enable(GL11.GL_ALPHA_TEST);
    }

    public static void alphaFunc(int func, float ref) {
        GL11.glAlphaFunc(func, ref);
    }

    public static void enableLighting() {
        enable(GL11.GL_LIGHTING);
    }

    public static void disableLighting() {
        disable(GL11.GL_LIGHTING);
    }

    public static void disableDepth() {
        disable(GL11.GL_DEPTH);
    }

    public static void enableDepth() {
        enable(GL11.GL_DEPTH);
    }

    public static void depthFunc(int func) {
        GL11.glDepthFunc(func);
    }

    public static void depthMask(boolean flag) {
        GL11.glDepthMask(flag);
    }

    public static void disableBlend() {
        disable(GL11.GL_BLEND);
    }

    public static void enableBlend() {
        enable(GL11.GL_BLEND);
    }

    public static void blendFunc(int sfactor, int dfactor) {
        GL11.glBlendFunc(sfactor, dfactor);
    }

    public static void enableCull() {
        enable(GL11.GL_CULL_FACE);
    }

    public static void disableCull() {
        disable(GL11.GL_CULL_FACE);
    }

    public static void enableRescaleNormal() {
        enable(GL12.GL_RESCALE_NORMAL);
    }

    public static void disableRescaleNormal() {
        disable(GL12.GL_RESCALE_NORMAL);
    }

    public static void matrixMode(int mode) {
        GL11.glMatrixMode(mode);
    }

    public static void pushMatrix() {
        GL11.glPushMatrix();
    }

    public static void popMatrix() {
        GL11.glPopMatrix();
    }

    public static void rotate(double angle, double x, double y, double z) {
        GL11.glRotated(angle, x, y, z);
    }

    public static void rotate(float angle, float x, float y, float z) {
        GL11.glRotatef(angle, x, y, z);
    }

    public static void scale(float x, float y, float z) {
        GL11.glScalef(x, y, z);
    }

    public static void scale(double x, double y, double z) {
        GL11.glScaled(x, y, z);
    }

    public static void translate(float x, float y, float z) {
        GL11.glTranslatef(x, y, z);
    }

    public static void translate(double x, double y, double z) {
        GL11.glTranslated(x, y, z);
    }

    public static void colorMask(boolean r, boolean g, boolean b, boolean a) {
        GL11.glColorMask(r, g, b, a);
    }

    public static void colour(float r, float g, float b) {
        GL11.glColor3f(r, g, b);
    }

    public static void colour(int colour) {
        float r = (colour >> 16 & 255) / 255F;
        float g = (colour >> 8 & 255) / 255F;
        float b = (colour & 255) / 255F;

        colour(r, g, b);
    }

    public static void viewport(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }

    public static void normal(float x, float y, float z) {
        GL11.glNormal3f(x, y, z);
    }
}
