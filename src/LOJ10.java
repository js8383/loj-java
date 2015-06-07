
public class LOJ10 {
	
	public static void main(String[] args) {

		System.out.println(isMatch("aaa", "ab*a*c*a"));
//		System.out.println(9646324351-);
	}
	
	public static boolean isMatch(String s, String p) {
		int lens2 = p.length();
        int lens1 = s.length();
        int counter = 0;
        
        char prev = ' ';
        int i = 0;
        for (; i < lens2; i++) {
            if (counter == lens1) {
                break;
            }
            char c = p.charAt(i);
            
            if (c == '.') {
                prev = '.';
                counter++;
                continue;
            } else if (c == '*') {
                if (prev == '.') {
                	i++;
                	counter = lens1;
                    break;
                }
                
                while(s.charAt(counter) == prev) {
                    prev = '*';
                    counter++;
                    if (counter == lens1) {
                        break;
                    }
                }
            } else {
                if (s.charAt(counter) == c) {
                    counter++;
                    prev = c;
                } else {
                    if (i + 1 >= lens2 || p.charAt(i+1) != '*') {
                        return false;
                    }
                    prev = c;
                }
            }
        }
        
        if (counter == lens1 && i == lens2) {
            return true;
        } else {
            return false;
        }
    }
}
