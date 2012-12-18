import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

public class MatrixGenerator {
	private Map<String, String[]> lexicon;
	private Map<String, Integer> freq;
	private String corpus;
	private String tagset;
	
	public MatrixGenerator(Map<String, String[]> lexicon, String corpus, String tagset) {
		this.corpus = corpus;
		this.lexicon = lexicon;
		this.tagset = tagset;
		countLexems();
		System.out.println("Tagset: " + tagset);
		System.out.println();
		System.out.println("Corpus: " + corpus);
		System.out.println();
		System.out.println("Frequencies:");
		System.out.println();
		printMap(freq);
	}
	
	private double getValue(String string, HashMap<String, Double> map){
		if (map.get(string) == null) return 0.0;
		return map.get(string);
	}
	
	public HashMap<String, Double> createMatrixB() {
		String[] l = lexicon.keySet().toArray(new String[0]);
		
		HashMap<String, Double> result = new HashMap<String, Double>();
		for (int j = 0; j < l.length; j++) {
	        String[] tags = lexicon.get(l[j]);
	        double value = 1.0 / tags.length;
	        
	        for(int k = 0; k < tags.length; k++){
	        	result.put(l[j] + " " + tags[k], value);
	        }
		}
		
		for (int j = 0; j < l.length; j++) {
			String[] tags = lexicon.get(l[j]);
			
		    for (int k = 0; k < tags.length; k++) {
		    	double a = result.get(l[j] + " " + tags[k]) * getFreq(l[j]) + 1;
		        double b = 0.0;
		        for (int index = 0; index < l.length; index++) {
		            b += getValue(l[index] + " " + tags[k], result) * getFreq(l[index]) + lexicon.get(l[index]).length;       
		        }
		        result.put(l[j] + " " + tags[k], a / b);
		    }
		}
		
		for(int j = 0; j < l.length; j++) {
			String[] tags = lexicon.get(l[j]);
			
			for(int k = 0; k < tags.length; k++){
				String key = l[j] + " " + tags[k];
				result.put(key, Math.log(result.get(key)));
			}
		}
		/*
		System.out.print("MANG B\n");
		for(int j = 0; j < l.length; j++) {
			String[] tags = lexicon.get(l[j]);
			
			for(int k = 0; k < tags.length; k++) {
				System.out.print(result.get(l[j] + " " + tags[k]) + " ");
			}
			System.out.println();
		}*/
		
		return result;
	}
	
	int getFreq(String string){
		if (freq.containsKey(string)) return freq.get(string);
		return 0;
	}
	
	public double[][] createMatrixA(double diff) {
	    String[] t = tagset.split(" ");
	    double[][] res = new double[t.length][t.length];
	    double average = 1.0 / res[0].length;
	    for (int i = 0; i < res.length; i++) {
	        Arrays.fill(res[i], average);
	    }
	    
	    Random r = new Random();
	    for (int i = 0; i < res.length; i++){
	        double var = 1.0;
	        while (var > diff)
	            var = r.nextDouble();
	        int pos = r.nextInt(res[i].length);
	        boolean plus = true;
	        for (int j = 0; j < res[i].length; j++) {
	            if (res[i].length % 2 != 0 && j == pos)
	                continue;
	            if (plus && res[i][j] - var > 0) {
	                res[i][j] += var;
	                plus = false;
	            } else if (res[i][j] - var > 0) {
	                res[i][j] -= var;
	                plus = true;
	            }
	        }
	    }
	    return res;
	}
	
	private void printMap(Map<String, Integer> map) {
	    for (Map.Entry<String, Integer> e : map.entrySet() ) {
	        System.out.println(e.getKey() + ": " + e.getValue());
	    }
	}
	
	private void countLexems() {
	    freq = new HashMap<String, Integer>();
	    
	    for (StringTokenizer tok = new StringTokenizer(corpus, " \t\n.,", true);
	         tok.hasMoreTokens(); ) {
	        String rec = tok.nextToken();
	        if (lexicon.containsKey(rec)) {
	            if (freq.containsKey(rec)) {
	                freq.put(rec, freq.get(rec) + 1);
	            } else {
	                freq.put(rec, 1);
	            }
	        }
	    }
	}
}