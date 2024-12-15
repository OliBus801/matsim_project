package org.matsim.analysis;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.*;
import org.matsim.core.config.groups.ControllerConfigGroup.RoutingAlgorithmType;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.gbl.MatsimRandom;

import java.util.Random;

public class analysis_withrun2 {

    public static final String BASE_OUTPUT_DIRECTORY = "scenarios/siouxfalls-2014/outputs/ANOVA_Analysis/";
    static String baselineConfig = "scenarios/siouxfalls-2014/configs/config_default_baseline.xml";

    public static void main(String[] args) {
        // Validation des arguments
        if (args.length < 2) {
            System.out.println("Usage: java AnalysisWithRun <ParameterName> <ParameterValue>");
            return;
        }

        String parameterName = args[0];
        String parameterValue = args[1];

        // Création d'un objet Random
        Random rand = new Random();

        // Boucle sur les 20 simulations
        for (int i = 1; i <= 20; i++) {

            // Charger la configuration de base
            Config config = ConfigUtils.loadConfig(baselineConfig);

            // Configurer le répertoire de sortie
            String iteration = String.valueOf(i);
            String outputDirectory = BASE_OUTPUT_DIRECTORY + parameterName + "/" + parameterValue + "/" + iteration;
            config.controller().setOutputDirectory(outputDirectory);
            config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

            // Obtenir les groupes de configuration
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

            // Définir un nouveau RandomSeed
            long randomSeed = Math.abs(rand.nextLong());
            MatsimRandom.reset(randomSeed);
            globalConfigGroup.setRandomSeed(randomSeed);

            // Appliquer les modifications en fonction du paramètre choisi
            switch (parameterName.toLowerCase()) {
                case "affectingduration":
                    timeAllocationMutatorConfigGroup.setAffectingDuration(Boolean.parseBoolean(parameterValue));
                    break;
                case "mutatearoundinitialendtimeonly":
                    timeAllocationMutatorConfigGroup.setMutateAroundInitialEndTimeOnly(Boolean.parseBoolean(parameterValue));
                    break;
                case "routingalgorithmtype":
                    controllerConfigGroup.setRoutingAlgorithmType(RoutingAlgorithmType.valueOf(parameterValue));
                    break;
                case "modes":
                    String[] modes = parameterValue.split(",");
                    subtourModeChoiceConfigGroup.setModes(modes);
                    break;
                case "planselectorforremoval":
                    replanningConfigGroup.setPlanSelectorForRemoval(parameterValue);
                    break;
                default:
                    System.out.println("Paramètre non reconnu : " + parameterName);
                    return;
            }

            // Lancer la simulation
            Controler controler = new Controler(config);
            controler.run();
        }
    }
}
