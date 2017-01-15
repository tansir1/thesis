from pylab import *
import numpy as np
from scipy.interpolate import griddata

detectAngleDegradationSlope = -0.005555556 # -1/180
minDetectValue = 0.001;

number_points = 360

PI = math.pi
TAU = 2 * PI


def smallestSignedAngleBetween(x, y):
    x = math.radians(x)
    y = math.radians(y)
    a = (x - y) % TAU
    b = (y - x) % TAU
    return -a if a < b else b

def angleDiff(angle1, angle2):
    diff = (angle2 - angle1 + 180) % 360 - 180
    if diff < -180:
        diff += 360
    return math.fabs(diff)

bestAngle = 45 #degrees
#values = np.zeros((number_points,2))
angles = np.zeros(number_points)
radii =  np.zeros(number_points)
#for i in range (0, 360, 1):
for i in range (-180, 180, 1):
    snsrToTgtHdg = i
    '''
    delta1 = snsrToTgtHdg - bestAngle
    if delta1 > 180:
        delta1 -= 360
    if delta1 < -180:
        delta1 += 360

    percentOfBestProbAngle1 = (detectAngleDegradationSlope * delta1) + 1

    delta2 = snsrToTgtHdg + bestAngle
    if delta2 > 180:
        delta2 -= 360
    if delta2 < -180:
        delta2 += 360

    percentOfBestProbAngle2 = (detectAngleDegradationSlope * delta2) + 1
    percentOfBestProbAngle = max(percentOfBestProbAngle1, percentOfBestProbAngle2)
    '''
    '''
    delta1 = smallestSignedAngleBetween(snsrToTgtHdg, bestAngle)
    percentOfBestProbAngle1 = (detectAngleDegradationSlope * delta1) + 1
    delta2 = smallestSignedAngleBetween(snsrToTgtHdg, -bestAngle)
    percentOfBestProbAngle2 = (detectAngleDegradationSlope * delta2) + 1
    percentOfBestProbAngle = max(percentOfBestProbAngle1, percentOfBestProbAngle2)
    '''

    '''
    if snsrToTgtHdg > bestAngle:
        delta = snsrToTgtHdg - bestAngle
    else:
        delta = bestAngle - snsrToTgtHdg

    percentOfBestProbAngle = (detectAngleDegradationSlope * delta) + 1

    '''


    delta1 = angleDiff(snsrToTgtHdg, bestAngle)
    percentOfBestProbAngle1 = (detectAngleDegradationSlope * delta1) + 1

    delta2 = angleDiff(snsrToTgtHdg, -bestAngle)
    percentOfBestProbAngle2 = (detectAngleDegradationSlope * delta2) + 1

    percentOfBestProbAngle = max(percentOfBestProbAngle1, percentOfBestProbAngle2)

    '''
    #delta1 = (snsrToTgtHdg - bestAngle) % 180
    delta1 = math.fabs(snsrToTgtHdg - bestAngle) % 180
    #delta1 = (snsrToTgtHdg - bestAngle)
    #if delta1 > 180:
    #    delta1 -= 180
    percentOfBestProbAngle1 = (detectAngleDegradationSlope * delta1) + 1

    #delta2 = (snsrToTgtHdg + bestAngle) % 180
    delta2 = math.fabs(snsrToTgtHdg + bestAngle) % 180
    #delta2 = (snsrToTgtHdg + bestAngle)
    #if delta2 > 180:
    #    delta2 -= 180
    percentOfBestProbAngle2 = (detectAngleDegradationSlope * delta2) + 1

    percentOfBestProbAngle = max(percentOfBestProbAngle1, percentOfBestProbAngle2)

    '''
    angles[i] = math.radians(i)
    radii[i] = percentOfBestProbAngle * 100
    #radii[i] = (i * 1.0) / 360.0 * 100
    #radii[i] = 5
    print("{0},{1},{2}....{3},{4}".format(i, delta1, delta2, percentOfBestProbAngle1, percentOfBestProbAngle2))
    #print("{0},{1},{2}".format(i, delta, percentOfBestProbAngle))


#print(angles)
#print(radii)

#plt.polar(angles, radii, linewidth=0.5, color='red')
#ax.plot(values, color='r', linewidth=3)
#ax.set_rmax(100)

fig = plt.figure()
ax = plt.subplot(111, polar=True)
ax.set_theta_zero_location('N')
ax.set_theta_direction(-1)
ax.set_rlabel_position(60)
ax.plot(angles, radii, color='b', linewidth=1)
ax.set_rmax(100)
# ax.set_rmin(70.0)
ax.grid(True)
ax.set_title("Scaling coefficients from a 45$^{0}$ Best Angle", va='bottom')
plt.show()
fig.savefig('../dissertation/diagrams/best_angle_45.png')

'''
for i in range (-179, 179, 1):
    snsrToTgtHdg = i
    delta = 0
    #delta = (snsrToTgtHdg - bestAngle) % 180
    if snsrToTgtHdg > bestAngle:
        delta = (snsrToTgtHdg - bestAngle) % 180
    else:
        delta = (bestAngle - snsrToTgtHdg) % 180

    percentOfBestProbAngle = (detectAngleDegradationSlope * delta) + 1

    angles[i] = math.radians(i)
    radii[i] = percentOfBestProbAngle * 100
    #radii[i] = (i * 1.0) / 360.0 * 100
    #radii[i] = 5
    print("{0},{1},{2},{3}".format(i, delta, math.degrees(angles[i]), radii[i]))
'''