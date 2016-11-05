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


worlds = np.array([0, 1, 2, 3, 4, 5, 6, 7, 8, 9])

comm100 = ObjectivesData()
comm100.detected = np.array([  86,  74, 111, 65, 191,  64, 205,  71, 118, 175])
#Skip world 3
#comm100.destroyed = np.array([172, 159, 160, -1, 248, 175, 307, 130, 280, 221])
comm100.destroyed = np.array([172, 159, 160, 248, 175, 307, 130, 280, 221])
comm100.known = np.array([     85,  68,  82, 95,  16,  58,  95,  20,  63,  29])
comm100.plotColor = 'blue'

comm20 = ObjectivesData()
comm20.detected = np.array([122, 66, 176, 61, 144, 117, 238, 166, 261, 156])
comm20.destroyed = np.array([348, 168, 196, 179, 244, 238, 260, 209, 300, 251])
comm20.known = np.array([104, 76, 41, 22, 20, 107, 74, 157, 256, 47])
comm20.plotColor = 'green'

comm10 = ObjectivesData()
comm10.detected = np.array([119, 102, 217, 55, 74, 198, 119, 109, 123, 44])
#Skip world 3
#comm10.destroyed = np.array([305, 378, 294, -1, 174, 244, 170, 222, 165, 125])
comm10.destroyed = np.array([305, 378, 294, 174, 244, 170, 222, 165, 125])
comm10.known = np.array([117, 144, 104, 41, 19, 184, 91, 53, 124, 69])
comm10.plotColor = 'red'

comm5 = ObjectivesData()
comm5.detected = np.array([238, 112, 164, 55, 76, 171, 394, 172, 133, 100])
#Skip world 4
#comm5.destroyed = np.array([279, 237, 199, 351, -1, 263, 424, 440, 244, 210])
comm5.destroyed = np.array([279, 237, 199, 351, 263, 424, 440, 244, 210])
comm5.known = np.array([242, 123, 85, 65, 66, 136, 161, 142, 156, 115])
comm5.plotColor = 'black'
#comm5.plotColor = 'yellow'

comm2 = ObjectivesData()
comm2.detected = np.array([380, 167, 288, 50, 269, 261, 199, 257, 234, 103])
#Skip world 3
#comm2.destroyed = np.array([554, 498, 596, -1, 337, 299, 395, 468, 323, -1])
comm2.destroyed = np.array([554, 498, 596, 337, 299, 395, 468, 323, -1])
comm2.known = np.array([339, 468, 233, 164, 93, 166, 332, 183, 293, 110])
comm2.plotColor = 'magenta'

plotLineWidth = 2
plotMarkerSize = 10


def plot_data(chart, axis_data, data_cfg, local_worlds=worlds):
    chart.plot(local_worlds, axis_data, linewidth=plotLineWidth, markersize=plotMarkerSize, marker=data_cfg.plotMarker, color=data_cfg.plotColor)


def detected_plot():
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
    # plt.savefig('../dissertation/diagrams/shannon.png')
    plt.savefig('../dissertation/diagrams/detected.png')
    plt.show()


def destroyed_plot():
    skip_3rd_world = np.array([0, 1, 2, 4, 5, 6, 7, 8, 9])
    skip_4th_world = np.array([0, 1, 2, 3, 5, 6, 7, 8, 9])

    plt.xlabel('World')
    plt.ylabel('Time (s)')
    plt.title('Time to destroy all targets')
    plt.grid()
    #plt.xlim([-0.5,9.5])
    plot_data(plt, comm100.destroyed, comm100, skip_3rd_world)
    plot_data(plt, comm20.destroyed, comm20)
    plot_data(plt, comm10.destroyed, comm10, skip_3rd_world)
    plot_data(plt, comm5.destroyed, comm5, skip_4th_world)
    plot_data(plt, comm2.destroyed, comm2, skip_3rd_world)
    plt.savefig('../dissertation/diagrams/destroyed.png')
    plt.show()


def known_plot():
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
    plt.savefig('../dissertation/diagrams/world_known.png')
    plt.show()


detected_plot()
destroyed_plot()
known_plot()