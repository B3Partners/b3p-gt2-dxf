package nl.b3p.geotools.data.dxf.entities;

import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import java.awt.geom.Arc2D;
import java.io.EOFException;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTables;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFEllipse extends DXFEntity {

    private static final Log log = LogFactory.getLog(DXFEllipse.class);
    public DXFPoint _centre = new DXFPoint();
    public DXFPoint _point = new DXFPoint();
    public double _ratio = 0;
    public double _start = 0;
    public double _end = 0;
    private Arc2D.Double _e = new Arc2D.Double();

    public DXFEllipse(DXFPoint centre, DXFPoint p, double r, double s, double e, int c, DXFLayer l, int visibility, DXFLineType typeLine) {
        super(c, l, visibility, typeLine, DXFTables.defaultThickness);
        _centre = centre;
        _point = p;
        _ratio = r;
        _end = e;
        _start = s;
        _e.setArcType(Arc2D.OPEN);
        setName("DXFEllipse");
    }

    public DXFEllipse() {
        super(-1, null, 0, null, DXFTables.defaultThickness);
        setName("DXFEllipse");
    }

    public static DXFEllipse read(DXFLineNumberReader br, DXFUnivers univers) throws NumberFormatException, IOException {

        int visibility = 0, c = 0;
        double x = 0, y = 0, x1 = 0, y1 = 0, r = 0, s = 0, e = 0;
        DXFLayer l = null;
        DXFLineType lineType = null;

        int sln = br.getLineNumber();
        log.debug(">>Enter at line: " + sln);

        DXFCodeValuePair cvp = null;
        DXFGroupCode gc = null;

        boolean doLoop = true;
        while (doLoop) {
            cvp = new DXFCodeValuePair();
            try {
                gc = cvp.read(br);
            } catch (DXFParseException ex) {
                throw new IOException("DXF parse error", ex);
            } catch (EOFException eofe) {
                doLoop = false;
                break;
            }

            switch (gc) {
                case TYPE:
                    String type = cvp.getStringValue();
                    // geldt voor alle waarden van type
                    br.reset();
                    doLoop = false;
                    break;
                case LAYER_NAME: //"8"
                    l = univers.findLayer(cvp.getStringValue());
                    break;
                case LINETYPE_NAME: //"6"
                    lineType = univers.findLType(cvp.getStringValue());
                    break;
                case VISIBILITY: //"60"
                    visibility = cvp.getShortValue();
                    break;
                case COLOR: //"62"
                    c = cvp.getShortValue();
                    break;
                case DOUBLE_1: //"40"
                    r = cvp.getDoubleValue();
                    break;
                case DOUBLE_2: //"41"
                    s = cvp.getDoubleValue();
                    break;
                case DOUBLE_3: //"42"
                    e = cvp.getDoubleValue();
                    break;
                case X_1: //"10"
                    x = cvp.getDoubleValue();
                    break;
                case Y_1: //"20"
                    y = cvp.getDoubleValue();
                    break;
                case X_2: //"11"
                    x1 = cvp.getDoubleValue();
                    break;
                case Y_2: //"21"
                    y1 = cvp.getDoubleValue();
                    break;
                default:
                    break;
            }

        }
        DXFEllipse m = new DXFEllipse(
                new DXFPoint(x, y, c, l, visibility, 1),
                new DXFPoint(x1, y1, c, l, visibility, 1),
                r, s, e, c, l, visibility, lineType);
        m.setType(DXFEntity.TYPE_POLYGON);
        m.setStartingLineNumber(sln);
        m.setUnivers(univers);
        log.debug(m.toString(visibility, c, r, s, e, x, y, x1, y1));
        log.debug(">>Exit at line: " + br.getLineNumber());
        return m;
    }

    public String toString(int visibility, int c, double r, double t, double e, double x, double y, double x1, double y1) {
        StringBuffer s = new StringBuffer();
        s.append("DXFEllipse [");
        s.append("visibility: ");
        s.append(visibility + ", ");
        s.append("color: ");
        s.append(c + ", ");
        s.append("r: ");
        s.append(r + ", ");
        s.append("s: ");
        s.append(t + ", ");
        s.append("e: ");
        s.append(e + ", ");
        s.append("x: ");
        s.append(x + ", ");
        s.append("y: ");
        s.append(y + ", ");
        s.append("x1: ");
        s.append(x1 + ", ");
        s.append("y1: ");
        s.append(y1);
        s.append("]");
        return s.toString();
    }
}
