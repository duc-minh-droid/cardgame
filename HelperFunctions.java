import java.io.*;
import java.util.*;

public class HelperFunctions {
    private static List<Integer> readPack(String filePath, int n) {
        List<Integer> pack = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                pack.add(Integer.parseInt(line));
            }
        } catch (IOException | NumberFormatException e) {
            return null; // Return null if an error occurs
        }
        return pack.size() == 8 * n ? pack : null; // Check if the list size is as expected
    }
    public static void main(String[] args) {
        // Create a Scanner to read input from the terminal
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter the number of players
        System.out.println("Enter the number of players:");
        int n = scanner.nextInt();

        // Consume the newline character left by nextInt()
        scanner.nextLine();

        // Prompt the user to enter the file path
        System.out.print("Please enter the file path: ");
        String filePath = scanner.nextLine();

        // Close the scanner
        scanner.close();

        // Read the pack file
        List<Integer> pack = readPack(filePath, n);
        if (pack == null) {
            System.out.println("Invalid pack file. Please provide a valid pack.");
            return;
        }

        // Print the file content
        System.out.println("File Contents:\n" + pack);
        
        List<Integer> pack = readPack(packFilePath, n);
        if (pack == null) {
            System.out.println("Invalid pack file. Please provide a valid pack.");
            return;
        }
    }
}
