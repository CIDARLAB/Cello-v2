/*
 T-flipflop
 */

module t_flipflop (T,Q);

   input      T;

   output reg Q;

   wire       S, R;

   reg        Q_not;

   xnor (R,Q_not,T);

   not (S,R);

   nor (Q,R,Q_not);
   nor (Q_not,S,Q);

endmodule // t_flipflop
