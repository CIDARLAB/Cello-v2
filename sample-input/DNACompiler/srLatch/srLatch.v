/* 
 SR Latch
*/
module sr_latch
(
 s,
 r,
 q,
 qnot
 );

   input s;
   input r;

   output q;
   output qnot;

   nor(q,r,qnot);
   nor(qnot,s,q);
 
endmodule // sr_latch
