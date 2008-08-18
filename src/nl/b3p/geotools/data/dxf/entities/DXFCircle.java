package nl.b3p.geotools.data.dxf.entities;

import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import java.awt.geom.Ellipse2D;
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

public class DXFCircle extends DXFEntity {
    private static final Log log = LogFactory.getLog(DXFCircle.class);

    private Ellipse2D.Double _e = new Ellipse2D.Double();
    public DXFPoint _point = new DXFPoint();
    public double _radius = 0;

    public DXFCircle(DXFPoint p, double r, DXFLineType lineType, int c, DXFLayer l, int visibility, double thickness) {
        super(c, l, visibility, lineType, thickness);
        _point = p;
        _radius = r;

    }

    public DXFCircle() {
        super(0, null, 0, null, DXFTables.defaultThickness);
    }

    public DXFCircle(DXFCircle orig) {
        super(orig._color, orig._refLayer, 0, orig._lineType, orig._thickness);
        _point = new DXFPoint(orig._point);
        _radius = orig._radius;

    }

    public static DXFCircle read(DXFLineNumberReader br, DXFUnivers univers) throws NumberFormatException, IOException {

        int visibility = 0, c = 0;
        double x = 0, y = 0, r = 0, thickness = 1;
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
                case LINETYPE_NAME: //"6"
                    lineType = univers.findLType(cvp.getStringValue());
                    break;
                case LAYER_NAME: //"8"
                    l = univers.findLayer(cvp.getStringValue());
                    break;
                case THICKNESS: //"39"
                    thickness = cvp.getDoubleValue();
                    break;
                case VISIBILITY: //"60"
                    visibility = cvp.getShortValue();
                    break;
                case COLOR: //"62"
                    c = cvp.getShortValue();
                    break;
                case X_1: //"10"
                    x = cvp.getDoubleValue();
                    break;
                case Y_1: //"20"
                    y = cvp.getDoubleValue();
                    break;
                case DOUBLE_1: //"40"
                    r = cvp.getDoubleValue();
                    break;
                default:
                    break;
            }

        }
        DXFCircle e = new DXFCircle(new DXFPoint(x, y, c, l, visibility, 1), r, lineType, c, l, visibility, thickness);
        e.setType(DXFEntity.TYPE_UNSUPPORTED);
        e.setStartingLineNumber(sln);
        log.debug(e.toString(x, y, c, visibility, thickness));
        return e;
    }

    public String toString(double x, double y, int c, int visibility, double thickness) {
        StringBuffer s = new StringBuffer();
        s.append("DXFCircle [");
        s.append("x: ");
        s.append(x+", ");
        s.append("y: ");
        s.append(y+", ");
        s.append("color: ");
        s.append(c+", ");
        s.append("visibility: ");
        s.append(visibility+", ");
        s.append("thickness: ");
        s.append(thickness);
        s.append("]");
        return s.toString();
    }
}
