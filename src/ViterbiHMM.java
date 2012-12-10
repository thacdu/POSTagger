
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ViterbiHMM{
    private double[][] delta;
    private int[][] psi;
    private String[] hiddenAlphabet;
    private String[] observableAlphabet;
    private String[] observation;
    private Map<String, String> lexicon;
    
    private double[][] A;
    private HashMap<String, Double> B;
    
    public ViterbiHMM(String tagSet, String obsA,
            Map<String, String> lexicon, String corpus) {
    	MatrixGenerator gen = new MatrixGenerator(lexicon, corpus, tagSet);
    	this.hiddenAlphabet = tagSet.split(" ");
    	this.observableAlphabet = obsA.split(" ");
        this.lexicon = lexicon;
        this.A = gen.createMatrixA(0.01);
        this.B = gen.createMatrixB();
    }
    
    public String mostProbableSequence(String input) {
        input = ". " + input;
        this.observation = input.split("\\s");
        
        init();
        induct();
        
        System.out.println("Delta:");
        ViterbiMatrixTools.printMatrix(delta);
        System.out.println();
        System.out.println("Psi:");
        ViterbiMatrixTools.printMatrix(psi);
        System.out.println();
        return getResult();
    }
    
    private void init() {
        delta = new double[hiddenAlphabet.length][observation.length];
        //System.out.println(delta.length + " " + delta[0].length );
        delta[delta.length - 1][0] = 1.0;
        psi = new int[hiddenAlphabet.length][observation.length - 1];
        for (int i = 0; i < psi.length; i++) {
            Arrays.fill(psi[i], -1);
        }
    }
    
    private void induct() {
        for (int i = 1; i < observation.length; i++) {
        	String[] tags = lexicon.get(observation[i]).split(" ");
        	
            for (int j = 0; j < tags.length; j++) {
                int prevIndex = ViterbiMatrixTools.indexOfMaximimumForCol(
                        i - 1, delta);
                
                double emisValue = getEmisValue(observation[i] + " " + tags[j]);
                //if(lexIndex == -1){
                	//lexIndex = observableAlphabet.length-1;
                //}
                int tagIndex = getTagIndex(tags[j]);
                //double res = delta[prevIndex][i-1] * B.get(observation[lexIndex] + " " + hiddenAlphabet[j]) * A[prevIndex][j];
                double res = delta[prevIndex][i-1] * emisValue * A[prevIndex][tagIndex];
                delta[tagIndex][i] = res;
                if (res > 0.0)
                    psi[tagIndex][i - 1] = prevIndex;
            }

        }
    }
    
    private String getResult() {
        String[] resultArray = new String[psi[0].length];
        int lastIndexInPsi = ViterbiMatrixTools.indexOfMaximimumForCol(
                delta[0].length - 1, delta);
        if (lastIndexInPsi == -1) {
            System.out.println("no tag-sequence found for input, exit.");
            System.exit(0);
        }
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
    
    double getEmisValue(String string) {
        if (B.get(string) == null){
        	String alter = observableAlphabet[observableAlphabet.length - 1];
        	return B.get(alter);
        }
        return B.get(string);
    }
    
    int getTagIndex(String string){
    	for(int i = 0; i < hiddenAlphabet.length; i++)
    		if(hiddenAlphabet[i].contains(string)) return i;
    	return 0;
    }
}
