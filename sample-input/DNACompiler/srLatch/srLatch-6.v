/*
 SR Latch
 */

module sr_latch_6 (S_not,R_not,Q,Q_not);

   input      S_not, R_not;

   output     Q, Q_not;

   wire       S, R;
   
   reg        Qp, Qp_not;

   not (S,S_not);
   not (R,R_not);
   
   nor (Qp,R,Qp_not);
   nor (Qp_not,S,Qp);

   not (Q,Qp_not);
   not (Q_not,Qp);

endmodule // sr_latch_6
