import numpy as np
import matplotlib.pyplot as plt

frames = []
emptyCellProbs = []
notEmptyCellProbs = []
shannonUncert = []
hdgEst = []
type0Probs = []
type1Probs = []
type2Probs = []

with open('sensorScanTest.csv') as csvFile:
   for line in csvFile:
      dataLine = line.split(',')
      if line == '\n':
         continue
      frames.append(int(dataLine[0]))
      emptyCellProbs.append(float(dataLine[1]))
      notEmptyCellProbs.append(float(dataLine[2]))
      hdgEst.append(float(dataLine[3]))
      shannonUncert.append(float(dataLine[4]))
      type0Probs.append(float(dataLine[5]))
      type1Probs.append(float(dataLine[6]))
      type2Probs.append(float(dataLine[7]))

print("Type 0 mean prob: {} std dev: {}".format(np.mean(type0Probs), np.std(type0Probs)))
print("Type 1 mean prob: {} std dev: {}".format(np.mean(type1Probs), np.std(type1Probs)))
print("Type 2 mean prob: {} std dev: {}".format(np.mean(type2Probs), np.std(type2Probs)))

plotLineWidth=5
plt.plot(frames, type0Probs, label='Type 0 Prob', linestyle="-", linewidth=plotLineWidth)
plt.plot(frames, type1Probs, label='Type 1 Prob', linestyle=":", linewidth=plotLineWidth)
plt.plot(frames, type2Probs, label='Type 2 Prob', linestyle="--", linewidth=plotLineWidth)
plt.plot(frames, emptyCellProbs, label='Empty Cell Prob', linestyle="-.", linewidth=plotLineWidth/2)
plt.plot(frames, notEmptyCellProbs, label='Not Empty Cell Prob', linestyle="-", linewidth=plotLineWidth/2)
plt.plot(frames, shannonUncert, label='Shannon Uncert', linestyle="--", linewidth=plotLineWidth/2)
axes = plt.gca()
#axes.set_xlim([xmin,xmax])
axes.set_ylim([-0.1,1.1])
plt.xlabel('Frames')
plt.ylabel('Probability')
plt.title('Change in belief over time for a single cell and single target')
plt.legend()
plt.show()
'''
plt.plot(frames, tgt0Hdg, label='No targets hdg')
plt.plot(frames, tgt1Hdg, label='Target Type 1 hdg')
plt.plot(frames, tgt2Hdg, label='Target Type 2 hdg')
plt.xlabel('Frames')
plt.ylabel('Heading in degrees')
plt.title('Change in target belief over time for a single cell')
plt.legend()
plt.show()
'''
