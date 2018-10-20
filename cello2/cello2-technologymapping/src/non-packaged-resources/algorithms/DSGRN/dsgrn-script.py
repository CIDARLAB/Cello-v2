# The MIT License (MIT)

# Copyright (c) 2018 Bree Cummins

# Permission is hereby granted, free of charge, to any person
# obtaining a copy of this software and associated documentation files
# (the "Software"), to deal in the Software without restriction,
# including without limitation the rights to use, copy, modify, merge,
# publish, distribute, sublicense, and/or sell copies of the Software,
# and to permit persons to whom the Software is furnished to do so,
# subject to the following conditions:

# The above copyright notice and this permission notice shall be
# included in all copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
# NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
# BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
# ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

import DSGRN
import graphviz
import json

def is_FP(annotation):
    return annotation.startswith("FP")

def is_FP_match(state, annotation):
    digits = [int(s) for s in annotation.replace(",", "").split() if s.isdigit()]
    return all(digits[k] >= state[k][0] and digits[k] <= state[k][1] for k in state)

network = DSGRN.Network("##NONCE##21##NETWORKFILE##21##NONCE")
graph = graphviz.Source(network.graphviz())

# write graph
graph.save(filename="##NONCE##21##DOTFILENAME##21##NONCE",directory="##NONCE##21##OUTPUTDIR##21##NONCE")
graph.format = 'pdf'
graph.render()

# FPs = [{"s1" : [2,2], "s2" : [0,0], "GFP" : [1,1]},{"s1" : [2,2], "s2" : [0,0], "GFP" : [0,0]}]
FPs = [##NONCE##21##FIXEDPOINTS##21##NONCE]
new_FPs = []
for bounds in FPs:
    new_bounds = {}
    for name,bds in bounds.items():
        new_bounds[network.index(name)]=bds
    new_FPs.append(new_bounds)
FPs = new_FPs

pg = DSGRN.ParameterGraph(network)

params = []
ineq = []
for k in range(pg.size()):
    param = pg.parameter(k)
    dg = DSGRN.DomainGraph(param)
    md = DSGRN.MorseDecomposition(dg.digraph())
    mg = DSGRN.MorseGraph(dg, md)
    stable_FP_annotations = [mg.annotation(i)[0] for i in range(0, mg.poset().size()) if is_FP(mg.annotation(i)[0]) and len(mg.poset().children(i)) == 0]
    if all(any([is_FP_match(states,a) for a in stable_FP_annotations]) for states in FPs):
        params.append(k)
        d = {}
        d['param'] = param.stringify()
        d.update(json.loads(param.inequalities()))
        ineq.append(d)

with open("##NONCE##21##OUTPUTDIR##21##NONCE/##NONCE##21##OUTPUTFILENAME##21##NONCE","w") as inequalities_file:
    json.dump(ineq,inequalities_file,indent=2)
