package org.matsim.simulation_directive;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ReplanningConfigGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class FactorialReplanningSimulationDirective extends SimulationDirective{

    private final String factor1;
    private final String factor2;

    public FactorialReplanningSimulationDirective(String factor1, String factor2, double[] parameterValues) {
        super(factor1 + "_" + factor2, generateHalvedFactorialParameterValues(parameterValues));
        this.factor1 = factor1;
        this.factor2 = factor2;
    }

    private static double[][] generateHalvedFactorialParameterValues(double[] factorValues) {
        List<double[]> combinations = new ArrayList<>();
        for (double i : factorValues) {
            for (double j : factorValues) {
                if (i + j <= 0.9) {
                    combinations.add(new double[]{i, j});
                }
            }
        }
        return combinations.toArray(new double[combinations.size()][]) ;
    }

    public String getFactor1() {
        return factor1;
    }

    public String getFactor2() { return factor2; }

    @Override
    public Config modifyConfig(String config_path, double value) {
        // Load the config from the baseline config path
        Config config = ConfigUtils.loadConfig(config_path);

        // Get the StrategyConfigGroup Module
        ReplanningConfigGroup replanningConfigGroup = ConfigUtils.addOrGetModule(config, ReplanningConfigGroup.GROUP_NAME, ReplanningConfigGroup.class);
        Collection<ReplanningConfigGroup.StrategySettings> strategies = replanningConfigGroup.getStrategySettings();

        for(org.matsim.core.config.groups.ReplanningConfigGroup.StrategySettings strategy : strategies){
            if(Objects.equals(getFactor1(), strategy.getStrategyName()) || Objects.equals(getFactor2(), strategy.getStrategyName())){
                strategy.setWeight(value);
            } else if (Objects.equals("ChangeExpBeta", strategy.getStrategyName())) {
                strategy.setWeight(1 - (2*value));
            } else {
                strategy.setWeight(0.0);
            }
        }

        return config;
    }

    public Config modifyConfig(String config_path, double[] pairValues){
        // Load the config from the baseline config path
        Config config = ConfigUtils.loadConfig(config_path);

        // Get the StrategyConfigGroup Module
        ReplanningConfigGroup replanningConfigGroup = ConfigUtils.addOrGetModule(config, ReplanningConfigGroup.GROUP_NAME, ReplanningConfigGroup.class);
        Collection<ReplanningConfigGroup.StrategySettings> strategies = replanningConfigGroup.getStrategySettings();

        for(org.matsim.core.config.groups.ReplanningConfigGroup.StrategySettings strategy : strategies){
            if(Objects.equals(getFactor1(), strategy.getStrategyName())){
                strategy.setWeight(pairValues[0]);
            } else if (Objects.equals(getFactor2(), strategy.getStrategyName())){
                strategy.setWeight(pairValues[1]);
            }
            else if (Objects.equals("ChangeExpBeta", strategy.getStrategyName())) {
                strategy.setWeight(1 - (pairValues[0] + pairValues[1]));
            } else {
                strategy.setWeight(0.0);
            }
        }

        return config;
    }
}
