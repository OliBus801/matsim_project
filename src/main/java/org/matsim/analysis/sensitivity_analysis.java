package org.matsim.analysis;

import org.matsim.core.config.Config;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.simulation_directive.FactorialReplanningSimulationDirective;
import org.matsim.simulation_directive.ReplanningSimulationDirective;
import org.matsim.simulation_directive.ScoringSimulationDirective;
import org.matsim.simulation_directive.SimulationDirective;

import java.util.Arrays;
import java.util.List;

public class sensitivity_analysis {
    public static final String baseOutputDirectory = "scenarios/equil/outputs/";
    public static final String RTOutputDirectory = "scenarios/siouxfalls-2014/outputs/Sensitivity_Analysis/ReRouteTimeAllocator";
    public static final String RMOutputDirectory = "scenarios/siouxfalls-2014/outputs/Sensitivity_Analysis/ReRouteModeChoice";
    public static final String TMOutputDirectory = "scenarios/siouxfalls-2014/outputs/Sensitivity_Analysis/TimeAllocatorModeChoice";
    static String baselineConfig = "scenarios/equil/config.xml";

    // Declare the array for the parameter values
    public static double[] scoringParameterValues = new double[(50 / 5) + 1];
    public static double[] negativeScoringParameterValues = new double[(50 / 5) + 1];
    public static double[] replanningParameterValues = new double[9];
    public static double[] halvedFactorialReplanningParameterValues = new double[32];


    public static void main(String[] args){
        // We populate the array for the parameter values
        for (int i = 0; i < scoringParameterValues.length; i++) {
            scoringParameterValues[i] = i * 5;
        }
        for (int i = 0; i < negativeScoringParameterValues.length; i++) {
            negativeScoringParameterValues[i] = -(i * 5);
        }
        for (int i = 0; i < replanningParameterValues.length; i++) {
            replanningParameterValues[i] = Math.round((i * 0.1 + 0.1) * 10) / 10.0;
        }
        for (int i = 0; i < halvedFactorialReplanningParameterValues.length; i++) {
            halvedFactorialReplanningParameterValues[i] = Math.round((i * 0.1 + 0.1) * 10) / 10.0;
        }

        // We create the SimulationDirective objects for each set of test
        ScoringSimulationDirective moneyScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfMoney",
                        scoringParameterValues);

        ScoringSimulationDirective performingScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfPerforming",
                        scoringParameterValues);

        ScoringSimulationDirective lateArrivalScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfLateArrival",
                        scoringParameterValues);

        ScoringSimulationDirective travelingScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfTraveling",
                        negativeScoringParameterValues);

        ReplanningSimulationDirective rerouteReplanningSimulationDirective =
                new ReplanningSimulationDirective(
                        "ReRoute",
                        replanningParameterValues);

        ReplanningSimulationDirective timeAllocationReplanningSimulationDirective =
                new ReplanningSimulationDirective(
                        "TimeAllocationMutator",
                        replanningParameterValues);

        ReplanningSimulationDirective modeChoiceReplanningSimulationDirective =
                new ReplanningSimulationDirective(
                        "SubtourModeChoice",
                        replanningParameterValues);

        FactorialReplanningSimulationDirective factorialReplanningSimulationDirective1 =
                new FactorialReplanningSimulationDirective(
                        "ReRoute",
                        "TimeAllocationMutator",
                        new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8});

        FactorialReplanningSimulationDirective factorialReplanningSimulationDirective2 =
                new FactorialReplanningSimulationDirective(
                        "ReRoute",
                        "SubtourModeChoice",
                        new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8});

        FactorialReplanningSimulationDirective factorialReplanningSimulationDirective3 =
                new FactorialReplanningSimulationDirective(
                        "SubtourModeChoice",
                        "TimeAllocationMutator",
                        new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8});

        // List of simulation directives (can be expanded if needed)
        List<SimulationDirective> simulationDirectives = Arrays.asList(
                moneyScoringSimulationDirective
                //performingScoringSimulationDirective,
                //lateArrivalScoringSimulationDirective,
                //travelingScoringSimulationDirective,
                //rerouteReplanningSimulationDirective,
                //timeAllocationReplanningSimulationDirective,
                //modeChoiceReplanningSimulationDirective
        );



/*        // Running OFAT Simulations
        System.out.println("Starting OFAT Simulations...");

        for (SimulationDirective directive : simulationDirectives) {
            String outputDirectory = baseOutputDirectory + directive.getParameterName() + "/";
            System.out.println("Starting directive " + directive);
            System.out.println("Output directory: " + outputDirectory );
            runSimulations(directive, baselineConfig, outputDirectory);
        }*/

        System.out.println("Starting Factorial Simulations...");

        // Running Factorial Simulations
        runFactorialSimulations(factorialReplanningSimulationDirective1, baselineConfig, RTOutputDirectory);
        runFactorialSimulations(factorialReplanningSimulationDirective2, baselineConfig, RMOutputDirectory);
        runFactorialSimulations(factorialReplanningSimulationDirective3, baselineConfig, TMOutputDirectory);
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

            // Print some information for debugging
            System.out.println("Starting simulation for parameter " + directive.getParameterName() + " with value " + value);
            System.out.println("Output directory : " + outputDirectory);

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
