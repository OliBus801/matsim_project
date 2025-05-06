package org.matsim.project;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ScoringConfigGroup;

import java.util.Map;
import java.util.Objects;

public class HelloWorld {
    public static double[] negativeBase10ParameterValues = new double[9];
    public static void main(String[] args) {
        System.out.println("Hello World!");
        for (int i = 0; i < negativeBase10ParameterValues.length; i++) {
            negativeBase10ParameterValues[i] = i * 0.25;
        }
        for (double negativeBase10ParameterValue : negativeBase10ParameterValues) {
            System.out.println(negativeBase10ParameterValue);
        }
    }
}
