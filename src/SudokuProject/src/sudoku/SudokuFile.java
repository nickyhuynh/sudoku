package sudoku;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cspSolver.BTSolver;

public class SudokuFile {
	
	/**
	 * p, q, N represent the dimensions of a game board.
	 * Assuming a game board has values p = 3, q = 4, N = 12, 
	 * an empty board will look like this. 
	 * 
	 * [] [] [] [] | [] [] [] [] | [] [] [] []
	 * [] [] [] [] | [] [] [] [] | [] [] [] []
	 * [] [] [] [] | [] [] [] [] | [] [] [] []
	 * ---------------------------------------
	 * [] [] [] [] | [] [] [] [] | [] [] [] []
	 * [] [] [] [] | [] [] [] [] | [] [] [] []
	 * [] [] [] [] | [] [] [] [] | [] [] [] []
	 * ---------------------------------------
	 * [] [] [] [] | [] [] [] [] | [] [] [] []
	 * [] [] [] [] | [] [] [] [] | [] [] [] []
	 * [] [] [] [] | [] [] [] [] | [] [] [] []
	 * ---------------------------------------
	 * [] [] [] [] | [] [] [] [] | [] [] [] []
	 * [] [] [] [] | [] [] [] [] | [] [] [] []
	 * [] [] [] [] | [] [] [] [] | [] [] [] []
	 * 
	 */
	private int p;//number of rows in a block && number of blocks per row
	private int q;//number of columns in a block && number of blocks per column
	private int N;//number of cells in a block && edge length of a NxN puzzle

	
	private int[][] board = null;
	
	protected SudokuFile(){}
	
	public SudokuFile(int N, int p, int q, int[][] board)
	{
		if(N != p * q || N < 1)
		{
			System.out.println("Board parameters invalid. Creating a 9x9 sudoku file instead.");
			setP(3);
			setQ(3);
			setN(9);
			setBoard(new int[9][9]);
		}
		else
		{
			setP(p);
			setQ(q);
			setN(N);
			setBoard(board==null ? new int[9][9] : board);
		}
	}
	
	public SudokuFile(int N, int p, int q)
	{
		this(N,p,q,null);
	}
	
	public int getP() {
		return p;
	}

	protected void setP(int p) {
		this.p = p;
	}

	public int getQ() {
		return q;
	}

	protected void setQ(int q) {
		this.q = q;
	}

	public int getN() {
		return N;
	}

	protected void setN(int n) {
		N = n;
	}

	public int[][] getBoard() {
		return board;
	}

	protected void setBoard(int[][] board) {
		this.board = board;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("N: ");
		sb.append(N);
		sb.append("\tP: ");
		sb.append(p);
		sb.append("\tQ: ");
		sb.append(q);
		sb.append("\n");
		for(int i = 0; i < N; i ++)
		{
			for(int j = 0; j < N; j++)
			{
				sb.append(Odometer.intToOdometer(board[i][j]) + " ");
				if((j+1)%q==0 && j!= 0 && j != N-1)
				{
					sb.append("| ");
				}
			}
			sb.append("\n");
			if((i+1)%p == 0 && i != 0 && i != N-1)
			{
				for(int k = 0; k < N+p-1;k++)
				{
					sb.append("- ");
				}
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	public void statsFile(String[] args, BTSolver solver, long startTime, long preprocessStart, long preprocessEnd,	long searchStart, long searchEnd, Boolean error)
	{
		String sep = System.getProperty("line.separator");
		
		try
		{
			FileWriter fw = new FileWriter(args[1]);
			fw.write("TOTAL_START(SYSTEM_TIME) = " + startTime/1000 + " " + sep);
			fw.write("RELATIVE TO START TIME: " + sep);
			fw.write("PREPROCESSING_START = " + (preprocessStart - startTime)/1000 + "s" + sep);
			fw.write("PREPROCESSING_END = " + (preprocessEnd - startTime)/1000 + "s" + sep);
			fw.write("SEARCH_START = " + (searchStart - startTime)/1000 + "s" + sep);
			fw.write("SEARCH_END = " + (searchEnd - startTime)/1000 + "s" + sep);
			fw.write("SOLUTION_TIME = " + ((preprocessEnd - preprocessStart) + (searchEnd - searchStart))/1000 + "s" + sep);
			if(error)
			{
				fw.write("STATUS = ERROR" + sep);
			}
			else if (((preprocessEnd - preprocessStart) + (searchEnd - searchStart))/1000 > Integer.parseInt(args[2]))
			{
				fw.write("STATUS = TIMEOUT" + sep);
			}
			else
			{
				fw.write("STATUS = SUCCESS" + sep);
			}
			
			int[][] sb = solver.getSolution().getBoard();
			
			if(solver.hasSolution())
			{
				fw.write("(");
				for(int i = 0; i < this.getN(); i++)
				{
					for(int j = 0; j < sb[i].length; j++)
					{
						if((i == this.getN()-1) && (j == this.getN()-1))
						{
							fw.write(sb[i][j] + ")" + sep);
						}
						else
						{
							fw.write(sb[i][j] + ",");
						}
					}
				}
			}
			else
			{
				fw.write("(0");
				for(int i = 0; i < this.getN(); i++)
				{
					fw.write(",0");
				}
				fw.write(")" + sep);
			}
			
			fw.write("COUNT_NODES = " + solver.getNumAssignments() + sep);
			fw.write("COUNT_DEADENDS = " + solver.getNumBacktracks() + sep);
			
			fw.flush();
			fw.close();
		}
		catch(IOException e)
		{
			System.err.print("Error in checking.");
		}
	}
}
