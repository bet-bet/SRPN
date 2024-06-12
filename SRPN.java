/*
* References
* Skinner, G. (2008) RegExr. Available at: https://regexr.com/ (Accessed: 13 February 2024).
* */


/*
 * Program class for an SRPN calculator
 */
import java.lang.Math;
import java.util.*;
import java.util.List;
import java.util.Random;

public class SRPN {
    /* setting up 3 stacks, one for numeric input one for operators, and one for output*/
    List<Integer> inputStack = new ArrayList<>();
    List<Integer> outputStack = new ArrayList<>();
    List<Character> operatorStack = new ArrayList<>();

    /* instantiating java random number class */
    Random randomNumber = new Random();

    /* setting random number generator seed in constructor to get same random number generation each time*/
    public SRPN() {
        randomNumber.setSeed(0);
    }

    /* processes input from the commandline */
    public void processCommand(String s) {
        /* setting up list for allowed non-numeric strings */
        String[] allowedChars = new String[]{"+", "-", "/", "+", "%", "^", "d", "r", "=", "*"};
        List<String> allowedCharsList = Arrays.asList(allowedChars);

        /* handle input edge cases */
        handleEmptyInput(s);
        handleEmptyInputStack(inputStack, s);

        /* adding input to numeric stack and operator stack */
        inputStack.addAll(separateIntinMixedString(s));
        operatorStack.addAll(separateCharinMixedString(s, allowedCharsList));

        /* loop while operator stack is not empty*/
        while (!operatorStack.isEmpty()) {
            /* if last value in operator stack is an operator */
            if (checkOperator(operatorStack.getLast())) {
                /* if there are at least 2 numbers in the input stack*/
                if (inputStack.size() > 1) {
                    /* if not throwing a div 0 error*/
                    if (!checkDivZero(inputStack.getLast(), operatorStack.getLast())) {
                        /* call doOperation method*/
                        int outValue = doOperation(inputStack.get(inputStack.size() - 2), inputStack.getLast(), operatorStack.getLast());
                        /* remove last 2 digits used in operation from input stack*/
                        inputStack.removeLast();
                        inputStack.removeLast();
                        /* remove used operator from operator stack*/
                        operatorStack.removeLast();
                        /* add result to input stack */
                        inputStack.add(outValue);
                        /* add result to output stream */
                        outputStack.add(outValue);
                    } else {
                        /* print if trying to divide by 0 */
                        System.out.println("Divide by 0.");
                        operatorStack.removeLast();
                    }
                }
                /* print stack underflow if trying to do operation on 1 or less ints in input stack */
                else {
                    /* if d is last input in stack, handle and pop from operator stack*/
                    handleDInput(operatorStack.getLast().toString());
                    operatorStack.removeLast();
                    System.out.println("Stack Underflow.");
                    /* if operator stack is bigger than input stack , keep removing from stack, handling d input as you go*/
                    if (operatorStack.size() >= inputStack.size()) {
                        if (checkOperator(operatorStack.getLast())) {
                            operatorStack.removeLast();
                        }
                        /* if not an operator, handle d input, and remove last operator from stack */
                        else {
                            handleDInput(operatorStack.getLast().toString());
                            operatorStack.removeLast();
                        }
                    }
                    /* break while loop on stack underflow*/
                    break;
                }
                /* if last char in operator stack is =, and output stream is not empty, print output stack*/
            } else if (operatorStack.getLast() == '=' && !outputStack.isEmpty()) {
                /* remove = from operator stack*/
                operatorStack.removeLast();
                System.out.println(outputStack.getLast());
            }
            /* if output stack is empty, but input stack is not, and = is given, print out the last int in input stack */
            else if (operatorStack.getLast() == '=' && inputStack.size() > 1) {
                operatorStack.removeLast();
                System.out.println(inputStack.getLast());

            } else if (operatorStack.getLast() == 'd') {
                handleDInput(operatorStack.getLast().toString());
                operatorStack.removeLast();
            }
            /* if input = r, add random number to stack from 0 to max int limit*/
            else if (operatorStack.getLast() == 'r') {
                int generatedNumber = randomNumber.nextInt(0, 2147483647);
                outputStack.add(generatedNumber);
                inputStack.add(generatedNumber);
                operatorStack.removeLast();
            }
            /* if output stack is bigger than 21, pop off excess numbers and then print stack overflow */
            if (outputStack.size() > 21) {
                int numberToPop = outputStack.size() - 21;
                for(int i=0; i < numberToPop; i++){
                    outputStack.removeLast();
                }
                System.out.print("Stack Overflow.");
                break;
            }
        }
    }

    /* handle empty (return) input */
    public void handleEmptyInput(String s) {
        if (s.isEmpty()) {
            System.out.print("");
        }
    }

    /* handle trying to return = when input stack is empty*/
    public void handleEmptyInputStack(List<Integer> inputStack, String s) {
        if (inputStack.isEmpty() && s.charAt(0) == '=') {
            System.out.println("Stack Empty.");
        }
    }

    /* function performs numeric operations from user input of string operators
     * also handles saturation (max and min int in java) by maxing out at maxvalue and minvalue.
     * This saturation negates java inbuilt wraparound */
    public int doOperation(int firstNum, int secondNum,
                           char c) {
        /* assigning reult to long as has larger max and min range than int */
        long result = 0;
        int maxvalue = 2147483647;
        int minvalue = -2147483648;

        /* do operations and assign to result long */
        if (c == '+') {
            result = (long) firstNum + secondNum;
        }
        if (c == '-') {
            result = (long) firstNum - secondNum;
        }
        if (c == '*') {
            result = (long) firstNum * secondNum;
        }
        if (c == '/') {
            result = (long) firstNum / secondNum;
        }
        if (c == '%') {
            result = (long) firstNum % secondNum;
        }
        if (c == '^') {
            result = (long) Math.pow(firstNum, secondNum);
        }

        /* check if result exceeds int max / min, and return max / min as int if so (saturation handling) */
        if (result > maxvalue) {
            return maxvalue;
        }
        if (result < minvalue) {
            return minvalue;
        }
        return (int) result;
    }

    /* check if divide by zero would occur, and return true / false */
    public boolean checkDivZero(int secondNum, char c) {
        return c == '/' && secondNum == 0;
    }

    /* returns true if char given is an operator */
    public boolean checkOperator(char s) {
        return (s == '+' || s == '-' || s == '*' || s == '/' || s == '%' || s == '^');
    }

    /* handles d entry */
    public void handleDInput(String s) {
        // checking if user trying to print input stack when empty and printing negative limit.
        if (s.charAt(0) == 'd') {
            if (inputStack.isEmpty()) {
                System.out.println("-2147483648");
            }
            // else print out the entire input stack directly when d given as input.
            else {
                for (Integer integer : inputStack) {
                    System.out.println(integer);
                }
            }
        }
    }


    /* pulling out numbers from user input and adds them to a stack (list), works for mixed input also */
    public List separateIntinMixedString(String s) {
        List<Integer> extractedInts = new ArrayList<>();

        /* this try is needed to handle single line negative number entries*/
        try {
            Integer inputInt;
            inputInt = Integer.valueOf(s, 10);
            extractedInts.add(inputInt);
        } catch (NumberFormatException ignore) {
        }

        /* if cannot convert input to integer directly
         * split string at any non-numeric number */
        if (extractedInts.isEmpty()) {
            /* split string on non-numeric character (regex \\D)
            * Regex expression adapted from https://regexr.com/ (Skinner, G.)*/
            String[] numericArray = s.split("\\D", 0);
            try {
                /* for each split item in numeric array, if it can now be converted to int, add to extractedInts
                 * catch any exceptions */
                for (String string : numericArray) {
                    extractedInts.add(Integer.valueOf(string, 10));
                }
                /* ignore number format exceptions generated */
            } catch (NumberFormatException ignored) {
            }

            /* if single input given, return now and don't do below*/
            if (extractedInts.size() == 1) {
                return extractedInts;
            }
        }
        /* if multiple input given, reverse stack ready for operations*/
        Collections.reverse(extractedInts);
        return extractedInts;
    }


    /* pulling out allowed chars, allows for mixed user input of letters and numbers*/
    public List separateCharinMixedString(String s, List<String> allowedCharsList) {
        /* setting up lists to store letters and numeric inputs */
        List<Character> extractedChars = new ArrayList<>();
        List<Integer> extractedInts = new ArrayList<>();

        /* try converting input directly to number*/
        try {
            Integer inputInt;
            inputInt = Integer.valueOf(s, 10);
            extractedInts.add(inputInt);
        } catch (NumberFormatException ignore) {
        }

        /* if single number has not been given */
        if (extractedInts.isEmpty()) {
            for (int i = 0; i < s.length(); i++) {
                /* if character in input is a letter and is not in the list of allowed chars print out the below error to user */
                if (Character.isLetter(s.charAt(i)) &&
                        !allowedCharsList.contains(String.valueOf(s.charAt(i)))) {
                    System.out.println("Unrecognised operator or operand \"" + s.charAt(i) + "\".");
                    /* else if character is not a digit, and not a space it must be an allowed operator or char,
                    * therefore add to char stack */
                } else if (!Character.isDigit(s.charAt(i)) && allowedCharsList.contains(String.valueOf(s.charAt(i) ))) {
                    extractedChars.add(s.charAt(i));
                }
            }
        }

        /* is char stack only has one char in it, return it as is. */
        if (extractedChars.size() == 1) {
            return extractedChars;
            /* if stack is larger than 1, reverse the stack, making sure d stays on the top of the stack */
        } else if (extractedChars.size() > 1) {
            /*  logic so d stays in position while reversing stack */
            if (extractedChars.getLast() == 'd') {
                Collections.reverse(extractedChars);
                return extractedChars;
                /* logic so = stays in position while reversing stack */
            } else if (extractedChars.getLast() == '=') {
                extractedChars.removeLast();
                Collections.reverse(extractedChars);
                extractedChars.add('=');
                return extractedChars;
            }
        }
        /* reverse so in right order for popping off stack */
        Collections.reverse(extractedChars);
        return extractedChars;
    }
}
