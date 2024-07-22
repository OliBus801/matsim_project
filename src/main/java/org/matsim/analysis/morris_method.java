package org.matsim.analysis;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.ScoringConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class morris_method {
    public static final String baseOutputDirectory = "scenarios/siouxfalls-2014/outputs/Morris_Method/";
    static String baselineConfig = "scenarios/siouxfalls-2014/configs/config_baseline_50iterations.xml";
    static String parameterValuesCSV = "scenarios/siouxfalls-2014/configs/config_parameter_values.csv";

    public static void main(String[] args){
        String line;
        int iterations = 0;
        List<double[]> parameter_values = new ArrayList<>();

        // Read CSV file
        System.out.println("Starting Morris Method...");
        System.out.println("Reading CSV file at " + parameterValuesCSV);
        try (BufferedReader br = new BufferedReader(new FileReader(parameterValuesCSV))) {
            br.readLine(); // Ignorer la première ligne (noms des paramètres)
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                double[] param = new double[values.length];
                for (int i = 0; i < values.length; i++) {
                    param[i] = Double.parseDouble(values[i]);
                }
                parameter_values.add(param);
            }
            System.out.println("Finished reading CSV file at " + parameterValuesCSV + " !");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Starting simulation runs...");
        // For each set of parameter - Run simulation comparing to baseline
        for(double[] param : parameter_values){
            System.out.println("Setting up experiment with following parameters: [" + param[0] + ", " + param[1] + ", " + param[2] + "]");

            // Load the config from baseline
            Config config = ConfigUtils.loadConfig(baselineConfig);

            // Get the ScoringConfigGroup Module
            ScoringConfigGroup scoringConfigGroup = ConfigUtils.addOrGetModule(config,
                    ScoringConfigGroup.GROUP_NAME, ScoringConfigGroup.class);

            // Modify the corresponding parameters
            scoringConfigGroup.setMarginalUtilityOfMoney(param[0]);
            scoringConfigGroup.setPerforming_utils_hr(param[1]);
            scoringConfigGroup.setLateArrival_utils_hr(param[2]);

            // Set up the output directory
            String outputFolder = String.valueOf(iterations);
            String outputDirectory = baseOutputDirectory + outputFolder;
            config.controller().setOutputDirectory(outputDirectory);
            config.controller().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

            // Print some information for debugging
            System.out.println("Starting simulation for parameters " + param[0] + ", " + param[1] + ", " + param[2]);
            System.out.println("Output directory : " + outputDirectory);

            Controler controler = new Controler(config);
            controler.run();

            // Increase iteration number
            iterations = iterations + 1;
        }
    }
}
