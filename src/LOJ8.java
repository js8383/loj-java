import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LOJ8 {

	public static void main(String[] args) {

		char c = '1';
		System.out.println(myAtoi("-2147483647"));

		// System.out.println(9646324351-);
	}

	public static int myAtoi(String str) {
		int lens = str.length();
		int result = 0;

		boolean isValid = false;

		boolean isNegative = false;

		for (int i = 0; i < lens; i++) {

			char c = str.charAt(i);

			if (c == ' ') {
			    if (isValid) {
					break;
				}
				continue;
			}
			
			
			if (c == '+') {
				if (isValid) {
					break;
				}
				isNegative = false;
				isValid = true;
				continue;
			}

			if (c == '-') {
				if (isValid) {
					break;
				}
				isNegative = true;
				isValid = true;
				continue;
			}

			if (c >= '0' && c <= '9') {
				isValid = true;
				int cc = Character.getNumericValue(c);
				if (result >= Integer.MAX_VALUE / 10 && !isNegative) {

					if (result == Integer.MAX_VALUE / 10 && cc < 7) {
						result = result * 10 + cc;
					} else {
					    result = 2147483647;
					}
					
				} else if (result < Integer.MAX_VALUE / 10 && !isNegative) {
					result = result * 10 + cc;
				}

				if (result <= Integer.MIN_VALUE / 10 && isNegative) {

					
					if (result == Integer.MIN_VALUE / 10 && cc < 8) {
						result = result * 10 - cc;
					} else {
					    result = -2147483648;
					}
					
				} else if (result > Integer.MIN_VALUE / 10 && isNegative) {
					result = result * 10 - cc;
				}

			} else {
				break;
			}
		}

		if (isValid) {
			return result;
		} else {
			return 0;
		}
    }
}
