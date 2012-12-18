
import java.text.NumberFormat;

public class ViterbiMatrixTools {
	private static double oo = 999999;
	
	static int indexOfMaximimumForCol(int i, double[][] matrix) {
	    int maxIndex = -1;
	    double maxValue = -oo;
	    for (int j = 0; j < matrix.length; j++) {
	        if (matrix[j][i] > maxValue) {
	            maxIndex = j;
	            maxValue = matrix[j][i];
	        }
	    }
	    return maxIndex;
	}
	
	static void printMatrix(double[][] matrix) {
	    for (int i = 0; i < matrix.length; i++) {
	        for (int j = 0; j < matrix[i].length; j++) {
	            String myString = NumberFormat.getInstance().format(
	                    matrix[i][j]);
	            if (myString.length() < 5) {
	                for (int k = myString.length(); k < 5; k++) {
	                    myString += " ";
	                }
	            }
	            System.out.print(myString + "   ");
	        }
	        System.out.println();
	    }
	}
	
	static void printMatrix(int[][] matrix) {
	    for (int i = 0; i < matrix.length; i++) {
	        for (int j = 0; j < matrix[i].length; j++) {
	            if (matrix[i][j] >= 0)
	                System.out.print(" ");
	            System.out.print(matrix[i][j] + "   ");
	        }
	        System.out.println();
	    }
	}
}

