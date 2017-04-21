package Instruction;

/***
 * Interface for Instruction
 */
public interface Instruction {
    /***
     * Returns the unique identifier of Instruction
     * @return
     */
    public String getInstructionID();

    /***
     * Returns the Instruction type
     * @return
     */
    public String getInstructionType();

    /***
     * Generates Java code for the Instruction
     * @return
     */
    public String generateCode();

    /***
     * Set unqiue identifier of the Instruction
     * @param id
     */
    public void setId(String id);
}


