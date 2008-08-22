package nl.b3p.geotools.data.dxf.header;

import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;


import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFLineType {

    private static final Log log = LogFactory.getLog(DXFLineType.class);
    public static final String DEFAULT_NAME = "default";
    private float _motif[] = parseTxt("_");
    public String _name = "DXFLineType";                       // 2
    public String _value = "";					// 3
    public float _length = 0;					// 40
    public float _count = 0;					// 73
    public Vector<Float> _spacing = new Vector<Float>();	// 49

    public DXFLineType() {
    }

    public DXFLineType(String nom, String value, float length, float count, Vector<Float> spacing) {
        _name = nom;
        _value = value;
        _length = length;
        _count = count;

        if (spacing != null) {
            _spacing = spacing;
        }
        _motif = parseDXF();
    }

    public static DXFLineType read(DXFLineNumberReader br) throws IOException {
        String value = "", name = "";
        Vector<Float> spacing = new Vector<Float>();
        float count = 0, length = 0;

        int sln = br.getLineNumber();
        log.debug(">Enter at line: " + sln);
        DXFCodeValuePair cvp = null;
        DXFGroupCode gc = null;

        boolean doLoop = true;
        while (doLoop) {
            cvp = new DXFCodeValuePair();
            try {
                gc = cvp.read(br);
            } catch (DXFParseException ex) {
                throw new IOException("DXF parse error", ex);
            } catch (EOFException e) {
                doLoop = false;
                break;
            }

            switch (gc) {
                case TYPE:
                case VARIABLE_NAME:
                    br.reset();
                    doLoop = false;
                    break;
                case NAME:
                    name = cvp.getStringValue();
                    break;
                case TEXT_OR_NAME_2:
                    value = cvp.getStringValue();
                    break;
                case INT_4:
                    count = cvp.getShortValue();
                    break;
                case DOUBLE_1:
                    length = (float) cvp.getDoubleValue();
                    break;
                case REPEATED_DOUBLE_VALUE:
                    spacing.add((float) cvp.getDoubleValue());
                    break;
                default:
                    break;
            }

        }

        log.debug(">Exit at line: " + br.getLineNumber());
        if (value.equals("") && name.equals("")) {
            return null;
        } else {
            DXFLineType e = new DXFLineType(name, value, length, count, spacing);
            log.debug(e.toString(name, value, length, count));
            return e;
        }
    }

    public String toString(String name, String value, float length, float count) {
        StringBuffer s = new StringBuffer();
        s.append("DXFLineType [");
        s.append("name: ");
        s.append(name + ", ");
        s.append("value: ");
        s.append(value + ", ");
        s.append("length: ");
        s.append(length + ", ");
        s.append("count: ");
        s.append(count);
        s.append("]");
        return s.toString();
    }

    public static float[] parseTxt(String s) {
        boolean no = false;
        boolean incr = false;
        s = s.trim();
        if (s.length() % 2 == 1) {
            s += " ";
        }
        char[] strChr = new char[s.length()];
        strChr = s.toCharArray();
        int end = strChr.length - (strChr.length % 2);

        float[] floatLine = new float[end * 2];
        int j = 0;
        for (int i = 0; i < floatLine.length; i++) {
            if (strChr[j] == ' ') {
                floatLine[i] = 0.0f;
                incr = true;
            } else if (strChr[j] == '.') {
                floatLine[i] = 1.0f;
            } else if (strChr[j] == '-') {
                floatLine[i] = 5.0f;
            } else if (strChr[j] == '_') {
                floatLine[i] = 10.0f;
                no = true;
            } else {
                floatLine[i] = 3.0f;
            }
            i++;
            if (no) {
                floatLine[i] = 0.0f;
            } else if (incr) {
                floatLine[i] = 10.0f;
            } else {
                floatLine[i] = 3.0f;
            }
            j++;
            no = false;
            incr = false;
        }
        if (floatLine.length > 0) {
            return floatLine;
        } else {
            return new float[]{1.0f, 0.0f};
        }
    }

    public float[] parseDXF() {
        if (_count != this._spacing.size()) {
            return DXFTables.defautMotif;
        }
        float[] ret = new float[(int) _count];

        for (int i = 0; i < _count; i++) {
            ret[i] = ((Math.abs(_spacing.elementAt(i)) * 100) / this._length) / 10;
        }

        if (ret.length == 0) {
            ret = DXFTables.defautMotif;
        }
        return ret;
    }

    public float[] getMotif() {
        return _motif;
    }
}
