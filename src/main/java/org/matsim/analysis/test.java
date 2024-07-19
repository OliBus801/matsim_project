package org.matsim.analysis;
import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.simulation_directive.ScoringSimulationDirective;
import org.matsim.simulation_directive.SimulationDirective;

import java.util.*;

public class test {

    public static final String baseOutputDirectory = "scenarios/siouxfalls-2014/outputs/Sensitivity_Analysis_OFAT/";
    static String baselineConfig = "scenarios/siouxfalls-2014/configs/config_test_randomseed.xml";

    public static void main(String[] args){
        System.out.println("Running Experiment for marginal utility of traveling...");

        ScoringSimulationDirective travelingCarScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfTraveling_Car",
                        new double[]{-1.0});

        ScoringSimulationDirective travelingPtScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfTraveling_Pt",
                        new double[]{0.0});

        ScoringSimulationDirective travelingWalkScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfTraveling_Walk",
                        new double[]{0.0});

        List<SimulationDirective> simulationDirectives = Arrays.asList(
                travelingCarScoringSimulationDirective,
                travelingPtScoringSimulationDirective,
                travelingWalkScoringSimulationDirective
        );

        for (SimulationDirective directive : simulationDirectives) {
            String outputDirectory = baseOutputDirectory + directive.getParameterName() + "/";
            System.out.println("Starting directive " + directive);
            System.out.println("Output directory: " + outputDirectory );
            runSimulations(directive, baselineConfig, outputDirectory);
        }

    }

    public static void runSimulations(SimulationDirective directive, String baselineConfig, String baseOutputDirectory) {
        for (double value : directive.getParameterValues()) {
            // Create the specific config from the directive and the baselineConfig
            Config modifiedConfig = directive.modifyConfig(baselineConfig, value);

            // Set up the output directory and enable overriding
            String outputFolder = String.valueOf(value);
            String outputDirectory = baseOutputDirectory + outputFolder;
            modifiedConfig.controller().setOutputDirectory(outputDirectory);
            modifiedConfig.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

            // Print some information for debugging
            System.out.println("Starting simulation for parameter " + directive.getParameterName() + " with value " + value);
            System.out.println("Output directory : " + outputDirectory);

            // Create an instance of the controller using current config and run simulation
            Controler controler = new Controler(modifiedConfig);
            controler.run();
        }
    }
}
