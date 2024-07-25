package org.matsim.analysis;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.config.groups.ScoringConfigGroup;
import org.matsim.core.config.groups.SubtourModeChoiceConfigGroup;
import org.matsim.core.config.groups.TimeAllocationMutatorConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.gbl.MatsimRandom;

import java.util.*;

public class Analysis {

    public static final String baseOutputDirectory = "scenarios/siouxfalls-2014/outputs/Preliminary_Analysis/";
    public static final String mutationRangeOutputDirectory = baseOutputDirectory + "MutationRange/";
    public static final String coordDistanceOutputDirectory = baseOutputDirectory + "CoordDistance/";
    static String baselineConfig = "scenarios/siouxfalls-2014/configs/config_default_baseline.xml";

    public static List<List<Object>> experiments = new ArrayList<>();

    public static void main(String[] args){
        // Create a Random class
        Random random = new Random();

        // Adding Experiments Parameters into a list
        for (int i = 0; i < 6; i++) {
            // Generate four random (long) integers between 0 and 9999.
            long randomNumber1 = random.nextLong() % 9999L + 1;
            long randomNumber2 = random.nextLong() % 9999L + 1;
            if (randomNumber1 < 0) { randomNumber1 += 9999L; }
            if (randomNumber2 < 0) { randomNumber2 += 9999L; }

            double base10 = Math.round(Math.pow(10, i));
            double time_seconds = Math.round(Math.pow(2, i)) * 900;


            experiments.add(Arrays.asList(baselineConfig, randomNumber1, time_seconds, 0.0, mutationRangeOutputDirectory  + time_seconds));
            experiments.add(Arrays.asList(baselineConfig, randomNumber2, 1800.0, base10, coordDistanceOutputDirectory + base10));
        }
        experiments.add(Arrays.asList(baselineConfig, 7236, 450, 0.0, mutationRangeOutputDirectory  + "450"));
        experiments.add(Arrays.asList(baselineConfig, 8515, 1800.0, 0.0, coordDistanceOutputDirectory + "0"));

        // Shuffle the list
        Collections.shuffle(experiments);

        // Iterate through the list
        for(List<Object> experiment: experiments){
            System.out.println("Running Experiment: " + experiment);
            MatsimRandom.reset((Long) experiment.get(1));


            // Load the baseline config and add pertinent groups
            Config config = ConfigUtils.loadConfig((String) experiment.get(0));
            SubtourModeChoiceConfigGroup subtourModeChoiceConfigGroup = ConfigUtils.addOrGetModule(config, SubtourModeChoiceConfigGroup.GROUP_NAME,
                    SubtourModeChoiceConfigGroup.class);
            GlobalConfigGroup globalConfigGroup = ConfigUtils.addOrGetModule(config, GlobalConfigGroup.GROUP_NAME,
                    GlobalConfigGroup.class);
            TimeAllocationMutatorConfigGroup timeAllocationMutatorConfigGroup = ConfigUtils.addOrGetModule(config, TimeAllocationMutatorConfigGroup.GROUP_NAME,
                    TimeAllocationMutatorConfigGroup.class);

            // Modify the config
            globalConfigGroup.setRandomSeed((Long) experiment.get(1));
            timeAllocationMutatorConfigGroup.setMutationRange((Double) experiment.get(2));
            subtourModeChoiceConfigGroup.setCoordDistance((Double) experiment.get(3));
            config.controller().setOutputDirectory((String) experiment.get(4));

            // Run the simulation
            Controler controler = new Controler(config);
            controler.run();
        }
    }

}


