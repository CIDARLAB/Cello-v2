/* 
Full Adder Module for bit Addition
*/
module fulladder
(
	x,
	y,
	cin,

	A, 
	cout
 );
// Width
parameter WIDTH = 1;  

input [WIDTH-1:0] x;
input [WIDTH-1:0] y;
input cin;

output cout;
output [WIDTH-1:0] A;

assign {cout,A} =  cin + y + x;
 
endmodule
