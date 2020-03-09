package cz.test.console.terminal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.BiConsumer;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

/**
 * Terminal that allows to enter input while the console is still being written to.
 * 
 * @author jarom
 *
 */
public class PkgTerminal {
	
	// Terminal that is put in raw mode.
	private Terminal terminal;
	
	// Reader for input from the terminal.
	private NonBlockingReader reader;
	
	// Writer to write to that terminal.
	private PrintWriter writer;
	
	// Current input line accummulated so far.
	private String lineSofar = "";
	
	// Whether the terminal should stop processing input.
	private volatile boolean close = false;
	
	// Consumer to process the line.
	private BiConsumer<PkgTerminal, String> lineConsumer;
	
	public PkgTerminal() throws IOException {
		
		// Build the terminal and put it to raw mode (do not block on waiting for input).
		terminal = TerminalBuilder.builder()
				.system(true)
				.build();
		terminal.enterRawMode();
		
		reader = terminal.reader();
		writer = terminal.writer();
	}
	
	public void setLineConsumer(BiConsumer<PkgTerminal, String> lineConsumer) {
		synchronized(this) {
			this.lineConsumer = lineConsumer;
		}
	}
	
	/**
	 * Start reading input. If there is a write while waiting for input the write operation is finished, the original 
	 * input line is repeated and the input can continue.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	/**
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void startReading() throws IOException, InterruptedException {
		// while the terminal should still process input.
		while (!close) {
			if (reader.ready()) {
				// There is some input available.
				char read = 0;
				BiConsumer<PkgTerminal, String> lc = null;
				synchronized (this) {
					read = (char) reader.read();
					// write the read character to console to provide feedback to user as with a standard console.
					writer.write(read);
					writer.flush();
					lc = lineConsumer;
				}
				if (read != 13 && read != 10) {
					// The read character is not related to enter, add it to current line of text.
					lineSofar = lineSofar + read;
				} else {
					// Enter was pressed, we do not have to worry about empty lines.
					writer.write("\n");
					if (lc != null && lineSofar.length() > 0) {
						lc.accept(this, lineSofar);
					}
					lineSofar = "";
				}
			} else {
				// Don't keep the CPU too busy.
				Thread.sleep(100);
			}
		}
	}
	
	/**
	 * Indicate the console processing should stop.
	 */
	public void close() {		
		close = true;
	}
	
	/**
	 * Write string to console. 
	 * Don't write text one line at a time, but provide the complete text, since the currently accummulated input 
	 * line is repeated immediately after the written text.
	 * @param s
	 */
	public void write(String s) {
		synchronized (this) {
			writer.write("\n");
			writer.write(s);
			writer.write("\n");
			writer.write(lineSofar);
			writer.flush();
		}
	}
	
}
