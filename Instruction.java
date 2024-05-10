public class Instruction {

    private String operationString;
    private String opcode;
    private String dest;
    private String source;
    private String target;
    private String function;
    private String shamt;
    private String immediate;
    private String jump;
    private String labelName;

    // Constructor
    public Instruction() {

    }

    public Instruction(String operationString, String opcode, String dest, String source, String target, String function, String shamt, String immediate, String jump, String labelName) {
        this.operationString = operationString;
        this.opcode = opcode;
        this.dest = dest;
        this.source = source;
        this.shamt = shamt;
        this.target = target;
        this.function = function;
        this.immediate = immediate;
        this.jump = jump;
        this.labelName = labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelName() {
        return this.labelName;
    }
    
    public void setOperationString(String operationString) {
        this.operationString = operationString;
    }

    public String getOperationString() {
        return this.operationString;
    }

    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    public String getOpcode() {
        return opcode;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getDest() {
        return dest;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getFunction() {
        return function;
    }

    public void setShamt(String shamt) {
        this.shamt = shamt;
    }

    public String getShamt() {
        return shamt;
    }

    public void setImm(String immediate) {
        this.immediate = immediate;
    }

    public String getImm() {
        return immediate;
    }

    public void setJump(String jump) {
        this.jump = jump;
    }

    public String getJump() {
        return jump;
    }

}