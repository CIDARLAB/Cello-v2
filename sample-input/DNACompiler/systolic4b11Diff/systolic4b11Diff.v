module systolic(inRow, inColumn, out);
	
// Width
parameter ROW = 4;
parameter COLUMN = 11;

input [ROW-1:0] inRow;
input [COLUMN-1:0] inColumn;
wire [(ROW+1)*(COLUMN+1)-1:0] w;
output out;

genvar iRow;
generate
	for (iRow=1; iRow<=ROW; iRow=iRow+1)
	begin : I_inR
		//assign w[iRow][0] = inRow[iRow];
		assign w[iRow*(COLUMN+1) + 0] = inRow[iRow-1];
	end
endgenerate
genvar iColumn;
generate
	for (iColumn=1; iColumn<=COLUMN; iColumn=iColumn+1)
	begin : I_inC
		//assign w[0][iColumn] = inColumn[iColumn];
		assign w[(COLUMN+1)*0 + iColumn] = inColumn[iColumn-1];
	end
endgenerate

genvar i, j;
generate
	for (i=1; i<=ROW; i=i+1)
	begin : I
		for (j=1; j<=COLUMN; j=j+1)
		begin : J
			//assign w[i][j] = w[i][j-1] & w[i-1][j];
			if (i == j)
			begin
				assign w[i*(COLUMN+1) + j] = w[i*(COLUMN+1) + (j-1)] & w[(i-1)*(COLUMN+1) + j];
			end
			else if (i < j)
			begin
				assign w[i*(COLUMN+1) + j] = w[i*(COLUMN+1) + (j-1)] ^ w[(i-1)*(COLUMN+1) + j];
			end
			else
			begin
				assign w[i*(COLUMN+1) + j] = w[i*(COLUMN+1) + (j-1)] | w[(i-1)*(COLUMN+1) + j];
			end
			//assign w[i*(COLUMN+1) + j] = ~(w[i*(COLUMN+1) + (j-1)] | w[(i-1)*(COLUMN+1) + j]);
		end
	end
endgenerate

//assign out = w[ROW][COLUMN];
assign out = w[(ROW+1)*(COLUMN+1)-1];

endmodule
