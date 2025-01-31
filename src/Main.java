import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Main {

    static int Number;
    static int VISITED_ALL;
    static double[][] dynamicPrograming;
    static double[][] cost;
    static double[][] travelTime; // 2D array to store travel time between cities
    static int[][] parent;
    static String[] Vertex;
    static List<String> pathList = new ArrayList<>();
    static double totalTravelTime = 0.0; // Variable to store total travel time


    public static void readInterest(String file , Map<String , Double> map){
        try {
            BufferedReader interest = new BufferedReader(new FileReader(file));
            interest.readLine();
            int index = 0;
            String line;
            while ((line = interest.readLine()) != null) {
                String[] separate = line.split("\t");
                Vertex[index] = separate[0];
                map.put(separate[0], Double.valueOf(separate[1]));
                index++;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void readVisitedLoad(String file , Map<String , Double> map){
        try {
            BufferedReader load = new BufferedReader(new FileReader(file));
            load.readLine();
            int index = 0;
            String line = null;
            while ((line = load.readLine()) != null) {
                String[] separate = line.split("\t");
                Vertex[index] = separate[0];
                map.put(separate[0],1.0 - Double.valueOf(separate[1]));
                index++;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void readLandMark(String file ,int number , Map<String , Double> loadMap , Map<String , Double> interestMap){
        try {
            BufferedReader text = new BufferedReader(new FileReader(file));
            text.readLine();
            for (int i = 0; i < number; i++) {
                for (int j = 0; j < number; j++) {
                    if (i != j) {
                        String[] parts = text.readLine().split("\t");
                        String from = parts[0];
                        String to = parts[1];
                        double baseScore = Double.parseDouble(parts[2]);
                        double time = Double.parseDouble(parts[3]);
                        double val = loadMap.get(to) * interestMap.get(to) * baseScore;
                        cost[i][j] = val;
                        travelTime[i][j] = time; // Fill the travel time array
                    } else {
                        cost[i][j] = 0; // Travel cost from a city to itself is considered 0
                        travelTime[i][j] = 0; // Travel time from a city to itself is considered 0
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in); // Create a Scanner object
        System.out.print("Please enter the total number of landmarks (including Hotel):");
        int number = scanner.nextInt();
        System.out.println("\n");
        cost = new double[number][number];
        travelTime = new double[number][number]; // Initialize the travel time array
        parent = new int[1 << number][number];
        Vertex = new String[number];
        Map<String, Double> interestMap = new HashMap<>();
        Map<String, Double> loadMap = new HashMap<>();
        readVisitedLoad("visitor_load.txt" , loadMap);
        readInterest("personal_interest.txt" , interestMap);
        readLandMark("landmark_map_data.txt" , number , loadMap , interestMap);

        System.out.println("Three input files are read.\n");
        System.out.println("The tour planning is now processing…\n");
        Number = cost.length;
        VISITED_ALL = (1 << Number) - 1;
        dynamicPrograming = new double[1 << Number][Number];
        for (double[] row : dynamicPrograming) {
            Arrays.fill(row, -1);
        }
        for (int[] row : parent) {
            Arrays.fill(row, -1);
        }

        //System.out.println("Minimum Hamiltonian cycle distance: " + tsp(1, 0));
        tsp(1, 0);
        printPath(1, 0); // Find path using backtracking
        System.out.println();

        Collections.reverse(pathList);
        System.out.print("The visited landmarks: \n");
        for (String city : pathList) {
            System.out.println(city);
        }
        System.out.println("Hotel");

        // Calculate total travel time by summing up travel times between cities in the path
        for (int i = 0; i < pathList.size() - 1; i++) {
            int fromIndex = Arrays.asList(Vertex).indexOf(pathList.get(i));
            int toIndex = Arrays.asList(Vertex).indexOf(pathList.get(i + 1));
            totalTravelTime += travelTime[fromIndex][toIndex];
        }

        // Add travel time from the last city back to the hotel
        int lastIndex = Arrays.asList(Vertex).indexOf(pathList.get(pathList.size() - 1));
        totalTravelTime += travelTime[lastIndex][0];
        System.out.println();
        System.out.println("Minimum Hamiltonian cycle distance: " + tsp(1, 0));
        System.out.println();
        String formattedTravelTime = String.format("%.2f", totalTravelTime);
        System.out.println("Total Travel Time: " + formattedTravelTime); // Print total travel time
}


    static double tsp(int swap, int pos) {
        if (swap == VISITED_ALL) {
            return cost[pos][0];
        }
        if (dynamicPrograming[swap][pos] != -1) {
            return dynamicPrograming[swap][pos];
        }
        double ans = Double.MIN_VALUE;
        int cityIndex = -1;
        for (int city = 0; city < Number; city++) {
            if ((swap & (1 << city)) == 0) {
                double newAns = cost[pos][city] + tsp(swap | (1 << city), city);
                if (newAns > ans) {
                    ans = newAns;
                    cityIndex = city;
                }
            }
        }
        parent[swap][pos] = cityIndex;
        return dynamicPrograming[swap][pos] = ans;
    }

    static void printPath(int swap, int pos) {
        int nextCity = parent[swap][pos];
        if (nextCity == -1) {
            // Add current city to the path list
            pathList.add(Vertex[pos]);

            return;
        }

        printPath(swap | (1 << nextCity), nextCity);
        // After backtracking, add current city to the path list
        pathList.add(Vertex[pos]);
    }
}