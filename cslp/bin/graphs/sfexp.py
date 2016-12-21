import matplotlib.pyplot as plt
import numpy as np
import math

sf = []
thr0 = 0.4
thr1 = 0.5
thr2 = 0.55
thr3 = 0.6
thr4 = 0.65
thr5 = 0.75
thr6 = 0.8
thr = [thr0,thr1,thr2,thr3,thr4,thr5,thr6]
N = 10

avg_trip_dur0 = []
avg_trip_dur1 = []
avg_trip_dur2 = []
avg_trip_dur3 = []
avg_trip_dur4 = []
avg_trip_dur5 = []
avg_trip_dur6 = []

avg_no_trips0 = []
avg_no_trips1 = []
avg_no_trips2 = []
avg_no_trips3 = []
avg_no_trips4 = []
avg_no_trips5 = []
avg_no_trips6 = []

trip_eff0 = []
trip_eff1 = []
trip_eff2 = []
trip_eff3 = []
trip_eff4 = []
trip_eff5 = []
trip_eff6 = []

avg_vol_col0 = []
avg_vol_col1 = []
avg_vol_col2 = []
avg_vol_col3 = []
avg_vol_col4 = []
avg_vol_col5 = []
avg_vol_col6 = []

per_overflow0 = []
per_overflow1 = []
per_overflow2 = []
per_overflow3 = []
per_overflow4 = []
per_overflow5 = []
per_overflow6 = []

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


f = open('../output_files/sa{}sf.txt'.format(N))

for line in f:
    tokens = line.strip().split()

    if line.startswith('Experiment'):
            sf.append(tokens[3])

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

    elif line.startswith('area 1:'):
        if 'average trip duration' in line:
            avg_trip_dur1.append(tokens[5])
        elif 'average no. trips' in line:
            avg_no_trips1.append(tokens[5])
        elif 'trip efficiency' in line:
            trip_eff1.append(tokens[4])
        elif 'average volume collected' in line:
            avg_vol_col1.append(tokens[5])
        elif 'percentage of bins overflowed' in line:
            per_overflow1.append(tokens[6])

    elif line.startswith('area 2:'):
        if 'average trip duration' in line:
            avg_trip_dur2.append(tokens[5])
        elif 'average no. trips' in line:
            avg_no_trips2.append(tokens[5])
        elif 'trip efficiency' in line:
            trip_eff2.append(tokens[4])
        elif 'average volume collected' in line:
            avg_vol_col2.append(tokens[5])
        elif 'percentage of bins overflowed' in line:
            per_overflow2.append(tokens[6])

    elif line.startswith('area 3:'):
        if 'average trip duration' in line:
            avg_trip_dur3.append(tokens[5])
        elif 'average no. trips' in line:
            avg_no_trips3.append(tokens[5])
        elif 'trip efficiency' in line:
            trip_eff3.append(tokens[4])
        elif 'average volume collected' in line:
            avg_vol_col3.append(tokens[5])
        elif 'percentage of bins overflowed' in line:
            per_overflow3.append(tokens[6])

    elif line.startswith('area 4:'):
        if 'average trip duration' in line:
            avg_trip_dur4.append(tokens[5])
        elif 'average no. trips' in line:
            avg_no_trips4.append(tokens[5])
        elif 'trip efficiency' in line:
            trip_eff4.append(tokens[4])
        elif 'average volume collected' in line:
            avg_vol_col4.append(tokens[5])
        elif 'percentage of bins overflowed' in line:
            per_overflow4.append(tokens[6])

    elif line.startswith('area 5:'):
        if 'average trip duration' in line:
            avg_trip_dur5.append(tokens[5])
        elif 'average no. trips' in line:
            avg_no_trips5.append(tokens[5])
        elif 'trip efficiency' in line:
            trip_eff5.append(tokens[4])
        elif 'average volume collected' in line:
            avg_vol_col5.append(tokens[5])
        elif 'percentage of bins overflowed' in line:
            per_overflow5.append(tokens[6])

    elif line.startswith('area 6:'):
        if 'average trip duration' in line:
            avg_trip_dur6.append(tokens[5])
        elif 'average no. trips' in line:
            avg_no_trips6.append(tokens[5])
        elif 'trip efficiency' in line:
            trip_eff6.append(tokens[4])
        elif 'average volume collected' in line:
            avg_vol_col6.append(tokens[5])
        elif 'percentage of bins overflowed' in line:
            per_overflow6.append(tokens[6])

avg_trip_dur0 = convert_trip_dur(avg_trip_dur0)
avg_trip_dur1 = convert_trip_dur(avg_trip_dur1)
avg_trip_dur2 = convert_trip_dur(avg_trip_dur2)
avg_trip_dur3 = convert_trip_dur(avg_trip_dur3)
avg_trip_dur4 = convert_trip_dur(avg_trip_dur4)
avg_trip_dur5 = convert_trip_dur(avg_trip_dur5)
avg_trip_dur6 = convert_trip_dur(avg_trip_dur6)

area0 = {'avg_trip_dur':avg_trip_dur0, 'avg_no_trips':avg_no_trips0,
         'trip_eff':trip_eff0, 'avg_vol_col':avg_vol_col0,
         'per_overflow':per_overflow0}
area1 = {'avg_trip_dur':avg_trip_dur1, 'avg_no_trips':avg_no_trips1,
         'trip_eff':trip_eff1, 'avg_vol_col':avg_vol_col1,
         'per_overflow':per_overflow1}
area2 = {'avg_trip_dur':avg_trip_dur2, 'avg_no_trips':avg_no_trips2,
         'trip_eff':trip_eff2, 'avg_vol_col':avg_vol_col2,
         'per_overflow':per_overflow2}
area3 = {'avg_trip_dur':avg_trip_dur3, 'avg_no_trips':avg_no_trips3,
         'trip_eff':trip_eff3, 'avg_vol_col':avg_vol_col3,
         'per_overflow':per_overflow3}
area4 = {'avg_trip_dur':avg_trip_dur4, 'avg_no_trips':avg_no_trips4,
         'trip_eff':trip_eff4, 'avg_vol_col':avg_vol_col4,
         'per_overflow':per_overflow4}
area5 = {'avg_trip_dur':avg_trip_dur5, 'avg_no_trips':avg_no_trips5,
         'trip_eff':trip_eff5, 'avg_vol_col':avg_vol_col5,
         'per_overflow':per_overflow5}
area6 = {'avg_trip_dur':avg_trip_dur6, 'avg_no_trips':avg_no_trips6,
         'trip_eff':trip_eff6, 'avg_vol_col':avg_vol_col6,
         'per_overflow':per_overflow6}

print(sf)
print(trip_eff0)

fig = plt.figure()
ax = plt.subplot(111)
ax.plot(sf,avg_trip_dur0,'b',marker='x',label='thr={}'.format(thr0))
# ax.plot(sf,avg_trip_dur1,'g',marker='x',label='thr={}'.format(thr1))
ax.plot(sf,avg_trip_dur2,'r',marker='x',label='thr={}'.format(thr2))
ax.plot(sf,avg_trip_dur3,'c',marker='x',label='thr={}'.format(thr3))
# ax.plot(sf,avg_trip_dur4,'m',marker='x',label='thr={}'.format(thr4))
# ax.plot(sf,avg_trip_dur5,'y',marker='x',label='thr={}'.format(thr5))
ax.plot(sf,avg_trip_dur6,'k',marker='x',label='thr={}'.format(thr6))
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs average trip duration \nfor different threshold values, noBins={}'.format(N))
ax.set_xlabel('Service frequency (trips per hour)')
ax.set_ylabel('average trip duration (seconds)')
plt.savefig('sfVSthr{}tripDur.jpg'.format(N))

fig = plt.figure()
ax = plt.subplot(111)
ax.plot(sf,avg_no_trips0,'b',marker='x',label='thr={}'.format(thr0))
# ax.plot(sf,avg_no_trips1,'g',marker='x',label='thr={}'.format(thr1))
# ax.plot(sf,avg_no_trips2,'r',marker='x',label='thr={}'.format(thr2))
# ax.plot(sf,avg_no_trips3,'c',marker='x',label='thr={}'.format(thr3))
# ax.plot(sf,avg_no_trips4,'m',marker='x',label='thr={}'.format(thr4))
# ax.plot(sf,avg_no_trips5,'y',marker='x',label='thr={}'.format(thr5))
ax.plot(sf,avg_no_trips6,'k',marker='x',label='thr={}'.format(thr6))
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs average no. of trips \nfor different threshold values, noBins={}'.format(N))
ax.set_xlabel('Service frequency (trips per hour)')
ax.set_ylabel('average no. of trips')
plt.savefig('sfVSthr{}noTrips.jpg'.format(N))


fig = plt.figure()
ax = plt.subplot(111)
ax.plot(sf,per_overflow0,'b',marker='x',label='thr={}'.format(thr0))
# ax.plot(sf,per_overflow1,'g',marker='x',label='thr={}'.format(thr1))
ax.plot(sf,per_overflow2,'r',marker='x',label='thr={}'.format(thr2))
ax.plot(sf,per_overflow3,'c',marker='x',label='thr={}'.format(thr3))
# ax.plot(sf,per_overflow4,'m',marker='x',label='thr={}'.format(thr4))
# ax.plot(sf,per_overflow5,'y',marker='x',label='thr={}'.format(thr5))
ax.plot(sf,per_overflow6,'k',marker='x',label='thr={}'.format(thr6))
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs percentage overflow \nfor different threshold values, noBins={}'.format(N))
ax.set_xlabel('Service frequency (trips per hour)')
ax.set_ylabel('percentage overflow')
ax.set_xlim([0,0.15])
plt.savefig('sfVSthr{}overflow.jpg'.format(N))

fig = plt.figure()
ax = plt.subplot(111)
ax.plot(sf,trip_eff0,'b',marker='x',label='thr={}'.format(thr0))
# ax.plot(sf,trip_eff1,'g',marker='x',label='thr={}'.format(thr1))
ax.plot(sf,trip_eff2,'r',marker='x',label='thr={}'.format(thr2))
ax.plot(sf,trip_eff3,'c',marker='x',label='thr={}'.format(thr3))
# ax.plot(sf,trip_eff4,'m',marker='x',label='thr={}'.format(thr4))
# ax.plot(sf,trip_eff5,'y',marker='x',label='thr={}'.format(thr5))
ax.plot(sf,trip_eff6,'k',marker='x',label='thr={}'.format(thr6))
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs trip efficiency \nfor different threshold values, noBins={}'.format(N))
ax.set_xlabel('Service frequency (trips per hour)')
ax.set_ylabel('trip efficiency (kg/min)')
plt.savefig('sfVSthr{}tripEff.jpg'.format(N))

fig = plt.figure()
ax = plt.subplot(111)
ax.plot(sf,avg_vol_col0,'b',marker='x',label='thr={}'.format(thr0))
# ax.plot(sf,avg_vol_col1,'g',marker='x',label='thr={}'.format(thr1))
ax.plot(sf,avg_vol_col2,'r',marker='x',label='thr={}'.format(thr2))
ax.plot(sf,avg_vol_col3,'c',marker='x',label='thr={}'.format(thr3))
# ax.plot(sf,avg_vol_col4,'m',marker='x',label='thr={}'.format(thr4))
# ax.plot(sf,avg_vol_col5,'y',marker='x',label='thr={}'.format(thr5))
ax.plot(sf,avg_vol_col6,'k',marker='x',label='thr={}'.format(thr6))
box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs average volume collected \nfor different threshold values, noBins={}'.format(N))
ax.set_xlabel('Service frequency (trips per hour)')
ax.set_ylabel('average volume collected (kg)')
plt.savefig('sfVSthr{}volCol.jpg'.format(N))


print()
notripsstr = '['
for i in range(len(sf)):
    if i == len(sf)-1:
        notripsstr += '{}]'.format(sf[i])
    else:
        notripsstr += '{}, '.format(sf[i])
print(notripsstr)
print()

print()
notripsstr = '['
for i in range(len(avg_no_trips6)):
    if i == len(avg_no_trips0)-1:
        notripsstr += '{}]'.format(trip_eff6[i])
    else:
        notripsstr += '{}, '.format(trip_eff6[i])
print(notripsstr)
print()
