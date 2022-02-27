
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MinimumEditDistance {
	public static final String filePath = "C:/Users/zekiy/Downloads/vocabulary_tr.txt";
	public static final int gap = 1;
	public static int[][] editDistanceMatrix;

	public static void main(String[] args) throws IOException {
		menu();
	}

	public static void menu() throws IOException {
		System.out.println("Choose from these choices");
		System.out.println("-------------------------");
		System.out.println("1 - (PART1)");
		System.out.println("2 - (PART2)");
		System.out.println("3 - Quit");

		Scanner input = new Scanner(System.in);
		int selection = input.nextInt();
		switch (selection) {
		case 1:
			Scanner part1 = new Scanner(System.in);
			System.out.println("Note: Words will be not case sensetive.(PüTürlü = pütürlü)");
			System.out.println("Note: Each operation costs 1.\n");
			System.out.print("Enter the word: ");
			String source_part1 = part1.nextLine();

			long part1startTime = System.currentTimeMillis();
			part1(source_part1.toLowerCase());
			long part1endTime = System.currentTimeMillis();
			float sec1 = (part1endTime - part1startTime) / 1000F;
			System.out.println("Part 1 Total Running Time " + sec1 + " seconds.");
			break;
		case 2:
			Scanner part2 = new Scanner(System.in);
			System.out.println("Note: Words will be not case sensetive.(PüTürlü = pütürlü)");
			System.out.println("Note: Each operation costs 1.\n");
			System.out.print("Enter the first word: ");
			String source = part2.nextLine();
			System.out.print("Enter the second word: ");
			String target = part2.nextLine();

			long part2startTime = System.currentTimeMillis();
			part2(source.toLowerCase(), target.toLowerCase());
			long part2endTime = System.currentTimeMillis();
			float sec2 = (part2endTime - part2startTime) / 1000F;
			System.out.println("Part 2 Total Running Time " + sec2 + " seconds.");
			break;
		case 3:
			System.exit(0);
			break;
		default:
			System.out.println("Invalid selection.");
			menu();
		}
	}

	public static void part1(String source) throws IOException {
		// read file and get words as a list
		List<String> words = readFile();
		// find first 5 alternative correct words
		findTop5(words, source);
	}

	public static void part2(String source, String target) {
		scoringMatrix(source, target);
		print2D(editDistanceMatrix);
		// finding minimum edit distance
		int distance = scoringMatrix(source, target);
		System.out.println("\nMininum Edit Distance: " + distance);
		System.out.println();
		// find best alignment
		printBestAllinment(source, target);
	}

	public static List<String> readFile() throws IOException {
		List<String> words = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String line;
		while ((line = reader.readLine()) != null) {
			words.add(line);
		}
		reader.close();
		return words;
	}

	public static void findTop5(List<String> words, String source) {
		System.out.println("Source Word: " + source);
		Map<String, Integer> unSortedMap = new HashMap<>();
		for (int i = 0; i < words.size(); i++) {
			int dist = scoringMatrix(source.toLowerCase(), words.get(i));
			unSortedMap.put(words.get(i), dist);
		}

		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
		unSortedMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
		int count = 6;
		for (Map.Entry<String, Integer> en : sortedMap.entrySet()) {
			if (count > 0 && en.getValue() != 0) {
				System.out.println("Word = " + en.getKey() + ", Distance = " + en.getValue());
			}
			count--;
		}
	}

	public static int scoringMatrix(String source, String target) {
		int sourceSize = source.length() + 1;
		int targetSize = target.length() + 1;
		int[][] scoreMatrix = new int[sourceSize][targetSize];
		for (int i = 0; i < sourceSize; i++) {
			for (int j = 0; j < targetSize; j++) {
				if (i == 0) {
					scoreMatrix[i][j] = j;
				} else if (j == 0) {
					scoreMatrix[i][j] = i;
				} else if (source.charAt(i - 1) == target.charAt(j - 1)) {
					scoreMatrix[i][j] = scoreMatrix[i - 1][j - 1];
				} else {
					scoreMatrix[i][j] = min(1 + scoreMatrix[i - 1][j], 1 + scoreMatrix[i][j - 1],
							1 + scoreMatrix[i - 1][j - 1]);
				}
			}
		}
		editDistanceMatrix = scoreMatrix;
		return scoreMatrix[sourceSize - 1][targetSize - 1];
	}

	public static void printBestAllinment(String source, String target) {
		System.out.println("Source: " + source + " / Target: " + target);
		int sourceSize = source.length();
		int targetSize = target.length();
		int dist = 0;
		String allignmentX = "";
		String allignmentY = "";

		while (sourceSize > 0 || targetSize > 0) {
			// if diagonal
			if (sourceSize > 0 && targetSize > 0
					&& (editDistanceMatrix[sourceSize][targetSize] == editDistanceMatrix[sourceSize - 1][targetSize
							- 1])
					&& (source.charAt(sourceSize - 1) == target.charAt(targetSize - 1))) {
				allignmentX = String.valueOf(source.charAt(sourceSize - 1)) + " " + allignmentX;
				allignmentY = String.valueOf(target.charAt(targetSize - 1)) + " " + allignmentY;
				System.out.println("Equal/No operation needed : " + String.valueOf(source.charAt(sourceSize - 1))
						+ " --> Current Operation Count = " + dist);
				sourceSize--;
				targetSize--;
			}
			// if up
			else if (sourceSize > 0
					&& editDistanceMatrix[sourceSize][targetSize] == editDistanceMatrix[sourceSize - 1][targetSize]
							+ gap) {
				allignmentX = String.valueOf(source.charAt(sourceSize - 1)) + " " + allignmentX;
				allignmentY = "_" + " " + allignmentY;
				dist++;
				System.out.println("Deletion from Source : " + String.valueOf(source.charAt(sourceSize - 1))
						+ " --> Current Operation Count = " + dist);
				sourceSize--;
			}
			
			
			// if left
			else if (targetSize > 0
					&& editDistanceMatrix[sourceSize][targetSize] == editDistanceMatrix[sourceSize][targetSize - 1]
							+ gap) {
				allignmentX = "*" + " " + allignmentX;
				allignmentY = String.valueOf(target.charAt(targetSize - 1)) + " " + allignmentY;
				dist++;
				System.out.println("Addition to Target : " + String.valueOf(target.charAt(targetSize - 1))
						+ " --> Current Operation Count = " + dist);
				targetSize--;
			}
			// if diagonal but a and b are not equal
			else {
				allignmentX = String.valueOf(source.charAt(sourceSize - 1)) + " " + allignmentX;
				allignmentY = String.valueOf(target.charAt(targetSize - 1)) + " " + allignmentY;
				dist++;
				System.out.println("Substitution between Source : " + String.valueOf(source.charAt(sourceSize - 1))
						+ " and Target : " + String.valueOf(target.charAt(targetSize - 1))
						+ " --> Current Operation Count = " + dist);
				sourceSize--;
				targetSize--;
			}
		}

		System.out.println("\nBest Allignmnet:\n");
		System.out.println("Source: " + allignmentX);
		System.out.println("Target: " + allignmentY);
	}

	public static void print2D(int scoreMatrix[][]) {
		int sourceSize = scoreMatrix.length;
		int targetSize = scoreMatrix[0].length;
		for (int i = 0; i < sourceSize; i++) {
			for (int j = 0; j < targetSize; j++) {
				System.out.print(scoreMatrix[i][j] + " ");
			}
			System.out.println();
		}
	}

	private static int min(int a, int b) {
		return (a < b) ? a : b;
	}

	private static int min(int a, int b, int c) {
		return min(min(a, b), c);
	}
}
