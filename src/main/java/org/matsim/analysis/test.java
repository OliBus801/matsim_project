package org.matsim.analysis;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.gbl.MatsimRandom;

import java.util.*;

public class test {

    static String brainExpConfig = "scenarios/siouxfalls-2014/configs/config_baseline_brainExp.xml";
    static String pathLogitConfig = "scenarios/siouxfalls-2014/configs/config_baseline_pathSize.xml";

    public static void main(String[] args){
        System.out.println("Running Experiment for baseline of brainExp...");

        // Reset Random Seed
        Random random = new Random();
        long randomSeed = random.nextLong() % 9999L + 1;
        if (randomSeed < 0) { randomSeed += 9999L; }
        MatsimRandom.reset(randomSeed);

        // Load the config file
        Config config = ConfigUtils.loadConfig(brainExpConfig);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Controler controler = new Controler(scenario);
        controler.run();

        System.out.println("Running Experiment for baseline of pathSize...");

        // Reset Random Seed
        randomSeed = random.nextLong() % 9999L + 1;
        if (randomSeed < 0) { randomSeed += 9999L; }
        MatsimRandom.reset(randomSeed);

        // Load the config file
        config = ConfigUtils.loadConfig(brainExpConfig);
        scenario = ScenarioUtils.loadScenario(config);
        controler = new Controler(scenario);
        controler.run();
    }
}
