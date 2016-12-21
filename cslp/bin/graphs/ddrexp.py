import matplotlib.pyplot as plt
import numpy as np
import math

sf = []
ddr = []
dds = []
thr = 8
s = 1
sflen = 14

avg_trip_dur0 = []

avg_no_trips0 = []

trip_eff0 = []

avg_vol_col0 = []

per_overflow0 = []

def convert(str):
    time = 0
    tokens = str.split(':')
    ss = int(tokens[3])
    hh = int(tokens[1])
    mm = int(tokens[2])
    dd = int(tokens[0])
    time = dd*24*60*60 + hh*60*60 + mm*60 +ss
    # in hours
    # time = time/(60*60)
    # in seconds
    return time

def convert_trip_dur(avg_trip_dur):
    avg_trip_dur_hh = []
    for i in range(len(avg_trip_dur)):
        time = convert(avg_trip_dur[i])
        avg_trip_dur_hh.append(time)
    return avg_trip_dur_hh


f = open('../output_files/erlangkThr{}dds{}.txt'.format(thr,s))

c = ['r','r','r','b','b','b','g','g','y','y','y','y']
for line in f:
    tokens = line.strip().split()

    if line.startswith('Experiment'):
        ddr.append(tokens[3])
        dds.append(tokens[5])
        sf.append(tokens[7])

    elif line.startswith('area 0:'):
        if 'average trip duration' in line:
            avg_trip_dur0.append(tokens[5])
        elif 'average no. trips' in line:
            avg_no_trips0.append(tokens[5])
        elif 'trip efficiency' in line:
            trip_eff0.append(tokens[4])
        elif 'average volume collected' in line:
            avg_vol_col0.append(tokens[5])
        elif 'percentage of bins overflowed' in line:
            per_overflow0.append(tokens[6])


avg_trip_dur0 = convert_trip_dur(avg_trip_dur0)


# print(sf)
# print(len(sf))

fig = plt.figure()
ax = plt.subplot(111)
N = len(ddr)
# print(N)
idx = 0;
while N > 0:
    curr_ddr = ddr[idx]
    curr_dds = dds[idx]
    curr_sf = []
    curr_avg_trip_dur = []
    # curr_avg_no_trips = []
    # curr_trip_eff = []
    # curr_avg_vol_col = []
    # curr_per_overflow = []
    for j in range(sflen):
        # print(idx)
        curr_sf.append(sf[idx])
        curr_avg_trip_dur.append(avg_trip_dur0[idx])
        # curr_avg_no_trips.append(avg_no_trips0[idx])
        # curr_trip_eff.append(trip_eff0[idx])
        # curr_avg_vol_col.append(avg_vol_col0[idx])
        # curr_per_overflow.append(per_overflow0[idx])
        idx += 1
        N -= 1
    cidx = int(curr_ddr[0])
    ax.plot(curr_sf,curr_avg_trip_dur,c[cidx],label='ddr={} dds={}'.format(curr_ddr,curr_dds))
    curr_sf = []
    curr_avg_trip_dur = []
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs average trip duration \nfor different erlangk distribution, noBins=30, thr=0.{}'.format(thr))
ax.set_xlabel('Service frequency (trips per hour)')
ax.set_ylabel('average trip duration (seconds)')
ax.set_xlim([0.03,0.25])
ax.set_ylim([5000,26000])
plt.savefig('ddrddsthr{}dds{}avgTripDur.jpg'.format(thr,s))





fig = plt.figure()
ax = plt.subplot(111)

N = len(ddr)
idx = 0;
while N > 0:
    curr_ddr = ddr[idx]
    curr_dds = dds[idx]
    curr_sf = []
    # curr_avg_trip_dur = []
    curr_avg_no_trips = []
    # curr_trip_eff = []
    # curr_avg_vol_col = []
    # curr_per_overflow = []
    for j in range(sflen):
        curr_sf.append(sf[idx])
        # curr_avg_trip_dur.append(avg_trip_dur0[idx])
        curr_avg_no_trips.append(avg_no_trips0[idx])
        # curr_trip_eff.append(trip_eff0[idx])
        # curr_avg_vol_col.append(avg_vol_col0[idx])
        # curr_per_overflow.append(per_overflow0[idx])
        idx += 1
        N -= 1
    cidx = int(curr_ddr[0])
    ax.plot(curr_sf,curr_avg_no_trips,c[cidx],label='ddr={} dds={}'.format(curr_ddr,curr_dds))
    curr_sf = []
    curr_avg_no_trips = []
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs average no. of trips \nfor different erlangk distribution, noBins=30, thr=0.{}'.format(thr))
ax.set_xlabel('Service frequency (trips per hour)')
ax.set_ylabel('average number of trips')
ax.set_xlim([0.03,0.25])
ax.set_ylim([-1,3])
plt.savefig('ddrddsthr{}dds{}noTrips.jpg'.format(thr,s))



fig = plt.figure()
ax = plt.subplot(111)

N = len(ddr)
idx = 0;
while N > 0:
    curr_ddr = ddr[idx]
    curr_dds = dds[idx]
    curr_sf = []
    # curr_avg_trip_dur = []
    # curr_avg_no_trips = []
    curr_trip_eff = []
    # curr_avg_vol_col = []
    # curr_per_overflow = []
    for j in range(sflen):
        curr_sf.append(sf[idx])
        # curr_avg_trip_dur.append(avg_trip_dur0[idx])
        # curr_avg_no_trips.append(avg_no_trips0[idx])
        curr_trip_eff.append(trip_eff0[idx])
        # curr_avg_vol_col.append(avg_vol_col0[idx])
        # curr_per_overflow.append(per_overflow0[idx])
        idx += 1
        N -= 1
    cidx = int(curr_ddr[0])
    ax.plot(curr_sf,curr_trip_eff,c[cidx],label='ddr={} dds={}'.format(curr_ddr,curr_dds))
    curr_sf = []
    curr_trip_eff = []
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs trip efficiency \nfor different erlangk distribution, noBins=30, thr=0.{}'.format(thr))
ax.set_xlabel('Service frequency (trips per hour)')
ax.set_ylabel('Trip efficiency (kg/min)')
ax.set_xlim([0.03,0.25])
ax.set_ylim([0,35])
plt.savefig('ddrddsthr{}dds{}tripEff.jpg'.format(thr,s))




fig = plt.figure()
ax = plt.subplot(111)

N = len(ddr)
idx = 0;
while N > 0:
    curr_ddr = ddr[idx]
    curr_dds = dds[idx]
    curr_sf = []
    # curr_avg_trip_dur = []
    # curr_avg_no_trips = []
    # curr_trip_eff = []
    curr_avg_vol_col = []
    # curr_per_overflow = []
    for j in range(sflen):
        curr_sf.append(sf[idx])
        # curr_avg_trip_dur.append(avg_trip_dur0[idx])
        # curr_avg_no_trips.append(avg_no_trips0[idx])
        # curr_trip_eff.append(trip_eff0[idx])
        curr_avg_vol_col.append(avg_vol_col0[idx])
        # curr_per_overflow.append(per_overflow0[idx])
        idx += 1
        N -= 1
    cidx = int(curr_ddr[0])
    ax.plot(curr_sf,curr_avg_vol_col,c[cidx],label='ddr={} dds={}'.format(curr_ddr,curr_dds))
    curr_sf = []
    curr_avg_vol_col = []
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs average volume collected \nfor different erlangk distribution, noBins=30, thr=0.{}'.format(thr))
ax.set_xlabel('Service frequency (trips per hour)')
ax.set_ylabel('Average volume collected (kg^3)')
ax.set_xlim([0.03,0.25])
ax.set_ylim([3, 25])
plt.savefig('ddrddsthr{}dds{}volCol.jpg'.format(thr,s))




fig = plt.figure()
ax = plt.subplot(111)

N = len(ddr)
idx = 0;
while N > 0:
    curr_ddr = ddr[idx]
    curr_dds = dds[idx]
    curr_sf = []
    # curr_avg_trip_dur = []
    # curr_avg_no_trips = []
    # curr_trip_eff = []
    # curr_avg_vol_col = []
    curr_per_overflow = []
    for j in range(sflen):
        curr_sf.append(sf[idx])
        # curr_avg_trip_dur.append(avg_trip_dur0[idx])
        # curr_avg_no_trips.append(avg_no_trips0[idx])
        # curr_trip_eff.append(trip_eff0[idx])
        # curr_avg_vol_col.append(avg_vol_col0[idx])
        curr_per_overflow.append(per_overflow0[idx])
        idx += 1
        N -= 1
    cidx = int(curr_ddr[0])
    ax.plot(curr_sf,curr_per_overflow,c[cidx],label='ddr={} dds={}'.format(curr_ddr,curr_dds))
    curr_sf = []
    curr_per_overflow = []
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs percentage overflow \nfor different erlangk distribution, noBins=30, thr=0.{}'.format(thr))
ax.set_xlabel('Service frequency (trips per hour)')
ax.set_ylabel('percentage overflow')
ax.set_xlim([0.03,0.10])
ax.set_ylim([-5,105])

plt.savefig('ddrddsthr{}dds{}overflow.jpg'.format(thr,s))
