import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LOJ54 {
	public static void main(String[] args) {
		int[][] input = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
		List<Integer> result = spiralOrder(input);
		System.out.println(Arrays.toString(result.toArray()));
	}

	public static List<Integer> spiralOrder(int[][] matrix) {
		List<Integer> result = new ArrayList<Integer>();

		if (matrix.length == 0 || matrix[0].length == 0)
			return result;
		int row = matrix.length;
		int col = matrix[0].length;

		String[] directions = { "right", "down", "left", "up" };
		int currentDirection = 0;

		Set<String> visited = new HashSet<String>();
		int currentRow = 0;
		int currentCol = 0;

		while (visited.size() != row * col) {
			if (directions[currentDirection].equals("right")) {
				if (visited.contains(currentRow + ","
						+ currentCol)
						|| currentCol == col) {
					currentDirection = (currentDirection + 1) % 4;
					currentCol--;
					currentRow++;

				} else {
					result.add(matrix[currentRow][currentCol]);
					visited.add(currentRow + ","
							+ currentCol);
					currentCol++;
				}
			}

			else if (directions[currentDirection].equals("down")) {
				if (visited.contains(currentRow + ","
						+ currentCol)
						|| currentRow == row) {
					currentDirection = (currentDirection + 1) % 4;
					currentCol--;
					currentRow--;

				} else {
					result.add(matrix[currentRow][currentCol]);
					visited.add(currentRow + ","
							+ currentCol);
					currentRow++;
				}
			}

			else if (directions[currentDirection].equals("left")) {
				if (visited.contains(currentRow + ","
						+ currentCol)
						|| currentCol == -1) {
					currentDirection = (currentDirection + 1) % 4;
					currentCol++;
					currentRow--;

				} else {
					result.add(matrix[currentRow][currentCol]);
					visited.add(currentRow + ","
							+ currentCol);
					currentCol--;
				}
			}

			else if (directions[currentDirection].equals("up")) {
				if (visited.contains(currentRow + ","
						+ currentCol)
						|| currentRow == -1) {
					currentDirection = (currentDirection + 1) % 4;
					currentCol++;
					currentRow++;

				} else {
					result.add(matrix[currentRow][currentCol]);
					visited.add(currentRow + ","
							+ currentCol);
					currentRow--;
				}
			}
		}
		return result;

	}
}
