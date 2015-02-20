import java.util.HashMap;
import java.util.Map;

public class LOJ1 {
	public static void main(String[] args) {
		int[] numbers = {2,7,11,15};
		int target = 9;
		int[] result = twoSum(numbers, target);
		System.out.println(result[0] + ", " + result[1]);
	}
	
	public static int[] twoSum(int[] numbers, int target) {
		Map<Integer, Integer> storage = new HashMap<Integer, Integer>();
		for (int i = 0; i < numbers.length; i++) {
			storage.put(target - numbers[i], i);
		}

		int[] result = new int[2];
		for (int i = 0; i < numbers.length; i++) {
			if ((storage.get(numbers[i])) != null) {
				int j = storage.get(numbers[i]);
				if (i == j)
					continue;
				result[0] = i + 1;
				result[1] = j + 1;
				break;
			}
		}
		return result;
	}
}
