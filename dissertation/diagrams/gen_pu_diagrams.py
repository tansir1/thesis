import os
import argparse
import subprocess

puSourceFiles = ['uav_task_state.pu',
                 'belief_class.pu']

def generateDiagrams(puJarPath):

    for src in puSourceFiles:
        if not os.path.exists(src):
            print("ERROR: {0} does not exist.".format(src))
        else:
            cmds = ['java', '-jar', os.path.abspath(puJarPath)]
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
            generateDiagrams(args.puJarPath)

if __name__ == "__main__":
    main()
