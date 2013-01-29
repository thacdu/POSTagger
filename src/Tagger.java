
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Tagger {
	String tagset;
	Map<String, String[]> lexicon;
	String lexiconString;
	
	private void readLexicon(){
		lexiconString = "";
		FileInteraction file = new FileInteraction("newDict.txt");
		try{
			file.openInputFile();
			
			while(file.hasNext()){
				String line = file.readLine();
				String res[] = line.split("[ {}]+");
				String[] tag = new String[res.length-1];
				for(int i = 1; i < res.length; i++)
					tag[i-1] = res[i];
				
				lexicon.put(res[0], tag);
				lexiconString = lexiconString + " " + res[0];
			}
			
			file.closeInputFile();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void initData() {
	    lexicon = new HashMap<String, String[]>();
	    
	    readLexicon();
	    
	    tagset = "Np Nc Nu Nb Ny Yb Md N V A P L M R E F C I T B Y S X H ! \" ' ( ) *  + , - _ . / : ; ? ...";
	}

	
	public void tag(String inputFileName) {
	    initData();
	    
	    ViterbiHMM hmm = new ViterbiHMM(tagset, lexiconString, lexicon);
	    
	    FileInteraction tagFile = new FileInteraction(inputFileName);
	    FileInteraction resultFile = new FileInteraction("output.txt");
	    
	    try{
	    	tagFile.openInputFile();
	    	resultFile.openOutputFile();
	    	
	    	while(tagFile.hasNext()){
			    String input = tagFile.readLine();
			    String[] word = input.split(" ");
			    String[] tag = hmm.mostProbableSequence(input).split(" ");
			    
			    StringBuilder res = new StringBuilder();
			    for (int i = 0; i < word.length; i++) {
			        res.append(word[i] + "/" + tag[i] + " ");
			    }
			    
			    resultFile.printLine(res.toString());
	    	}
		    
		    tagFile.closeInputFile();
		    resultFile.closeOutputFile();
	    }catch(IOException e){
	    	e.printStackTrace();
	    }
	}
	
	public static void main(String[] args){
		Tagger t = new Tagger();
		t.tag("input.txt");
		System.out.println("Done!");
	}
}
