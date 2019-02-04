/*
 SR Latch
 */

module sr_latch_5 (Sa,Ra,Sb,Rb,Y);

   input   Sa, Ra;
   input   Sb, Rb;

   reg     Qa, Qa_not;
   reg     Qb, Qb_not;

   output  Y;

   nor (Qa,Ra,Qa_not);
   nor (Qa_not,Sa,Qa);

   nor (Qb,Rb,Qb_not);
   nor (Qb_not,Sb,Qb);

   nor (Y,Qa,Qb);
   
endmodule // sr_latch_5
