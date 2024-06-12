package org.matsim.analysis;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;


public class analysis_withrun {

    public static final String baseOutputDirectory = "scenarios/siouxfalls-2014/outputs//";
    public static void main(String[] args) {

        for (int i = 1; i < 6; i++) {

            // We load the default configurations then set up the output directory
            Config config = ConfigUtils.loadConfig("scenarios/siouxfalls-2014/configs/config_baseline_50iterations.xml");
            String outputFolder = "baseline_0" + i;
            String outputDirectory = baseOutputDirectory + outputFolder;
            config.controller().setOutputDirectory(outputDirectory);

            // Set number of iterations
            config.controller().setLastIteration(50); // Adjust as needed


            // Create an instance of the controller using current config and run simulation
            Controler controler = new Controler(config);
            controler.run();
        }
    }
}
