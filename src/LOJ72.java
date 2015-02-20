import java.util.Arrays;
import java.util.List;

public class LOJ72 {
	public static void main(String[] args) {
		
	}

	// Use dynamic programming
	public int minDistance(String word1, String word2) {
		int l1 = word1.length()+1;
		int l2 = word2.length()+1;

		int[][] dp = new int[l1+1][l2+1];
		for (int i = 0; i < l1+1; i++) {
			for (int j = 0; j < l2+1; j++) {
				dp[i][j] = Integer.MAX_VALUE;
			}
		}
		
		for (int i = 0; i < l1+1; i++) {
			for (int j = 0; j < l2+1; j++) {
				dp[i][j] = Integer.MAX_VALUE;
			}
		}
		
	}

}
