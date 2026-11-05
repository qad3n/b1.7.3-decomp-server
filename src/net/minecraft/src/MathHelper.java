package net.minecraft.src;

public class MathHelper {
    private static float[] SIN_TABLE = new float[65536];

    public static final float sin(float x) {
        return SIN_TABLE[(int)(x * 10430.378f) & 0xFFFF];
    }

    public static final float cos(float x) {
        return SIN_TABLE[(int)(x * 10430.378f + 16384.0f) & 0xFFFF];
    }

    public static final float sqrt_float(float x) {
        return (float)Math.sqrt(x);
    }

    public static final float sqrt_double(double x) {
        return (float)Math.sqrt(x);
    }

    public static int floor_float(float x) {
        int n = (int)x;
        return x < (float)n ? n - 1 : n;
    }

    public static int floor_double(double x) {
        int n = (int)x;
        return x < (double)n ? n - 1 : n;
    }

    public static float abs(float x) {
        return x >= 0.0f ? x : -x;
    }

    public static double abs_max(double a, double b) {
        if (a < 0.0) {
            a = -a;
        }
        if (b < 0.0) {
            b = -b;
        }
        return a > b ? a : b;
    }

    static {
        for (int i = 0; i < 65536; ++i) {
            MathHelper.SIN_TABLE[i] = (float)Math.sin((double)i * Math.PI * 2.0 / 65536.0);
        }
    }
}

