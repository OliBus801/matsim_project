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
    public static final String brainExpBetaOutputDirectory = baseOutputDirectory + "brainExpBeta/";
    static String baselineConfig = "scenarios/siouxfalls-2014/configs/config_default_baseline.xml";

    public static List<List<Object>> experiments = new ArrayList<>();

    public static void main(String[] args){
        // Create a Random class
        Random random = new Random();

        // Adding Experiments Parameters into a list
        for (int i = 0; i < 6; i++) {
            // Generate four random (long) integers between 0 and 9999.
            long randomNumber1 = random.nextLong() % 9999L + 1;
            if (randomNumber1 < 0) { randomNumber1 += 9999L; }

            double base10 = Math.pow(10, -i);

            experiments.add(Arrays.asList(baselineConfig, randomNumber1, base10, brainExpBetaOutputDirectory  + base10));
        }

        experiments.add(Arrays.asList(baselineConfig, 7637L, 1.0, brainExpBetaOutputDirectory  + "Baseline_1.0"));

        // Shuffle the list
        Collections.shuffle(experiments);

        // Iterate through the list
        for(List<Object> experiment: experiments){
            System.out.println("Running Experiment: " + experiment);
            MatsimRandom.reset((Long) experiment.get(1));


            // Load the baseline config and add pertinent groups
            Config config = ConfigUtils.loadConfig((String) experiment.get(0));
            GlobalConfigGroup globalConfigGroup = ConfigUtils.addOrGetModule(config, GlobalConfigGroup.GROUP_NAME,
                    GlobalConfigGroup.class);
            ScoringConfigGroup scoringConfigGroup = ConfigUtils.addOrGetModule(config, ScoringConfigGroup.GROUP_NAME,
                    ScoringConfigGroup.class);

            // Modify the config
            globalConfigGroup.setRandomSeed((Long) experiment.get(1));
            scoringConfigGroup.setBrainExpBeta((Double) experiment.get(2));
            config.controller().setOutputDirectory((String) experiment.get(3));

            // Run the simulation
            Controler controler = new Controler(config);
            controler.run();
        }
    }

}


