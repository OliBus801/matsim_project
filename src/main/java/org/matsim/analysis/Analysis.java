package org.matsim.analysis;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.*;
import org.matsim.core.controler.Controler;
import org.matsim.core.gbl.MatsimRandom;

import java.util.*;

public class Analysis {

    public static final String baseOutputDirectory = "scenarios/siouxfalls-2014/outputs/RandomSeedAnalysis/";
    static String baselineConfig = "scenarios/siouxfalls-2014/configs/config_default_baseline.xml";

    public static List<List<Object>> experiments = new ArrayList<>();

    public static void main(String[] args){
        // Create a Random class
        Random random = new Random();

        // Adding Experiments Parameters into a list
        for (int i = 0; i < 25; i++) {
            experiments.add(Arrays.asList(baselineConfig, Math.abs(random.nextLong()), baseOutputDirectory + "RandomSeed/" + i));
            experiments.add(Arrays.asList(baselineConfig, 60161L, baseOutputDirectory + "FixedSeed/" + i));
        }

        // Shuffle the list
        Collections.shuffle(experiments);

        // Iterate through the list
        for(List<Object> experiment: experiments){
            System.out.println("Running Experiment with Random Seed: " + experiment.get(1));
            System.out.println("Output Directory: " + experiment.get(2));
            MatsimRandom.reset((Long) experiment.get(1));


            // Load the baseline config and add pertinent groups
            Config config = ConfigUtils.loadConfig((String) experiment.get(0));
            GlobalConfigGroup globalConfigGroup = ConfigUtils.addOrGetModule(config, GlobalConfigGroup.GROUP_NAME,
                    GlobalConfigGroup.class);

            // Modify the config
            globalConfigGroup.setRandomSeed((Long) experiment.get(1));
            config.controller().setOutputDirectory((String) experiment.get(2));

            // Run the simulation
            Controler controler = new Controler(config);
            controler.run();
        }
    }

}


