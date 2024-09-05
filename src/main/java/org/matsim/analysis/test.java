package org.matsim.analysis;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.gbl.MatsimRandom;
import java.util.Random;


public class test {

    static String base_config = "scenarios/Québec/configs/config_base.xml";

    public static void main(String[] args){
        System.out.println("Running a test of Québec City scenario...");

        // Reset Random Seed
        Random random = new Random();
        long randomSeed = random.nextLong() % 9999L + 1;
        if (randomSeed < 0) { randomSeed += 9999L; }
        MatsimRandom.reset(randomSeed);

        // Load the config file
        Config config = ConfigUtils.loadConfig(base_config);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Controler controler = new Controler(scenario);


        // Run the scenario
        controler.run();
    }
}