import os
import argparse
import subprocess

# python3 gen_pu_diagrams.py -pu /opt/apps/plantuml/plantuml.jar

'''
puSourceFiles = ['belief_class.pu',
                 'uav_activity_attack.pu',
                 'uav_activity_bda.pu',
                 'uav_activity_confirm.pu',
                 'uav_activity_main_loop.pu',
                 'uav_activity_merge_beliefs.pu',
                 'uav_activity_pathing.pu',
                 'uav_activity_proc_contracts.pu',
                 'uav_activity_relay_comms.pu',
                 'uav_activity_search.pu',
                 'uav_activity_track.pu',
                 'uav_state_tasks.pu',
                 'uav_usecase_infrastructure.pu',
                 'uav_usecase_tasks.pu',
                 'uav_monitor_states.pu']
'''
puSourceFiles = ['uav_activity_attack.pu',
                 'uav_activity_confirm.pu',
                 'uav_monitor_states.pu',
                 'belief_object.pu',
                 'monitor_attack_sequence.pu']


def generate_diagrams(pu_jar_path):

    for src in puSourceFiles:
        if not os.path.exists(src):
            print("ERROR: {0} does not exist.".format(src))
        else:
            cmds = ['java', '-jar', os.path.abspath(pu_jar_path)]
            cmds.append(src)
            print("Generating {0}".format(src))
            subprocess.run(cmds, stdout=subprocess.PIPE, stderr=subprocess.PIPE)


def main():
    parser = argparse.ArgumentParser(description="Generate all of the PlantUML diagrams.")
    parser.add_argument("-pu", required=True, dest="puJarPath",
                        help="Path to the PlantUML jar file.")
    args = parser.parse_args()

    if args.puJarPath:
        if not os.path.exists(args.puJarPath):
            parser.error("The file %s does not exist." % args.puJarPath)
        else:
            generate_diagrams(args.puJarPath)

if __name__ == "__main__":
    main()
