package org.matsim.analysis;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.*;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.gbl.MatsimRandom;


import java.io.*;
import java.util.*;

public class morris_method {
    public static final String baseOutputDirectory = "scenarios/siouxfalls-2014/outputs/Morris_Method/";
    static String baselineConfig = "scenarios/siouxfalls-2014/configs/config_default_baseline.xml";
    static String parameterValuesCSV = "scenarios/siouxfalls-2014/configs/morris_method_sample.csv";

    public static void main(String[] args){
        String line;
        Random random = new Random();
        int iterations = 21;
        List<double[]> parameter_values = new ArrayList<>();

        // Read CSV file
        System.out.println("Starting Morris Method...");
        System.out.println("Reading CSV file at " + parameterValuesCSV);
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

        System.out.println("Starting simulation runs...");
        // For each set of parameter - Run simulation comparing to baseline
        for(double[] param : parameter_values){
            System.out.println("Setting up experiment with following parameters: ");
            System.out.println("TimeAllocationMutator: " + param[0]);
            System.out.println("mutationRange: " + param[1]);
            System.out.println("ReRoute: " + param[2]);
            System.out.println("SubtourModeChoice: " + param[3]);
            System.out.println("coordDistance: " + param[4]);
            System.out.println("probaForRandomSingleTripMode: " + param[5]);
            System.out.println("maxAgentPlanMemorySize: " + param[6]);
            System.out.println("fractionOfIterationsToDisableInnovation: " + param[7]);
            System.out.println("brainExpBeta: " + param[8]);

            // Generate Random Seed
            long randomNumber = random.nextLong() % 9999L + 1;
            if (randomNumber < 0) { randomNumber += 9999L; }

            // Load the config from baseline
            MatsimRandom.reset(randomNumber);
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
            globalConfigGroup.setRandomSeed(randomNumber);
            timeAllocationMutatorConfigGroup.setMutationRange(param[1]);
            subtourModeChoiceConfigGroup.setCoordDistance(param[4]);
            subtourModeChoiceConfigGroup.setProbaForRandomSingleTripMode(param[5]);
            replanningConfigGroup.setMaxAgentPlanMemorySize((int) param[6]);
            replanningConfigGroup.setFractionOfIterationsToDisableInnovation(param[7]);
            scoringConfigGroup.setBrainExpBeta(param[8]);



            // Set up the output directory
            String outputFolder = String.valueOf(iterations);
            String outputDirectory = baseOutputDirectory + outputFolder;
            config.controller().setOutputDirectory(outputDirectory);
            config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

            // Print some information for debugging
            System.out.println("Starting Simulation...");
            System.out.println("Output directory : " + outputDirectory);

            Controler controler = new Controler(config);
            controler.run();

            // Increase iteration number
            iterations = iterations + 1;
        }
    }
}
