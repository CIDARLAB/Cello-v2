/* 
 Xor gate
*/
module xor_gate
(
 a,
 b,
 out
 );

   input a;
   input b;

   output out;

   assign out = a ^ b;
 
endmodule // xor_gate
