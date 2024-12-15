package org.matsim.project;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ControllerConfigGroup;
import org.matsim.core.config.groups.GlobalConfigGroup;
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
import java.util.Collection;
import java.lang.Integer;

public class RunMatsimHP {

    // This Map stocks the values of the different hyperparameters 
    private static Map<String, String> thetaMap = new HashMap<>();
    private static String output_name;
    public static final String BASE_OUTPUT_DIRECTORY = "BO/cache/";

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
            System.out.println("Usage: You must provide a complete hyperparameter configuration as argument : <ParameterName1>=<ParameterValue1>,<ParameterName2>=<ParameterValue2>, ...");
            return;
        } else {
            // Stock arguments in thetaMap
            parseTheta(args[0]);
            output_name = args[1];
            // I think this should stay hard-coded for now, but eventually we could extend this to provide flexibility - OB 2024
            config = ConfigUtils.loadConfig("/home/olbus4/scratch/matsim_project/scenarios/siouxfalls-2014/configs/config_default_baseline.xml");
        }

        // Modify config here --------------------
        
        // Output Directory
        config.controller().setOutputDirectory(BASE_OUTPUT_DIRECTORY + output_name);
        config.controller().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);

        // Initialize the different configuration groups
        GlobalConfigGroup globalConfigGroup = ConfigUtils.addOrGetModule(config, 
            GlobalConfigGroup.GROUP_NAME, GlobalConfigGroup.class);
        TimeAllocationMutatorConfigGroup timeAllocationMutatorConfigGroup = ConfigUtils.addOrGetModule(config, 
            TimeAllocationMutatorConfigGroup.GROUP_NAME, TimeAllocationMutatorConfigGroup.class);
        ReplanningConfigGroup replanningConfigGroup = ConfigUtils.addOrGetModule(config, 
            ReplanningConfigGroup.GROUP_NAME, ReplanningConfigGroup.class);
        SubtourModeChoiceConfigGroup subtourModeChoiceConfigGroup = ConfigUtils.addOrGetModule(config, 
            SubtourModeChoiceConfigGroup.GROUP_NAME, SubtourModeChoiceConfigGroup.class);
        ControllerConfigGroup controllerConfigGroup = ConfigUtils.addOrGetModule(config, 
            ControllerConfigGroup.GROUP_NAME, ControllerConfigGroup.class);
        ScoringConfigGroup scoringConfigGroup = ConfigUtils.addOrGetModule(config,
            ScoringConfigGroup.GROUP_NAME, ScoringConfigGroup.class);

        // Random Seed
        Random rand = new Random();
        long randomSeed = Math.abs(rand.nextLong());
        MatsimRandom.reset(randomSeed);
        globalConfigGroup.setRandomSeed(randomSeed);

        // Modify the different hyperparameters with thetaMap values.

        // Replanning strategy weights
        Collection<ReplanningConfigGroup.StrategySettings> strategies = replanningConfigGroup.getStrategySettings();
        
        for (ReplanningConfigGroup.StrategySettings strategy : strategies) {
            switch (strategy.getStrategyName()) {
                case "timeAllocationMutator":
                    strategy.setWeight(Double.parseDouble(getThetaParam("timeAllocationMutator")));
                    break;
                case "ReRoute":
                strategy.setWeight(Double.parseDouble(getThetaParam("ReRoute")));
                    break;
                default:
                    // Optional: Handle unknown strategy names if necessary
                    break;
            }
        }

        // mutationRange
        timeAllocationMutatorConfigGroup.setMutationRange(Double.parseDouble(getThetaParam("mutationRange")));

        // maxAgentPlanMemorySize
        replanningConfigGroup.setMaxAgentPlanMemorySize(Integer.parseInt(getThetaParam("maxAgentPlanMemorySize")));
        

        // brainExpBeta
        scoringConfigGroup.setBrainExpBeta(Double.parseDouble(getThetaParam("brainExpBeta")));

        // fractionOfIterationsToDisableInnovation 
        replanningConfigGroup.setFractionOfIterationsToDisableInnovation(Double.parseDouble(getThetaParam("fractionOfIterationsToDisableInnovation")));        

        Scenario scenario = ScenarioUtils.loadScenario(config);

        // ---

        Controler controler = new Controler(scenario);

        // ---

        controler.run();
    }
}
