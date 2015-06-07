import java.util.Arrays;
import java.util.List;

public class LOJ5 {

	public static void main(String[] args) {

		System.out.println(longestPalindrome("abcbaaaaa"));
	}

	public static String longestPalindrome(String s) {
		int len = s.length();
		int[][] dp = new int[len][len];
		for (int i = 0; i < len; i++) {
			dp[i][i] = 1;
		}

		int maxL = -1;
		String maxS = "";
		for (int i = 0; i < len; i++) {
			for (int j = i; j < len; j++) {
				if (dp[i][j] == 1) {
					if (j - i + 1 > maxL) {
						maxL = j - i + 1;
						maxS = s.substring(i, j + 1);
					}
				} else if (dp[i][j] == 0) {
					recursiveDp(dp, i, j, s);
					if (dp[i][j] == 1) {
						if (j - i + 1 > maxL) {
							maxL = j - i + 1;
							maxS = s.substring(i, j + 1);
						}
					}
				}
			}
		}
		return maxS;
	}

	private static void recursiveDp(int[][] dp, int i, int j, String s) {
		if (dp[i][j] == 1) {
			return;
		}

		// if (i == j) {
		// dp[i][j] = 1;
		// return;
		// }

		if (j == i + 1) {
			if (s.charAt(i) == s.charAt(j)) {
				dp[i][j] = 1;
			} else {
				dp[i][j] = -1;
			}
			return;
		}

		if (s.charAt(i) == s.charAt(j)) {
			if (dp[i + 1][j - 1] == 1) {
				dp[i][j] = 1;
				return;
			} else if (dp[i + 1][j - 1] == 0) {
				recursiveDp(dp, i + 1, j - 1, s);
				if (dp[i + 1][j - 1] == 1) {
					dp[i][j] = 1;
					return;
				} else {
					dp[i][j] = -1;
					return;
				}
			} else {
				dp[i][j] = -1;
				return;
			}
		} else {
			dp[i][j] = -1;
			return;
		}
	}

}
