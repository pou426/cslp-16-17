import matplotlib.pyplot as plt
import numpy as np
import math

sf = []
ddr = []
thr = 0.4
# thr0 = 0.4
# thr1 = 0.5
# thr2 = 0.55
# thr3 = 0.6
# thr4 = 0.65
# thr5 = 0.75
# thr6 = 0.8
# thr = [thr0,thr1,thr2,thr3,thr4,thr5,thr6]
ddr_org = [5,8,10,12,15]
sf_org = [0.08,0.12,0.125,0.15]
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

f = open('../output_files/ddrVSsf_sa{}.txt'.format(N))

for line in f:
    tokens = line.strip().split()

    if line.startswith('Experiment'):
        ddr.append(tokens[3])
        sf.append(tokens[5])

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
print(area0.get('per_overflow'))
print(area1.get('per_overflow'))
print(area2.get('per_overflow'))
print(area3.get('per_overflow'))
print(area5.get('per_overflow'))
print(area6.get('per_overflow'))

c=['b','g','r','c','m','y','k']
idx = 0
fig = plt.figure()
ax = plt.subplot(111)
for i in range(len(ddr_org)):
    x=[]
    y=[]
    for j in range(len(sf_org)):
        x.append(sf[idx])
        y.append(per_overflow0[idx])
        idx+=1
    ax.plot(x,y,c=c[i],marker='x',label='ddr={}'.format(ddr_org[i]))

box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs percentage overflow \nfor different ddr values, thr={}, noBins={}'.format(thr,N))
ax.set_xlabel('Service frequency')
ax.set_ylabel('percentage overflow')
plt.savefig('sfVSddr{}(per overflow).jpg'.format(N))

idx = 0
fig = plt.figure()
ax = plt.subplot(111)
for i in range(len(ddr_org)):
    x=[]
    y=[]
    for j in range(len(sf_org)):
        x.append(sf[idx])
        y.append(trip_eff0[idx])
        idx+=1
    ax.plot(x,y,c=c[i],marker='x',label='ddr={}'.format(ddr_org[i]))

box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs trip efficiency \nfor different ddr values, thr={}, noBins={}'.format(thr,N))
ax.set_xlabel('Service frequency')
ax.set_ylabel('trip efficiency')
plt.savefig('sfVSddr{}(trip efficiency).jpg'.format(N))

idx = 0
fig = plt.figure()
ax = plt.subplot(111)
for i in range(len(sf_org)):
    x=[]
    y=[]
    for j in range(len(ddr_org)):
        x.append(sf[idx])
        y.append(avg_vol_col0[idx])
        idx+=1
    ax.plot(x,y,c=c[i],marker='x',label='ddr={}'.format(ddr_org[i]))

box = ax.get_position()
ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.title('Service frequency vs average volume collected \nfor different ddr values, thr={}, noBins={}'.format(thr,N))
ax.set_xlabel('Service frequency')
ax.set_ylabel('Average volume collected')
plt.savefig('sfVSddr{}(Average volume collected).jpg'.format(N))

#
#
# #
# fig = plt.figure()
# ax = plt.subplot(111)
# ax.plot(sf,per_overflow0,'b',marker='x',label='thr={}'.format(thr0))
# # ax.plot(sf,per_overflow1,'g',marker='x',label='thr={}'.format(thr1))
# # ax.plot(sf,per_overflow2,'r',marker='x',label='thr={}'.format(thr2))
# # ax.plot(sf,per_overflow3,'c',marker='x',label='thr={}'.format(thr3))
# # ax.plot(sf,per_overflow4,'m',marker='x',label='thr={}'.format(thr4))
# # ax.plot(sf,per_overflow5,'y',marker='x',label='thr={}'.format(thr5))
# # ax.plot(sf,per_overflow6,'k',marker='x',label='thr={}'.format(thr6))
# box = ax.get_position()
# ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
# ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
# plt.title('Service frequency vs percentage overflow \nfor different threshold values, noBins={}'.format(N))
# ax.set_xlabel('Service frequency')
# ax.set_ylabel('percentage overflow')
# plt.savefig('sfVSthr{}(per overflow).jpg'.format(N))
#
# fig = plt.figure()
# ax = plt.subplot(111)
# ax.plot(sf,trip_eff0,'b',marker='x',label='thr={}'.format(thr0))
# # ax.plot(sf,trip_eff1,'g',marker='x',label='thr={}'.format(thr1))
# # ax.plot(sf,trip_eff2,'r',marker='x',label='thr={}'.format(thr2))
# # ax.plot(sf,trip_eff3,'c',marker='x',label='thr={}'.format(thr3))
# # ax.plot(sf,trip_eff4,'m',marker='x',label='thr={}'.format(thr4))
# # ax.plot(sf,trip_eff5,'y',marker='x',label='thr={}'.format(thr5))
# # ax.plot(sf,trip_eff6,'k',marker='x',label='thr={}'.format(thr6))
# box = ax.get_position()
# ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
# ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
# plt.title('Service frequency vs trip effiency \nfor different threshold values, noBins={}'.format(N))
# ax.set_xlabel('Service frequency')
# ax.set_ylabel('trip efficiency')
# plt.savefig('sfVSthr{}(trip efficiency).jpg'.format(N))
#
# fig = plt.figure()
# ax = plt.subplot(111)
# ax.plot(sf,avg_vol_col0,'b',marker='x',label='thr={}'.format(thr0))
# # ax.plot(sf,avg_vol_col1,'g',marker='x',label='thr={}'.format(thr1))
# # ax.plot(sf,avg_vol_col2,'r',marker='x',label='thr={}'.format(thr2))
# # ax.plot(sf,avg_vol_col3,'c',marker='x',label='thr={}'.format(thr3))
# # ax.plot(sf,avg_vol_col4,'m',marker='x',label='thr={}'.format(thr4))
# # ax.plot(sf,avg_vol_col5,'y',marker='x',label='thr={}'.format(thr5))
# # ax.plot(sf,avg_vol_col6,'k',marker='x',label='thr={}'.format(thr6))
# box = ax.get_position()
# ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])
# ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))
# plt.title('Service frequency vs average volume collected \nfor different threshold values, noBins={}'.format(N))
# ax.set_xlabel('Service frequency')
# ax.set_ylabel('average volume collected')
# plt.savefig('sfVSthr{}(vol collected).jpg'.format(N))
