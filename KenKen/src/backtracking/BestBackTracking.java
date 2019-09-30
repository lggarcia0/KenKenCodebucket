package backtracking;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class BestBackTracking {
	static int N = 0;
	int nodes = 0;
	static Square board[][];
	static HashMap<String, Integer> numVar = new HashMap<String, Integer>(); // Keeps track of the number of variables in each group
	static HashMap<String, String> groupOperand = new HashMap<String, String>(); // For use to extract operands
	static HashMap<String, Integer> groupValue = new HashMap<String, Integer>(); // Extracts the product of each equation
	static HashMap<String, HashMap<Integer, int[]>> groupMem = new HashMap<String, HashMap<Integer, int[]>>();

	public static void main(String[] args) {
		try {
			System.out.print("Enter the file name with extension : ");

            Scanner input = new Scanner(System.in);

            File file = new File(input.nextLine());

            input = new Scanner(file);
            N = input.nextInt();
            board = new Square[N][N];
            input.nextLine();
            //Initialize one boards that contains Square objects
            for (int i = 0; i < N; i++) {
            	String horizontal = input.nextLine();
            	for (int j = 0; j < N; j++) {
            		String group = Character.toString(horizontal.charAt(j));
            		if (!(numVar.containsKey(group))) {
            			numVar.put(group, 1);
            		} else {
            			numVar.computeIfPresent(group, (k, v) -> v + 1);
            		}
            		board[i][j] = new Square(group, numVar.get(group) - 1, i, j);
            		if(!groupMem.containsKey(group)) {
            			groupMem.put(group, new HashMap<Integer, int[]>());
            		}
            		int temp[] = {i, j};
            		groupMem.get(group).put(numVar.get(group) - 1, temp);
            	}
            }
            //Retrieve the values and equation types from the rest
            while (input.hasNextLine()) {
                String line = input.nextLine();
                String aKey = Character.toString(line.charAt(0));
                String operand = Character.toString(line.charAt(line.length()-1));
                if (numVar.get(aKey) == 1) {
                	int value = Integer.valueOf(line.substring(2, (line.length())));
                	groupOperand.put(aKey, "none");
                	groupValue.put(aKey, value);
                } else {
                	int value = Integer.valueOf(line.substring(2, (line.length()-1)));
                    groupOperand.put(aKey, operand);
                    groupValue.put(aKey, value);
                }
            }
            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
		BestBackTracking BT = new BestBackTracking();
		BT.solve(0, 0);
		BT.printSolution(board);

	}
	//Recursion function to test and place all possible variables
		boolean solve(int x, int y) {
			if (x >= N) {
				return true;
			}
			for (int i = 1; i <= N; i++) {
				if (crossCheck(i, board[x][y])) {
					if (equation(i, board[x][y])) {
						board[x][y].changeVal(i);
						nodes++;
						int nextY = y + 1;
						if (nextY < N) {
							if (solve(x, nextY)) {
								return true;
							}
						} else {
							if ( solve(x + 1, 0)) {
								return true;
							}
						}
						board[x][y].changeVal(0);;
					}
				}
			}
			return false;
		}
		void printSolution(Square board[][]) 
	    { 
	        for (int i = 0; i < N; i++) { 
	            for (int j = 0; j < N; j++) 
	                System.out.print(" " + board[i][j].getVal()
	                                 + " "); 
	            System.out.println();
	        } 
	        System.out.println(nodes);
	    } 
		//Checking whether there is at least one possible solution given the shape of the box and its restrictions
		boolean possAdd(int var, Square currentSquare) {
			String group = currentSquare.getGroup();
			int id = currentSquare.getID();
			int ans = groupValue.get(group);
			if (id != 0) {
				for (int i = 0; i < id; i++) {
					int coordinate[] = groupMem.get(group).get(i);
					int temp = board[coordinate[0]][coordinate[1]].getVal();
					ans = ans - temp;
				}
			}
			ans  = ans - var;
			if (ans < 0) {
				return false;
			}
			switch(numVar.get(group) - id) {
			case 4:
				int[] coorB = groupMem.get(group).get(1);
				int[] coorC = groupMem.get(group).get(2);
				int[] coorD = groupMem.get(group).get(3);
				Square squareB = board[coorB[0]][coorB[1]];
				Square squareC = board[coorC[0]][coorC[1]];
				Square squareD = board[coorD[0]][coorD[1]];
				for (int b = N; b > 0; b--) {
					int ansB = ans - b;
					if (!((var == b) && currentSquare.beside(squareB)) && crossCheck(b, squareB) && (ansB > 0)) {
						for (int c = N; c > 0; c--) {
							int d = ansB - c;
							if ((d > 0) && (d <= N)) {
								if (!((b == c) && squareB.beside(squareC)) && crossCheck(c, squareC)) {
									if (!((var == c) && currentSquare.beside(squareC))) {
										if(!((var == d) && currentSquare.beside(squareD)) && !((b == d) && squareB.beside(squareD)) && !((c == d) && squareC.beside(squareD))) {
											if (crossCheck(d, squareD)) {
												return true;
											}
										}
									}
								}
							}
						}
					}
				}
				return false;
			case 3:
				int[] coB = groupMem.get(group).get(id + 1);
				int[] coC = groupMem.get(group).get(id + 2);
				Square sqB = board[coB[0]][coB[1]];
				Square sqC = board[coC[0]][coC[1]];
				for (int b = N; b > 0; b--) {
					int c = ans - b;
					if ((c > 0) && (c <= N)) {
						if (!((var == b) && currentSquare.beside(sqB)) && crossCheck(b, sqB)) {
							if(!((var == c) && currentSquare.beside(sqC)) && !((b == c) && sqB.beside(sqC))) {
								if (crossCheck(c, sqC)) {
									return true;
								}
							} 
						}
					}	
				}
				return false;
			case 2:
				int[] cB = groupMem.get(group).get(id + 1);
				Square sB = board[cB[0]][cB[1]];
				if ((ans <= N)) {
					if (!((var == ans) && currentSquare.beside(sB)) && crossCheck(ans, sB)) {
						return true;
					}
				}
				return false;
			case 1:
				if (ans == 0) {
					return true;
				}
				return false;
			}
			return false;
		}
		boolean possSub(int var, Square currentSquare) {
			String group = currentSquare.getGroup();
			int ans = groupValue.get(group);
			boolean affirmative = false;
			switch (numVar.get(group) - currentSquare.id) {
			case 1:
				int coor[] = groupMem.get(group).get(0);
				int member = board[coor[0]][coor[1]].getVal();
				int ansA = member + ans;
				int ansB = member - ans;
				if (var == ansA) {
					affirmative = true;
				}
				if (var == ansB) {
					affirmative = true;
				}
				break;
			case 2:
				int co[] = groupMem.get(group).get(1);
				Square mem = board[co[0]][co[1]];
				int a = var + ans;
				int b = var - ans;
				boolean testA = (a <= N) && (a > 0);
				boolean testB = (b <= N) && (b > 0);
				if (testA && crossCheck(a, mem)) {
					affirmative = true;
				}
				if (testB && crossCheck(b, mem)) {
					affirmative = true;
				}
			}
			return affirmative;
		}
		//Now checking if there is at least 1 possible solution given the shape of the box, the current variable
		boolean possMult(int var, Square currentSquare) {
			String group = currentSquare.getGroup();
			int id = currentSquare.getID();
			double product = groupValue.get(group);
			double variable = var;
			if (id != 0) {
				for (int i = 0; i < id; i++) {
					int coordinate[] = groupMem.get(group).get(i);
					double temp = board[coordinate[0]][coordinate[1]].getVal();
					product = product / temp;
				}
			}
			product = product / variable;
			if (Math.floor(product) < product) {
				return false;
			}
			switch(numVar.get(group) - id) {
			case 4:
				int[] coorB = groupMem.get(group).get(1);
				int[] coorC = groupMem.get(group).get(2);
				int[] coorD = groupMem.get(group).get(3);
				Square squareB = board[coorB[0]][coorB[1]];
				Square squareC = board[coorC[0]][coorC[1]];
				Square squareD = board[coorD[0]][coorD[1]];
				for (double b = N; b > 0.0; b--) {
					double productB = product / b;
					if (!(Math.floor(productB) < productB)) {
						int tempInt = (int)b;
						if (!((variable == b) && currentSquare.beside(squareB)) && crossCheck(tempInt, squareB)) {
							for (double c = N; c > 0.0; c--) {
								double productC = productB / c;
								if (!(Math.floor(productC) < productC) && (productC <= N)) {
									tempInt = (int)c;
									if (!((b == c) && squareB.beside(squareC)) && crossCheck(tempInt, squareC)) {
										if (!((variable == c) && currentSquare.beside(squareC))) {
											double d = productC;
											if(!((variable == d) && currentSquare.beside(squareD)) && !((b == d) && squareB.beside(squareD)) && !((c == d) && squareC.beside(squareD))) {
												tempInt = (int)d;
												if (crossCheck(tempInt, squareD)) {
													return true;
												}
											}
										}
									}
								}
							}
						}
					}
				}
				return false;
			case 3:
				int[] coB = groupMem.get(group).get(id + 1);
				int[] coC = groupMem.get(group).get(id + 2);
				Square sqB = board[coB[0]][coB[1]];
				Square sqC = board[coC[0]][coC[1]];
				for (double b = N; b > 0.0; b--) {
					double productB = product / b;
					if (!(Math.floor(productB) < productB) && (productB <= N)) {
						int tempInt = (int)(b);
						if (!((variable == b) && currentSquare.beside(sqB)) && crossCheck(tempInt, sqB)) {
							double c = productB;
							if(!((variable == c) && currentSquare.beside(sqC)) && !((b == c) && sqB.beside(sqC))) {
								tempInt = (int)c;
								if (crossCheck(tempInt, sqC)) {
									return true;
								}
							} 
						}
					}	
				}
				return false;
			case 2:
				int[] cB = groupMem.get(group).get(id + 1);
				Square sB = board[cB[0]][cB[1]];
				if (product <= N) {
					int b = (int)product;
					if (!((variable == product) && currentSquare.beside(sB)) && crossCheck(b, sB)) {
						return true;
					}
				}
				return false;
			case 1:
				if (product == 1.0) {
					return true;
				}
				return false;
			}
			return false;
			
		}
		boolean possDiv(int var, Square currentSquare) {
			String group = currentSquare.getGroup();
			double variable = var;
			double ans = groupValue.get(group);
			double result = 0.0;
			double result2 = 0.0;
			boolean affirmative = false;
			switch (numVar.get(group) - currentSquare.getID()) {
			case 1:
				int coor[] = groupMem.get(group).get(0);
				double member = board[coor[0]][coor[1]].getVal();
				result = member / ans;
				result2 = ans * member;
				if (variable == result) {
					affirmative = true;
				} 
				if (variable == result2) {
					affirmative = true;
				}
				break;
			case 2:
				int co[] = groupMem.get(group).get(1);
				Square mem = board[co[0]][co[1]];
				result = variable/ans;
				result2 = ans*variable;
				if ((result == Math.floor(result)) && ((int)result <= N) && (result != variable)) {
					int intResult = (int)result;
					if (crossCheck(intResult, mem)) {
						affirmative =  true;
					}
				}
				if (((int)result2 <= N) && (result2 != variable)) {
					int intResult = (int)result2;
					if (crossCheck(intResult, mem)) {
						affirmative =  true;
					}
				}
				break;
			}
			return affirmative;
		}
		//assigns which function to test out
		boolean equation(int val, Square currentSquare) {
			String group = currentSquare.getGroup();
			switch (groupOperand.get(group)) {
			case "*" :
				return possMult(val, currentSquare);
			case "/" :
				return possDiv(val, currentSquare);
			case "+" :
				return possAdd(val, currentSquare); 
			case "-" :
				return possSub(val, currentSquare);	
			case "none":
				if (val == groupValue.get(group)) {
					return true;
				}
			}

			return false;
		}
		boolean crossCheck(int val, Square currentSquare) {
			int[] coordinates = currentSquare.getCoordinates();
			for (int i = 0; i < N; i++) {
				if (board[coordinates[0]][i].getVal() == val) {
					return false;
				}
				if (board[i][coordinates[1]].getVal() == val) {
					return false;
				}
			}
			return true;
		}

}
