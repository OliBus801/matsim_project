package org.matsim.analysis;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.controler.Controler;

import java.util.*;

public class RandomSeedAnalysis {

    public static final String baseOutputDirectory = "scenarios/siouxfalls-2014/outputs/ExpBeta_Preliminary/";
    public static final String brainExpOutputDirectory = baseOutputDirectory + "brainExpBeta/";
    public static final String pathSizeOutputDirectory = baseOutputDirectory + "pathSizeLogitBeta/";
    static String brainExpConfig = "scenarios/siouxfalls-2014/configs/config_baseline_brainExp.xml";
    static String pathLogitConfig = "scenarios/siouxfalls-2014/configs/config_baseline_pathSize.xml";

    public static List<List<Object>> experiments = new ArrayList<>();

    public static void main(String[] args){
        // Create a Random class
        Random random = new Random();

        // Adding Experiments Parameters into a list
        for (int i = 0; i < 6; i++) {
            // Generate four random (long) integers between 0 and 9999.
            long randomNumber1 = random.nextLong() % 9999L + 1;
            long randomNumber2 = random.nextLong() % 9999L + 1;
            long randomNumber3 = random.nextLong() % 9999L + 1;
            long randomNumber4 = random.nextLong() % 9999L + 1;
            if (randomNumber1 < 0) { randomNumber1 += 9999L; }
            if (randomNumber2 < 0) { randomNumber2 += 9999L; }
            if (randomNumber3 < 0) { randomNumber3 += 9999L; }
            if (randomNumber4 < 0) { randomNumber4 += 9999L; }

            long base10 = Math.round(Math.pow(10, i));
            long base2 = Math.round(Math.pow(2, i));


            experiments.add(Arrays.asList(brainExpConfig, randomNumber1, base10, 0.0, brainExpOutputDirectory + "/base10/" + base10));
            experiments.add(Arrays.asList(brainExpConfig, randomNumber2, base2, 0.0, brainExpOutputDirectory + "/base2/" + base2));
            experiments.add(Arrays.asList(pathLogitConfig, randomNumber3, 0.0, base10, pathSizeOutputDirectory + "/base10/" + base10));
            experiments.add(Arrays.asList(pathLogitConfig, randomNumber4, 0.0, base2, pathSizeOutputDirectory + "/base2/" + base2));
        }

        // Shuffle the list
        Collections.shuffle(experiments);

        // Iterate through the list
        for(List<Object> experiment: experiments){
            System.out.println("Running Experiment: " + experiment);

            // Load the baseline config and add a globalConfigGroup
            Config config = ConfigUtils.loadConfig(experiment.get(0).toString());
            GlobalConfigGroup globalConfigGroup = ConfigUtils.addOrGetModule(config, GlobalConfigGroup.GROUP_NAME,
                    GlobalConfigGroup.class);

            // Modify the config
            globalConfigGroup.setRandomSeed((Long) experiment.get(0));
            globalConfigGroup.setNumberOfThreads((Integer) experiment.get(1));
            config.controller().setOutputDirectory((String) experiment.get(2));

            // Run the simulation
            Controler controler = new Controler(config);
            controler.run();
        }
    }

}


