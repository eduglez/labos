package ooxmlreports;

import java.io.BufferedReader;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FilterReaderXmlCommentsEreaser extends FilterReader{

    // This variable holds the current line.
    // If null and emitNewline is false, a newline must be fetched.
    String curLine;

    // This is the index of the first unread character in curLine.
    // If at any time curLineIx == curLine.length, curLine is set to null.
    int curLineIx;

    // If true, the newline at the end of curLine has not been returned.
    // It would have been more convenient to append the newline
    // onto freshly fetched lines. However, that would incur another
    // allocation and copy.
    boolean emitNewline;

    // Matcher used to test every line
    Matcher matcher;

    public FilterReaderXmlCommentsEreaser(InputStreamReader in) {
        super(new BufferedReader(in));
        Pattern pattern = Pattern.compile("(\\x3c\\x21\\x2d\\x2d)|(\\x2d\\x2d\\x3e)");
        matcher = pattern.matcher("");
    }

    // This overridden method fills cbuf with characters read from in.
    public int read(char cbuf[], int off, int len) throws IOException {
        // Fetch new line if necessary
        if (curLine == null && !emitNewline) {
            getNextLine();
        }

        // Return characters from current line
        if (curLine != null) {
            int num = Math.min(len, Math.min(cbuf.length-off,
                                         curLine.length()-curLineIx));
            // Copy characters from curLine to cbuf
            for (int i=0; i<num; i++) {
                cbuf[off++] = curLine.charAt(curLineIx++);
            }

            // No more characters in curLine
            if (curLineIx == curLine.length()) {
                curLine = null;

                // Is there room for the newline?
                if (num < len && off < cbuf.length) {
                    cbuf[off++] = '\n';
                    emitNewline = false;
                    num++;
                }
            }

            // Return number of character read
            return num;
        } else if (emitNewline && len > 0) {
            // Emit just the newline
            cbuf[off] = '\n';
            emitNewline = false;
            return 1;
        } else if (len > 0) {
            // No more characters left in input reader
            return -1;
        } else {
            // Client did not ask for any characters
            return 0;
        }
    }

    // Get next matching line
    private void getNextLine() throws IOException {
        curLine = ((BufferedReader)in).readLine();
        while (curLine != null) {
            matcher.reset(curLine);
            if (matcher.find()) {
                emitNewline = true;
                curLineIx = 0;
                return;
            }
            curLine = ((BufferedReader)in).readLine();
        }
        return;
    }
    public boolean ready() throws IOException {
        return curLine != null || emitNewline || in.ready();
    }
    public boolean markSupported() {
        return false;
    }




}
