import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;

/**
 * Interact with file
 */
public class FileInteraction {
	private String fileName;
	private Scanner input;
	private Writer output;
	
	public FileInteraction(String fileName){
		this.fileName = fileName;
	}
	
	public void openInputFile() throws IOException{
		input = new Scanner(new FileInputStream(fileName), "UTF8");
	}
	
	public void openOutputFile() throws IOException{
		output = new OutputStreamWriter(new FileOutputStream(fileName), "UTF8"); 
	}
	
	public void closeInputFile(){
		input.close();
	}
	
	public void closeOutputFile() throws IOException{
		output.close();
	}
	
	public String readLine(){
		return input.nextLine();
	}
	
	public boolean hasNext(){
		return input.hasNext();
	}
	
	public void printLine(String line) throws IOException{
		output.write(line + "\n");
	}
}
