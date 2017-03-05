import os
import numpy as np
import matplotlib.pyplot as plt
import math

import ObjectivesData as od

#Script configuration
plotLineWidth = 2
plotMarkerSize = 10
worlds = np.array([0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
figWidthInches = 11
figHeightInches = 8.5

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
    #plt.title('Time to detect all targets')
    plt.grid()

    #plt.xlim([-0.5,9.5])
    plot_data(plt, od.comm100.detected, od.comm100)
    plot_data(plt, od.comm20.detected, od.comm20)
    plot_data(plt, od.comm10.detected, od.comm10)
    plot_data(plt, od.comm5.detected, od.comm5)
    plot_data(plt, od.comm2.detected, od.comm2)
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
    #plt.title('Time to destroy all targets')
    plt.grid()

    #plt.xlim([-0.5,9.5])
    plot_data(plt, od.comm100.destroyed, od.comm100, skip_3rd_world)
    plot_data(plt, od.comm20.destroyed, od.comm20)
    plot_data(plt, od.comm10.destroyed, od.comm10, skip_3rd_world)
    plot_data(plt, od.comm5.destroyed, od.comm5, skip_4th_world)
    plot_data(plt, od.comm2.destroyed, od.comm2, skip_3rd_9th_world)
    #plot_missing_points(plt, comm2, asdf)

    add_legend_below_plot(plt)
    plt.savefig('../dissertation/diagrams/destroyed.png')
    plt.show()


def known_plot():
    plt.figure(figsize=(figWidthInches, figHeightInches))
    plt.xlabel('World')
    plt.ylabel('Time (s)')
    #plt.title('Time all world known')
    plt.grid()
    #plt.xlim([-0.5,9.5])
    plot_data(plt, od.comm100.known, od.comm100)
    plot_data(plt, od.comm20.known, od.comm20)
    plot_data(plt, od.comm10.known, od.comm10)
    plot_data(plt, od.comm5.known, od.comm5)
    plot_data(plt, od.comm2.known, od.comm2)
    add_legend_below_plot(plt)
    plt.savefig('../dissertation/diagrams/world_known.png')
    plt.show()

def average_plot():
    plt.figure(figsize=(figWidthInches, figHeightInches))
    plt.xlabel('Communication Range %')
    plt.ylabel('Time (s)')
    #plt.title('Average time to complete objectives per communication range')
    plt.grid()

    ticks = [0, 1, 2, 3, 4]

    plt.plot(ticks, od.average_detected, linewidth=plotLineWidth, markersize=plotMarkerSize,
             marker='o', label='Detected')
    plt.plot(ticks, od.average_destroyed, linewidth=plotLineWidth, markersize=plotMarkerSize,
             marker='o', label='Destroyed')
    plt.plot(ticks, od.average_known, linewidth=plotLineWidth, markersize=plotMarkerSize,
             marker='o', label='World Known')

    tick_labels = ["100", '20', '10', '5', '2']
    plt.xticks(ticks, tick_labels)

    plt.legend(loc='upper center', bbox_to_anchor=(0.5, -0.055),
                 fancybox=True, shadow=True, ncol=3)

    #plt.gca().invert_xaxis()
    plt.savefig('../dissertation/diagrams/averages.png')
    plt.show()


def detected_box_plot():
    labels = ['Rng 100%', 'Rng 20%', 'Rng 10%', 'Rng 5%', 'Rng 2%']
    boxData = []
    boxData.append(od.comm100.detected)
    boxData.append(od.comm20.detected)
    boxData.append(od.comm10.detected)
    boxData.append(od.comm5.detected)
    boxData.append(od.comm2.detected)

    plt.figure(figsize=(figWidthInches, figHeightInches))
    plt.xlabel('Communication Range %')
    plt.ylabel('Time (s)')
    #plt.title('Time to detect all targets')
    plt.grid()

    plt.boxplot(boxData, labels=labels, showmeans=True, sym='bd')
    plt.savefig('../dissertation/diagrams/detected_box.png')
    plt.show()


def destroyed_box_plot():
    labels = ['Rng 100%', 'Rng 20%', 'Rng 10%', 'Rng 5%', 'Rng 2%']
    boxData = []
    boxData.append(od.comm100.destroyed)
    boxData.append(od.comm20.destroyed)
    boxData.append(od.comm10.destroyed)
    boxData.append(od.comm5.destroyed)
    boxData.append(od.comm2.destroyed)

    plt.figure(figsize=(figWidthInches, figHeightInches))
    plt.xlabel('Communication Range %')
    plt.ylabel('Time (s)')
    #plt.title('Time to destroy all targets')
    plt.grid()

    plt.boxplot(boxData, labels=labels, showmeans=True, sym='bd')
    plt.savefig('../dissertation/diagrams/destroyed_box.png')
    plt.show()


def known_box_plot():
    labels = ['Rng 100%', 'Rng 20%', 'Rng 10%', 'Rng 5%', 'Rng 2%']
    boxData = []
    boxData.append(od.comm100.known)
    boxData.append(od.comm20.known)
    boxData.append(od.comm10.known)
    boxData.append(od.comm5.known)
    boxData.append(od.comm2.known)

    plt.figure(figsize=(figWidthInches, figHeightInches))
    plt.xlabel('Communication Range %')
    plt.ylabel('Time (s)')
    #plt.title('Time all world known')
    plt.grid()

    plt.boxplot(boxData, labels=labels, showmeans=True, sym='bd')
    plt.savefig('../dissertation/diagrams/known_box.png')
    plt.show()

detected_plot()
destroyed_plot()
known_plot()
average_plot()
detected_box_plot()
destroyed_box_plot()
known_box_plot()
