public class ProcessInstructionHelper {
    public ProcessInstructionHelper() {

    }
    public static void ProcessInstruction(Instruction instruction) {                  
            int j = 0;
            String label;
            int sourceIndex = 0;
            int targetIndex = 0;
            int destIndex = 0;
            int immediate = 0;
            int memoryAddress = 0;
            int labelAddr = 0;

            switch(instruction.getOperationString()){

                case "and":
                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
            
                    lab2.Registers[destIndex] = lab2.Registers[sourceIndex] & lab2.Registers[targetIndex];

                    break;

                case "or":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
        
                    lab2.Registers[destIndex] = lab2.Registers[sourceIndex] | lab2.Registers[targetIndex];

                    break;

                case "add":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
                    
                    lab2.Registers[destIndex] = lab2.Registers[sourceIndex] + lab2.Registers[targetIndex];

                    break;

                case "addi":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    
                    lab2.Registers[targetIndex] = lab2.Registers[sourceIndex] + immediate;
                    break;

                case "sll":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
            
                    lab2.Registers[destIndex] = lab2.Registers[sourceIndex] << immediate;

                    break;

                case "sub":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);
                
                    lab2.Registers[destIndex] = lab2.Registers[sourceIndex] - lab2.Registers[targetIndex];

                    break;

                case "slt":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);

                    if (lab2.Registers[sourceIndex] < lab2.Registers[targetIndex]) {
                        lab2.Registers[destIndex] = 1;
                    } else {
                        lab2.Registers[destIndex] = 0;
                    }

                    break;

                case "beq":
                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);            
            
                    if (lab2.Registers[sourceIndex] == lab2.Registers[targetIndex]) {
                        lab2.pc =  getLabelAddr(instruction.getLabelName());;  
                    }

                    break;

                case "bne":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);
            
                    // if(R[rs]!=R[rt])
                    // PC=PC+1+BranchAddr
                    if (lab2.Registers[sourceIndex] != lab2.Registers[targetIndex]) {
                        lab2.pc = getLabelAddr(instruction.getLabelName());  
                    }

                    break; 
                case "lw":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    destIndex = Integer.parseInt(instruction.getDest(), 2);

                    memoryAddress = lab2.Registers[sourceIndex] + immediate;
                    lab2.Registers[destIndex] = lab2.dataMemory[memoryAddress];

                    break; 
                case "sw":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    immediate = Integer.parseInt(instruction.getImm(), 2);
                    targetIndex = Integer.parseInt(instruction.getTarget(), 2);

                    int memoryAddressSW = lab2.Registers[sourceIndex] + immediate;
                    lab2.dataMemory[memoryAddressSW] = lab2.Registers[targetIndex];

                    break; 

                case "j":

                    label = instruction.getImm();

                    j = getLabelAddr(label);
                    lab2.pc = j;

                    break;

                case "jr":

                    sourceIndex = Integer.parseInt(instruction.getSource(), 2);
                    lab2.pc = lab2.Registers[sourceIndex];

                    break;

                case "jal":

                    label = instruction.getImm();

                    j = getLabelAddr(label);
                    lab2.Registers[31] = lab2.pc + 1;
                    lab2.pc = j; 

                    break;

                default:
                    System.out.println("invalid instruction: " + instruction.getOperationString() );
                    System.exit(0);
                    break;

            }
        }

        public static int getLabelAddr(String labelName) {
            int addr = -1;
            
            if(labelName != null && labelName != "") {
                addr = lab2.labelToLineMap.get(labelName) - 1;
            }

            return addr;
        }
    }
