"""
Generates Verilog for all 3-input 1-output circuits (except 00 and FF)
with the Wolfram naming scheme. You probably don't need to run this.
"""

__author__ = 'Timothy S. Jones <jonests@bu.edu>, Densmore Lab, BU'
__license__ = 'GPL3'

import os

verilog = """module m0x{name}(output out, input in1, in2, in3);

   always @(in1, in2, in3)
     begin
        case({{in1, in2, in3}})
          3'b000: {{out}} = 1'b{b[0]};
          3'b001: {{out}} = 1'b{b[1]};
          3'b010: {{out}} = 1'b{b[2]};
          3'b011: {{out}} = 1'b{b[3]};
          3'b100: {{out}} = 1'b{b[4]};
          3'b101: {{out}} = 1'b{b[5]};
          3'b110: {{out}} = 1'b{b[6]};
          3'b111: {{out}} = 1'b{b[7]};
        endcase // case ({{in1, in2, in3}})
     end // always @ (in1, in2, in3)

endmodule // m0x{name}"""

for i in range(1, 255):
    name = "{:02X}".format(i)
    if not os.path.isdir(name):
        os.mkdir(name)
    with open(os.path.join(name, f"0x{name}.v"), 'w') as fp:
        fp.write(verilog.format(name=name, b=list(f"{i:08b}")))
