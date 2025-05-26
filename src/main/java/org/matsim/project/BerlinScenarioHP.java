package org.matsim.project;

import org.matsim.application.MATSimApplication;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.*;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.gbl.MatsimRandom;
import picocli.CommandLine;

import java.util.*;

/**
 * Lance OpenBerlinScenario en injectant des hyper‑paramètres passés en CLI.
 *
 * Usage :
 *   java ... BerlinScenarioHP run "<k1=v1,k2=v2,…>" <simId> [--help]
 */
@CommandLine.Command(
        name = "BerlinScenarioHP",
        description = "OpenBerlinScenario avec hyper‑paramètres dynamiques",
        mixinStandardHelpOptions = true,
        version = OpenBerlinScenario.VERSION)
public final class BerlinScenarioHP extends OpenBerlinScenario {

    /* -------------------- paramètres CLI -------------------- */

    @CommandLine.Parameters(index = "0", paramLabel = "<theta>", description = "Liste k=v séparée par des virgules")
    private String thetaString;

    @CommandLine.Parameters(index = "1", paramLabel = "<simId>", description = "Identifiant unique de la simulation")
    private String simId;

    @CommandLine.Parameters(index = "2", paramLabel = "<configPath>", description = "Chemin vers le fichier de configuration")
    private String configPath;

    @CommandLine.Parameters(index = "3", paramLabel = "<outputPath>", description = "Chemin vers le répertoire de sortie")
    private String outputPath;

    @CommandLine.Option(names = "--firstIteration", description = "Itération de début (surcharge config.xml)")
    private Integer firstIteration = null;

    @CommandLine.Option(names = "--lastIteration", description = "Itération de fin (surcharge config.xml)")
    private Integer lastIteration = null;

    private Map<String, String> theta;   // stocke k‑v après parse

    /* -------------------- constructeur -------------------- */
    public BerlinScenarioHP() {
        super();   // garde le config.xml défini par la classe mère
    }

    /* -------------------- hook MATSim -------------------- */
    @Override
    protected Config prepareConfig(Config config) {

        // 1) conserver les réglages originaux
        config = super.prepareConfig(config);

        // 2) parser la chaîne k=v,k2=v2…
        theta = parseTheta(thetaString);

        /* --------- seed vraiment aléatoire --------- */
        long seed = Math.abs(new Random().nextLong());
        MatsimRandom.reset(seed);
        config.global().setRandomSeed(seed);

        /* --------- paramètres globaux / exécution --------- */
        ControllerConfigGroup ctrl = config.controller();

        /* On s'occupe du nombre d'itérations */
        int lastIt = Integer.parseInt(theta.get("numberOfIterations"));
        ctrl.setLastIteration(lastIt);
        ctrl.setWriteEventsInterval(lastIt);
        ctrl.setWritePlansInterval(lastIt);

        if (firstIteration != null) {
            ctrl.setFirstIteration(firstIteration);
            if (lastIteration != null && lastIteration <= lastIt) {
                ctrl.setLastIteration(lastIteration);
                ctrl.setWriteEventsInterval(lastIteration);
                ctrl.setWritePlansInterval(lastIteration);
            }
        }

        ctrl.setOutputDirectory(outputPath + "/simulation_" + simId);
        ctrl.setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

        /* --------- scoring --------- */
        ScoringConfigGroup sc = config.scoring();
        sc.setEarlyDeparture_utils_hr(   Double.parseDouble(theta.get("earlyDeparture_util")));
        sc.setLateArrival_utils_hr(      Double.parseDouble(theta.get("lateArrival_util")));
        sc.setPerforming_utils_hr(       Double.parseDouble(theta.get("performing_util")));
        sc.setMarginalUtlOfWaitingPt_utils_hr(Double.parseDouble(theta.get("waitingPt_util")));
        sc.setMarginalUtilityOfMoney(    Double.parseDouble(theta.get("money_util")));

        sc.getAllModes().forEach(mode -> {
            String v = theta.get("ASC_" + mode);
            if (v != null)
                sc.getOrCreateModeParams(mode)
                  .setConstant(Double.parseDouble(v));
        });

        /* --------- replanning --------- */
        ReplanningConfigGroup repl = config.replanning();
        repl.getStrategySettings().forEach(s -> {
            String w = theta.get(s.getStrategyName());
            if (w != null) s.setWeight(Double.parseDouble(w));
        });
        repl.setMaxAgentPlanMemorySize(Integer.parseInt(theta.get("maxAgentPlanMemorySize")));
        
        TimeAllocationMutatorConfigGroup timeAlloc = config.timeAllocationMutator();
        timeAlloc.setMutationRange(Double.parseDouble(theta.get("mutationRange")));


        /* --------- QSim --------- */
        config.qsim().setTimeStepSize(Double.parseDouble(theta.get("timeStepSize")));

        return config;   // MATSimApplication fera le reste
    }

    /* -------------------- parse utilitaire -------------------- */
    private static Map<String, String> parseTheta(String str) {
        Map<String, String> map = new HashMap<>();
        for (String kv : str.split(",")) {
            String[] t = kv.split("=");
            if (t.length == 2) map.put(t[0].trim(), t[1].trim());
            else System.err.println("Mauvais format : " + kv);
        }
        return map;
    }

    /* -------------------- main -------------------- */
    public static void main(String[] args) {
        MATSimApplication.run(BerlinScenarioHP.class, args);
    }
}
