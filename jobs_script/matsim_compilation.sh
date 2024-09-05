#!/bin/bash
#SBATCH --time=00:30:00
#SBATCH --mem-per-cpu=10G      # increase as needed
#SBATCH --account=def-chgag196
#--------------------------------------
module load java/17.0.6
echo 'Module Loaded !'
#--------------------------------------
./mvnw clean package