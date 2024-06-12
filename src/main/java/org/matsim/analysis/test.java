package org.matsim.analysis;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.simulation_directive.FactorialReplanningSimulationDirective;
import org.matsim.simulation_directive.ReplanningSimulationDirective;
import org.matsim.simulation_directive.ScoringSimulationDirective;
import org.matsim.simulation_directive.SimulationDirective;


import java.util.*;

public class test {

    public static final String baseOutputDirectory = "scenarios/equil/outputs//";
    static String baselineConfig = "scenarios/equil/config.xml";

    static ScoringSimulationDirective moneyScoringSimulationDirective =
            new ScoringSimulationDirective(
                    "MarginalUtilityOfMoney",
                    new double[]{1.0, 1.1});

    static ReplanningSimulationDirective testReplanningSimulationDirective =
            new ReplanningSimulationDirective(
                    "ReRoute",
                    new double[]{0.1, 0.2, 1.0});

    static FactorialReplanningSimulationDirective factorialReplanningSimulationDirective =
            new FactorialReplanningSimulationDirective(
                    "ReRoute",
                    "TimeAllocationMutator",
                    new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8});

    public static void main(String[] args){
        System.out.println("Hello World ! - From a Docker Container !");
    }

    public static void runSimulations(SimulationDirective directive, String baselineConfig, String baseOutputDirectory) {
        for (double value : directive.getParameterValues()) {
            // Create the specific config from the directive and the baselineConfig
            Config modifiedConfig = directive.modifyConfig(baselineConfig, value);

            // Set up the output directory and enable overriding
            String outputFolder = directive.getParameterName() + "_" + value;
            String outputDirectory = baseOutputDirectory + outputFolder;
            modifiedConfig.controller().setOutputDirectory(outputDirectory);
            modifiedConfig.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

            // Create an instance of the controller using current config and run simulation
            Controler controler = new Controler(modifiedConfig);
            controler.run();
        }
    }

    public static void runFactorialSimulations(FactorialReplanningSimulationDirective directive, String baselineConfig, String baseOutputDirectory) {
        for (double[] value : directive.getPairedParameterValues()) {
            // Create the specific config from the directive and the baselineConfig
            Config modifiedConfig = directive.modifyConfig(baselineConfig, value);

            // Set up the output directory and enable overriding
            String outputFolder = directive.getFactor1() + "_" + Math.round(value[0]*100) + directive.getFactor2() + "_" + Math.round(value[1]*100);
            String outputDirectory = baseOutputDirectory + outputFolder;
            modifiedConfig.controller().setOutputDirectory(outputDirectory);
            modifiedConfig.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

            // Create an instance of the controller using current config and run simulation
            Controler controler = new Controler(modifiedConfig);
            controler.run();
        }
    }

}
