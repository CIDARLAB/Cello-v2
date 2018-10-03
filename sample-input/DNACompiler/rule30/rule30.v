/*
 * Rule30 circuit
 */
module rule30(
	      a,
	      b,
	      c,
	      out
	      );

   input a;
   input b;
   input c;

   output out;
   
   always@(a,b,c)
     begin
	case({a,b,c})
	  3'b000: out = 1'b0;
	  3'b001: out = 1'b1;
          3'b010: out = 1'b1;
          3'b011: out = 1'b1;
          3'b100: out = 1'b1;
          3'b101: out = 1'b0;
          3'b110: out = 1'b0;
          3'b111: out = 1'b0;
          default: out = 1'b0;
	endcase // case ({a,b,c})
     end // always@ (a,b,c)
endmodule // rule30
