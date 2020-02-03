import numpy as np
import matplotlib.pyplot as plt
from matplotlib.ticker import FixedLocator, NullLocator
import argparse
import json
import csv
import dnaplotlib as dpl
import logging
import re

import pycello2.netlist
import pycello2.dnaplotlib
import pycello2.ucf
import pycello2.utils

__author__ = 'Timothy S. Jones <jonests@bu.edu>, Densmore Lab, BU'
__license__ = 'GPL3'


BASAL_TRANSCRIPTION = 1e-6
TOLERANCE = 1e-5


def get_node_logic(node, logic):
    for row in logic:
        if row[0] == node.name:
            return row[1:]


def get_node_activity(node, activity):
    for row in activity:
        if row[0] == node.name:
            return row[1:]


def main():
    parser = argparse.ArgumentParser(
        description="Plot RNAseq profile from predicted RPU."
    )
    parser.add_argument("--ucf", "-u",
                        required=True, help="UCF file.", metavar="FILE")
    parser.add_argument("--input-sensors", "-i", dest="sensors",
                        required=True, help="Input sensors file.", metavar="FILE")
    parser.add_argument("--output-devices", "-x", dest="outputs",
                        required=True, help="Output reporters file.", metavar="FILE")
    parser.add_argument("--activity-table", "-a", dest="activity",
                        required=True, help="Activity table.", metavar="FILE")
    parser.add_argument("--logic-table", "-l", dest="logic",
                        required=True, help="Logic table.", metavar="FILE")
    parser.add_argument("--netlist", "-n",
                        required=True, help="Netlist.", metavar="FILE")
    parser.add_argument("--output", "-o",
                        required=False, help="Output file.", metavar="FILE")
    parser.add_argument("--debug", "-d",
                        required=False, help="Debug.", action='store_true')
    args = parser.parse_args()

    level = logging.DEBUG if args.debug else logging.INFO
    logging.basicConfig(format='%(levelname)s:%(message)s', level=level)

    activity = []
    logic = []
    with open(args.ucf, 'r') as ucf_file:
        ucf = json.load(ucf_file)
    with open(args.sensors, 'r') as sensors_file:
        sensors = json.load(sensors_file)
    with open(args.outputs, 'r') as outputs_file:
        outputs = json.load(outputs_file)
    for item in sensors:
        ucf.append(item)
    for item in outputs:
        ucf.append(item)
    ucf = pycello2.ucf.UCF(ucf)
    with open(args.activity, 'r') as activity_file:
        activity_reader = csv.reader(activity_file)
        for row in activity_reader:
            activity.append(row)
    with open(args.logic, 'r') as logic_file:
        logic_reader = csv.reader(logic_file)
        for row in logic_reader:
            logic.append(row)
    with open(args.netlist, 'r') as netlist_file:
        text = netlist_file.read();
        text = re.sub(r"(\")\s*,\s*(})", r"\1\2", text)
        text = re.sub(r"(\")\s*,\s*(])", r"\1\2", text)
        text = re.sub(r"(})\s*,\s*(])", r"\1\2", text)
        text = re.sub(r"(])\s*,\s*(])", r"\1\2", text)
        text = re.sub(r"(})\s*,\s*(})", r"\1\2", text)
        text = re.sub(r"(])\s*,\s*(})", r"\1\2", text)
        text = re.sub(r",\s*$", r"", text)

        netlist = pycello2.netlist.Netlist(json.loads(text), ucf)

    # dnaplotlib specifications
    designs = pycello2.dnaplotlib.get_designs(netlist)

    # placement = netlist.placements[0]
    for placement_num, placement in enumerate(netlist.placements):

        skip = []
        for i, group in enumerate(placement.groups):
            f = True
            for component in group.components:
                if component.node.type == "PRIMARY_INPUT" or component.node.type == "PRIMARY_OUTPUT":
                    f = False
                    skip.append(i)
                    break

        num_plots = len(logic[0])

        widths = [len(group.sequence) for i, group in enumerate(placement.groups) if i not in skip]

        fig = plt.figure(figsize=(np.sum(widths)/650, num_plots))
        gs = fig.add_gridspec(
            num_plots, len(placement.groups) - len(skip), width_ratios=widths
        )

        locator = FixedLocator((1e-5, 1e-3, 1e-1, 1e1, 1e3))

        axes = []

        for row in range(num_plots - 1):
            axes_row = []
            axes.append(axes_row)
            col = 0
            for j, group in enumerate(placement.groups):
                if j in skip:
                    continue
                sharex = axes[row - 1][col] if row > 0 else None
                sharey = axes[0][0] if (row != 0 or col != 0) else None
                ax = fig.add_subplot(gs[row, col], sharex=sharex, sharey=sharey)
                axes_row.append(ax)

                profile = []
                temp = []
                for component in group.components:
                    for part in component.parts:
                        temp.append(1.0)
                        profile.append(100.0)

                iteration = 0
                while (np.max(np.abs(np.array(profile) - np.array(temp))) > TOLERANCE):
                    format_str = "state: {:>4d}  group: {:>4d}  iteration: {:>4d}"
                    logging.debug(format_str.format(row, col, iteration))
                    temp = profile.copy()
                    profile = []
                    for i, component in enumerate(group.components):
                        for j, part_instance in enumerate(component.parts):
                            # offset to which we add the flux
                            # (readthrough, upstream promoter flux)
                            if j == 0 and i > 0:
                                offset = group.components[i-1].parts[-1].flux
                            elif j > 0:
                                offset = component.parts[j-1].flux
                            else:
                                offset = 0.0

                            if part_instance.part.type == 'promoter':
                                upstream_node = pycello2.utils.get_upstream_node(part_instance.part, component.node, netlist)
                                if upstream_node.type == 'PRIMARY_INPUT':
                                    delta_flux = float(get_node_activity(upstream_node, activity)[row])
                                else:
                                    upstream_components = pycello2.utils.get_components(upstream_node, placement)
                                    input_flux = 0.0
                                    for upstream_component in upstream_components:
                                        input_flux += upstream_component.parts[-2].flux
                                    delta_flux = pycello2.utils.evaluate_equation(upstream_node.gate, {'x': input_flux})
                                part_instance.flux = pycello2.utils.get_ribozyme(component).efficiency * delta_flux + offset
                            if part_instance.part.type == 'ribozyme':
                                part_instance.flux = offset / pycello2.utils.get_ribozyme(component).efficiency
                            if part_instance.part.type in ('cds', 'rbs'):
                                part_instance.flux = offset
                            if part_instance.part.type == 'terminator':
                                part_instance.flux = offset / part_instance.part.strength

                            profile.append(part_instance.flux)

                    iteration += 1

                col += 1

                ax.set_yscale('log')
                ax.xaxis.set_major_locator(NullLocator())
                ax.yaxis.set_major_locator(locator)
                if col > 0:
                    plt.setp(ax.get_yticklabels(), visible=False)

                x = []
                y = []
                last_x = 0
                last_y = BASAL_TRANSCRIPTION

                for i, component in enumerate(group.components):
                    x.append([])
                    y.append([])

                    for j, part_instance in enumerate(component.parts):
                        if j == 0:
                            initial_x = last_x
                            initial_y = last_y
                        else:
                            initial_x = x[-1][-1]
                            initial_y = y[-1][-1]

                        if part_instance.part.type == 'terminator':
                            x[-1].append(initial_x)
                            x[-1].append(initial_x + int(0.5*len(part_instance.part. sequence)))
                            x[-1].append(initial_x + int(0.5*len(part_instance.part. sequence)))
                            x[-1].append(initial_x + len(part_instance.part.sequence))
                            y[-1].append(initial_y)
                            y[-1].append(initial_y)
                            y[-1].append(part_instance.flux)
                            y[-1].append(part_instance.flux)
                        elif part_instance.part.type == 'promoter':
                            x[-1].append(initial_x)
                            x[-1].append(initial_x + len(part_instance.part.sequence))
                            x[-1].append(initial_x + len(part_instance.part.sequence))
                            y[-1].append(initial_y)
                            y[-1].append(initial_y)
                            y[-1].append(part_instance.flux)
                        elif part_instance.part.type == 'ribozyme':
                            x[-1].append(initial_x)
                            x[-1].append(initial_x + 7)
                            x[-1].append(initial_x + 7)
                            x[-1].append(initial_x + len(part_instance.part.sequence))
                            y[-1].append(initial_y)
                            y[-1].append(initial_y)
                            y[-1].append(part_instance.flux)
                            y[-1].append(part_instance.flux)
                        else:
                            x[-1].append(initial_x)
                            x[-1].append(initial_x + len(part_instance.part.sequence))
                            y[-1].append(part_instance.flux)
                            y[-1].append(part_instance.flux)

                    if i == 0:
                        pre_x = []
                        pre_y = []
                    else:
                        pre_x = [last_x, ]
                        pre_y = [last_y, ]

                    x[-1] = pre_x + x[-1]
                    y[-1] = pre_y + y[-1]

                    last_x = x[-1][-1]
                    last_y = y[-1][-1]

                for i, component in enumerate(group.components):
                    this_x = np.array(x[i], dtype=np.float64)
                    this_y = np.array(y[i], dtype=np.float64)
                    ax.plot(this_x, this_y, '-', color='black', lw=1)
                    ax.fill_between(this_x, this_y, 1e-10, fc='black', alpha=0.1)

                ax.set_xlim(x[0][0], x[-1][-1])
                ax.set_ylim(1e-4, 1e2)

        j = 0
        for i, group in enumerate(placement.groups):
            if i in skip:
                continue
            ax_dna = fig.add_subplot(gs[-1, j])
            design = designs[placement_num][i]
            for part in design:
                if part['type'] == 'Promoter':
                    part['opts']['x_extent'] = np.sum(widths)/30
                if part['type'] == 'Terminator':
                    part['opts']['x_extent'] = np.sum(widths)/100
                if part['type'] == 'CDS':
                    part['opts']['arrowhead_length'] = np.sum(widths)/50
                    part['opts']['y_extent'] = 1

            dr = dpl.DNARenderer()
            start, end = dr.renderDNA(ax_dna, design, dr.trace_part_renderers())
            ax_dna.set_xlim([start, end])
            ax_dna.set_ylim([-5, 10])
            ax_dna.set_aspect('auto')
            ax_dna.axis('off')
            j += 1

        label_x = 0.02
        label_y = (1.0 + (1.0/num_plots))/2.0
        fig.text(
            label_x, label_y, "predicted transcription (RPU)",
            va='center', ha='center', rotation='vertical'
        )

        if (args.output):
            out_file = args.output
        else:
            out_file = 'out'

        if (len(netlist.placements) > 1):
            num = "{:02d}".format(placement_num)
        else:
            num = ""

        plt.tight_layout(rect=[0.025, 0., 1., 1.])
        plt.subplots_adjust(hspace=0.0, wspace=0.1)
        plt.savefig(out_file + num + '.png', bbox_to_inches='tight')


if __name__ == "__main__":
    main()
