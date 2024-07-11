package org.matsim.analysis;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.controler.Controler;

import java.util.*;

public class RandomSeedAnalysis {

    public static final String baseOutputDirectory = "scenarios/siouxfalls-2014/outputs/RandomSeedAnalysis/";
    public static final String fixedRandomSeedMultipleThreadsDirectory = baseOutputDirectory + "fixedRandomSeedMultipleThreads/";
    public static final String fixedRandomSeedSingleThreadDirectory = baseOutputDirectory + "fixedRandomSeedSingleThread/";
    public static final String RandomSeedSingleThreadDirectory = baseOutputDirectory + "RandomSeedMultipleThreads/";
    static String baselineConfig = "scenarios/equil/config.xml";

    public static List<List<Object>> experiments = new ArrayList<>();

    public static void main(String[] args){
        // Create a Random class
        Random random = new Random();

        // Adding Experiments Parameters into a list
        for (int i = 1; i < 11; i++) {
            experiments.add(Arrays.asList((long) 24, 4, fixedRandomSeedMultipleThreadsDirectory + i));
            experiments.add(Arrays.asList((long) 24, 1, fixedRandomSeedSingleThreadDirectory + i));
            long randomNumber = random.nextLong() % 9999L + 1;
            if (randomNumber < 0) { randomNumber += 9999L; }
            experiments.add(Arrays.asList(randomNumber, 1, RandomSeedSingleThreadDirectory + randomNumber));
        }

        // Shuffle the list
        Collections.shuffle(experiments);

        // Iterate through the list
        for(List<Object> experiment: experiments){
            System.out.println("Running Experiment: " + experiment);

            // Load the baseline config and add a globalConfigGroup
            Config config = ConfigUtils.loadConfig(baselineConfig);
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


