package interpolator;

public class Interpolate {

	static {
		System.loadLibrary("native");
	}

	public static void main(String[] args) {
		
		//new Interpolate.interpolate();
		
	}
	
	private native void interpolate();
	
}
