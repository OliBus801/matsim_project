package org.matsim.simulation_directive;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ScoringConfigGroup;

import java.util.Map;
import java.util.Objects;

public class ScoringSimulationDirective extends SimulationDirective {

    public ScoringSimulationDirective(String parameterName, double[] parameterValues) {
        super(parameterName, parameterValues);
    }

    @Override
    public Config modifyConfig(String config_path, double value) {
        // Load the config from the baseline config path
        Config config = ConfigUtils.loadConfig(config_path);

        // Get the PlanCalcScoreConfigGroup Module
        ScoringConfigGroup scoringConfigGroup = ConfigUtils.addOrGetModule(config,
                ScoringConfigGroup.GROUP_NAME, ScoringConfigGroup.class);

        // Modify the scoring-related parameter in the config
        if(Objects.equals(getParameterName(), "MarginalUtilityOfMoney")){scoringConfigGroup.setMarginalUtilityOfMoney(value);}
        else if (Objects.equals(getParameterName(), "MarginalUtilityOfPerforming")){scoringConfigGroup.setPerforming_utils_hr(value);}
        else if (Objects.equals(getParameterName(), "MarginalUtilityOfLateArrival")) {scoringConfigGroup.setLateArrival_utils_hr(value);}
        else if (Objects.equals(getParameterName(), "MarginalUtilityOfTraveling")) {
            // As of right now, we only implement the option to change the utility for all mode
            Map<String, ScoringConfigGroup.ModeParams> allModes = scoringConfigGroup.getModes();
            for (Map.Entry<String, ScoringConfigGroup.ModeParams> modeEntry : allModes.entrySet()) {
                ScoringConfigGroup.ModeParams modeParams = modeEntry.getValue();
                modeParams.setMarginalUtilityOfTraveling(value);
            }
        }

        return config;
    }
}
