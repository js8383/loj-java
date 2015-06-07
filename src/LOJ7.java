import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LOJ7 {

	public static void main(String[] args) {

		System.out.println(Integer.MAX_VALUE);
//		System.out.println(9646324351-);
	}

	public static int reverse(int x) {
		if (x == 0) return 0;
        boolean pFlag = true;
        if (x < 0) {
            pFlag = false;
        }
        
        int nx = Math.abs(x);
        List<Integer> record = new ArrayList<Integer>();
        while (nx != 0) {
            int rem = nx % 10;
            record.add(rem);
            nx = (nx - rem) / 10;
        }
        
        int result = 0;
        int limit1 = Integer.MAX_VALUE/10;
        int limit2 = Integer.MAX_VALUE%10;
        int limit3 = Integer.MIN_VALUE/10;
        int limit4 = -(Integer.MIN_VALUE%10);
        
        for (int i = 0; i < record.size(); i++) {
            if (pFlag) {
                if (result > limit1) {
                    return 0;
                } else if (result == limit1) {
                    if (record.get(i) > limit2) {
                        return 0;
                    }
                    result *= 10;
                    result += record.get(i);
                } else {
                    result *= 10;
                    result += record.get(i);
                }
                
            
            } else {
                if (result < limit3) {
                    return 0;
                } else if (result == limit3) {
                    if (record.get(i) > limit4) {
                        return 0;
                    }
                    result *= 10;
                    result -= record.get(i);
                } else {
                    result *= 10;
                    result -= record.get(i);
                }
                
            }
        }
        return result;
	}

}
