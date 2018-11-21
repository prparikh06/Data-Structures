package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with arrays
	 * in the expression. For every variable (simple or array), a SINGLE instance is created 
	 * and stored, even if it appears more than once in the expression.
	 * At this time, values for all variables and all array items are set to
	 * zero - they will be loaded from a file in the loadVariableValues method.
	 * 
	 * @param expr The expression
	 * @param vars The variables array list - already created by the caller
	 * @param arrays The arrays array list - already created by the caller
	 */
	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) { 

		expr = expr.replaceAll("\\s", ""); //remove all spaces
		int index = 0;

		while (index < expr.length()) {  //index and traverse string
			if(!Character.isLetter(expr.charAt(index))) {
				index++;
				continue;
			}
			String variableName = "";
			char c = ' ';		
			for (int i = 0; i < expr.length()-index; i++) { //traverse remaining length of string
				c = expr.charAt(index+i);
				if (!Character.isLetter(c)) { 
					break;
				}
				variableName += c; //add character to string variable
			}

			while (!arrays.contains(new Array(variableName)) && !vars.contains(new Variable(variableName))) { //as long as variable does not already exist in respective arraylists
				if (c == '[') {
					arrays.add(new Array(variableName));
				}
				else {
					vars.add(new Variable(variableName));
				}
			}
			index += variableName.length(); //move index along to next sequence of letters
		}

		System.out.println(arrays);
		System.out.println(vars);

	}

	/**
	 * Loads values for variables and arrays in the expression
	 * 
	 * @param sc Scanner for values input
	 * @throws IOException If there is a problem with the input 
	 * @param vars The variables array list, previously populated by makeVariableLists
	 * @param arrays The arrays array list - previously populated by makeVariableLists
	 */
	public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays)
			throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				vars.get(vari).value = num;
			} else { // array symbol
				arr = arrays.get(arri);
				arr.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok," (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;              
				}
			}
		}
	}
	
	

	private static boolean checkPrecedence( char c,  char operator) {
		return (operator == '-' || operator == '+') && (c == '*' || c == '/');
	}

	private static String doArithmetic(Stack<String> operands,  Stack<Character> operators,  ArrayList<Array> arrays) {
		char c = operators.pop();
		String secOp = operands.pop();
		String firstOp = operands.pop();
		switch (c) {
		case '+':
			return Float.toString(Float.parseFloat(firstOp) + Float.parseFloat(secOp));
		case '-':
			return Float.toString(Float.parseFloat(firstOp) - Float.parseFloat(secOp));
		case '*':
			return Float.toString(Float.parseFloat(firstOp) * Float.parseFloat(secOp));
		case '/':
			return Float.toString(Float.parseFloat(firstOp) / Float.parseFloat(secOp));
		case '[':
			 int index = (int) Float.parseFloat(secOp);
			return Float.toString(arrays.get(arrays.indexOf(new Array(firstOp))).values[index]);
		default:
			return "0";
		}
	}

	private static float getVarValue( String name, ArrayList<Variable> vars) {
		return vars.get(vars.indexOf(new Variable(name))).value;
	}

	/**
	 * Evaluates the expression.
	 * 
	 * @param vars The variables array list, with values for all variables in the expression
	 * @param arrays The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {

		 Stack<String> operands = new Stack<>();
		 Stack<Character> operators = new Stack<>();
		expr = "(" + expr.replaceAll("\\s", "") + ")";
		int ptr = 0;

		while (ptr < expr.length()) {
			// Case of operators
			char c = expr.charAt(ptr);
			if (delims.contains(String.valueOf(c))) {
				if (c == '(' || c == '[') {  
					operators.push(c);
					ptr++;
				} else if (c == ')' || c == ']') {  
					while (!operators.isEmpty()) {
						char op = operators.peek();
						if (op == '(') {  
							operators.pop();
							break;
						}
						operands.push(doArithmetic(operands, operators, arrays));
						if (op == '[') {  
							break;
						}
					}
					ptr++;
				} else if (!operators.isEmpty() && !(operators.peek() == '(' || operators.peek() == '[' || checkPrecedence(c, operators.peek()))) {  // Evaluate left to right
					operands.push(doArithmetic(operands, operators, arrays));
				} else {  // Skip evaluation because current operator is of higher precedence than one on stack
					operators.push(c);
					ptr++;
				}
				continue;
			}

			// Case of simple variables, arrays, or numbers
			 StringBuilder varNameBuilder = new StringBuilder();
			char nextChar = ' ';
			for (int ptr2 = 0; ptr2 < expr.length() - ptr; ptr2++) {
				nextChar = expr.charAt(ptr + ptr2);
				if (delims.contains(String.valueOf(nextChar))) {
					break;
				}
				varNameBuilder.append(nextChar);
			}
			 String varName = varNameBuilder.toString();

			if (nextChar == '[' || Character.isDigit(varName.charAt(0))) {
				operands.push(varName);
			} else {
				operands.push(Float.toString(getVarValue(varName, vars)));
			}

			// Move pointer along
			ptr += varName.length();
		}

		while (operands.size() > 1) {
			operands.push(doArithmetic(operands, operators, arrays));
		}
		return Float.parseFloat(operands.pop());
	}


}