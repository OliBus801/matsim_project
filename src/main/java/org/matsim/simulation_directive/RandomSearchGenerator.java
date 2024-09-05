package org.matsim.simulation_directive;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSearchGenerator {

    public static void main(String[] args) {
        Object[][] randomNumbers = new Object[33][7];
        Random rand = new Random();

        for (int i = 0; i < 33; i++) {
            for (int j = 0; j < 7; j++) {
                Object randomNumber = generateRandomObject(j, rand);
                randomNumbers[i][j] = randomNumber;
            }
        }

        // Print the generated random numbers
        for (int i = 0; i < 33; i++) {
            for (int j = 0; j < 7; j++) {
                System.out.print((i+1) + ": " + randomNumbers[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static List<Object[]> generateRandomNumbers(int numRows) {
        List<Object[]> randomNumbers = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < numRows; i++) {
            Object[] row = new Object[7];
            for (int j = 0; j < 7; j++) {
                row[j] = generateRandomObject(j, rand);
            }
            randomNumbers.add(row);
        }

        return randomNumbers;
    }

    private static Object generateRandomObject(int index, Random rand) {
        switch (index) {
            case 0: // Time Allocation Mutator (R ∈ [0, 1])
                return rand.nextDouble();
            case 1: // mutationRange (R ∈ [450, 14400])
                return rand.nextDouble() * 13951 + 450;
            case 2: // Re-Route (R ∈ [0, 1])
                return rand.nextDouble();
            case 3: // maxAgentPlanMemory (N ∈ [0, 20])
                return rand.nextInt(21);
            case 4: // brainExpBeta (R ∈ [0, 1000])
                return rand.nextDouble() * 1000;
            case 5: // fractionOfIteration (R ∈ [0.8, 1.0])
                return rand.nextDouble() * 0.2 + 0.8;
            case 6: // RandomSeed (long) (R ∈ [1, 9999])
                return Math.abs(rand.nextLong());
            default:
                return null;
        }
    }
}
