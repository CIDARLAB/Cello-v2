/*
 SR Latch
 */

module sr_latch (S,R,Q,Q_not);

   input   S, R;
   output  Q, Q_not;

   nor (Q,Q_not,R);
   nor (Q_not,Q,S);
   
endmodule // sr_latch
