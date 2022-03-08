package interpolator;

import java.io.File;
import java.io.IOException;

public class AlgCompiler {
	
	String current_algorithm = null;
	
	public AlgCompiler() {
		
		
	}
	
	public void setAlg(String alg_name) {
		this.current_algorithm = alg_name;
	}
	
	public boolean run_command_line() throws IOException {
		
		if (current_algorithm == null) {
			return false;
		}
		
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec("javac -h " + this.current_algorithm);
		System.out.println("ran");
		return true;
		
	}

}
