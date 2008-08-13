/*
 * $Id: DXFParseException.java 8672 2008-07-17 16:37:57Z Matthijs $
 */

package nl.b3p.geotools.data.dxf;

import java.io.LineNumberReader;

/**
 * Exception thrown while parsing a SDL file, adds line number in front of
 * specified message.
 *
 * @author Matthijs Laan, B3Partners
 */
public class DXFParseException extends Exception {

    private String message;

    DXFParseException(LineNumberReader reader, String message) {
        super();
        this.message = "line " + reader.getLineNumber() + ": " + message;
    }

    DXFParseException(DXFEntry entry, String message) {
        super();
        this.message = "entry starting at line " + entry.getStartingLineNumber() + ": " + message;
    }

    DXFParseException(DXFEntry entry, String message, Exception cause) {
        super(cause);
        this.message = "entry starting at line " + entry.getStartingLineNumber() + ": " + message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
