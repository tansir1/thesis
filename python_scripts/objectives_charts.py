import os
import numpy as np
import matplotlib.pyplot as plt
import math


class ObjectivesData:

    def __init__(self):
        self.detected = None
        self.destroyed = None
        self.known = None
        self.plotMarker = 'o'
        self.plotColor = None
        self.plotLabel = None

#Script configuration
plotLineWidth = 2
plotMarkerSize = 10
worlds = np.array([0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
figWidthInches = 11
figHeightInches = 8.5

#Data values
comm100 = ObjectivesData()
comm100.detected = np.array([  86,  74, 111, 65, 191,  64, 205,  71, 118, 175])
#Skip world 3
#comm100.destroyed = np.array([172, 159, 160, -1, 248, 175, 307, 130, 280, 221])
comm100.destroyed = np.array([172, 159, 160, 248, 175, 307, 130, 280, 221])
comm100.known = np.array([     85,  68,  82, 95,  16,  58,  95,  20,  63,  29])
comm100.plotColor = 'blue'
comm100.plotLabel = 'Rng 100%'

comm20 = ObjectivesData()
comm20.detected = np.array([122, 66, 176, 61, 144, 117, 238, 166, 261, 156])
comm20.destroyed = np.array([348, 168, 196, 179, 244, 238, 260, 209, 300, 251])
comm20.known = np.array([104, 76, 41, 22, 20, 107, 74, 157, 256, 47])
comm20.plotColor = 'green'
comm20.plotLabel = 'Rng 20%'

comm10 = ObjectivesData()
comm10.detected = np.array([119, 102, 217, 55, 74, 198, 119, 109, 123, 44])
#Skip world 3
#comm10.destroyed = np.array([305, 378, 294, -1, 174, 244, 170, 222, 165, 125])
comm10.destroyed = np.array([305, 378, 294, 174, 244, 170, 222, 165, 125])
comm10.known = np.array([117, 144, 104, 41, 19, 184, 91, 53, 124, 69])
comm10.plotColor = 'red'
comm10.plotLabel = 'Rng 10%'

comm5 = ObjectivesData()
comm5.detected = np.array([238, 112, 164, 55, 76, 171, 394, 172, 133, 100])
#Skip world 4
#comm5.destroyed = np.array([279, 237, 199, 351, -1, 263, 424, 440, 244, 210])
comm5.destroyed = np.array([279, 237, 199, 351, 263, 424, 440, 244, 210])
comm5.known = np.array([242, 123, 85, 65, 66, 136, 161, 142, 156, 115])
comm5.plotColor = 'black'
comm5.plotLabel = 'Rng 5%'
#comm5.plotColor = 'yellow'

comm2 = ObjectivesData()
comm2.detected = np.array([380, 167, 288, 50, 269, 261, 199, 257, 234, 103])
#Skip world 3 and 9
#comm2.destroyed = np.array([554, 498, 596, -1, 337, 299, 395, 468, 323, -1])
comm2.destroyed = np.array([554, 498, 596, 337, 299, 395, 468, 323])
comm2.known = np.array([339, 468, 233, 164, 93, 166, 332, 183, 293, 110])
comm2.plotColor = 'magenta'
comm2.plotLabel = 'Rng 2%'

#comm_ranges = np.array([100,20,10,5,2])
average_detected = np.array([116, 151, 116, 238, 380])
average_destroyed = np.array([206, 239, 231, 279, 554])
average_known = np.array([61, 90, 95, 242, 339])

def plot_data(chart, axis_data, data_cfg, local_worlds=worlds):
    chart.plot(local_worlds, axis_data,
               linewidth=plotLineWidth, markersize=plotMarkerSize, marker=data_cfg.plotMarker, color=data_cfg.plotColor,
               label=data_cfg.plotLabel)


def add_legend_below_plot(chart):
    chart.legend(loc='upper center', bbox_to_anchor=(0.5, -0.055),
                 fancybox=True, shadow=True, ncol=5)


def plot_missing_points(chart, data_cfg, missing_pts):
    chart.plot(missing_pts[:, 0], missing_pts[:, 1], '*', markersize=10, marker='^', color=data_cfg.plotColor)


def detected_plot():
    plt.figure(figsize=(figWidthInches, figHeightInches))
    plt.xlabel('World')
    plt.ylabel('Time (s)')
    plt.title('Time to detect all targets')
    plt.grid()

    #plt.xlim([-0.5,9.5])
    plot_data(plt, comm100.detected, comm100)
    plot_data(plt, comm20.detected, comm20)
    plot_data(plt, comm10.detected, comm10)
    plot_data(plt, comm5.detected, comm5)
    plot_data(plt, comm2.detected, comm2)
    add_legend_below_plot(plt)
    plt.savefig('../dissertation/diagrams/detected.png')
    plt.show()


def destroyed_plot():
    skip_3rd_world = np.array([0, 1, 2, 4, 5, 6, 7, 8, 9])
    skip_3rd_9th_world = np.array([0, 1, 2, 4, 5, 6, 7, 8])
    skip_4th_world = np.array([0, 1, 2, 3, 5, 6, 7, 8, 9])

    #asdf = np.array([[3, 450],
                     #[4, 100]])

    plt.figure(figsize=(figWidthInches, figHeightInches))
    plt.xlabel('World')
    plt.ylabel('Time (s)')
    plt.title('Time to destroy all targets')
    plt.grid()

    #plt.xlim([-0.5,9.5])
    plot_data(plt, comm100.destroyed, comm100, skip_3rd_world)
    plot_data(plt, comm20.destroyed, comm20)
    plot_data(plt, comm10.destroyed, comm10, skip_3rd_world)
    plot_data(plt, comm5.destroyed, comm5, skip_4th_world)
    plot_data(plt, comm2.destroyed, comm2, skip_3rd_9th_world)
    #plot_missing_points(plt, comm2, asdf)

    add_legend_below_plot(plt)
    plt.savefig('../dissertation/diagrams/destroyed.png')
    plt.show()


def known_plot():
    plt.figure(figsize=(figWidthInches, figHeightInches))
    plt.xlabel('World')
    plt.ylabel('Time (s)')
    plt.title('Time all world known')
    plt.grid()
    #plt.xlim([-0.5,9.5])
    plot_data(plt, comm100.known, comm100)
    plot_data(plt, comm20.known, comm20)
    plot_data(plt, comm10.known, comm10)
    plot_data(plt, comm5.known, comm5)
    plot_data(plt, comm2.known, comm2)
    add_legend_below_plot(plt)
    plt.savefig('../dissertation/diagrams/world_known.png')
    plt.show()

def average_plot():
    plt.figure(figsize=(figWidthInches, figHeightInches))
    plt.xlabel('Communication Range %')
    plt.ylabel('Time (s)')
    plt.title('Average time to complete objectives per communication range')
    plt.grid()

    ticks = [0, 1, 2, 3, 4]

    plt.plot(ticks, average_detected, linewidth=plotLineWidth, markersize=plotMarkerSize,
             marker='o', label='Detected')
    plt.plot(ticks, average_destroyed, linewidth=plotLineWidth, markersize=plotMarkerSize,
             marker='o', label='Destroyed')
    plt.plot(ticks, average_known, linewidth=plotLineWidth, markersize=plotMarkerSize,
             marker='o', label='World Known')

    tick_labels = ["100", '20', '10', '5', '2']
    plt.xticks(ticks, tick_labels)

    plt.legend(loc='upper center', bbox_to_anchor=(0.5, -0.055),
                 fancybox=True, shadow=True, ncol=3)

    #plt.gca().invert_xaxis()
    plt.savefig('../dissertation/diagrams/averages.png')
    plt.show()

detected_plot()
destroyed_plot()
known_plot()
average_plot()
