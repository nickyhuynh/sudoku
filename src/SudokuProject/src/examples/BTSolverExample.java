package examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import cspSolver.BTSolver;
import cspSolver.BTSolver.ConsistencyCheck;
import cspSolver.BTSolver.ValueSelectionHeuristic;
import cspSolver.BTSolver.VariableSelectionHeuristic;
import sudoku.SudokuBoardGenerator;
import sudoku.SudokuBoardReader;
import sudoku.SudokuFile;

public class BTSolverExample {

	public static void main(String[] args)
	{
		if (args.length > 0)
		{
			SudokuFile sf = SudokuBoardReader.readFile(args[0]);
			long startTime = System.currentTimeMillis();
			long preprocessStart;
			long preprocessEnd;
			long searchStart;
			long searchEnd;
			Boolean error = false;
			Boolean dh = false;
			Boolean mrv = false;
			
			BTSolver solver = new BTSolver(sf);
			
			preprocessStart = System.currentTimeMillis();
			

			solver.setConsistencyChecks(ConsistencyCheck.None);
			solver.setValueSelectionHeuristic(ValueSelectionHeuristic.None);
			solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.None);
			
			if(args.length > 3)
			{
				for (String option : Arrays.copyOfRange(args, 3, args.length))
				{

					System.out.println("option: " + option);
					switch(option.toUpperCase())
					{
					case "FC":
						System.out.println("Forward Checking\n");
						solver.setConsistencyChecks(ConsistencyCheck.ForwardChecking);
						break;
					case "MRV":
						mrv = true;
						System.out.println("Minimum Remaining Values\n");
						solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.MinimumRemainingValue);
						break;
					case "DH":
						dh = true;
						System.out.println("Degree Heuristic\n");
						solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.Degree);
						break;
					case "LCV":
						System.out.println("Least Constraining Value\n");
						solver.setValueSelectionHeuristic(ValueSelectionHeuristic.LeastConstrainingValue);
						break;
					case "ACP":
						System.out.println("Arc Consistency Preprocessor\n");
						System.out.println("Not yet implemented.\n");
						break;
					case "MAC":
						System.out.println("Maintaining Arc Consistency\n");
						System.out.println("Not yet implemented.\n");
						break;
					default:
						System.out.println("Defaulting options.");
						solver.setConsistencyChecks(ConsistencyCheck.ForwardChecking);
						solver.setValueSelectionHeuristic(ValueSelectionHeuristic.None);
						solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.None);
						break;
					}
				}
				
				if(mrv&&dh)
				{
					solver.setVariableSelectionHeuristic(VariableSelectionHeuristic.MinimumRemainingValue);
					solver.setDH(true);
				}
			}

			preprocessEnd = System.currentTimeMillis();
			
			Thread t1 = new Thread(solver);
			
			searchStart = System.currentTimeMillis();
			
			try	
			{
				t1.start();
				t1.join(Long.parseLong(args[2]));
				if(t1.isAlive())
				{
					t1.interrupt();
				}
			}catch(InterruptedException e)
			{
				error = true;
			}
	
			searchEnd = System.currentTimeMillis();
	
			if(solver.hasSolution())
			{
				solver.printSolverStats();
				System.out.println(solver.getSolution());	
			}
			else
			{
				System.out.println("Failed to find a solution");
			}
			
			sf.statsFile(args, solver, startTime, preprocessStart, preprocessEnd, searchStart, searchEnd, error);
		}
		else
		{
			System.err.print("Invalid arguments to command line: not enough arguments.");
		}
	}
}
