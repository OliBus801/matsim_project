package org.matsim.analysis;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.controler.Controler;

import java.util.*;

public class test {

    static String baselineConfig = "scenarios/siouxfalls-2014/configs/config_baseline_50iterations.xml";

    public static List<List<Object>> experiments = new ArrayList<>();

    public static void main(String[] args){
        System.out.println("Running Experiment for baseline...");
        Config config = ConfigUtils.loadConfig(baselineConfig);

        // Run the simulation
        Controler controler = new Controler(config);
        controler.run();
    }
}
