package org.matsim.analysis;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import java.util.Random;
import java.time.Duration;
import java.time.Instant;


public class test {

    static String base_config = "scenarios/siouxfalls-2014/configs/config_default_baseline.xml";
    static String BASE_OUTPUT_DIRECTORY = "scenarios/siouxfalls-2014/outputs/Time_Analysis/";

    public static void main(String[] args){
        System.out.println("Running a test Sioux Falls scenario for time benchmark...");

        for(int i = 1; i <= 5; i++){
            // Reset Random Seed
            Random rand = new Random();
            MatsimRandom.reset(Math.abs(rand.nextLong()));

            // Load the config file
            Config config = ConfigUtils.loadConfig(base_config);

            // Configurer le répertoire de sortie
            String iteration = String.valueOf(i);
            String outputDirectory = BASE_OUTPUT_DIRECTORY + iteration;
            config.controller().setOutputDirectory(outputDirectory);
            config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
            Scenario scenario = ScenarioUtils.loadScenario(config);
            Controler controler = new Controler(scenario);

            // Run the scenario
            controler.run();
        }
    }
}