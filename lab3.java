import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class lab3 {

    // Label name and the line numbers that the label is seen on
    public static HashMap<String, Integer> labelToLineMap = new HashMap<String, Integer>();

    // Label name and the line numbers that the label is seen on
    public static HashMap<String, String> labelToInstructionMap = new HashMap<String, String>();

    // Operations Array
    public static ArrayList<Instruction> instructionsList = new ArrayList<Instruction>();

    public static int dataMemory[] = new int[8192];

    public static int pc = 0;

       /*
     * $zero 0 The Constant Value 0 N.A.
     * $at 1 Assembler Temporary No
     * $v0-$v1 2-3 Values for Function Results
     * and Expression Evaluation No
     * $a0-$a3 4-7 Arguments No
     * $t0-$t7 8-15 Temporaries No
     * $s0-$s7 16-23 Saved Temporaries Yes
     * $t8-$t9 24-25 Temporaries No
     * $k0-$k1 26-27 Reserved for OS Kernel No
     * $gp 28 Global Pointer Yes
     * $sp 29 Stack Pointer Yes
     * $fp 30 Frame Pointer Yes
     * $ra 31
     */

     public static HashMap<String, Integer> registerNameToIntegerMap = new HashMap<String, Integer>();

     public static int Registers[] = new int[32];

    // 1. Define Instruction Set
    // 2. Create Data Structure (Array)
    // 3. Implement First Pass (run thru all lines of code and compute address of
    // each label)
    // 4. Implement Second Pass (all instruction converted to machine code)
    // 5. Error Handling
    // 6. Output Machine Code
    // 7. Testing

    
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.out.println("You need to specify the full path to the input file.");
                return;
            }
            InputStream input = System.in;
            if (args.length > 1) {
                input = new FileInputStream(args[1]);  // Open the script file as an input stream
            }
            readAndProcessFile(args[0], input);
        } catch (Exception ex) {
            System.out.println("Exception occurred: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    // Pass 1
    // check if empty
    // check if hashtag
    // check if white space and !$ <= this tells us instruction
    // if next char is : lable! put in hash
    public static void readAndProcessFile(String fileNameWithPath, InputStream input) throws Exception {
        Scanner scanner = new Scanner(input);
        String command = "";
        Boolean printCommands = false;
        int numSteps;

        firstPass(fileNameWithPath);

        if(input != System.in) {
            printCommands = true;
        }

        while (!command.equals("q")) {
           
            if(!printCommands) {
                System.out.print("mips> ");
            }
            
            command = scanner.nextLine();

            if(printCommands) {
                System.out.println("mips> " + command);
            }

            String[] tokens = command.split("\\s+");
            
            switch (tokens[0]) {
                case "h":
                    System.out.println("Available commands:");
                    System.out.println("h = show help");
                    System.out.println("d = dump register state");
                    System.out.println("s = single step through the program (i.e. execute 1 instruction and stop)");
                    System.out.println("s num = step through num instructions of the program");
                    System.out.println("r = run until the program ends");
                    System.out.println("m num1 num2 = display data memory from location num1 to num2");
                    System.out.println("c = clear all registers, memory, and the program counter to 0");
                    System.out.println("q = exit the program");

                    break;

                case "d":
                    System.out.println();
                    System.out.printf("pc = %d\n", pc); // Assuming 'pc' is your program counter variable
                    System.out.printf("$0 = %-11d $v0 = %-10d $v1 = %-10d $a0 = %-10d\n", Registers[0], Registers[2], Registers[3], Registers[4]);
                    System.out.printf("$a1 = %-10d $a2 = %-10d $a3 = %-10d $t0 = %-10d\n", Registers[5], Registers[6], Registers[7], Registers[8]);
                    System.out.printf("$t1 = %-10d $t2 = %-10d $t3 = %-10d $t4 = %-10d\n", Registers[9], Registers[10], Registers[11], Registers[12]);
                    System.out.printf("$t5 = %-10d $t6 = %-10d $t7 = %-10d $s0 = %-10d\n", Registers[13], Registers[14], Registers[15], Registers[16]);
                    System.out.printf("$s1 = %-10d $s2 = %-10d $s3 = %-10d $s4 = %-10d\n", Registers[17], Registers[18], Registers[19], Registers[20]);
                    System.out.printf("$s5 = %-10d $s6 = %-10d $s7 = %-10d $t8 = %-10d\n", Registers[21], Registers[22], Registers[23], Registers[24]);
                    System.out.printf("$t9 = %-10d $sp = %-10d $ra = %-10d\n", Registers[25], Registers[29], Registers[31]);
                    System.out.println();
                    break;

                case "s":
                    if(tokens.length > 1) {
                        numSteps = Integer.parseInt(tokens[1]);
                    } else {
                        numSteps = 1;
                    }
                    
                    for (int i = 0; i < numSteps; i++) {
                        if (pc >= instructionsList.size()) {
                            System.out.println("End of instructions list.");
                            break;
                        }
                        ProcessInstructionHelper.ProcessInstruction(instructionsList.get(pc));
                        pc++;                        
                    }
                    System.out.printf("        %d instruction(s) executed\n", numSteps);
                    break;

                case "r":
                    while (pc < instructionsList.size()) {
                        Instruction instruction = instructionsList.get(pc);
                        if(!ProcessInstructionHelper.ProcessInstruction(instruction)){                        
                            pc++;
                        }
                    }

                    break;

                case "m":
                    if (tokens.length == 3) {
                        int start = Integer.parseInt(tokens[1]);
                        int end = Integer.parseInt(tokens[2]);
                        for (int i = start; i <= end; i++) {
                            System.out.printf("[%d] = %d\n", i, dataMemory[i]);
                        }
                    } else {
                        System.out.println("Invalid memory range.");
                    }
                    break;

                case "c":
                    java.util.Arrays.fill(Registers, 0);
                    java.util.Arrays.fill(dataMemory, 0);
                    pc = 0;
                    System.out.println("        Simulator reset\n");
                    break;

                case "q":                    
                    break;

                default:
                    System.out.println("Invalid command.");
            }
        }

        scanner.close();
    }

    private static void firstPass(String fileNameWithPath) {
        File file = new File(fileNameWithPath);

        BufferedReader reader = null;

        try {
            // Reader is used to read the file pointer passed to readFile for parsing
            reader = new BufferedReader(new FileReader(file));

            // Declare a string variable representing a line of the line
            String line;

            // Keep track of the number of lines in the files (needed for identiying which
            // line a label is on)
            int lineCount = 0;
            String currentLabelName = null;
            
            // first pass find labels
            // also added instructions to arrayList
            line = reader.readLine();
            while (line != null) {
                line = line.trim();

                // Seperate the string into words (break on space)
                // String[] words = line.split("(?<!#.*),(?=(?:[^#]*#)?[^#]*$)|(\\s+)|(?=#)");
                String[] words = line.split("\\s*,\\s*|\\s+|(?<=\\S)(?=#)|(?<=[a-zA-Z])(?=\\$)");
                // Process each word to identify what to do, this is a nested loop to process
                // the line
                Boolean isComment = false;
                Boolean isLabel = false;
                String finalInstruction = "";
                String finalLabelInstruction = "";
                String finalLabelName = "";
                // String finalComment = ""; // currently unused because we ignore comments

                // test: add $s0, $s0, $a0 # this is a comment
                // addi $a0, $a0, -1
                // bne $a0, $0, test # this is another comment
                // beq$t0,$t1,test
                for (int i = 0; i < words.length; ++i) {
                    int indexOfHash = words[i].indexOf("#");
                    int indexOfColon = words[i].indexOf(":");

                    // If we are a comment, at this point we can break and complete the instructions
                    // and labels
                    if (isComment) {
                        break;
                    }

                    // Process the the case of comments
                    if (indexOfHash >= 0 && indexOfColon >= 0) {
                        if (isLabel) {
                            throw new Exception("You cannot define 2 labels on 1 line");
                        }

                        isLabel = true;
                        // We know the string contains a comment (#), now it also contains a label (:).
                        // This means it is either a label and then a comment, a comment that contains a
                        // label which can be ignored
                        // or a label, then an instruction, then a comment
                        if (indexOfColon < indexOfHash) {
                            isComment = true;
                            // check if the string contains a $, otherwise we know that the

                            finalLabelName = words[i].substring(0, indexOfColon);
                            currentLabelName = finalLabelName.trim();
                            finalLabelInstruction += (" " + words[i].substring(indexOfColon + 1, indexOfHash));
                        } else {
                            isComment = true;
                            // The hash comes before the label, but that means it could also have a leading
                            // instruction before the comment
                            finalInstruction += (" " + words[i].substring(0, indexOfHash));
                        }
                    } else if (indexOfColon >= 0) {
                        // Process the case of a label found, it is similiar to the comment found, but
                        // in a different sequence
                        if (isLabel) {
                            throw new Exception("You cannot define 2 labels on 1 line");
                        }

                        isLabel = true;

                        // Identify the label and if necessary start the label instructions
                        finalLabelName = words[i].substring(0, indexOfColon);
                        currentLabelName = finalLabelName.trim();
                        finalLabelInstruction += (" " + words[i].substring(indexOfColon + 1));
                    } else if (indexOfHash >= 0) {
                        isComment = true;
                        // The hash comes before the label, but that means it could also have a leading
                        // instruction before the comment
                        finalInstruction += (" " + words[i].substring(0, indexOfHash));
                    } else {
                        // We are in the normal case of a word, the word is either part of a label
                        // instruction or part of a normal instruction
                        // we ignore comments, so it can't be part of a comment
                        if (isLabel) {
                            finalLabelInstruction += (" " + words[i]);
                        } else {
                            finalInstruction += (" " + words[i]);
                        }
                    }
                }

                finalLabelName = finalLabelName.trim();
                finalInstruction = finalInstruction.trim();
                finalLabelInstruction = finalLabelInstruction.trim();

                if (finalInstruction != null && finalInstruction.length() > 0) {
                    Instruction instruction = lab3.convertSpaceSeparatedLineToBinaryString(finalInstruction, lineCount);                    
                    if (instruction == null) {
                        break;
                    } else {
                        if(instruction.getLabelName() == null || instruction.getLabelName() == "") {
                            instruction.setLabelName(currentLabelName);
                        }
                        instructionsList.add(instruction);
                    }

                    //instructionArray.add(finalInstruction);
                }

                if (isLabel) {
                    labelToLineMap.put(finalLabelName, lineCount);
                }

                if (isLabel && finalLabelInstruction.length() > 0) {
                    labelToInstructionMap.put(finalLabelName, finalLabelInstruction);
                    //instructionArray.add(finalLabelInstruction);

                    Instruction instruction = lab3.convertSpaceSeparatedLineToBinaryString(finalLabelInstruction, lineCount);
                    if (instruction == null) {
                        break;
                    } else {
                        instructionsList.add(instruction);
                    }
                }

                // We only want to increment line count if we have an actionable line (ignore
                // blanks and comments)
                if (finalLabelInstruction.length() > 0 || finalInstruction.length() > 0) {
                    lineCount += 1;
                }

                // Reset variables
                finalLabelName = "";
                finalLabelInstruction = "";
                finalInstruction = "";
                isComment = false;
                isLabel = false;

                line = reader.readLine();
            }



        } catch (IOException e) {
            System.out.println("Error reading file");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing file");
            }
        }
    }

    private static void populateRegisterHashMap() {
        /*
         * $zero 0 The Constant Value 0 N.A.
         * $at 1 Assembler Temporary No
         * $v0-$v1 2-3 Values for Function Results
         * and Expression Evaluation No
         * $a0-$a3 4-7 Arguments No
         * $t0-$t7 8-15 Temporaries No
         * $s0-$s7 16-23 Saved Temporaries Yes
         * $t8-$t9 24-25 Temporaries No
         * $k0-$k1 26-27 Reserved for OS Kernel No
         * $gp 28 Global Pointer Yes
         * $sp 29 Stack Pointer Yes
         * $fp 30 Frame Pointer Yes
         * $ra 31
         */

        registerNameToIntegerMap.put("$zero", 0);
        registerNameToIntegerMap.put("$0", 0);
        registerNameToIntegerMap.put("$at", 1);
        registerNameToIntegerMap.put("$v0", 2);
        registerNameToIntegerMap.put("$v1", 3);
        registerNameToIntegerMap.put("$a0", 4);
        registerNameToIntegerMap.put("$a1", 5);
        registerNameToIntegerMap.put("$a2", 6);
        registerNameToIntegerMap.put("$a3", 7);
        registerNameToIntegerMap.put("$t0", 8);
        registerNameToIntegerMap.put("$t1", 9);
        registerNameToIntegerMap.put("$t2", 10);
        registerNameToIntegerMap.put("$t3", 11);
        registerNameToIntegerMap.put("$t4", 12);
        registerNameToIntegerMap.put("$t5", 13);
        registerNameToIntegerMap.put("$t6", 14);
        registerNameToIntegerMap.put("$t7", 15);
        registerNameToIntegerMap.put("$s0", 16);
        registerNameToIntegerMap.put("$s1", 17);
        registerNameToIntegerMap.put("$s2", 18);
        registerNameToIntegerMap.put("$s3", 19);
        registerNameToIntegerMap.put("$s4", 20);
        registerNameToIntegerMap.put("$s5", 21);
        registerNameToIntegerMap.put("$s6", 22);
        registerNameToIntegerMap.put("$s7", 23);
        registerNameToIntegerMap.put("$t8", 24);
        registerNameToIntegerMap.put("$t9", 25);
        registerNameToIntegerMap.put("$k0", 26);
        registerNameToIntegerMap.put("$k1", 27);
        registerNameToIntegerMap.put("$gp", 28);
        registerNameToIntegerMap.put("$sp", 29);
        registerNameToIntegerMap.put("$fp", 30);
        registerNameToIntegerMap.put("$ra", 31);
    }

    public static Instruction convertSpaceSeparatedLineToBinaryString(String line, int currentInstructionIndex)
            throws Exception {

        populateRegisterHashMap();

        Instruction instruction = new Instruction();
        String result = "";

        String[] segments = line.split(" ");

        // operationString
        instruction.setOperationString(segments[0]);

        switch (segments[0]) {
            case "add":
                // Format in line for add is: opcode rd rs rt
                // opcode (6 bits) | rs (5 bits) | rt (5 bits) | rd (5 bits) | shamt (5 bits) | funct (6 bits)

                // opcode (6 bits)
                result += "000000";
                instruction.setOpcode("000000");

                // rs (5 bits)
                if (segments[2].contains("$")) {
                    String rsString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[2]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                } else {
                    String rsString = Integer.toBinaryString(Integer.parseInt(segments[2]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                }

                // rt (5 bits)
                if (segments[3].contains("$")) {
                    String rtString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[3]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                } else {
                    String rtString = Integer.toBinaryString(Integer.parseInt(segments[3]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                }

                // rd (5 bits)
                if (segments[1].contains("$")) {
                    String rtString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[1]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setDest(rtString);
                } else {
                    String rtString = Integer.toBinaryString(Integer.parseInt(segments[1]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setDest(rtString);
                }

                // shamt (5 bits) - NOT USED IN add
                result += " 00000";
                instruction.setShamt("00000");

                // function (6 bits) - add = 100000
                result += " 100000";
                instruction.setFunction("100000");

                instruction.setImm(null);
                instruction.setJump(null);

                break;

            case "sub":
                // Format in line for sub is: opcode rd rs rt
                // opcode (6 bits) | rs (5 bits) | rt (5 bits) | rd (5 bits) | shamt (5 bits) | funct (6 bits)

                // opcode (6 bits)
                result += "000000";
                instruction.setOpcode("000000");

                // rs (5 bits)
                if (segments[2].contains("$")) {
                    String rsString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[2]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                } else {
                    String rsString = Integer.toBinaryString(Integer.parseInt(segments[2]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                }

                // rt (5 bits)
                if (segments[3].contains("$")) {
                    String rtString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[3]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                } else {
                    String rtString = Integer.toBinaryString(Integer.parseInt(segments[3]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                }

                // rd (5 bits)
                if (segments[1].contains("$")) {
                    String rdString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[1]));
                    rdString = String.format("%5s", rdString).replace(' ', '0');
                    result += " " + rdString;
                    instruction.setDest(rdString);
                } else {
                    String rdString = Integer.toBinaryString(Integer.parseInt(segments[1]));
                    rdString = String.format("%5s", rdString).replace(' ', '0');
                    result += " " + rdString;
                    instruction.setDest(rdString);
                }

                // shamt (5 bits) - NOT USED IN sub
                result += " 00000";
                instruction.setShamt("00000");

                // funct (6 bits) - sub = 100010
                result += " 100010";
                instruction.setFunction("100010");

                instruction.setImm(null);
                instruction.setJump(null);

                break;

            case "and":
                // Format in line for and is: opcode rd rs rt
                // opcode (6 bits) | rs (5 bits) | rt (5 bits) | rd (5 bits) | shamt (5 bits) | funct (6 bits)

                // opcode (6 bits)
                result += "000000";
                instruction.setOpcode("000000");
                // rs (5 bits)
                if (segments[2].contains("$")) {
                    String rsString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[2]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                } else {
                    String rsString = Integer.toBinaryString(Integer.parseInt(segments[2]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                }

                // rt (5 bits)
                if (segments[3].contains("$")) {
                    String rtString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[3]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                } else {
                    String rtString = Integer.toBinaryString(Integer.parseInt(segments[3]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                }

                // rd (5 bits)
                if (segments[1].contains("$")) {
                    String rdString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[1]));
                    rdString = String.format("%5s", rdString).replace(' ', '0');
                    result += " " + rdString;
                    instruction.setDest(rdString);
                } else {
                    String rdString = Integer.toBinaryString(Integer.parseInt(segments[1]));
                    rdString = String.format("%5s", rdString).replace(' ', '0');
                    result += " " + rdString;
                    instruction.setDest(rdString);
                }

                // shamt (5 bits) - NOT USED IN and
                result += " 00000";
                instruction.setShamt("00000");

                // funct (6 bits) - and = 100100
                result += " 100100";
                instruction.setFunction("100100");

                instruction.setImm(null);
                instruction.setJump(null);

                break;

            case "or":
                // Format in line for or is: opcode rd rs rt
                // opcode (6 bits) | rs (5 bits) | rt (5 bits) | rd (5 bits) | shamt (5 bits) | funct (6 bits)

                // opcode (6 bits)
                result += "000000";
                instruction.setOpcode("000000");
                // rs (5 bits)
                if (segments[2].contains("$")) {
                    String rsString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[2]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                } else {
                    String rsString = Integer.toBinaryString(Integer.parseInt(segments[2]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                }

                // rt (5 bits)
                if (segments[3].contains("$")) {
                    String rtString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[3]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                } else {
                    String rtString = Integer.toBinaryString(Integer.parseInt(segments[3]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                }

                // rd (5 bits)
                if (segments[1].contains("$")) {
                    String rdString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[1]));
                    rdString = String.format("%5s", rdString).replace(' ', '0');
                    result += " " + rdString;
                    instruction.setDest(rdString);
                } else {
                    String rdString = Integer.toBinaryString(Integer.parseInt(segments[1]));
                    rdString = String.format("%5s", rdString).replace(' ', '0');
                    result += " " + rdString;
                    instruction.setDest(rdString);
                }

                // shamt (5 bits) - NOT USED IN or
                result += " 00000";
                instruction.setShamt("00000");

                // funct (6 bits) - or = 100101
                result += " 100101";
                instruction.setFunction("100101");

                instruction.setImm(null);
                instruction.setJump(null);

                break;

            case "slt":
                // Format in line for slt is: opcode rd rs rt
                // opcode (6 bits) | rs (5 bits) | rt (5 bits) | rd (5 bits) | shamt (5 bits) | funct (6 bits)

                // opcode (6 bits)
                result += "000000";
                instruction.setOpcode("000000");
                // rs (5 bits)
                if (segments[2].contains("$")) {
                    String rsString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[2]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                } else {
                    String rsString = Integer.toBinaryString(Integer.parseInt(segments[2]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                }

                // rt (5 bits)
                if (segments[3].contains("$")) {
                    String rtString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[3]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                } else {
                    String rtString = Integer.toBinaryString(Integer.parseInt(segments[3]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                }

                // rd (5 bits)
                if (segments[1].contains("$")) {
                    String rdString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[1]));
                    rdString = String.format("%5s", rdString).replace(' ', '0');
                    result += " " + rdString;
                    instruction.setDest(rdString);
                } else {
                    String rdString = Integer.toBinaryString(Integer.parseInt(segments[1]));
                    rdString = String.format("%5s", rdString).replace(' ', '0');
                    result += " " + rdString;
                    instruction.setDest(rdString);
                }

                // shamt (5 bits) - NOT USED IN slt
                result += " 00000";
                instruction.setShamt("00000");

                // funct (6 bits) - slt = 101010
                result += " 101010";
                instruction.setFunction("101010");

                instruction.setImm(null);
                instruction.setJump(null);

                break;

            case "sll":
                // Format in line for sll is: opcode rd rt shamt
                // opcode (6 bits) | rs (5 bits, unused) | rt (5 bits) | rd (5 bits) | shamt (5 bits) | funct (6 bits)

                // opcode (6 bits)
                result += "000000";
                instruction.setOpcode("000000");
                // rs (5 bits) - NOT USED IN sll
                result += " 00000";
                instruction.setSource("00000");

                // rt (5 bits)
                if (segments[2].contains("$")) {
                    String rtString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[2]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                } else {
                    String rtString = Integer.toBinaryString(Integer.parseInt(segments[2]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                }

                // rd (5 bits)
                if (segments[1].contains("$")) {
                    String rdString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[1]));
                    rdString = String.format("%5s", rdString).replace(' ', '0');
                    result += " " + rdString;
                    instruction.setDest(rdString);
                } else {
                    String rdString = Integer.toBinaryString(Integer.parseInt(segments[1]));
                    rdString = String.format("%5s", rdString).replace(' ', '0');
                    result += " " + rdString;
                    instruction.setDest(rdString);
                }

                // shamt (5 bits)
                if (segments[3].matches("\\d+")) { // shamt is a direct number
                    String shamtString = Integer.toBinaryString(Integer.parseInt(segments[3]));
                    shamtString = String.format("%5s", shamtString).replace(' ', '0');
                    result += " " + shamtString;
                    instruction.setShamt(shamtString);
                } else {
                    result += " 00000"; // Default shamt if not provided correctly
                    instruction.setShamt("00000");
                }

                // funct (6 bits) - sll = 000000
                result += " 000000";
                instruction.setFunction("000000");

                instruction.setImm(null);
                instruction.setJump(null);
        
                break;

            case "jr":
                // Format in line for jr is: opcode rs
                // opcode (6 bits) | rs (5 bits) | rt (5 bits, unused) | rd (5 bits, unused) | shamt (5 bits, unused) | funct (6 bits)

                // opcode (6 bits)
                result += "000000";
                instruction.setOpcode("000000");

                // rs (5 bits)
                if (segments[1].contains("$")) {
                    String rsString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[1]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                } else {
                    String rsString = Integer.toBinaryString(Integer.parseInt(segments[1]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                }

                // rt (5 bits) - NOT USED IN jr
                result += " 00000";
                instruction.setTarget("00000");

                // rd (5 bits) - NOT USED IN jr
                result += " 00000";
                instruction.setDest("00000");

                // shamt (5 bits) - NOT USED IN jr
                result += " 00000";
                instruction.setShamt("00000");

                // funct (6 bits) - jr = 001000
                result += " 001000";
                instruction.setFunction("001000");

                instruction.setImm(null);
                instruction.setJump(null);
                break;

            case "addi":
                // Format in line for addi is: opcode rt rs immediate
                // opcode (6 bits) | rs (5 bits) | rt (5 bits) | immediate (16 bits)

                // opcode (6 bits) - addi = 001000
                result += "001000";
                instruction.setOpcode("001000");

                // rs (5 bits)
                if (segments[2].contains("$")) {
                    String rsString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[2]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                }

                // rt (5 bits)
                if (segments[1].contains("$")) {
                    String rtString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[1]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                }

                try {
                    // Parsing and formatting the immediate value
                    int immediateValue = Integer.parseInt(segments[3]);
                    String immediateString = Integer.toBinaryString(immediateValue & 0xFFFF);
                    if (immediateString.length() > 16) {
                        immediateString = immediateString.substring(immediateString.length() - 16);
                    }
                    immediateString = String.format("%16s", immediateString).replace(' ', '0');
                    result += " " + immediateString;
                    instruction.setImm(immediateString);
                } catch (NumberFormatException e) {
                    result += " 0000000000000000";
                    instruction.setImm("0000000000000000");
                }

                instruction.setDest(null);
                instruction.setFunction(null);
                instruction.setShamt(null);
                instruction.setJump(null);

                break;

            case "beq":
                // Format in line for beq is: opcode rs rt offset
                // opcode (6 bits) | rs (5 bits) | rt (5 bits) | offset (16 bits)

                // opcode (6 bits) - beq = 000100
                result += "000100";
                instruction.setOpcode("000100");

                // rs (5 bits)
                if (segments[1].contains("$")) {
                    String rsString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[1]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                }

                // rt (5 bits)
                if (segments[2].contains("$")) {
                    String rtString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[2]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                }

                instruction.setLabelName(segments[3]);

                instruction.setDest(null);
                instruction.setFunction(null);
                instruction.setShamt(null);
                instruction.setJump(null);

                break;

            case "bne":
                // Format in line for bne is: opcode rs rt offset
                // opcode (6 bits) | rs (5 bits) | rt (5 bits) | offset (16 bits)

                // opcode (6 bits) - bne = 000101
                result += "000101";
                instruction.setOpcode("000101");

                // rs (5 bits)
                if (segments[1].contains("$")) {
                    String rsString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[1]));
                    rsString = String.format("%5s", rsString).replace(' ', '0');
                    result += " " + rsString;
                    instruction.setSource(rsString);
                }

                // rt (5 bits)
                if (segments[2].contains("$")) {
                    String rtString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[2]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                }

                // offset (16 bits)
                instruction.setLabelName(segments[3]);


                instruction.setDest(null);
                instruction.setFunction(null);
                instruction.setShamt(null);
                instruction.setJump(null);

                break;

            case "lw":
                // Format in line for lw is: opcode base rt offset
                // opcode (6 bits) | base (5 bits) | rt (5 bits) | offset (16 bits)

                // opcode (6 bits) - lw = 100011
                result += "100011";
                instruction.setOpcode("100011");

                // Extracting the base register and offset from segments[2], which is in the
                // format 'offset($base)'
                String lwOffsetPart = segments[2].substring(0, segments[2].indexOf('(')).trim();
                String lwBasePart = segments[2].substring(segments[2].indexOf('$'), segments[2].indexOf(')'));

                // base (5 bits)
                if (registerNameToIntegerMap.containsKey(lwBasePart)) {
                    String baseString = Integer.toBinaryString(registerNameToIntegerMap.get(lwBasePart));
                    baseString = String.format("%5s", baseString).replace(' ', '0');
                    result += " " + baseString;
                    instruction.setSource(baseString);
                } else {
                    result += " 00000"; // Default base if not found or incorrect parsing
                    instruction.setSource("00000");
                }

                // rt (5 bits)
                if (segments[1].contains("$")) {
                    String rtString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[1]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                } else {
                    result += " 00000";
                    instruction.setTarget("00000");
                }

                try {
                    // Calculating and formatting the offset
                    int offsetValue = Integer.parseInt(lwOffsetPart);
                    String offsetString = Integer.toBinaryString(0xFFFF & offsetValue);
                    offsetString = String.format("%16s", offsetString).replace(' ', '0'); // Ensure it is padded to 16
                                                                                          // bits
                    result += " " + offsetString;
                    instruction.setImm(offsetString);
                } catch (NumberFormatException e) {
                    result += " 0000000000000000";
                    instruction.setImm("0000000000000000");

                }

                instruction.setDest(null);
                instruction.setFunction(null);
                instruction.setShamt(null);
                instruction.setJump(null);

                break;

            case "sw":
                // Format in line for sw is: opcode base rt offset
                // opcode (6 bits) | base (5 bits) | rt (5 bits) | offset (16 bits)

                // opcode (6 bits) - sw = 101011
                result += "101011";
                instruction.setOpcode("101011");

                // Extracting the base register and offset from segments[2], which is in the
                // format 'offset($base)'
                String swOffsetPart = segments[2].substring(0, segments[2].indexOf('(')).trim();
                String swBasePart = segments[2].substring(segments[2].indexOf('$'), segments[2].indexOf(')'));

                // base (5 bits)
                if (registerNameToIntegerMap.containsKey(swBasePart)) {
                    String baseString = Integer.toBinaryString(registerNameToIntegerMap.get(swBasePart));
                    baseString = String.format("%5s", baseString).replace(' ', '0');
                    result += " " + baseString;
                    instruction.setSource(baseString);
                } else {
                    result += " 00000";
                    instruction.setSource("00000");
                }

                // rt (5 bits)
                if (segments[1].contains("$")) {
                    String rtString = Integer.toBinaryString(registerNameToIntegerMap.get(segments[1]));
                    rtString = String.format("%5s", rtString).replace(' ', '0');
                    result += " " + rtString;
                    instruction.setTarget(rtString);
                } else {
                    result += " 00000"; // Default if not properly formatted
                    instruction.setTarget("00000");
                }

                try {
                    // Calculating and formatting the offset
                    int offsetValue = Integer.parseInt(swOffsetPart);
                    String offsetString = Integer.toBinaryString(0xFFFF & offsetValue);
                    offsetString = String.format("%16s", offsetString).replace(' ', '0');
                    result += " " + offsetString;
                    instruction.setImm(offsetString);
                } catch (NumberFormatException e) {
                    result += " 0000000000000000";
                    instruction.setImm("0000000000000000");
                }

                instruction.setDest(null);
                instruction.setFunction(null);
                instruction.setShamt(null);
                instruction.setJump(null);

                break;

            case "j":
                // Format for the j instruction is: opcode address
                // opcode (6 bits) - j = 000010
                result += "000010";
                instruction.setOpcode("000010");

                // segments[1] is the label name
                instruction.setLabelName(segments[1]); // This should be a label like 'test1'

                instruction.setSource(null);
                instruction.setDest(null);
                instruction.setFunction(null);
                instruction.setTarget(null);
                instruction.setShamt(null);
                instruction.setImm(null);

                break;

            case "jal":
                // Format in line for jal is: opcode address
                // opcode (6 bits) - jal = 000011
                result += "000011";
                instruction.setOpcode("000011");

                // Address (26 bits)
                // segment[1] contains the label
                instruction.setLabelName(segments[1]);                
                instruction.setSource(null);
                instruction.setDest(null);
                instruction.setFunction(null);
                instruction.setTarget(null);
                instruction.setShamt(null);
                instruction.setImm(null);

                break;

            default:
                result = "invalid instruction: " + segments[0];
                instruction = null;
                break;
        }

        return instruction;
    }

 
}
