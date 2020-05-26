module xA1(input in1, in2, in3, output out);

   wire r, s, t, u, v, w, x, y, z;

   nor (v, w, u);
   not (y, in2);
   not (s, z);
   nor (t, y, x);
   not (w, in3);
   nor (z, in1, in3);
   not (u, t);
   not (r, s);
   not (x, in1);
   or (out, r, v);

endmodule
