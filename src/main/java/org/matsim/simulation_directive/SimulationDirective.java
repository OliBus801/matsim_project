package org.matsim.simulation_directive;
import org.matsim.core.config.Config;
import java.util.Random;

public abstract class SimulationDirective {

    private final String parameterName;
    private double[] parameterValues;
    private double[][] pairedParameterValues;

    public SimulationDirective(String parameterName, double[] parameterValues) {
        this.parameterName = parameterName;
        this.parameterValues = parameterValues;
        shuffleParameterValues(); // Shuffle values during initialization
    }

    public SimulationDirective(String parameterName, double[][] pairedParameterValues) {
        this.parameterName = parameterName;
        this.pairedParameterValues = pairedParameterValues;
    }

    public String getParameterName() {
        return parameterName;
    }

    public double[] getParameterValues() {
        return parameterValues;
    }

    public double[][] getPairedParameterValues() { return pairedParameterValues; }

    private void shuffleParameterValues() {
        Random random = new Random();
        for (int i = parameterValues.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            double temp = parameterValues[i];
            parameterValues[i] = parameterValues[j];
            parameterValues[j] = temp;
        }
    }

    // Define abstract modifyConfig() method without implementation
    public abstract Config modifyConfig(String config_path, double value);
}


