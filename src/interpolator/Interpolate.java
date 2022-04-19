package interpolator;

public class Interpolate {

    static {
        System.loadLibrary("native");
    }
    
    public static int[] link(int[] pixels, int width, int height, int target_width, int target_height) {
    	return (new Interpolate().interpolate(pixels, width, height, target_width, target_height));
    }

    private native int[] interpolate(int[] pixels, int width, int height, int target_width, int target_height);
}