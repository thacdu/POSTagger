import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MatrixGenerator {
	private Map<String, String[]> lexicon;
	private Map<String, Integer> freq;
	private String[] tagset;
	private String trainingFile = "mergeFile.txt";
	
	public MatrixGenerator(Map<String, String[]> lexicon, String[] tagset) {
		this.lexicon = lexicon;
		this.tagset = tagset;
	}
	
	public HashMap<String, Double> createMatrixB(){
		String[] l = lexicon.keySet().toArray(new String[0]);
		HashMap<String, Double> result = new HashMap<String, Double>();
		HashMap<String, Integer> C = new HashMap<String, Integer>();
		
		for(int i = 0; i < l.length; i++){
			String tags[] = lexicon.get(l[i]);
			for(int j = 0; j < tags.length; j++)
				result.put(l[i] + " " + tags[j], 0.0);
		}
		
		for(int i = 0; i < tagset.length; i++)
			C.put(tagset[i], 0);
			
		FileInteraction file = new FileInteraction(trainingFile);
		
		try{
			file.openInputFile();
			
			while(file.hasNext()){
				String[] line = file.readLine().split("[ ]+");
				for(int i = 0; i < line.length; i++){
					String[] temp = line[i].split("[/]+");
					if(temp.length < 2) continue;
					String key = temp[0] + " " + temp[1];
					
					if(result.get(key) == null)
						result.put(key, 1.0);
					else result.put(key, result.get(key) + 1.0);
					
					if(C.get(temp[1]) == null)
						C.put(temp[1], 1);
					else C.put(temp[1], C.get(temp[1]) + 1);
				}
			}
			
			int posibleWT = tagset.length * l.length;
			
			for (int j = 0; j < l.length; j++) {
				String[] tags = lexicon.get(l[j]);
				
			    for (int k = 0; k < tags.length; k++) {
			    	double a = result.get(l[j] + " " + tags[k]) + 0.5;
			        double b = C.get(tags[k]) + posibleWT*0.5;
			        result.put(l[j] + " " + tags[k], Math.log(a / b));
			    }
			}
			
			file.closeInputFile();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	int getFreq(String string){
		if (freq.containsKey(string)) return freq.get(string);
		return 0;
	}
	
	public double[][] createMatrixA(){
		FileInteraction file = new FileInteraction(trainingFile);

		double epsilon = 0.00005;
		
		String[] tagSet = new String[tagset.length];
		
		for(int i = 0; i < tagset.length; i ++)
			tagSet[i] = "/" + tagset[i];
		
		double res[][] = new double[tagset.length][tagset.length];
		
		try{
			file.openInputFile();
			int[] totalTag = new int[tagset.length];
			for(int i = 0 ; i < tagset.length ; i++){
				totalTag[i] = 0;
				for(int j = 0 ; j < tagset.length ; j++){
					res[i][j] = 0.0;
				}
			}
			
			while(file.hasNext()){
				String temp = "./. " + file.readLine();
				String[] line = temp.split("[ ]+");
				
				for(int i = 0; i < line.length; i++){
					String current = line[i];
					
					int indexTagCurrent = getTagIndex(current);
					totalTag[indexTagCurrent]++;
					if(i < line.length - 1){
						String after = line[i+1];
						int indexTagAfter = getTagIndex(after);
						res[indexTagCurrent][indexTagAfter]++;
					}
				}
				
			}
			
			for(int i = 0 ; i < tagset.length ; i++){
				for(int j = 0 ; j < tagset.length ; j++){
					if(res[i][j] != 0.0) res[i][j] = (1-epsilon) * res[i][j] / totalTag[i] + epsilon;
					else res[i][j] = epsilon;
					System.out.printf("%f ", res[i][j]);
				}
				System.out.println();
			}
			/*
			for(int i = 0 ; i < tagset.length ; i++){
				System.out.println(totalTag[i]+" " + tagset[i]);
			}*/
			file.closeInputFile();
		}catch(IOException e){
			e.printStackTrace();
		}
		return res;
	}
	
	private int getTagIndex(String string){
		int res = 0;
    	for(int i = 0; i < tagset.length; i++)
    		if(string.contains("/" + tagset[i])) res = i;
    	return res;
    }
}