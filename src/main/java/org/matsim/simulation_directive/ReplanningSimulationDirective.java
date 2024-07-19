package org.matsim.simulation_directive;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ReplanningConfigGroup;

import java.util.Collection;
import java.util.Objects;

public class ReplanningSimulationDirective extends SimulationDirective{

    public ReplanningSimulationDirective(String parameterName, double[] parameterValues) {
        super(parameterName, parameterValues);
    }

    @Override
    public Config modifyConfig(String config_path, double value){
        // Load the config from the baseline config path
        Config config = ConfigUtils.loadConfig(config_path);

        // Get the StrategyConfigGroup Module
        ReplanningConfigGroup replanningConfigGroup = ConfigUtils.addOrGetModule(config, ReplanningConfigGroup.GROUP_NAME, ReplanningConfigGroup.class);
        Collection<ReplanningConfigGroup.StrategySettings> strategies = replanningConfigGroup.getStrategySettings();

        for(org.matsim.core.config.groups.ReplanningConfigGroup.StrategySettings strategy : strategies){
            if(Objects.equals(getParameterName(), strategy.getStrategyName())){
                strategy.setWeight(value);
            }
        }

        return config;
    }
}
