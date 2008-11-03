/*
 * [ 1719398 ] First shot at LWPOLYLINE
 * Peter Hopfgartner - hopfgartner
 *  
 */
package nl.b3p.geotools.data.dxf.entities;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTables;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFLwPolyline extends DXFEntity {

    private static final Log log = LogFactory.getLog(DXFLwPolyline.class);
    public String _id = "DXFLwPolyline";
    public int _flag = 0;
    public Vector<DXFLwVertex> theVertices = new Vector<DXFLwVertex>();

    public DXFLwPolyline(String name, int flag, int c, DXFLayer l, Vector<DXFLwVertex> v, int visibility, DXFLineType lineType, double thickness) {
        super(c, l, visibility, lineType, thickness);
        _id = name;

        if (v == null) {
            v = new Vector<DXFLwVertex>();
        }
        theVertices = v;
        _flag = flag;
        setName("DXFLwPolyline");
    }

    public DXFLwPolyline(DXFLayer l) {
        super(-1, l, 0, null, DXFTables.defaultThickness);
        setName("DXFLwPolyline");
    }

    public DXFLwPolyline() {
        super(-1, null, 0, null, DXFTables.defaultThickness);
        setName("DXFLwPolyline");
    }

    public DXFLwPolyline(DXFLwPolyline orig) {
        super(orig.getColor(), orig.getRefLayer(), 0, orig.getLineType(), orig.getThickness());
        _id = orig._id;

        for (int i = 0; i < orig.theVertices.size(); i++) {
            theVertices.add(new DXFLwVertex((DXFLwVertex) orig.theVertices.elementAt(i), true));
        }
        _flag = orig._flag;
        setName("DXFLwPolyline");
    }

    public static DXFLwPolyline read(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        String name = "";
        int visibility = 0, flag = 0, c = -1;
        DXFLineType lineType = null;
        Vector<DXFLwVertex> lv = new Vector<DXFLwVertex>();
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
                throw new IOException("DXF parse error" + ex.getLocalizedMessage());
            } catch (EOFException e) {
                doLoop = false;
                break;
            }

            switch (gc) {
                case TYPE:
                    String type = cvp.getStringValue(); // SEQEND ???
                    // geldt voor alle waarden van type
                    br.reset();
                    doLoop = false;
                    break;
                case X_1: //"10"
                    br.reset();
                    readLwVertices(br, lv);
                    break;
                case NAME: //"2"
                    name = cvp.getStringValue();
                    break;
                case LAYER_NAME: //"8"
                    l = univers.findLayer(cvp.getStringValue());
                    break;
                case LINETYPE_NAME: //"6"
                    lineType = univers.findLType(cvp.getStringValue());
                    break;
                case COLOR: //"62"
                    c = cvp.getShortValue();
                    break;
                case INT_1: //"70"
                    flag = cvp.getShortValue();
                    break;
                case VISIBILITY: //"60"
                    visibility = cvp.getShortValue();
                    break;
                default:
                    break;
            }

        }
        DXFLwPolyline e = new DXFLwPolyline(name, flag, c, l, lv, visibility, lineType, DXFTables.defaultThickness);
        if ((flag & 1) == 1) {
            e.setType(DXFEntity.TYPE_POLYGON);
        } else {
            e.setType(DXFEntity.TYPE_LINE);
        }
        e.setStartingLineNumber(sln);
        e.setUnivers(univers);
        log.debug(e.toString(name, flag, lv.size(), c, visibility, DXFTables.defaultThickness));
        log.debug(">>Exit at line: " + br.getLineNumber());
        return e;
    }

    public static void readLwVertices(DXFLineNumberReader br, Vector<DXFLwVertex> theVertices) throws IOException {
        double x = 0, y = 0, b = 0;
        boolean xFound = false, yFound = false;

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
                throw new IOException("DXF parse error" + ex.getLocalizedMessage());
            } catch (EOFException e) {
                doLoop = false;
                break;
            }

            switch (gc) {
                case TYPE:
                case X_1: //"10"
                    // check of vorig vertex opgeslagen kan worden
                    if (xFound && yFound) {
                        DXFLwVertex e = new DXFLwVertex(x, y, b);
                        log.debug(e.toString(b, x, y));
                        theVertices.add(e);
                        xFound = false;
                        yFound = false;
                        x = 0;
                        y = 0;
                        b = 0;
                    }
                    // TODO klopt dit???
                    if (gc == DXFGroupCode.TYPE) {
                        br.reset();
                        doLoop = false;
                        break;
                    }
                    x = cvp.getDoubleValue();
                    xFound = true;
                    break;
                case Y_1: //"20"
                    y = cvp.getDoubleValue();
                    yFound = true;
                    break;
                case DOUBLE_3: //"42"
                    b = cvp.getDoubleValue();
                    break;
                default:
                    break;
            }

        }
        log.debug(">Exit at line: " + br.getLineNumber());
    }

    @Override
    public Geometry getGeometry() {
        if (geometry == null) {
            Coordinate[] ca = toCoordinateArray();
            if (ca != null && ca.length > 1) {
                if (getType() == DXFEntity.TYPE_POLYGON) {
                    LinearRing lr = getUnivers().getGeometryFactory().createLinearRing(ca);
                    geometry = getUnivers().getGeometryFactory().createPolygon(lr, null);
                } else {
                    geometry = getUnivers().getGeometryFactory().createLineString(ca);
                }
            } else {
                addError("coordinate array faulty, size: " + (ca == null ? 0 : ca.length));
            }
        }
        return super.getGeometry();
    }

    public Coordinate[] toCoordinateArray() {
        if (theVertices == null) {
            addError("coordinate array can not be created.");
            return null;
        }
        Iterator it = theVertices.iterator();
        List<Coordinate> lc = new ArrayList<Coordinate>();
        Coordinate firstc = null;
        Coordinate lastc = null;
        while (it.hasNext()) {
            DXFLwVertex v = (DXFLwVertex) it.next();
            lastc = v.toCoordinate();
            if (firstc == null) {
                firstc = v.toCoordinate();
            }
            lc.add(lastc);
        }
        // geforceerd sluiten?
        if (getType() == DXFEntity.TYPE_POLYGON) {
            if (!firstc.equals2D(lastc)) {
                lc.add(firstc);
            }
        }

        /* TODO uitzoeken of lijn zichzelf snijdt, zo ja nodding
         * zie jts union:
         * Collection lineStrings = . . .
         * Geometry nodedLineStrings = (LineString) lineStrings.get(0);
         * for (int i = 1; i < lineStrings.size(); i++) {
         * nodedLineStrings = nodedLineStrings.union((LineString)lineStrings.get(i));
         * */
        return lc.toArray(new Coordinate[]{});
    }

    public String toString(String name, int flag, int numVert, int c, int visibility, double thickness) {
        StringBuffer s = new StringBuffer();
        s.append("DXFPolyline [");
        s.append("name: ");
        s.append(name + ", ");
        s.append("flag: ");
        s.append(flag + ", ");
        s.append("numVert: ");
        s.append(numVert + ", ");
        s.append("color: ");
        s.append(c + ", ");
        s.append("visibility: ");
        s.append(visibility + ", ");
        s.append("thickness: ");
        s.append(thickness);
        s.append("]");
        return s.toString();
    }

    @Override
    public DXFEntity translate(double x, double y) {
        return this;
    }
}

