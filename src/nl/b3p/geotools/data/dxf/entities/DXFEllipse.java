package nl.b3p.geotools.data.dxf.entities;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import java.io.EOFException;
import java.io.IOException;


import java.util.ArrayList;
import java.util.List;
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

    public DXFEllipse(DXFPoint centre, DXFPoint p, double r, double s, double e, int c, DXFLayer l, int visibility, DXFLineType typeLine) {
        super(c, l, visibility, typeLine, DXFTables.defaultThickness);
        _centre = centre;
        _point = p;
        _ratio = r;
        _end = e;
        _start = s;
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

    public Coordinate[] toCoordinateArray() {

        /*
         * This functions returns an array containing 36 points to draw an
         * ellipse.
         *
         * @param x {double} X coordinate
         * @param y {double} Y coordinate
         * @param a {double} Semimajor axis
         * @param b {double} Semiminor axis
         * @param angle {double} Angle of the ellipse
        
        function calculateEllipse(x, y, a, b, angle, steps) 
        {
        if (steps == null)
        steps = 36;
        var points = [];
        
        // Angle is given by Degree Value
        var beta = -angle * (Math.PI / 180); //(Math.PI/180) converts Degree Value into Radiance
        var sinbeta = Math.sin(beta);
        var cosbeta = Math.cos(beta);
        
        for (var i = 0; i < 360; i += 360 / steps) 
        {
        var alpha = i * (Math.PI / 180) ;
        var sinalpha = Math.sin(alpha);
        var cosalpha = Math.cos(alpha);
        
        var X = x + (a * cosalpha * cosbeta - b * sinalpha * sinbeta);
        var Y = y + (a * cosalpha * sinbeta + b * sinalpha * cosbeta);
        
        points.push(new OpenLayers.Geometry.Point(X, Y));
        }
        
        return points;
        }
         */
        if (true) {
            addError("coordinate array can not be created.");
            return null;
        }
        List<Coordinate> lc = new ArrayList<Coordinate>();
        double startAngle = 0.0;
        double endAngle = 2 * Math.PI;
        double segAngle = 2 * Math.PI / DXFUnivers.NUM_OF_SEGMENTS;
        double angle = startAngle;
        for (;;) {
            //TODO
            double x = _centre._point.getX() + _ratio * Math.cos(angle);
            double y = _centre._point.getY() + _ratio * Math.sin(angle);
            Coordinate c = new Coordinate(x, y);
            lc.add(c);

            if (angle >= endAngle) {
                break;
            }
            angle += segAngle;
            if (angle > endAngle) {
                angle = endAngle;
            }
        }
        return lc.toArray(new Coordinate[]{});
    }

    @Override
    public Geometry getGeometry() {
        if (geometry == null) {
            Coordinate[] ca = toCoordinateArray();
            if (ca != null && ca.length > 1) {
                LinearRing lr = getUnivers().getGeometryFactory().createLinearRing(ca);
                geometry = getUnivers().getGeometryFactory().createPolygon(lr, null);
            } else {
                addError("coordinate array faulty, size: " + (ca == null ? 0 : ca.length));
            }
        }
        return super.getGeometry();
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

    @Override
    public DXFEntity translate(double x, double y) {
        return this;
    }
}
