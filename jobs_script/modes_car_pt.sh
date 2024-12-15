#!/bin/bash
#SBATCH --time=20:30:00
#SBATCH --mem=4G
#SBATCH --cpus-per-task=4     # I assigned 4 threads in MATSim config - Adjust as needed
#SBATCH --mincpus=4
#SBATCH --account=def-chgag196
#--------------------------------------
module purge; module load java
echo 'Module Loaded !'
#--------------------------------------
cd /home/olbus4/scratch/Transport # Make sure we are in project root directory
#--------------------------------------
echo 'Starting java...'
java -cp matsim-example-project-0.0.1-SNAPSHOT.jar org.matsim.analysis.analysis_withrun2 modes car,pt
echo 'Java code ended...'

export BASE="$(pwd)/scenarios/siouxfalls-2014/outputs"
export DIR="${BASE}/ANOVA_Analysis/modes/car,pt"
export NDIR="${DIR}_compressed"
export NITER=50

echo 'Starting compression algorithm'
python3 src/main/python/file_extractor.py $DIR $NITER $NDIR

echo 'Cleaning up...'
rm -r $DIR