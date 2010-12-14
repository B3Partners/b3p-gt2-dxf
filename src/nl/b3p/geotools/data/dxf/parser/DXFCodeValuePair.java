package nl.b3p.geotools.data.dxf.parser;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Chris
 */
public class DXFCodeValuePair {
    private static final Log log = LogFactory.getLog(DXFCodeValuePair.class);

    private DXFGroupCode gc;
    private short shortValue = 0;
    private int intValue = 0;
    private String stringValue = null;
    private boolean booleanValue = false;
    private double doubleValue = 0.0;
    private long longValue = 0l;
    private String handleHexValue = null;
    private String idHexValue = null;
    private String binHexValue = null;

    public DXFGroupCode read(DXFLineNumberReader br) throws IOException, DXFParseException {

        br.mark();
        // 1ste regel van paar
        String regel = br.readLine();

        int gcInt;
        try {
            gcInt = Integer.parseInt(regel);
        } catch (NumberFormatException nfe) {
            throw new DXFParseException(br, "Unknown Group Code: " + regel);
        }

        gc = DXFGroupCode.getGroupCode(gcInt);

        // 2de regel van paar
        regel = br.readLine();

        switch (gc.toType()) {
            case STRING:
                stringValue = regel;
                break;
            case HANDLEHEX:
                handleHexValue = regel;
                break;
            case IDHEX:
                idHexValue = regel;
                break;
            case BINHEX:
                binHexValue = regel;
                break;
            case SHORT:
                shortValue = Short.parseShort(regel);
                break;
            case INTEGER:
                intValue = Integer.parseInt(regel);
                break;
            case LONG:
                longValue = Long.parseLong(regel);
                break;
            case BOOLEAN:
                booleanValue = Boolean.parseBoolean(regel);
                break;
            case DOUBLE:
                doubleValue = Double.parseDouble(regel);
                break;
            default:
                throw new DXFParseException(br, "Unknown value type for Group Code: " + gc);
        }


        return gc;
    }

    public DXFGroupCode getGc() {
        return gc;
    }

    private IllegalArgumentException wrongTypeException(String askedType) {
        return new IllegalArgumentException("Wrong value type requested (" + askedType + ") for group code #" + gc.toInt() + " which is of type " + gc.toType());
    }

    public short getShortValue() throws IllegalArgumentException {
        if (!DXFValueType.SHORT.equals(gc.toType())) {
            throw wrongTypeException("short");
        }
        return shortValue;
    }

    public int getIntValue() throws IllegalArgumentException {
        if (!DXFValueType.INTEGER.equals(gc.toType())) {
            throw wrongTypeException("integer");
        }
        return intValue;
    }

    public String getStringValue() throws IllegalArgumentException {
        if (!DXFValueType.STRING.equals(gc.toType())) {
            throw wrongTypeException("string");
        }
        return stringValue;
    }

    public boolean isBooleanValue() throws IllegalArgumentException {
        if (!DXFValueType.BOOLEAN.equals(gc.toType())) {
            throw wrongTypeException("boolean");
        }
        return booleanValue;
    }

    public double getDoubleValue() throws IllegalArgumentException {
        if (!DXFValueType.DOUBLE.equals(gc.toType())) {
            throw wrongTypeException("double");
        }
        return doubleValue;
    }

    public long getLongValue() throws IllegalArgumentException {
        if (!DXFValueType.LONG.equals(gc.toType())) {
            throw wrongTypeException("long");
        }
        return longValue;
    }

    public String getHandleHexValue() throws IllegalArgumentException {
        if (!DXFValueType.HANDLEHEX.equals(gc.toType())) {
            throw wrongTypeException("handlehex");
        }
        return handleHexValue;
    }

    public String getIdHexValue() throws IllegalArgumentException {
        if (!DXFValueType.IDHEX.equals(gc.toType())) {
            throw wrongTypeException("idhex");
        }
        return idHexValue;
    }

    public String getBinHexValue() throws IllegalArgumentException {
        if (!DXFValueType.BINHEX.equals(gc.toType())) {
            throw new Error("Wrong Value Type for Group Code!");
        }
        return binHexValue;
    }

}
