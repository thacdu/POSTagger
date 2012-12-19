
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ViterbiHMM{
    private double[][] delta;
    private int[][] psi;
    private String[] hiddenAlphabet;
    private String[] observableAlphabet;
    private String[] observation;
    private Map<String, String[]> lexicon;
    private double oo = 9999;
    
    private double[][] A;
    private HashMap<String, Double> B;
    
    public ViterbiHMM(String tagSet, String obsA,
            Map<String, String[]> lexicon) {
    	this.hiddenAlphabet = tagSet.split(" ");
    	this.observableAlphabet = obsA.split(" ");
        this.lexicon = lexicon;
        MatrixGenerator gen = new MatrixGenerator(lexicon, hiddenAlphabet);
        this.A = gen.createMatrixA();
        this.B = gen.createMatrixB();
    }
    
    public String mostProbableSequence(String input) {
        input = ". " + input;
        this.observation = input.split("\\s");
        
        init();
        induct();
        
        //System.out.println("Delta:");
        //ViterbiMatrixTools.printMatrix(delta);
        //System.out.println();
        //System.out.println("Psi:");
        //ViterbiMatrixTools.printMatrix(psi);
        //System.out.println();
        return getResult();
    }
    
    private int positionOfStartTag(){
    	return getTagIndex(".");
    }
    
    private void init() {
        delta = new double[hiddenAlphabet.length][observation.length];
        
        for (int i = 0; i < delta.length; i ++) {
        	Arrays.fill(delta[i], -oo);
        }
        
        int posOfStart = positionOfStartTag(); 
        
        delta[posOfStart][0] = 0.0;
        psi = new int[hiddenAlphabet.length][observation.length - 1];
        for (int i = 0; i < psi.length; i++) {
            Arrays.fill(psi[i], -1);
        }
    }
    
    private void induct() {
        for (int i = 1; i < observation.length; i++) {
        	String obs = observation[i];
        	String[] tags = lexicon.get(obs);
        	if(tags == null){
        		obs = observableAlphabet[observableAlphabet.length - 1];
        		tags = lexicon.get(obs);
        	}
        	
            for (int j = 0; j < tags.length; j++) {
                double emisValue = B.get(obs + " " + tags[j]);
                int tagIndex = getTagIndex(tags[j]);
                
                for(int k = 0; k < hiddenAlphabet.length; k++){
                	double res = delta[k][i-1] + emisValue + Math.log(A[k][tagIndex]);
                	
	                if (res > delta[tagIndex][i]){
	                	delta[tagIndex][i] = res;
	                    psi[tagIndex][i - 1] = k;
	                }
                }
            }
        }
    }
    
    private String getResult() {
        String[] resultArray = new String[psi[0].length];
        int lastIndexInPsi = ViterbiMatrixTools.indexOfMaximimumForCol(
                delta[0].length - 1, delta);

        int lastValueInPsi = psi[lastIndexInPsi][psi[0].length - 1];
        String lastTag = hiddenAlphabet[lastIndexInPsi];
        resultArray[resultArray.length - 1] = lastTag;
        // retrieve other tags:
        for (int i = psi[0].length - 2; i >= 0; i--) {
            resultArray[i] = hiddenAlphabet[lastValueInPsi];
            lastValueInPsi = psi[lastValueInPsi][i];
        }
        StringBuffer resultString = new StringBuffer();
        for (int i = 0; i < resultArray.length; i++) {
            resultString.append(resultArray[i]);
            if (i < resultArray.length - 1)
                resultString.append(" ");
        }
        return resultString.toString();
    }
    /*
    private double getEmisValue(String string) {
        if (B.get(string) == null){
        	String alter = observableAlphabet[observableAlphabet.length - 1];
        	//System.out.println(alter);
        	return B.get(alter);
        }
        return B.get(string);
    }*/
    
    private int getTagIndex(String string){
    	for(int i = 0; i < hiddenAlphabet.length; i++)
    		if(hiddenAlphabet[i].compareTo(string) == 0) return i;
    	return 0;
    }
}
