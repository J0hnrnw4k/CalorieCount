import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CalorieTracker {
    private static final String FILE_NAME = "calories_data.txt";
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static final Map<String, ArrayList<Integer>> weeklyCalories = new HashMap<>();

    public static void main(String[] args) {
        loadCalories();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n📊 Calorie Tracker Menu:");
            System.out.println("1️⃣ Add Calories for a Day");
            System.out.println("2️⃣ View Weekly Breakdown");
            System.out.println("3️⃣ View Weekly Total");
            System.out.println("4️⃣ View Monthly Estimate");
            System.out.println("5️⃣ View All Entries");
            System.out.println("6️⃣ Exit");
            System.out.print("Choose an option: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                switch (choice) {
                    case 1 -> addCalories(scanner);
                    case 2 -> displayWeeklyBreakdown();
                    case 3 -> displayWeeklyTotal();
                    case 4 -> displayMonthlyEstimate();
                    case 5 -> viewAllEntries();
                    case 6 -> {
                        saveCalories();
                        running = false;
                        System.out.println("✅ Exiting Calorie Tracker. Stay healthy!");
                    }
                    default -> System.out.println("⚠ Invalid choice. Try again.");
                }
            } else {
                System.out.println("⚠ Invalid input. Please enter a number.");
                scanner.next(); // Consume invalid input
            }
        }
        scanner.close();
    }

    private static void addCalories(Scanner scanner) {
        System.out.println("📅 Choose a day (1-7):");
        for (int i = 0; i < DAYS.length; i++) {
            System.out.println((i + 1) + "️⃣ " + DAYS[i]);
        }
        System.out.print("Enter choice: ");

        if (scanner.hasNextInt()) {
            int dayIndex = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (dayIndex < 1 || dayIndex > 7) {
                System.out.println("⚠ Invalid day choice.");
                return;
            }

            String day = DAYS[dayIndex - 1];
            System.out.print("Enter calorie intake for " + day + ": ");
            
            if (scanner.hasNextInt()) {
                int calories = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                weeklyCalories.computeIfAbsent(day, _ -> new ArrayList<Integer>()).add(calories);
                System.out.println("✅ " + calories + " calories added for " + day);
            } else {
                System.out.println("⚠ Invalid input. Please enter a number.");
                scanner.next(); // Consume invalid input
            }
        } else {
            System.out.println("⚠ Invalid input. Please enter a number.");
            scanner.next(); // Consume invalid input
        }
    }

    private static void displayWeeklyBreakdown() {
        System.out.println("\n📆 Weekly Breakdown:");
        for (String day : DAYS) {
            ArrayList<Integer> dayCalories = weeklyCalories.getOrDefault(day, new ArrayList<>());
            System.out.println(day + ": " + (dayCalories.isEmpty() ? "No data" : dayCalories + " (Total: " + dayCalories.stream().mapToInt(Integer::intValue).sum() + " cal)"));
        }
    }

    private static void displayWeeklyTotal() {
        int total = weeklyCalories.values().stream()
                .flatMap(List::stream)
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("📅 Total Weekly Calories: " + total + " cal");
    }

    private static void displayMonthlyEstimate() {
        int weeklyTotal = weeklyCalories.values().stream()
                .flatMap(List::stream)
                .mapToInt(Integer::intValue)
                .sum();
        int estimatedMonthly = (int) ((weeklyTotal / 7.0) * 30); // Fixed integer division issue
        System.out.println("📆 Estimated Monthly Calories: " + estimatedMonthly + " cal");
    }

    private static void viewAllEntries() {
        System.out.println("\n📜 All Calorie Entries:");
        for (String day : DAYS) {
            ArrayList<Integer> dayCalories = weeklyCalories.getOrDefault(day, new ArrayList<>());
            System.out.println(day + ": " + (dayCalories.isEmpty() ? "No data" : dayCalories));
        }
    }

    private static void saveCalories() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (String day : DAYS) {
                ArrayList<Integer> dayCalories = weeklyCalories.get(day);
                if (dayCalories != null && !dayCalories.isEmpty()) {
                    writer.print(day + ": ");
                    for (int cal : dayCalories) {
                        writer.print(cal + " ");
                    }
                    writer.println();
                }
            }
            System.out.println("💾 Data saved successfully.");
        } catch (IOException e) {
            System.out.println("⚠ Error saving data: " + e.getMessage());
        }
    }

    private static void loadCalories() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (Scanner fileScanner = new Scanner(file)) {
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    String[] parts = line.split(": ");
                    if (parts.length == 2) {
                        String day = parts[0];
                        String[] calorieValues = parts[1].split(" ");
                        ArrayList<Integer> calList = new ArrayList<>();
                        for (String value : calorieValues) {
                            try {
                                calList.add(Integer.parseInt(value));
                            } catch (NumberFormatException e) {
                                System.out.println("⚠ Skipping invalid entry: " + value);
                            }
                        }
                        weeklyCalories.put(day, calList);
                    }
                }
                System.out.println("🔄 Previous data loaded.");
            } catch (FileNotFoundException e) {
                System.out.println("⚠ Error loading data: " + e.getMessage());
            }
        }
    }
}
