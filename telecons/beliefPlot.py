import numpy as np
import matplotlib.pyplot as plt

frames = []
tgt0Probs = []
tgt0Hdg = []
tgt1Probs = []
tgt1Hdg = []
tgt2Probs = []
tgt2Hdg = []
with open('data.csv') as csvFile:
   for line in csvFile:
      dataLine = line.split(',')
      if line == '\n':
         continue
      frames.append(int(dataLine[0]))
      tgt0Probs.append(float(dataLine[1]))
      tgt0Hdg.append(float(dataLine[2]))
      tgt1Probs.append(float(dataLine[3]))
      tgt1Hdg.append(float(dataLine[4]))
      tgt2Probs.append(float(dataLine[5]))
      tgt2Hdg.append(float(dataLine[6]))

print("Tgt0 mean prob: {} std dev: {}".format(np.mean(tgt0Probs), np.std(tgt0Probs)))
print("Tgt1 mean prob: {} std dev: {}".format(np.mean(tgt1Probs), np.std(tgt1Probs)))
print("Tgt2 mean prob: {} std dev: {}".format(np.mean(tgt2Probs), np.std(tgt2Probs)))

plt.plot(frames, tgt0Probs, label='No targets')
plt.plot(frames, tgt1Probs, label='Target Type 1')
plt.plot(frames, tgt2Probs, label='Target Type 2')
plt.xlabel('Frames')
plt.ylabel('Probability target type exists')
plt.title('Change in target belief over time for a single cell')
plt.legend()
plt.show()

plt.plot(frames, tgt0Hdg, label='No targets hdg')
plt.plot(frames, tgt1Hdg, label='Target Type 1 hdg')
plt.plot(frames, tgt2Hdg, label='Target Type 2 hdg')
plt.xlabel('Frames')
plt.ylabel('Heading in degrees')
plt.title('Change in target belief over time for a single cell')
plt.legend()
plt.show()
