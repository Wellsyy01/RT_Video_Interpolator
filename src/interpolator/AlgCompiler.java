package interpolator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FilenameUtils;

public class AlgCompiler {
	
	String current_algorithm = null;
	
	public AlgCompiler() {
		
		
	}
	
	public void setAlg(String alg_name) throws IOException {
		this.current_algorithm = alg_name;
		try {
			compile_for_JNI(alg_name);
		} catch (IOException e) {
			App.log("Compiling code has gone wrong");
		}
	}
	
	private boolean compile_for_JNI(String name) throws IOException {
		
		String prep_cmd_1 = "gcc -c -I../include -I../include/win32 " + name;
		String prep_cmd_2 = "gcc -Wl,--add-stdcall-alias -I../include -I../include/win32 -shared "
							+ "-o native.dll " + name;
		
		
		// Compile c code
		Runtime runtime = Runtime.getRuntime();
		Process compile = runtime.exec(prep_cmd_1, null, new File("native"));
		try {
			compile.waitFor();
		} catch (InterruptedException e) {
			App.log("Compilation interrupted");
		}
		App.log(prep_cmd_1);
		if(compile.exitValue() == 0) {
			App.log("Source code compiled correctly");
		} else {
			App.log("Error compiling source code");
		}
				
		// Create link
		Process link = runtime.exec(prep_cmd_2, null, new File("native"));
		try {
			link.waitFor();
		} catch (InterruptedException e) {
			App.log("Linking of native library interrupted");
		}
		App.log(prep_cmd_2);
		if(link.exitValue() == 0) {
			App.log("Native library link established correctly");
		} else {
			App.log("Error creating native library link");
		}

		Interpolate.wave();
		return true;
		
		
	}
	
	public boolean run_command_line(String command) throws IOException {
		
		if (this.current_algorithm == null) {
			return false;
		}
		
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(command);
		try {
			process.waitFor();
			App.log.append(command + " exitValue() " + process.exitValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Interpolate.wave();
		System.out.println("ran");
		return true;
		
	}
	
	private static void printLines(String name, InputStream ins) throws Exception {
	    String line = null;
	    BufferedReader in = new BufferedReader(
	        new InputStreamReader(ins));
	    while ((line = in.readLine()) != null) {
	        System.out.println(name + " " + line);
	    }
	  }

}
