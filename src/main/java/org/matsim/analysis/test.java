package org.matsim.analysis;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.controler.Controler;

import java.util.*;

public class test {

    public static final String baseOutputDirectory = "scenarios/siouxfalls-2014/outputs/RandomSeedAnalysis/";
    public static final String fixedRandomSeedMultipleThreadsDirectory = baseOutputDirectory + "fixedRandomSeedMultipleThreads_Extreme/";
    public static final String fixedRandomSeedSingleThreadDirectory = baseOutputDirectory + "Baseline/";
    static String baselineConfig = "scenarios/siouxfalls-2014/configs/config_baseline_MultiThread_RandomSeed24.xml";

    public static List<List<Object>> experiments = new ArrayList<>();

    public static void main(String[] args){

        // Adding Experiments Parameters into a list
        for (int i = 1; i < 11; i++) {
            experiments.add(Arrays.asList((long) 24, 7, fixedRandomSeedMultipleThreadsDirectory + i));
        }

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
