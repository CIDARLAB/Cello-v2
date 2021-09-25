module m0xA6(output out, input in1, in2, in3);

   always @(in1, in2, in3)
     begin
        case({in1, in2, in3})
          3'b000: {out} = 1'b1;
          3'b001: {out} = 1'b0;
          3'b010: {out} = 1'b1;
          3'b011: {out} = 1'b0;
          3'b100: {out} = 1'b0;
          3'b101: {out} = 1'b1;
          3'b110: {out} = 1'b1;
          3'b111: {out} = 1'b0;
        endcase // case ({in1, in2, in3})
     end // always @ (in1, in2, in3)

endmodule // m0xA6