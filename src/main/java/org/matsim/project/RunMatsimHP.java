package org.matsim.project;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ControllerConfigGroup;
import org.matsim.core.config.groups.CountsConfigGroup;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.config.groups.ReplanningConfigGroup;
import org.matsim.core.config.groups.ScoringConfigGroup;
import org.matsim.core.config.groups.SubtourModeChoiceConfigGroup;
import org.matsim.core.config.groups.TimeAllocationMutatorConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.lang.Integer;

public class RunMatsimHP {

    // This Map stocks the values of the different hyperparameters 
    private static Map<String, String> thetaMap = new HashMap<>();
    private static String simulation_number;
    public static final String BASE_OUTPUT_DIRECTORY = "scenarios/equil/outputs/Sensitivity_Analysis/";
    public static final String BASE_CONFIG = "scenarios/equil/config.xml";

    // Function that analyses the string theta as key-value pairs
    private static void parseTheta(String theta) {
        String[] pairs = theta.split(","); // On suppose que les paramètres sont séparés par des virgules
        for (String pair : pairs) {
            String[] keyValue = pair.split("="); // On suppose que chaque paramètre est sous la forme clé=valeur
            if (keyValue.length == 2) {
                thetaMap.put(keyValue[0].trim(), keyValue[1].trim());
            } else {
                System.out.println("Bad formatting of key-value pair: " + keyValue);
            }
        }
    }

    // Function to retrieve the value of a parameter from its name
    public static String getThetaParam(String paramName) {
        return thetaMap.get(paramName);
    }

    public static void main(String[] args) {

        Config config;

        if (args == null || args.length == 0 || args[0] == null) {
            System.out.println("Usage: You must provide the following arguments:");
            System.out.println("1. Hyperparameters: A comma-separated list of key-value pairs in the format <ParameterName1>=<ParameterValue1>,<ParameterName2>=<ParameterValue2>,...");
            System.out.println("2. Simulation number: A unique identifier for the simulation run.");
            System.out.println("3. (Optional) Config path: The path to the MATSim configuration file. If not provided, the default config will be used.");
            System.out.println("4. (Optional) Output path: The directory where the simulation results will be stored. If not provided, the default output directory will be used.");
            return;
        } else {
            // Stock arguments in thetaMap
            parseTheta(args[0]);
            simulation_number = args[1];
            if (args.length > 2 && args[2] != null && !args[2].isEmpty()) {
                config = ConfigUtils.loadConfig(args[2]);
            } else {
                config = ConfigUtils.loadConfig(BASE_CONFIG);
            }
            if (args.length > 3 && args[3] != null && !args[3].isEmpty()) {
                config.controller().setOutputDirectory(args[3] + "/simulation_" + simulation_number);
            } else {
                config.controller().setOutputDirectory(BASE_OUTPUT_DIRECTORY + "simulation_" + simulation_number);
            }
        }

        // We modify the config here -----------------
        
        // Important since we want to keep overwriting the same simulation run to save space (should we ?) - OB 2024
        config.controller().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);

        // Initialize the different configuration groups
        GlobalConfigGroup globalConfigGroup = ConfigUtils.addOrGetModule(config, 
            GlobalConfigGroup.GROUP_NAME, GlobalConfigGroup.class);
        TimeAllocationMutatorConfigGroup timeAllocationMutatorConfigGroup = ConfigUtils.addOrGetModule(config, 
            TimeAllocationMutatorConfigGroup.GROUP_NAME, TimeAllocationMutatorConfigGroup.class);
        ReplanningConfigGroup replanningConfigGroup = ConfigUtils.addOrGetModule(config, 
            ReplanningConfigGroup.GROUP_NAME, ReplanningConfigGroup.class);
        ControllerConfigGroup controllerConfigGroup = ConfigUtils.addOrGetModule(config, 
            ControllerConfigGroup.GROUP_NAME, ControllerConfigGroup.class);
        ScoringConfigGroup scoringConfigGroup = ConfigUtils.addOrGetModule(config,
            ScoringConfigGroup.GROUP_NAME, ScoringConfigGroup.class);
        CountsConfigGroup countsConfigGroup = ConfigUtils.addOrGetModule(config, 
            CountsConfigGroup.GROUP_NAME, CountsConfigGroup.class);
        QSimConfigGroup qSimConfigGroup = ConfigUtils.addOrGetModule(config, 
            QSimConfigGroup.GROUP_NAME, QSimConfigGroup.class);

        

        // Random Seed
        Random rand = new Random();
        long randomSeed = Math.abs(rand.nextLong());
        MatsimRandom.reset(randomSeed);
        globalConfigGroup.setRandomSeed(randomSeed);

        // Logging
        controllerConfigGroup.setWriteEventsInterval(Integer.parseInt(getThetaParam("numberOfIterations")));
        controllerConfigGroup.setWritePlansInterval(Integer.parseInt(getThetaParam("numberOfIterations")));
        countsConfigGroup.setWriteCountsInterval(Integer.parseInt(getThetaParam("numberOfIterations")));


        // Modify the different hyperparameters with thetaMap values.

        // ----------- Scoring parameters -----------
        scoringConfigGroup.setEarlyDeparture_utils_hr(Double.parseDouble(getThetaParam("earlyDeparture_util")));
        scoringConfigGroup.setLateArrival_utils_hr(Double.parseDouble(getThetaParam("lateArrival_util")));
        scoringConfigGroup.setPerforming_utils_hr(Double.parseDouble(getThetaParam("performing_util")));
        scoringConfigGroup.setMarginalUtlOfWaiting_utils_hr(Double.parseDouble(getThetaParam("waiting_util")));
        
        // Set Individualized utility parameters for each mode and print the mode name
        scoringConfigGroup.getAllModes()
            .forEach(mode -> {
            String utilityStr = getThetaParam("traveling_util_" + mode);
            if (utilityStr != null) {
                scoringConfigGroup.getOrCreateModeParams(mode).setMarginalUtilityOfTraveling(Double.parseDouble(getThetaParam("traveling_util_" + mode)));
            }
        });

        // ----------- Replanning strategy weights -----------
        // We need to have an exact match between the strategy names in the config and the ones in the thetaMap
        replanningConfigGroup.getStrategySettings().forEach(strategy -> {
            String weightStr = getThetaParam(strategy.getStrategyName());
            if (weightStr != null) {
                strategy.setWeight(Double.parseDouble(weightStr));
            }
        });


        /*
        Collection<ReplanningConfigGroup.StrategySettings> strategies = replanningConfigGroup.getStrategySettings();
        
        for (ReplanningConfigGroup.StrategySettings strategy : strategies) {
            switch (strategy.getStrategyName()) {
                case "timeAllocationMutator":
                    strategy.setWeight(Double.parseDouble(getThetaParam("timeAllocationMutator")));
                    break;
                case "ReRoute":
                strategy.setWeight(Double.parseDouble(getThetaParam("ReRoute")));
                    break;
                case "ChangeExpBeta":
                    strategy.setWeight(Double.parseDouble(getThetaParam("ChangeExpBeta")));
                    break;
                case "subtourModeChoice":
                    strategy.setWeight(Double.parseDouble(getThetaParam("subtourModeChoice")));
                    break;
                default:
                    // Optional: Handle unknown strategy names if necessary
                    break;
            }
        }
         */
            

        // maxAgentPlanMemorySize
        replanningConfigGroup.setMaxAgentPlanMemorySize(Integer.parseInt(getThetaParam("maxAgentPlanMemorySize")));

        // ----------- Execution parameters -----------
        qSimConfigGroup.setTimeStepSize(Double.parseDouble(getThetaParam("timeStepSize")));

        // ----------- Global parameters -----------
        // numberOfIterations
        config.controller().setLastIteration(Integer.parseInt(getThetaParam("numberOfIterations")));
        // scalingFactor
        // countsConfigGroup.setCountsScaleFactor(Double.parseDouble(getThetaParam("scalingFactor"))); // 10 for 10% of the population
        

        // mutationRange
        //timeAllocationMutatorConfigGroup.setMutationRange(Double.parseDouble(getThetaParam("mutationRange")));

        // brainExpBeta
        // scoringConfigGroup.setBrainExpBeta(Double.parseDouble(getThetaParam("brainExpBeta")));

        // fractionOfIterationsToDisableInnovation 
        // replanningConfigGroup.setFractionOfIterationsToDisableInnovation(Double.parseDouble(getThetaParam("fractionOfIterationsToDisableInnovation")));        

        Scenario scenario = ScenarioUtils.loadScenario(config);

        // ---

        Controler controler = new Controler(scenario);

        // ---

        controler.run();
    }
}
