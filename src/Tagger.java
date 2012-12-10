
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Tagger {
	String tagset;
	String corpus;
	Map<String, String> lexicon;
	String lexiconString;
	
	private void readLexicon(){
		lexiconString = "";
		FileInteraction file = new FileInteraction("Dict-UTF8.txt");
		try{
			file.openInputFile();
			
			while(file.hasNext()){
				String line = file.readLine();
				String res[] = line.split("[ {}]+");
				String tag = "";
				for(int i = 1; i < res.length-1; i++)
					tag = tag.concat(res[i] + " ");
				tag = tag.concat(res[res.length - 1]);
				
				lexicon.put(res[0], tag);
				lexiconString = lexiconString + " " + res[0];
			}
			
			file.closeInputFile();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void readCorpus(String fileName){
		FileInteraction file = new FileInteraction(fileName);
		corpus = "";
		
		try{
			file.openInputFile();
			while(file.hasNext()){
				String line = file.readLine();
				corpus = corpus + line;
			}
			
			file.closeInputFile();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void initData() {
	    lexicon = new HashMap<String, String>();
	    lexicon.put(".", ".");
	    
	    readLexicon();
	    
	    StringBuffer lexBuf = new StringBuffer();
	    for (String word : lexicon.keySet()) {
	        lexBuf.append(word + " ");
	    }
	    
	    tagset = "A C E D I Nc M O N P S R V Np X Z Nu . , ...";
	}

	
	public void tag(String inputFileName) {
	    initData();
	    
	    readCorpus(inputFileName);
	    
	    ViterbiHMM hmm = new ViterbiHMM(tagset, lexiconString, lexicon,
	            corpus);
	    
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
	}
}
