package interpolator;

public class Interpolate {

    static {
        System.loadLibrary("native");
    }
    
    public static void wave() {
    	new Interpolate().sayHello();
    }

    // Declare a native method sayHello() that receives no arguments and returns void
    private native void sayHello();
}