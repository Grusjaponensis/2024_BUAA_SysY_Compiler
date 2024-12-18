package backend.instr;

public class MIPSSyscall extends MIPSInstr {
    /**
     * List of syscall:
     * <table>
     *     <thead>
     *         <tr>
     *             <th>Number in {@code $v0}</th>
     *             <th>Syscall Name</th>
     *             <th>Action</th>
     *         </tr>
     *     </thead>
     *     <tbody>
     *         <tr>
     *             <td>1</td>
     *             <td>Print Integer({@code putint})</td>
     *             <td>Prints an integer to the console({@code $a0} = integer to print)</td>
     *         </tr>
     *         <tr>
     *             <td>4</td>
     *             <td>Print String({@code putstr})</td>
     *             <td>Prints a string to the console({@code $a0} = address of string to print)</td>
     *         </tr>
     *         <tr>
     *             <td>11</td>
     *             <td>Print Character({@code putch})</td>
     *             <td>Prints a char to the console({@code $a0} = char to print)</td>
     *         </tr>
     *         <tr>
     *             <td>5</td>
     *             <td>Read Integer({@code getint})</td>
     *             <td>Reads an integer from the user input</td>
     *         </tr>
     *         <tr>
     *             <td>12</td>
     *             <td>Read Character({@code getchar})</td>
     *             <td>Reads a character from the user input</td>
     *         </tr>
     *         <tr>
     *             <td>10</td>
     *             <td>Exit</td>
     *             <td>Exits the program</td>
     *         </tr>
     *     </tbody>
     * </table>
     */
    public MIPSSyscall(String message) {
        super(MIPSInstrType.Syscall, false, message);
    }

    @Override
    public String toString() {
        return "syscall";
    }
}
