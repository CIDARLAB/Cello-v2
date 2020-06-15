import matplotlib.pyplot as plt
import numpy as np

num_plots = ##NONCE##21##NUM_PLOTS##NONCE##21##

fig, ax = plt.subplots(num_plots, 1, sharex=True, sharey=True)
fig.set_size_inches(4, 1*num_plots)

fig.suptitle("##NONCE##21##TITLE##NONCE##21##")

for a in ax:
    a.set_xscale('log')
    a.set_yscale('log')
    a.set_xlim(##NONCE##21##XMIN##NONCE##21##, ##NONCE##21##XMAX##NONCE##21##)

##NONCE##21##PLOTS##NONCE##21##

plt.savefig("##NONCE##21##OUTPUTFILE##NONCE##21##", bbox_inches='tight')
