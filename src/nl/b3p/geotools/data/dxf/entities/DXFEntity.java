package nl.b3p.geotools.data.dxf.entities;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.parser.DXFColor;
import nl.b3p.geotools.data.dxf.parser.DXFConstants;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import nl.b3p.geotools.data.dxf.parser.DXFUnivers;

public abstract class DXFEntity implements DXFConstants {

    private static final Log log = LogFactory.getLog(DXFEntity.class);

    /* feature write */
    public static final int TYPE_POINT = 0;
    public static final int TYPE_LINE = 1;
    public static final int TYPE_POLYGON = 2;
    public static final int TYPE_UNSUPPORTED = -1;
    private int type = TYPE_UNSUPPORTED;
    private String _name = null;
    private String key = null;
    private String urlLink = null;
    private boolean parseError = false;
    private String errorDescription = null;
    private Geometry geometry = null;
    /* dxf read */
    private DXFUnivers univers;
    private int startingLineNumber = -1;
    private DXFLineType _lineType;
    private int _color;
    private DXFLayer _refLayer;
    private double _thickness;
    private boolean visible = true;

    public DXFEntity(int c, DXFLayer l, int visibility, DXFLineType lineType, double thickness) {
        _lineType = lineType;
        _refLayer = l;
        _color = c;
        _thickness = thickness;

        if (visibility == 0) {
            visible = true;
        } else {
            visible = false;
        }
    }

    public String getName() {
        return _name;
    }

    public String getKey() {
        return key;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public boolean isParseError() {
        return parseError;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public int getStartingLineNumber() {
        return startingLineNumber;
    }

    public void setStartingLineNumber(int startingLineNumber) {
        this.startingLineNumber = startingLineNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setName(String name) {
        this._name = name;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public DXFLineType getLineType() {
        return _lineType;
    }

    public String getLineTypeName() {
        if (_lineType == null) {
            return DXFLineType.DEFAULT_NAME;
        }
        return _lineType._name;
    }

    public void setLineType(DXFLineType lineType) {
        this._lineType = lineType;
    }

    public int getColor() {
        return _color;
    }

    public String getColorRGB() {
        return DXFColor.getColorRGB(_color);
    }

    public void setColor(int color) {
        this._color = color;
    }

    public DXFLayer getRefLayer() {
        return _refLayer;
    }

    public String getRefLayerName() {
        if (_refLayer == null) {
            return DXFLayer.DEFAULT_NAME;
        }
        return _refLayer.getName();
    }

    public void setRefLayer(DXFLayer refLayer) {
        this._refLayer = refLayer;
    }

    public double getThickness() {
        return _thickness;
    }

    public void setThickness(double thickness) {
        this._thickness = thickness;
    }

    public DXFUnivers getUnivers() {
        return univers;
    }

    public void setUnivers(DXFUnivers univers) {
        this.univers = univers;
    }

    /**
     * Called when an error occurs but that error is constrained to a single
     * feature/subgeometry. Try to continue parsing features, but do set parseError
     * property to true and add and error message.
     * @param msg
     */
    public void addError(String msg) {
        if (!parseError) {
            parseError = true;
            errorDescription = "entry starting line " + getStartingLineNumber() + ": " + msg;
        } else {
            errorDescription += "; " + msg;
        }
    }

    protected Geometry createGeometry(List<Coordinate> entryCoordinates) throws DXFParseException {
        if (entryCoordinates.isEmpty()) {
            return new GeometryCollection(new Geometry[]{}, univers.getGeometryFactory());
        }

        try {
            switch (type) {
                case DXFEntity.TYPE_POINT:
                    return createPointGeometry(entryCoordinates);
                case DXFEntity.TYPE_LINE:
                    return createLineGeometry(entryCoordinates);
                case DXFEntity.TYPE_POLYGON:
                    return createPolygonGeometry(entryCoordinates);
                default:
                    throw new IllegalStateException();
            }
        } catch (Exception e) {
            if (e instanceof DXFParseException) {
                throw (DXFParseException) e;
            } else {
                throw new DXFParseException(this, "Error creating geometry", e);
            }
        }
    }

    protected Geometry createPointGeometry(List<Coordinate> entryCoordinates) throws DXFParseException {
        if (entryCoordinates.size() != 1) {
            throw new DXFParseException(this, "Point can have only one coordinate");
        }

        return univers.getGeometryFactory().createMultiPoint(new Coordinate[]{entryCoordinates.get(0)});
    }

    protected Geometry createLineGeometry(List<Coordinate> entryCoordinates) throws DXFParseException {
        final List<LineString> lineStrings = new ArrayList<LineString>();
        Iterator<Coordinate> coordinatesIterator = entryCoordinates.iterator();

        do {
            LineString lineString = createLineString(coordinatesIterator);
            if (!lineString.isEmpty()) {
                lineStrings.add(lineString);
            }
        } while (coordinatesIterator.hasNext());

        return univers.getGeometryFactory().createMultiLineString((LineString[]) lineStrings.toArray(new LineString[]{}));
    }

    /* Create a single polyline from a SDL entry which may contain multiple
     * polylines (polypolyline).
     * The iterator is advanced to the first coordinate of the next polyline or
     * the end.
     */
    protected LineString createLineString(Iterator<Coordinate> coordinatesIterator) throws DXFParseException {
        List<Coordinate> vertices = new ArrayList<Coordinate>();

        Coordinate first = coordinatesIterator.next();
        vertices.add(first);
        if (!coordinatesIterator.hasNext()) {
            addError("Polyline must have at least two coordinates");
            return univers.getGeometryFactory().createLineString((Coordinate[]) null);
        }
        vertices.add(coordinatesIterator.next());

        /* Add points to coordinates list, except when next points equals
         * first point or there are no more points.
         */
        while (coordinatesIterator.hasNext()) {
            Coordinate c = coordinatesIterator.next();
            if (c.equals(first)) {
                /* end of polyline in a polypolyline entry */
                break;
            }
            vertices.add(c);
        }
        return univers.getGeometryFactory().createLineString((Coordinate[]) vertices.toArray(new Coordinate[]{}));
    }

    protected Geometry createPolygonGeometry(List<Coordinate> entryCoordinates) throws DXFParseException {
        final List<LinearRing> rings = new ArrayList<LinearRing>();

        int index = 0;
        final int size = entryCoordinates.size();
        do {
            List<Coordinate> vertices = new ArrayList<Coordinate>(size);

            boolean closed = false;
            while (index < size) {
                Coordinate c = entryCoordinates.get(index++);
                if (vertices.size() != 0 && c.equals(vertices.get(vertices.size() - 1))) {
                    /* ignore duplicate coordinate without setting error */
                    continue;
                }
                vertices.add(c);

                if (vertices.size() > 1 && c.equals(vertices.get(0))) {
                    closed = true;
                    break;
                }
            }

            if (!closed) { /* also end of entry */
                if (rings.isEmpty()) {
                    addError("unclosed polygon -- empty entry");
                } else {
                    addError("unclosed polygon -- entry has " + rings.size() + " valid polygons");
                }
                break;
            }

            /* polygon must have minimum of 3 different vertices for a triangle,
             * but in the coordinate array the last vertex must be the same as 
             * the first to close it, so minimum of 4 vertices
             */
            if (vertices.size() <= 3) {
                boolean endOfEntry = index == size;
                boolean emptyEntry = rings.isEmpty();
                String error = "polygon " + (rings.size() + 1) + " has insufficient vertices";
                if (endOfEntry) {
                    if (emptyEntry) {
                        error += " -- empty entry";
                    } else {
                        error += " -- entry has " + rings.size() + " valid polygons";
                    }
                }
                addError(error);
            } else {
                rings.add(univers.getGeometryFactory().createLinearRing(vertices.toArray(new Coordinate[]{})));
            }
        } while (index < size);

        List<Polygon> polygons = foldHoles(rings);
        return univers.getGeometryFactory().createMultiPolygon(GeometryFactory.toPolygonArray(polygons));
    }

    /**
     * Convert a list of LinearRings to Polygons with holes. Uses the most naive
     * algorithm possible.
     */
    protected List<Polygon> foldHoles(List<LinearRing> rings) {
        /* Convert LinearRings to Polygons with holes as follows:
         * for each polygon, check if it is within another polygon. Take the
         * first polygon that contains it (and is oriented differently ccw/cw),
         * and a it to the holes of that polygon and remove it from the list.
         * If a polygon already has holes, assume it is not within another 
         * polygon so don't check for that.
         */
        List<Polygon> polygons = new ArrayList<Polygon>(rings.size());
        List<Boolean> ccwCache = new ArrayList<Boolean>(rings.size());
        for (int i = 0; i < rings.size(); i++) {
            polygons.add(univers.getGeometryFactory().createPolygon(rings.get(i), null));
            ccwCache.add(null);
        }

        int i = 0;

        while (i < polygons.size()) {
            Polygon p = polygons.get(i);
            if (p.getNumInteriorRing() == 0) {
                /* search for a polygon that contains p from the top */
                for (int j = 0; j < polygons.size(); j++) {
                    /* don't check if the polygon is within itself */
                    if (j != i) {
                        Polygon q = polygons.get(j);
                        try {
                            if (p.within(q) && (ccw(p, i, ccwCache) != ccw(q, j, ccwCache))) {
                                /* Recreate q with another hole */
                                LinearRing[] qHoles = new LinearRing[q.getNumInteriorRing() + 1];
                                for (int k = 0; k < q.getNumInteriorRing(); k++) {
                                    qHoles[k] = (LinearRing) q.getInteriorRingN(k);
                                }
                                qHoles[qHoles.length - 1] = (LinearRing) p.getExteriorRing();
                                q = univers.getGeometryFactory().createPolygon((LinearRing) q.getExteriorRing(), qHoles);
                                polygons.set(j, q);
                                polygons.remove(i);
                                ccwCache.remove(i);
                                i--;
                                /* found a polygon that contains p, so continue with 
                                 * the next polygon, however do not increase i
                                 * because p is removed from the polygons list.
                                 */
                                break;
                            }
                        } catch (TopologyException te) {
                            /* within() can throw this exception with "side-location conflict"
                             * message, for example these polygons:
                             * POLYGON ((209032.59375 552769.4375, 209031.75 552770.375, 209027.75 552767.125, 209025.78125 552769.375, 209026.734375 552770.625, 209027.8125 552771.625, 209029.5 552773, 209031.75 552770.375, 209032.59375 552769.4375))
                             * POLYGON ((209032.59375 552769.4375, 209031.75 552770.375, 209029.5 552773, 209028.71875 552773.8125, 209028.421875 552773.625, 209028.4375 552773.25, 209028.4375 552772.5, 209028.25 552772.0625, 209027.8125 552771.625, 209015.4375 552761.9375, 209000 552749.4375, 208988.5 552740.75, 208991.265625 552737.1875, 209008.171875 552750.4375, 209032.59375 552769.4375))
                             */
                            /* add error message and continue */
                            addError(te.getMessage());
                        }
                    }
                }
            }
            i++;
        }

        return polygons;
    }

    protected static boolean ccw(Polygon p, int index, List<Boolean> ccwCacheArray) {
        Boolean cached = ccwCacheArray.get(index);
        if (cached != null) {
            return cached;
        }
        Boolean ccw = CGAlgorithms.isCCW(p.getExteriorRing().getCoordinates());
        ccwCacheArray.set(index, ccw);
        return ccw;
    }

    public void setParseError(boolean parseError) {
        this.parseError = parseError;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }
}
