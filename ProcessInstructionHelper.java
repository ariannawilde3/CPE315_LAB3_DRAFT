public class ProcessInstructionHelper {
    public ProcessInstructionHelper() {

    }
    // return true if pc is moved
    public static Boolean ProcessInstruction(Instruction instruction) {                  
                    
            int sourceIndex = 0;
            int targetIndex = 0;
            int destIndex = 0;
            int immediate = 0;
            int memoryAddress = 0;
            Boolean pcMoved = false;

            switch(instruction.getOperationString()){

                case "and":
                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
            
                    lab3.Registers[destIndex] = lab3.Registers[sourceIndex] & lab3.Registers[targetIndex];

                    break;

                case "or":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
        
                    lab3.Registers[destIndex] = lab3.Registers[sourceIndex] | lab3.Registers[targetIndex];

                    break;

                case "add":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
                    
                    lab3.Registers[destIndex] = lab3.Registers[sourceIndex] + lab3.Registers[targetIndex];

                    break;

                case "addi":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    if (instruction.getImm().charAt(0) == '1') {
                        // It's a 16-bit binary, adjust for two's complement if negative
                        immediate = -1 * ((1 << instruction.getImm().length()) - immediate);
                    }
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);

                    lab3.Registers[targetIndex] = lab3.Registers[sourceIndex] + immediate;

                    break;

                case "sll":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    if (instruction.getImm().charAt(0) == '1') {
                        // It's a 16-bit binary, adjust for two's complement if negative
                        immediate = -1 * ((1 << instruction.getImm().length()) - immediate);
                    }
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
            
                    lab3.Registers[destIndex] = lab3.Registers[sourceIndex] << immediate;

                    break;

                case "sub":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
                
                    lab3.Registers[destIndex] = lab3.Registers[sourceIndex] - lab3.Registers[targetIndex];

                    break;

                case "slt":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);

                    if (lab3.Registers[sourceIndex] < lab3.Registers[targetIndex]) {
                        lab3.Registers[destIndex] = 1;
                    } else {
                        lab3.Registers[destIndex] = 0;
                    }

                    break;

                case "beq":
                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);            
            
                    if (lab3.Registers[sourceIndex] == lab3.Registers[targetIndex]) {
                        lab3.pc =  getLabelAddr(instruction.getLabelName());  
                        pcMoved = true;
                    }

                    break;

                case "bne":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
            
                    // if(R[rs]!=R[rt])
                    // PC=PC+1+BranchAddr
                    if (lab3.Registers[sourceIndex] != lab3.Registers[targetIndex]) {
                        lab3.pc = getLabelAddr(instruction.getLabelName());  
                        pcMoved = true;
                    }

                    break; 
                case "lw":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    if (instruction.getImm().charAt(0) == '1') {
                        // It's a 16-bit binary, adjust for two's complement if negative
                        immediate = -1 * ((1 << instruction.getImm().length()) - immediate);
                    }
                    
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);

                    memoryAddress = lab3.Registers[sourceIndex] + immediate;
                    lab3.Registers[targetIndex] = lab3.dataMemory[memoryAddress];

                    break; 
                case "sw":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    if (instruction.getImm().charAt(0) == '1') {
                        // It's a 16-bit binary, adjust for two's complement if negative
                        immediate = -1 * ((1 << instruction.getImm().length()) - immediate);
                    }
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);

                    int memoryAddressSW = lab3.Registers[sourceIndex] + immediate;
                    lab3.dataMemory[memoryAddressSW] = lab3.Registers[targetIndex];

                    break; 

                case "j":
                    lab3.pc = getLabelAddr(instruction.getLabelName());
                    pcMoved = true;

                    break;

                case "jr":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    lab3.pc = lab3.Registers[sourceIndex];
                    pcMoved = true;

                    break;

                case "jal":
                
                    lab3.Registers[31] = lab3.pc + 1;
                    lab3.pc = getLabelAddr(instruction.getLabelName());
                    pcMoved = true;

                    break;

                default:
                    System.out.println("invalid instruction: " + instruction.getOperationString() );
                    System.exit(0);
                    break;

            }

            return pcMoved;
        }

        public static int getLabelAddr(String labelName) {
            int addr = -1;
            
            if(labelName != null && labelName != "") {
                addr = lab3.labelToLineMap.get(labelName);
            }

            return addr;
        }
    }
