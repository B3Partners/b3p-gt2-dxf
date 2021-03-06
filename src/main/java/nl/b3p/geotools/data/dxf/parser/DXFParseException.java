/*
 * $Id: DXFParseException.java 8672 2008-07-17 16:37:57Z Matthijs $
 */

package nl.b3p.geotools.data.dxf.parser;

import nl.b3p.geotools.data.dxf.entities.DXFEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Exception thrown while parsing a SDL file, adds line number in front of
 * specified message.
 *
 * @author Matthijs Laan, B3Partners
 */
public class DXFParseException extends Exception {
    private static final Log log = LogFactory.getLog(DXFParseException.class);

    private String message;

    public DXFParseException(DXFLineNumberReader reader, String message) {
        super();
        this.message = "line " + reader.getLineNumber() + ": " + message;
    }

    public DXFParseException(DXFEntity entry, String message) {
        super();
        this.message = "entry starting at line " + entry.getStartingLineNumber() + ": " + message;
    }

    public DXFParseException(DXFEntity entry, String message, Exception cause) {
        super(cause);
        this.message = "entry starting at line " + entry.getStartingLineNumber() + ": " + message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
