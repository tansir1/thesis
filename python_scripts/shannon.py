import os
import numpy as np
import matplotlib.pyplot as plt
import math

def compute_shannon(num_pts):
    step_sz = 1.0 / num_pts
    # Crazy lower bound prevents a domain error when taking the log of absolute zero
    x = np.arange(0.0000000000000001, 1.0 + step_sz, step_sz)
    y = np.zeros(num_pts + 1)

    # Max uncertainty occurs at 0.5, get that value and use it to scale all results to [0, 1]
    max_uncert = (-0.5 * math.log10(0.5)) - (0.5 * math.log10(0.5))

    for i in range(len(x)-1):
        one_minus = 1 - x[i]
        y[i] = x[i] * math.log10(x[i])
        y[i] = (-x[i] * math.log10(x[i])) - (one_minus *math.log10(one_minus))
        y[i] /= max_uncert

    return x, y


x, y = compute_shannon(100)
#print(x)
#print(y)
plt.plot(x,y, linewidth=3)
plt.xlabel('Probability(Empty Cell)')
plt.ylabel('Shannon Uncertainty')
plt.title('Probability of empty cell versus uncertainty')
plt.grid()
plt.savefig('../dissertation/diagrams/shannon.png')
plt.show()