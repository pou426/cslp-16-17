import matplotlib.pyplot as plt
import numpy as np
import math

# thr = 0.4
# thr = 0.4

sf = [0.05, 0.07, 0.09, 0.12, 0.15, 0.18, 0.21, 0.23, 0.25, 0.27, 0.3, 0.33]
# notrips50 = [4.000, 4.000, 3.000, 2.000, 2.000, 2.000, 3.000, 3.000, 3.000, 3.000, 2.000, 2.000]
# notrips25 = [2.000, 2.000, 2.000, 2.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000, 1.000]

# tripEff50 = [36.120, 35.918, 36.313, 36.850, 36.904, 36.713, 36.467, 36.233, 36.472, 37.002, 36.512, 37.050]
# tripEff10 = [34.578, 34.443, 34.423, 34.746, 34.437, 34.577, 29.840, 21.234, 20.388]
# tripEff25 = [35.500, 35.506, 35.428, 34.078, 28.814, 27.594, 28.315, 29.358, 29.358, 30.526, 29.110, 28.056]

sf10 = [0.05, 0.053, 0.055, 0.057, 0.06, 0.09, 0.15, 0.2, 0.25]
notrips10 = [1.000, 1.000, 1.000, 0.000, 1.000, 1.000, 1.000, 1.000, 1.000]

# thr = 0.8
thr = 0.8
notrips25 = [2.000, 2.000, 2.000, 1.000, 1.000, 1.000, 1.000, 0.000, 1.000, 1.000, 1.000, 1.000]
notrips50 = [4.000, 3.000, 3.000, 2.000, 2.000, 2.000, 2.000, 2.000, 2.000, 2.000, 2.000, 1.000]


tripEff25 = [36.557, 36.818, 35.394, 31.065, 30.681, 28.617, 36.609, 36.857, 34.468, 32.654, 31.087, 29.965]
tripEff50 = [35.871, 36.382, 35.126, 34.344, 36.055, 35.499, 35.983, 35.796, 35.377, 35.668, 35.547, 35.811]
tripEff10 = [34.488, 34.299, 34.300, 34.297, 34.372, 34.171, 26.202, 22.357, 25.193]


fig = plt.figure()
ax = plt.subplot(111)
ax.plot(sf10,notrips10,'m',marker='+',label='#bins={}'.format(10))
ax.plot(sf,notrips25,'r',marker='.',label='#bins={}'.format(25))
ax.plot(sf,notrips50,'b',marker='x',label='#bins={}'.format(50))
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs average number of trips \nfor different service area sizes, threshold={}'.format(thr))
ax.set_xlabel('Service frequency (trips per hour)')
ax.set_ylabel('Average number of trips')
ax.set_ylim([-1,5])
plt.savefig('sfVSnoTrips.jpg')

fig = plt.figure()
ax = plt.subplot(111)
ax.plot(sf10,tripEff10,'m',marker='+',label='#bins={}'.format(10))
ax.plot(sf,tripEff25,'r',marker='.',label='#bins={}'.format(25))
ax.plot(sf,tripEff50,'b',marker='x',label='#bins={}'.format(50))
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs trip efficiency \nfor different service area sizes, threshold={}'.format(thr))
ax.set_xlabel('Service frequency (trips per hour)')
ax.set_ylabel('Trip efficiency (kg/min)')
ax.set_ylim([20,38])
plt.savefig('sfVStripEff1.jpg')
