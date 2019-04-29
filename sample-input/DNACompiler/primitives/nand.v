/* 
 Nand gate
*/
module nand_gate
(
 a,
 b,
 out
 );

   input a;
   input b;

   output out;

   assign out = ~(a & b);
 
endmodule // nand_gate
