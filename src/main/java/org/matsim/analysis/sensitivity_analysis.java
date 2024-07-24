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
    public static final String baseOutputDirectory = "scenarios/siouxfalls-2014/outputs/ExpBeta_Preliminary/";
    static String baselineConfig = "scenarios/siouxfalls-2014/configs/config_baseline_50iterations.xml";


    public static void main(String[] args){

        // We create the SimulationDirective objects for each set of test
        ScoringSimulationDirective moneyScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfMoney",
                        new double[]{0.0019375, 0.003875, 0.00775, 0.0155, 0.031, 0.062, 0.124, 0.248, 0.496, 0.992, 1.984});

        ScoringSimulationDirective performingScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfPerforming",
                        new double[]{0.03, 0.06, 0.12, 0.24, 0.48, 0.96, 1.92, 3.84, 7.68, 15.36, 30.72});

        ScoringSimulationDirective lateArrivalScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfLateArrival",
                        new double[]{-0.5625, -1.125, -2.25, -4.5, -9, -18, -36, -72, -144, -288, -576});

        ScoringSimulationDirective travelingCarScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfTraveling_Car",
                        new double[]{0.0, -0.01125, -0.0225, -0.045, -0.09, -0.18, -0.36, -0.72, -1.44, -2.88, -5.76});

        ScoringSimulationDirective travelingPtScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfTraveling_Pt",
                        new double[]{-0.005625, -0.01125, -0.0225, -0.045, -0.09, -0.18, -0.36, -0.72, -1.44, -2.88, -5.76});

        ScoringSimulationDirective travelingWalkScoringSimulationDirective =
                new ScoringSimulationDirective(
                        "MarginalUtilityOfTraveling_Walk",
                        new double[]{-0.035625, -0.07125, -0.1425, -0.285, -0.57, -1.14, -2.28, -4.56, -9.12, -18.24, -36.48});

        ReplanningSimulationDirective rerouteReplanningSimulationDirective =
                new ReplanningSimulationDirective(
                        "ReRoute",
                        new double[]{0.003125, 0.00625, 0.0125, 0.025, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6});

        ReplanningSimulationDirective timeAllocationReplanningSimulationDirective =
                new ReplanningSimulationDirective(
                        "TimeAllocationMutator",
                        new double[]{0.003125, 0.00625, 0.0125, 0.025, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6});

        ReplanningSimulationDirective modeChoiceReplanningSimulationDirective =
                new ReplanningSimulationDirective(
                        "SubtourModeChoice",
                        new double[]{0.003125, 0.00625, 0.0125, 0.025, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6});

        // Factorial Simulation Directive Artifacts
        /**FactorialReplanningSimulationDirective factorialReplanningSimulationDirective1 =
                new FactorialReplanningSimulationDirective(
                        "ReRoute",
                        "TimeAllocationMutator",
                        new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8});**/


        List<SimulationDirective> simulationDirectives = Arrays.asList(
                moneyScoringSimulationDirective,
                performingScoringSimulationDirective,
                lateArrivalScoringSimulationDirective,
                travelingCarScoringSimulationDirective,
                travelingPtScoringSimulationDirective,
                travelingWalkScoringSimulationDirective,
                rerouteReplanningSimulationDirective,
                timeAllocationReplanningSimulationDirective,
                modeChoiceReplanningSimulationDirective
        );



        // Running OFAT Simulations
        System.out.println("Starting OFAT Simulations...");

        for (SimulationDirective directive : simulationDirectives) {
            String outputDirectory = baseOutputDirectory + directive.getParameterName() + "/";
            System.out.println("Starting directive " + directive);
            System.out.println("Output directory: " + outputDirectory );
            runSimulations(directive, baselineConfig, outputDirectory);
        }

/*        System.out.println("Starting Factorial Simulations...");

        // Running Factorial Simulations
        runFactorialSimulations(factorialReplanningSimulationDirective1, baselineConfig, RTOutputDirectory);
        runFactorialSimulations(factorialReplanningSimulationDirective2, baselineConfig, RMOutputDirectory);
        runFactorialSimulations(factorialReplanningSimulationDirective3, baselineConfig, TMOutputDirectory);*/
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

    public static void runFactorialSimulations(FactorialReplanningSimulationDirective directive, String baselineConfig, String baseOutputDirectory) {
        for (double[] value : directive.getPairedParameterValues()) {
            // Create the specific config from the directive and the baselineConfig
            Config modifiedConfig = directive.modifyConfig(baselineConfig, value);

            // Set up the output directory and enable overriding
            String outputFolder = Math.round(value[0]*100) + "_" + Math.round(value[1]*100);
            String outputDirectory = baseOutputDirectory + outputFolder;
            System.out.println("OutputDirectory: " + outputDirectory);
            modifiedConfig.controller().setOutputDirectory(outputDirectory);
            modifiedConfig.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

            // Create an instance of the controller using current config and run simulation
            Controler controler = new Controler(modifiedConfig);
            controler.run();
        }
    }
}
