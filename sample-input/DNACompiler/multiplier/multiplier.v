module half_adder(a,b,carry,sum);
   input a, b;
   output carry, sum;

   and(carry,a,b);
   xor(sum,a,b);
endmodule // half_adder

module multiplier(a0,a1,b0,b1,o0,o1,o2,o3);
   input a0, a1;
   input b0, b1;
   output o0, o1, o2, o3;
   wire   a0b1, a1b0, a1b1, carry;

   and(o0,a0,b0);
   and(a0b1,a0,b1);
   and(a1b0,a1,b0);
   and(a1b1,a1,b1);

   half_adder ha0(a0b1,a1b0,carry,o1);
   half_adder ha1(carry,a1b1,o3,o2);
endmodule // multiplier
