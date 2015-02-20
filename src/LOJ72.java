import java.util.Arrays;
import java.util.List;

public class LOJ72 {
	public static void main(String[] args) {
		String a = "abw";
		String b = "abceed";
		System.out.println(minDistance(a,b));
	}

	// Use dynamic programming
	public static int minDistance(String word1, String word2) {
		int l1 = word1.length();
		int l2 = word2.length();

		int[][] dp = new int[l1+1][l2+1];
		for (int i = 0; i < l1+1; i++) {
			for (int j = 0; j < l2+1; j++) {
				if (i == 0) dp[i][j] = j;
				else if (j == 0) dp[i][j] = i;
				else {
					dp[i][j] = Integer.MAX_VALUE;
				}
			}
		}
		
		
		for (int i = 0; i < l1; i++) {
			for (int j = 0; j < l2; j++) {
				int minm = Integer.min(dp[i][j+1],dp[i+1][j]) + 1;
				if (word1.charAt(i) == word2.charAt(j)) {
					minm = Integer.min(minm, dp[i][j]);
				} else {
					minm = Integer.min(minm, dp[i][j]+1);
				}
				dp[i+1][j+1] = minm;
			}
		}
		return dp[l1][l2];
		
	}

}
