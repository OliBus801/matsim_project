package org.matsim.analysis;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.*;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.simulation_directive.RandomSearchGenerator;

import java.util.*;


public class analysis_withrun {

    public static final String baseOutputDirectory = "scenarios/siouxfalls-2014/outputs/RandomSearch/";
    static String baselineConfig = "scenarios/siouxfalls-2014/configs/config_default_baseline.xml";

    public static void main(String[] args){
        List<Object[]> randomSets = RandomSearchGenerator.generateRandomNumbers(33);
        int iteration_number = 1;

        // Adding Experiments Parameters into a list
        for (Object[] randomSet : randomSets) {
                System.out.println("Setting up experiment with following parameters: ");
                System.out.println("TimeAllocationMutator: " + randomSet[0]);
                System.out.println("mutationRange: " + randomSet[1]);
                System.out.println("ReRoute: " + randomSet[2]);
                System.out.println("maxAgentPlanMemorySize: " + randomSet[3]);
                System.out.println("brainExpBeta: " + randomSet[4]);
                System.out.println("fractionOfIteration: " + randomSet[5]);
                System.out.println("RandomSeed: " + randomSet[6]);
                System.out.println("Running experiment...");

                MatsimRandom.reset((Long) randomSet[6]);

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

                // Get the replanning strategies and modify its weight
                Collection<ReplanningConfigGroup.StrategySettings> strategies = replanningConfigGroup.getStrategySettings();

                for(ReplanningConfigGroup.StrategySettings strategy : strategies){
                    if(Objects.equals("TimeAllocationMutator", strategy.getStrategyName())){ strategy.setWeight((Double) randomSet[0]); }
                    if(Objects.equals("ReRoute", strategy.getStrategyName())){ strategy.setWeight((Double) randomSet[2]); }
                }

                // Modify the other parameters and set Random Seed
                timeAllocationMutatorConfigGroup.setMutationRange((Double) randomSet[1]);
                replanningConfigGroup.setMaxAgentPlanMemorySize((int) randomSet[3]);
                scoringConfigGroup.setBrainExpBeta((Double) randomSet[4]);
                replanningConfigGroup.setFractionOfIterationsToDisableInnovation((Double) randomSet[5]);
                globalConfigGroup.setRandomSeed((Long) randomSet[6]);

                // Set up the output directory
                String outputFolder = String.valueOf(iteration_number);
                iteration_number += 1;
                String outputDirectory = baseOutputDirectory + outputFolder;
                config.controller().setOutputDirectory(outputDirectory);
                config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

                // Run the simulation
                Controler controler = new Controler(config);
                controler.run();
        }
    }
}
