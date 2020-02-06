import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
import numpy as np

fig, ax = plt.subplots(figsize=(2.5,2.5))

plt.xlim(##NONCE##21##XMIN##NONCE##21##, ##NONCE##21##XMAX##NONCE##21##)
plt.ylim(##NONCE##21##YMIN##NONCE##21##, ##NONCE##21##YMAX##NONCE##21##)

x = np.array([##NONCE##21##XDATA##NONCE##21##])
y = np.array([##NONCE##21##YDATA##NONCE##21##])
c = "##NONCE##21##COLOR##NONCE##21##"

hi_x = np.array([##NONCE##21##HIX##NONCE##21##])
hi_y = np.array([##NONCE##21##HIY##NONCE##21##])
lo_x = np.array([##NONCE##21##LOX##NONCE##21##])
lo_y = np.array([##NONCE##21##LOY##NONCE##21##])

plt.loglog(x,y,lw=3,color=c)
plt.scatter(hi_x,hi_y,marker='o',s=50,color='black',zorder=10)
plt.scatter(lo_x,lo_y,marker='o',s=50,edgecolors='black',color='none',zorder=10)

ax.xaxis.set_major_locator(ticker.LogLocator(numticks=3))
ax.yaxis.set_major_locator(ticker.LogLocator(numticks=3))

ax.set_aspect('equal')
plt.tight_layout()

plt.savefig("##NONCE##21##OUTPUTFILE##NONCE##21##", bbox_inches='tight')
