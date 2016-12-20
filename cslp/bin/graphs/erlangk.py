import matplotlib.pyplot as plt
import numpy as np
import math

f = open('../output_files/output.txt','r')
ddr_file = open('erlangk-ddr.txt','w')
dds_file = open('erlangk-dds.txt','w')
eff_file = open('erlangk-overall-eff.txt','w')
ddr_str = ''
dds_str = ''
eff_str = ''

mean = []
ddr_arr = []
dds_arr = []
eff_arr = []

for line in f:

    if line.startswith('Experiment #'):
        tokens = line.strip().split()
        ddr = float(tokens[3])
        dds = int(tokens[5])
        mean.append((dds/ddr))
        ddr_arr.append(ddr)
        dds_arr.append(dds)
        ddr_str += '{}\n'.format(ddr)
        dds_str += '{}\n'.format(dds)

    elif line.startswith('overall percentage of bins overflowed'):
        tokens = line.strip().split()
        eff = float(tokens[5])
        eff_arr.append(eff)
        eff_str += '{}\n'.format(eff)

ddr_file.write(ddr_str)
dds_file.write(dds_str)
eff_file.write(eff_str)


# temp_mean = np.log(mean)
plt.plot(mean, eff_arr, 'ro')
plt.title('erlang-k parameters versus overall percentage overflow')
plt.xlabel('(disposalDistrShape/disposalDistrRate)')
plt.ylabel('overall percentage overflow')
plt.savefig('erlangk-overall-percentage.jpg')

# optimal_idx = np.argmax(eff)
# optimal_ddr = ddr_arr[optimal_idx]
# optimal_dds = dds_arr[optimal_idx]
# optimal = mean[optimal_idx]
# print('optimal mean = {} optimal ddr = {} optimal dds = {}'.format(optimal, optimal_ddr, optimal_dds))
