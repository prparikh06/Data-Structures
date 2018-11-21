package bigint;

/**
 * This class encapsulates a BigInteger, i.e. a positive or negative integer with 
 * any number of digits, which overcomes the computer storage length limitation of 
 * an integer.
 * 
 */
public class BigInteger {

	/**
	 * True if this is a negative integer
	 */
	boolean negative;

	/**
	 * Number of digits in this integer
	 */
	int numDigits;

	/**
	 * Reference to the first node of this integer's linked list representation
	 * NOTE: The linked list stores the Least Significant Digit in the FIRST node.
	 * For instance, the integer 235 would be stored as:
	 *    5 --> 3  --> 2
	 *    
	 * Insignificant digits are not stored. So the integer 00235 will be stored as:
	 *    5 --> 3 --> 2  (No zeros after the last 2)        
	 */
	DigitNode front;

	/**
	 * Initializes this integer to a positive number with zero digits, in other
	 * words this is the 0 (zero) valued integer.
	 */
	public BigInteger() {
		negative = false;
		numDigits = 0;
		front = null;
	}

	/**
	 * Parses an input integer string into a corresponding BigInteger instance.
	 * A correctly formatted integer would have an optional sign as the first 
	 * character (no sign means positive), and at least one digit character
	 * (including zero). 
	 * Examples of correct format, with corresponding values
	 *      Format     Value
	 *       +0            0
	 *       -0            0
	 *       +123        123
	 *       1023       1023
	 *       0012         12  
	 *       0             0
	 *       -123       -123
	 *       -001         -1
	 *       +000          0
	 *       
	 * Leading and trailing spaces are ignored. So "  +123  " will still parse 
	 * correctly, as +123, after ignoring leading and trailing spaces in the input
	 * string.
	 * 
	 * Spaces between digits are not ignored. So "12  345" will not parse as
	 * an integer - the input is incorrectly formatted.
	 * 
	 * An integer with value 0 will correspond to a null (empty) list - see the BigInteger
	 * constructor
	 * 
	 * @param integer Integer string that is to be parsed
	 * @return BigInteger instance that stores the input integer.
	 * @throws IllegalArgumentException If input is incorrectly formatted
	 */
	public static BigInteger parse(String integer) 
			throws IllegalArgumentException {

		int finalInt = 0;

		BigInteger digitLL = new BigInteger();

		if (integer.length() == 0 || integer == null) { //check if string is empty
			throw new IllegalArgumentException("Incorrect format");
		}

		integer = integer.trim(); //removes spaces from either side
		String s = integer;

		if (integer.charAt(0)== '-' || integer.charAt(0)== '+') { //gets rid of sign
			integer = integer.substring(1);
		}
		while(integer.length() > 1 && integer.indexOf("0") == 0) { //removes leading zeros for pos numbers
			integer = integer.substring(1);
		}  
		if (integer.equals("0")){ //if we have 0
			digitLL.negative = false; 
			digitLL.front = null;
			digitLL.numDigits = 0;
			return digitLL;
		}

		if (s.charAt(0) == '-' && integer != "0") { //PROBLEM LIES HERE
			digitLL.negative = true;
		}

		//checks to see if anything else within string is not a digit and creates the LL
		for (int i=0; i<integer.length(); i++) {
			char c = integer.charAt(i);
			if (Character.isDigit(c)==false){
				throw new IllegalArgumentException("Incorrect format");
			}
			else {
				finalInt*=10;
				finalInt = c-48;//chg into ASCII into int
				DigitNode d = new DigitNode(finalInt,digitLL.front); 
				digitLL.front = d;
				digitLL.numDigits++;
			}
		}

		System.out.println(digitLL.numDigits);
		System.out.println(digitLL.negative);


		return digitLL; 
	}


	/**
	 * Adds the first and second big integers, and returns the result in a NEW BigInteger object. 
	 * DOES NOT MODIFY the input big integers.
	 * 
	 * NOTE that either or both of the input big integers could be negative.
	 * (Which means this method can effectively subtract as well.)
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return Result big integer
	 */
	private void reverseList(){  
		DigitNode rev = null;
		DigitNode curr = front;
		while (curr != null) {
			DigitNode d = curr.next;
			curr.next = rev;
			rev = curr;
			curr = d;
		}
		front = rev;
	}

	public static BigInteger add(BigInteger first, BigInteger second) {

		int carry = 0;
		BigInteger result = new BigInteger();
		DigitNode p1 = first.front, p2 = second.front; //pointers for first,second

		if (!first.negative && !second.negative) { //both positive
			while(p1 != null && p2 != null){
				int firstInt = p1.digit;
				int secInt = p2.digit;
				int sum = firstInt + secInt + carry;
				if (sum >= 10) {
					sum = sum - 10;
					carry = 1;
				}
				else {
					carry = 0;
				}
				p1 = p1.next;
				p2 = p2.next;
				//add to result LL, create new node
				DigitNode temp = new DigitNode(sum,result.front); 
				result.front = temp;
			}
			while (p1 != null) { //we know that p2 == null, p1 > p2
				//add the remaining digits to sum
				p1.digit = p1.digit + carry;
				DigitNode temp = new DigitNode(p1.digit,result.front); 
				result.front = temp;
				p1 = p1.next;
				carry = 0;
			}
			while (p2 != null) { //we know that p1 == null, p2 > p1
				//add the remaining digits to sum
				p2.digit = p2.digit + carry;
				DigitNode temp = new DigitNode(p2.digit,result.front); 
				result.front = temp;
				p2 = p2.next;
				carry = 0;
			}

			//adding final carry
			if (carry != 0) {
				result.front = new DigitNode(carry,result.front); 
			}
			result.reverseList();
		}

		else if (!first.negative && second.negative) { //sec number is negative

			while(p1 != null && p2 != null){


				int firstInt = p1.digit;
				int secInt = p2.digit;
				int sum = secInt + (-1 * firstInt) + carry;
				if (sum < 0) {
					sum = sum + 10;
					carry = -1;
				}

				else {
					carry = 0;}

				p1 = p1.next;
				p2 = p2.next;

				//add to result LL, create new node
				DigitNode temp = new DigitNode(sum,result.front); 
				result.front = temp;}

			while (p1 != null) { //we know that p2 == null, p1 > p2
				//add the remaining digits to sum
				p1.digit = p1.digit + carry;
				DigitNode temp = new DigitNode(p1.digit,result.front); 
				result.front = temp;
				p1 = p1.next;
				carry = 0;
			}

			while (p2 != null) { //we know that p2 == null, p1 > p2
				//add the remaining digits to dif
				p2.digit = p2.digit + carry;
				DigitNode temp = new DigitNode(p2.digit,result.front); 
				result.front = temp;
				p2 = p2.next;
				carry = 0;

				result.front.digit = result.front.digit * -1;
				result.reverseList();}

		}

		if (first.negative && !second.negative) { //first is negative

			while(p1 != null && p2 != null){


				int firstInt = p1.digit;
				int secInt = p2.digit;
				int sum = firstInt + (-1 * secInt) + carry;
				if (sum < 0) {
					sum = sum + 10;
					carry = -1;
				}

				else {
					carry = 0;}

				p1 = p1.next;
				p2 = p2.next;

				//add to result LL, create new node
				DigitNode temp = new DigitNode(sum,result.front); 
				result.front = temp;}

			while (p1 != null) { //we know that p2 == null, p1 > p2
				//add the reaing digits to sum
				p1.digit = p1.digit + carry;
				DigitNode temp = new DigitNode(p1.digit,result.front); 
				result.front = temp;
				p1 = p1.next;
				carry = 0;

				result.front.digit = result.front.digit * -1;
				result.reverseList();}


			while (p2 != null) { //we know that p2 == null, p1 > p2
				//add the remaining digits to dif
				p2.digit = p2.digit + carry;
				DigitNode temp = new DigitNode(p2.digit,result.front); 
				result.front = temp;
				p2 = p2.next;
				carry = 0;
				result.reverseList();
			}
		}





		if (first.negative && second.negative) { //both negative
			while(p1 != null && p2 != null){
				int firstInt = p1.digit;
				int secInt = p2.digit;
				int sum = firstInt + secInt + carry;
				if (sum >= 10) {
					sum = sum - 10;
					carry = 1;
				}
				else {
					carry = 0;
				}
				p1 = p1.next;
				p2 = p2.next;
				//add to result LL, create new node
				DigitNode temp = new DigitNode(sum,result.front); 
				result.front = temp;
			}
			while (p1 != null) { //we know that p2 == null, p1 > p2
				//add the reaing digits to sum
				p1.digit = p1.digit + carry;
				DigitNode temp = new DigitNode(p1.digit,result.front); 
				result.front = temp;
				p1 = p1.next;
				carry = 0;

			}
			while (p2 != null) { //we know that p1 == null, p2 > p1
				//add the reaing digits to sum
				p2.digit = p2.digit + carry;
				DigitNode temp = new DigitNode(p2.digit,result.front); 
				result.front = temp;
				p2 = p2.next;
				carry = 0;
			}

			//adding final carry
			if (carry != 0) {
				result.front = new DigitNode(carry,result.front); 
			}

			result.front.digit = result.front.digit * -1;
			result.reverseList();

		}

		return result;

	}

	private static DigitNode addToEnd(int integer,DigitNode front) {
		DigitNode ptr = front;
		while (ptr.next != null) { //traverse
			ptr = ptr.next;
		}
		//create a new node to the end, points to null
		ptr.next = new DigitNode(integer,null);

		return ptr;
	}
	/**
	 * Returns the BigInteger obtained by multiplying the first big integer
	 * with the second big integer
	 * 
	 * This method DOES NOT MODIFY either of the input big integers
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return A new BigInteger which is the product of the first and second big integers
	 */
	public static BigInteger multiply(BigInteger first, BigInteger second) {

		BigInteger result = new BigInteger(); 

		DigitNode p1 = first.front, p2 = second.front, temp = new DigitNode(0,null); 
		result.front = temp;
		DigitNode res = temp;
		DigitNode curRes = temp;

		int firstInt = p1.digit;
		int secondInt = p2.digit;
		int product = 0;
		int i = 0;
		
		while (p1 != null) {

			int carry = 0;

			while(p2 != null) {

				product = (firstInt * secondInt) + carry;
				i = product % 10; //gives one's digit
				
				carry = product/10 + i/10;
				
				DigitNode d = new DigitNode(i,result.front); 
				result.front = d;
				
				
				p2 = p2.next;
			}
			
		
			if (carry > 0) {
				
				DigitNode d = new DigitNode(carry,result.front); 
				result.front = d;
			}
			
			p1 = p1.next;


		}
		
		result.reverseList();
		
		while (result.front.digit == 0) {
			
			result.front = result.front.next;
		}

		if (first.negative) {
			result.front.digit = result.front.digit * -1;
		}
		else if (second.negative) {
			result.front.digit = result.front.digit * -1;
		}

		return result; 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (front == null) {
			return "0";
		}
		String retval = front.digit + "";
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
			retval = curr.digit + retval;
		}

		if (negative) {
			retval = '-' + retval;
		}
		return retval;
	}

}
