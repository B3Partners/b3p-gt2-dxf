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

public class DXFPolyline extends DXFEntity {

    private static final Log log = LogFactory.getLog(DXFPolyline.class);
    public String _id;
    /*
    Polyline flag (bit-coded); default is 0:
    1 = This is a closed polyline (or a polygon mesh closed in the M direction).
    2 = Curve-fit vertices have been added.
    4 = Spline-fit vertices have been added.
    8 = This is a 3D polyline.
    16 = This is a 3D polygon mesh.
    32 = The polygon mesh is closed in the N direction.
    64 = The polyline is a polyface mesh.
    128 = The linetype pattern is generated continuously around the vertices of this polyline.
     */
    public int _flag = 0;
    public Vector<DXFVertex> theVertex = new Vector<DXFVertex>();

    public DXFPolyline(String name, int flag, int c, DXFLayer l, Vector<DXFVertex> v, int visibility, DXFLineType lineType, double thickness) {
        super(c, l, visibility, lineType, thickness);
        _id = name;

        if (v == null) {
            v = new Vector<DXFVertex>();
        }
        theVertex = v;
        _flag = flag;
        setName("DXFPolyline");
    }

    public DXFPolyline(DXFLayer l) {
        super(-1, l, 0, null, DXFTables.defaultThickness);
        setName("DXFPolyline");
    }

    public DXFPolyline() {
        super(-1, null, 0, null, DXFTables.defaultThickness);
        setName("DXFPolyline");
    }

    public DXFPolyline(DXFPolyline orig) {
        super(orig.getColor(), orig.getRefLayer(), 0, orig.getLineType(), orig.getThickness());
        _id = orig._id;

        for (int i = 0; i < orig.theVertex.size(); i++) {
            theVertex.add(new DXFVertex((DXFVertex) orig.theVertex.elementAt(i), true));
        }
        _flag = orig._flag;
        setName("DXFPolyline");
    }

    public static DXFPolyline read(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        String name = "";
        int visibility = 0, flag = 0, c = -1;
        DXFLineType lineType = null;
        Vector<DXFVertex> lv = new Vector<DXFVertex>();
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
                    if (SEQEND.equals(type)) {
                        doLoop = false;
                    } else if (VERTEX.equals(type)) {
                        lv.add(DXFVertex.read(br, univers));
                    } else {
                        br.reset();
                        doLoop = false;
                    }
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
        DXFPolyline e = new DXFPolyline(name, flag, c, l, lv, visibility, lineType, DXFTables.defaultThickness);
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
        if (theVertex == null) {
            addError("coordinate array can not be created.");
            return null;
        }
        Iterator it = theVertex.iterator();
        List<Coordinate> lc = new ArrayList<Coordinate>();
        while (it.hasNext()) {
            DXFVertex v = (DXFVertex) it.next();
            lc.add(v.toCoordinate());
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
}

