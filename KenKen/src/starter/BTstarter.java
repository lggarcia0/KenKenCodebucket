package starter;
import java.util.Scanner;  // Import the Scanner class
import java.util.HashMap;
import java.io.File;
public class BTstarter {
	static int N = 0;
	int nodes = 0;
	static String charBoard[][];
	static int board[][];
	static HashMap<String, Integer> numVar = new HashMap<String, Integer>(); // Keeps track of the number of variables in each group
	static HashMap<String, String> groupOperand = new HashMap<String, String>(); // For use to extract operands
	static HashMap<String, Integer> groupValue = new HashMap<String, Integer>(); // For General Use to extract function products
	static HashMap<String, Integer> groupValue2 = new HashMap<String, Integer>(); // For use with Subtraction and Division
	static HashMap<String, Integer> unmodifiedValue = new HashMap<String, Integer>(); // For use for reseting devision and subtraction
	public static void main(String[] args) {
		try {
			System.out.print("Enter the file name with extension : ");

            Scanner input = new Scanner(System.in);

            File file = new File(input.nextLine());

            input = new Scanner(file);
            N = input.nextInt();
            charBoard = new String[N][N];
            board = new int[N][N];
            input.nextLine();
            //Initialize two boards one with strings and another with zeros from input
            for (int i = 0; i < N; i++) {
            	String horizontal = input.nextLine();
            	for (int j = 0; j < N; j++) {
            		board[i][j] = 0;
            		charBoard[i][j] = Character.toString(horizontal.charAt(j));
            		if (!(numVar.containsKey(charBoard[i][j]))) {
            			numVar.put(charBoard[i][j], 1);
            		} else {
            			numVar.computeIfPresent(charBoard[i][j], (k, v) -> v + 1);
            		}
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
                    if (operand.equals("/") || operand.equals("-")) {
                    	groupValue2.put(aKey,value);
                    	unmodifiedValue.put(aKey, value);
                    }
                }
            }
            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
		BTstarter BT = new BTstarter();
		BT.solve(0, 0);
		BT.printSolution(board);
    }
	 void printSolution(int board[][]) 
	    { 
	        for (int i = 0; i < N; i++) { 
	            for (int j = 0; j < N; j++) 
	                System.out.print(" " + board[i][j] 
	                                 + " "); 
	            System.out.println();
	        } 
	        System.out.println(nodes);
	    } 
	boolean solve(int x, int y) {
		if (x >= N) {
			return true;
		}
		String currentGroup = charBoard[x][y];
		for (int i = 1; i <= N; i++) {
			if (crossCheck(i, x, y)) {
				if (equation(i, currentGroup)) {
					board[x][y] = i;
					nodes++;
					numVar.compute(currentGroup, (k, v) -> v - 1);
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
					reverseEquation(i, currentGroup);
					board[x][y] = 0;
					numVar.compute(charBoard[x][y], (k, v) -> v + 1);
				}
			}
		}
		return false;
	}
	boolean crossCheck(int val, int x, int y) {
		for (int i = 0; i < N; i++) {
			if (board[x][i] == val) {
				return false;
			}
			if (board[i][y] == val) {
				return false;
			}
		}
		return true;
	}
	boolean equation(int val, String group) {
		switch (groupOperand.get(group)) {
		case "*" :
			return mult(val, group);
		case "/" :
			return div(val, group);
		case "+" :
			return add(val, group); 
		case "-" :
			return sub(val, group);	
		case "none":
			if (val == groupValue.get(group)) {
				return true;
			}
		}

		return false;
	}
	void reverseEquation(int val, String group) {
		int modifiedAns = groupValue.get(group); 
		switch (groupOperand.get(group)) {
		case "*" :
			groupValue.compute(group, (k, v) -> modifiedAns * val);
			break;
		case "/" : // if numVar = 0; stored = val, else stored = num if num = val 
			if (numVar.get(group) == 0) {
				if (groupValue.get(group) == 1) {
					groupValue.compute(group, (k, v) -> val);
				} else {
					groupValue2.compute(group, (k, v) -> val);
				}
				
			} else {
				groupValue.compute(group, (k,v) -> unmodifiedValue.get(group));
				groupValue2.compute(group, (k,v) -> unmodifiedValue.get(group));
			}
			break;
		case "+" :
			groupValue.compute(group,(k,v) -> modifiedAns + val);
			break;
		case "-" :
			if (numVar.get(group) == 0) {
				if(groupValue.get(group) == 0) {
					groupValue.compute(group, (k, v) -> val);
				} else {
					groupValue2.compute(group, (k, v) -> val);
				}
			} else {
				groupValue.compute(group, (k,v) -> unmodifiedValue.get(group));
				groupValue2.compute(group, (k,v) -> unmodifiedValue.get(group));
			}
			break;
		}
	}
	boolean mult(int var, String group) { 
		double ansD = groupValue.get(group);
		double varD = var;
		double result = ansD/varD;
		//result 
		if (numVar.get(group) > 1) {
			if (Math.floor(result) < (result)) {
				return false;
			} else {
				int temp = (int)result;
				groupValue.compute(group, (k,v) -> temp);
				return true;
			}
		} else {
			if ((result) == 1.0) {
				int temp = (int)result;
				groupValue.compute(group, (k,v) -> temp);
				return true;
			} else {
				return false;
			}
		}
	}
	public boolean div(int var, String group) {
		double varD = var;
		double ansD = groupValue.get(group);
		double ansD2 = groupValue2.get(group);
		double result = 0.0;
		double result2 = 0.0;
		boolean affirmative = false;
		switch (numVar.get(group)) {
		case 1:
			if (varD == ansD) {
				groupValue.compute(group, (k, v) -> 1);
				affirmative = true;
			} 
			if (varD == ansD2) {
				groupValue2.compute(group, (k, v) -> 1);
				affirmative = true;
			}
			
			break;
		case 2:
			result = varD/ansD;
			result2 = ansD*varD;
			if ((result == Math.floor(result)) && ((int)result <= N) && (result != varD)) {
				int intResult = (int)result;
				groupValue.computeIfPresent(group, (k, v) -> intResult);
				affirmative =  true;
			}
			if (((int)result2 <= N) && (result2 != varD)) {
				int intResult = (int)result2;
				groupValue2.computeIfPresent(group, (k, v) -> intResult);
				affirmative = true;
			}
			break;
		}
		return affirmative;
	}
	public boolean add(int var, String group) {
		int ans = groupValue.get(group);
		int result = ans - var;
		if (numVar.get(group) > 1) {
			if (result <= 0) {
				return false;
			} else {
				groupValue.compute(group, (k,v) -> result);
				return true;
			}
		} else {
			if (result != 0) {
				return false;
			} else {
				groupValue.compute(group, (k,v) -> result);
				return true;
			}
		}
	}
	public boolean sub(int var, String group) {
		int ans = groupValue.get(group);
		boolean affirmative = false;
		switch (numVar.get(group)) {
		case 1:
			if (var == ans) {
				groupValue.compute(group, (k,v) -> 0);
				affirmative = true;
			}
			if (var == groupValue2.get(group)) {
				groupValue2.compute(group, (k,v) -> 0);
				affirmative = true;
			}
			break;
		case 2:
			int a = var + ans;
			int b = var - ans;
			boolean testA = (a <= N) && (a > 0);
			boolean testB = (b <= N) && (b > 0);
			if ((!testA && !testB)) {
				return false;
			}
			if (testA) {
				groupValue.compute(group, (k,v) -> a);
				affirmative = true;
			} else {
				groupValue.compute(group, (k,v) -> -1);
			}
			if (testB) {
				groupValue2.compute(group, (k,v) -> b);
				affirmative = true;
			} else {
				groupValue2.compute(group, (k,v) -> -1);
			}
		}
		return affirmative;
	}

}