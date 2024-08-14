import os
import shutil
import argparse

def copy_files(src_dir, num_iterations, dest_dir, multiple_parameters):
    # Create the destination directory if it does not exist
    if not os.path.exists(dest_dir):
        os.makedirs(dest_dir)

    if multiple_parameters:
        for param_dir in os.listdir(src_dir):
            param_path = os.path.join(src_dir, param_dir)
            if os.path.isdir(param_path):
                os.makedirs(os.path.join(dest_dir, param_dir))
                for value_dir in os.listdir(param_path):
                    os.makedirs(os.path.join(dest_dir, param_dir, value_dir))
                    value_path = os.path.join(param_path, value_dir)
                    if os.path.isdir(value_path):

                        # Copy modestats.csv or modestats.txt
                        modestats_csv_path = os.path.join(value_path, "modestats.csv")
                        modestats_txt_path = os.path.join(value_path, "modestats.txt")

                        if os.path.exists(modestats_csv_path):
                            shutil.copy(modestats_csv_path, os.path.join(dest_dir, f"{param_dir}/{value_dir}/modestats.csv"))
                        elif os.path.exists(modestats_txt_path):
                            shutil.copy(modestats_txt_path, os.path.join(dest_dir, f"{param_dir}/{value_dir}/modestats.txt"))
                        else:
                            print(f"❗️ Could not find modestats file in {value_path}... Ignoring. ❗️")

                        # Copy {num_iterations}.countscompare.txt
                        iters_dir = os.path.join(value_path, "ITERS", f"it.{num_iterations}")
                        countscompare_file = os.path.join(iters_dir, f"{num_iterations}.countscompare.txt")
                        if os.path.exists(countscompare_file):
                            shutil.copy(countscompare_file, os.path.join(dest_dir, f"{param_dir}/{value_dir}/{num_iterations}.countscompare.txt"))
                        else:
                            print(f"❗️ Could not find countscompare file in {value_path} at iteration {num_iterations}... Ignoring. ❗️")

                        # Copy traveldistancestats.csv
                        travel_distance_file = os.path.join(value_path, "traveldistancestats.csv")
                        if os.path.exists(travel_distance_file):
                            shutil.copy(travel_distance_file, os.path.join(dest_dir, f"{param_dir}/{value_dir}/traveldistancestats.csv"))
                        else:
                            print(f"❗️ Could not find travel distance file in {value_path}... Ignoring. ❗️")

                        # Copy scorestats.csv
                        score_stats_file = os.path.join(value_path, "scorestats.csv")
                        if os.path.exists(score_stats_file):
                            shutil.copy(score_stats_file, os.path.join(dest_dir, f"{param_dir}/{value_dir}/traveldistancestats.csv"))
                        else:
                            print(f"❗️ Could not find score stats file in {value_path}... Ignoring. ❗️")

                        # Copy ph_modestats.csv
                        ph_modestats_file = os.path.join(value_path, "ph_modestats.csv")
                        if os.path.exists(ph_modestats_file):
                            shutil.copy(ph_modestats_file, os.path.join(dest_dir, f"{param_dir}/{value_dir}/ph_modestats.csv"))
                        else:
                            print(f"❗️ Could not find travel time file in {value_path}... Ignoring. ❗️")
    else:
        for value_dir in os.listdir(src_dir):
            value_path = os.path.join(src_dir, value_dir)
            if os.path.isdir(value_path):
                os.makedirs(os.path.join(dest_dir, value_dir))
                if os.path.isdir(value_path):

                    # Copy modestats.csv or modestats.txt
                    modestats_csv_path = os.path.join(value_path, "modestats.csv")
                    modestats_txt_path = os.path.join(value_path, "modestats.txt")

                    if os.path.exists(modestats_csv_path):
                        shutil.copy(modestats_csv_path, os.path.join(dest_dir, f"{value_dir}/modestats.csv"))
                    elif os.path.exists(modestats_txt_path):
                        shutil.copy(modestats_txt_path, os.path.join(dest_dir, f"{value_dir}/modestats.txt"))
                    else:
                        print(f"❗️ Could not find modestats file in {value_path}... Ignoring. ❗️")

                    # Copy {num_iterations}.countscompare.txt
                    iters_dir = os.path.join(value_path, "ITERS", f"it.{num_iterations}")
                    countscompare_file = os.path.join(iters_dir, f"{num_iterations}.countscompare.txt")
                    if os.path.exists(countscompare_file):
                        shutil.copy(countscompare_file, os.path.join(dest_dir, f"{value_dir}/{num_iterations}.countscompare.txt"))
                    else:
                        print(f"❗️ Could not find countscompare file in {value_path} at iteration {num_iterations}... Ignoring. ❗️")

                    # Copy traveldistancestats.csv
                    travel_distance_file = os.path.join(value_path, "traveldistancestats.csv")
                    if os.path.exists(travel_distance_file):
                        shutil.copy(travel_distance_file, os.path.join(dest_dir, f"{value_dir}/traveldistancestats.csv"))
                    else:
                        print(f"❗️ Could not find travel distance file in {value_path}... Ignoring. ❗️")

                    # Copy scorestats.csv
                    score_stats_file = os.path.join(value_path, "scorestats.csv")
                    if os.path.exists(score_stats_file):
                        shutil.copy(score_stats_file, os.path.join(dest_dir, f"{value_dir}/traveldistancestats.csv"))
                    else:
                        print(f"❗️ Could not find score stats file in {value_path}... Ignoring. ❗️")

                    # Copy ph_modestats.csv
                    ph_modestats_file = os.path.join(value_path, "ph_modestats.csv")
                    if os.path.exists(ph_modestats_file):
                        shutil.copy(ph_modestats_file, os.path.join(dest_dir, f"{value_dir}/ph_modestats.csv"))
                    else:
                        print(f"❗️ Could not find travel time file in {value_path}... Ignoring. ❗️")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Extract and copy specific files from a directory structure.")
    parser.add_argument("src_dir", help="Source directory containing the simulation results.")
    parser.add_argument("num_iterations", type=int, help="Number of iterations to extract results for.")
    parser.add_argument("dest_dir", help="Destination directory to copy the extracted files to.")
    parser.add_argument("--multiple_parameters", help="Considers that the source directory contains multiple parameters directory.", action="store_true")
    args = parser.parse_args()

    copy_files(args.src_dir, args.num_iterations, args.dest_dir, args.multiple_parameters)
