package nl.b3p.geotools.data.dxf.entities;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import java.io.EOFException;
import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
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

public class DXFLine extends DXFEntity {

    private static final Log log = LogFactory.getLog(DXFLine.class);
    public DXFPoint _a = new DXFPoint();
    public DXFPoint _b = new DXFPoint();

    public DXFLine(DXFPoint a, DXFPoint b, int c, DXFLayer l, DXFLineType lineType, double thickness, int visibility) {
        super(c, l, visibility, lineType, thickness);
        _a = a;
        _b = b;
        setName("DXFLine");
    }

    public DXFLine() {
        super(-1, null, 0, null, DXFTables.defaultThickness);
        setName("DXFLine");
    }

    public DXFLine(DXFLine original) {
        super(original.getColor(), original.getRefLayer(), 0, original.getLineType(), original.getThickness());
        _a = new DXFPoint(original._a);
        _b = new DXFPoint(original._b);
        setName("DXFLine");
    }

    public static DXFLine read(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        DXFLayer l = null;
        double x1 = 0, y1 = 0, x2 = 0, y2 = 0, thickness = 0;
        DXFLineType lineType = null;
        int visibility = 0, c = -1;

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
                case X_1: //"10"
                    x1 = cvp.getDoubleValue();
                    break;
                case Y_1: //"20"
                    y1 = cvp.getDoubleValue();
                    break;
                case X_2: //"11"
                    x2 = cvp.getDoubleValue();
                    break;
                case Y_2: //"21"
                    y2 = cvp.getDoubleValue();
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
                case THICKNESS: //"39"
                    thickness = cvp.getDoubleValue();
                    break;
                case VISIBILITY: //"60"
                    visibility = cvp.getShortValue();
                    break;
                default:
                    break;
            }

        }
        DXFLine e = new DXFLine(new DXFPoint(x1, y1, c, l, visibility, 1),
                new DXFPoint(x2, y2, c, l, visibility, 1),
                c,
                l,
                lineType,
                thickness,
                visibility);
        e.setType(DXFEntity.TYPE_LINE);
        e.setStartingLineNumber(sln);
        e.setUnivers(univers);
        log.debug(e.toString(x1, y1, x2, y2, c, visibility, thickness));
        log.debug(">Exit at line: " + br.getLineNumber());
        return e;
    }

    public Coordinate[] toCoordinateArray() {
        if (_a == null || _b == null) {
            addError("coordinate array can not be created.");
            return null;
        }
        return new Coordinate[]{_a.toCoordinate(), _b.toCoordinate()};
    }

    @Override
    public Geometry getGeometry() {
        Geometry g = super.getGeometry();
        if (g == null) {
            Coordinate[] ca = toCoordinateArray();
            if (ca != null && ca.length > 1) {
                return getUnivers().getGeometryFactory().createLineString(ca);
            } else {
                addError("coordinate array faulty, size: " + (ca == null ? 0 : ca.length));
            }
        }
        return g;
    }

    public String toString(double x1, double y1, double x2, double y2, int c, int visibility, double thickness) {
        StringBuffer s = new StringBuffer();
        s.append("DXFLine [");
        s.append("x1: ");
        s.append(x1 + ", ");
        s.append("y1: ");
        s.append(y1 + ", ");
        s.append("x2: ");
        s.append(x2 + ", ");
        s.append("y2: ");
        s.append(y2 + ", ");
        s.append("color: ");
        s.append(c + ", ");
        s.append("visibility: ");
        s.append(visibility + ", ");
        s.append("thickness: ");
        s.append(thickness);
        s.append("]");
        return s.toString();
    }
}
