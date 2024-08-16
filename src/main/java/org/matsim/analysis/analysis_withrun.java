package org.matsim.analysis;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.*;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.gbl.MatsimRandom;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class analysis_withrun {

    public static final String baseOutputDirectory = "scenarios/siouxfalls-2014/outputs/Preliminary_Analysis/";
    public static final String fractionOfIterationsToDisableInnovationOutputDirectory = baseOutputDirectory + "fractionOfIterationsToDisableInnovation_RandomSampling/";
    static String baselineConfig = "scenarios/siouxfalls-2014/configs/config_default_baseline.xml";
    static String parameterValuesCSV = "scenarios/siouxfalls-2014/configs/random_sample.csv";

    public static void main(String[] args){
        // Create a Random class
        Random random = new Random();

        // Read CSV file
        System.out.println("Reading CSV file at " + parameterValuesCSV);
        String line;
        List<double[]> parameter_values = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(parameterValuesCSV))) {
            br.readLine(); // Ignorer la première ligne (noms des paramètres)
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                double[] param = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    param[i] = Double.parseDouble(values[i]);
                }
                parameter_values.add(param);
            }
            System.out.println("Finished reading CSV file at " + parameterValuesCSV + " !");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Adding Experiments Parameters into a list
        for (int i = 0; i < 11; i++) {
            // Generate a random (long) integer between 0 and 9999.
            long randomNumber1 = random.nextLong() % 9999L + 1;
            if (randomNumber1 < 0) { randomNumber1 += 9999L; }

            double fractionOfIteration = i * 0.1;

            for (int j = 0; j < 5; j++){
                double[] param = parameter_values.remove(parameter_values.size() - 1);
                System.out.println("Setting up experiment with following parameters: ");
                System.out.println("fractionOfIteration: " + fractionOfIteration);
                System.out.println("TimeAllocationMutator: " + param[0]);
                System.out.println("mutationRange: " + param[1]);
                System.out.println("ReRoute: " + param[2]);
                System.out.println("SubtourModeChoice: " + param[3]);
                System.out.println("probaForRandomSingleTripMode: " + param[4]);
                System.out.println("maxAgentPlanMemorySize: " + param[5]);
                System.out.println("brainExpBeta: " + param[6]);
                System.out.println("Running experiment...");

                MatsimRandom.reset(randomNumber1);

                // Load the baseline config
                Config config = ConfigUtils.loadConfig(baselineConfig);

                // Get the Config Groups Modules
                GlobalConfigGroup globalConfigGroup = ConfigUtils.addOrGetModule(config,
                        GlobalConfigGroup.GROUP_NAME, GlobalConfigGroup.class);
                ScoringConfigGroup scoringConfigGroup = ConfigUtils.addOrGetModule(config,
                        ScoringConfigGroup.GROUP_NAME, ScoringConfigGroup.class);
                TimeAllocationMutatorConfigGroup timeAllocationMutatorConfigGroup = ConfigUtils.addOrGetModule(config,
                        TimeAllocationMutatorConfigGroup.GROUP_NAME, TimeAllocationMutatorConfigGroup.class);
                ReplanningConfigGroup replanningConfigGroup = ConfigUtils.addOrGetModule(config,
                        ReplanningConfigGroup.GROUP_NAME, ReplanningConfigGroup.class);
                SubtourModeChoiceConfigGroup subtourModeChoiceConfigGroup = ConfigUtils.addOrGetModule(config,
                        SubtourModeChoiceConfigGroup.GROUP_NAME, SubtourModeChoiceConfigGroup.class);

                // Get the replanning strategies and modify its weight
                Collection<ReplanningConfigGroup.StrategySettings> strategies = replanningConfigGroup.getStrategySettings();

                for(ReplanningConfigGroup.StrategySettings strategy : strategies){
                    if(Objects.equals("TimeAllocationMutator", strategy.getStrategyName())){ strategy.setWeight(param[0]); }
                    if(Objects.equals("ReRoute", strategy.getStrategyName())){ strategy.setWeight(param[2]); }
                    if(Objects.equals("SubtourModeChoice", strategy.getStrategyName())){ strategy.setWeight(param[3]); }
                }

                // Modify the other parameters and set Random Seed
                globalConfigGroup.setRandomSeed(randomNumber1);
                timeAllocationMutatorConfigGroup.setMutationRange(param[1]);
                subtourModeChoiceConfigGroup.setProbaForRandomSingleTripMode(param[4]);
                replanningConfigGroup.setMaxAgentPlanMemorySize((int) param[5]);
                replanningConfigGroup.setFractionOfIterationsToDisableInnovation(fractionOfIteration);
                scoringConfigGroup.setBrainExpBeta(param[6]);

                // Set up the output directory
                String outputFolder = String.valueOf(j + 1);
                String outputDirectory = fractionOfIterationsToDisableInnovationOutputDirectory + fractionOfIteration + "/" + outputFolder;
                config.controller().setOutputDirectory(outputDirectory);
                config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

                // Run the simulation
                Controler controler = new Controler(config);
                controler.run();
            }
        }
    }
}
