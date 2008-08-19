package nl.b3p.geotools.data.dxf.entities;

import java.io.EOFException;
import java.io.IOException;

import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTables;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFArc extends DXFEntity {

    private static final Log log = LogFactory.getLog(DXFArc.class);
    public DXFPoint _point = new DXFPoint();
    public double _radius = 0;
    protected double _angle1 = 0;
    protected double _angle2 = 0;

    public DXFArc(double a1, double a2, DXFPoint p, double r, DXFLineType lineType, int c, DXFLayer l, int visibility, double thickness) {
        super(c, l, visibility, lineType, thickness);
        _point = p;
        _radius = r;
        _angle1 = a1;
        _angle2 = a2;
        _thickness = thickness;

    }

    public DXFArc() {
        super(-1, null, 0, null, DXFTables.defaultThickness);
        _point = new DXFPoint();
        _radius = 0;

    }

    public DXFArc(DXFArc orig) {
        super(orig._color, orig._refLayer, 0, orig._lineType, orig._thickness);
        _point = new DXFPoint(orig._point);
        _radius = orig._radius;
        _angle1 = orig._angle1;
        _angle2 = orig._angle2;
    }

    public static DXFArc read(DXFLineNumberReader br, DXFUnivers univers) throws NumberFormatException, IOException {

        double x = 0, y = 0, r = 0, a1 = 0, a2 = 0, thickness = 0;
        int visibility = 0, c = 0;
        DXFLineType lineType = null;
        DXFLayer l = null;

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
            } catch (EOFException e) {
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
                case COLOR: //"62"
                    c = cvp.getShortValue();
                    break;
                case LINETYPE_NAME: //"6"
                    lineType = univers.findLType(cvp.getStringValue());
                    break;
                case DOUBLE_1: //"40"
                    r = cvp.getDoubleValue();
                    break;
                case X_1: //"10"
                    x = cvp.getDoubleValue();
                    break;
                case Y_1: //"20"
                    y = cvp.getDoubleValue();
                    break;
                case ANGLE_1: //"50"
                    a1 = cvp.getDoubleValue();
                    break;
                case ANGLE_2: //"51"
                    a2 = cvp.getDoubleValue();
                    break;
                case VISIBILITY: //"60"
                    visibility = cvp.getShortValue();
                    break;
                case THICKNESS:
                    thickness = cvp.getDoubleValue();
                    break;
                default:
                    break;
            }

        }
        DXFArc e = new DXFArc(a1, a2, new DXFPoint(x, y, c, null, visibility, 1), r, lineType, c, l, visibility, thickness);
        e.setType(DXFEntity.TYPE_POLYGON);
        e.setStartingLineNumber(sln);
        log.debug(e.toString(c, r, x, y, a1, a2, visibility, thickness));
        log.debug(">>Exit at line: " + br.getLineNumber());
        return e;
    }

    public String toString(int c,
            double r,
            double x,
            double y,
            double a1,
            double a2,
            int visibility,
            double thickness) {
        StringBuffer s = new StringBuffer();
        s.append("DXFArc [");
        s.append("color: ");
        s.append(c + ", ");
        s.append("r: ");
        s.append(r + ", ");
        s.append("x: ");
        s.append(x + ", ");
        s.append("y: ");
        s.append(y + ", ");
        s.append("a1: ");
        s.append(a1 + ", ");
        s.append("a2: ");
        s.append(a2 + ", ");
        s.append("visibility: ");
        s.append(visibility + ", ");
        s.append("thickness: ");
        s.append(thickness);
        s.append("]");
        return s.toString();
    }
}
