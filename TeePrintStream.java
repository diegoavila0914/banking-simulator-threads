/* Name: Diego Avila
 * Course: CNT 4714 Fall 2024
 * Assignment Title:
 * 		Project 2 - Synchronized/Cooperating Threads - A Banking Simulation
 * Due Date: September 22, 2024
 */

package development;

import java.io.OutputStream;
import java.io.PrintStream;

public class TeePrintStream extends PrintStream {
	private final PrintStream second;

    public TeePrintStream(OutputStream mainStream, PrintStream secondStream) {
        super(mainStream);  // The main output stream (file)
        this.second = secondStream;  // The second output stream (console)
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        try {
            super.write(buf, off, len);  // Write to the file
            second.write(buf, off, len);  // Write to the console
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {
        super.flush();  // Flush the file stream
        second.flush();  // Flush the console stream
    }
}
