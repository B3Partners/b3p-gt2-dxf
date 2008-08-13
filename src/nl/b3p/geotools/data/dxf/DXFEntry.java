/*
 * $Id: DXFEntry.java 8672 2008-07-17 16:37:57Z Matthijs $
 */

package nl.b3p.geotools.data.dxf;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import java.io.EOFException;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a single feature from a DXF file, which can be a point, polyline(s)
 * or polygon(s). A DXF feature has name, key and urlLink attributes.
 * 
 * On construction, this class will parse the next feature from a LineNumberReader
 * and create a JTS Geometry.
 * 
 * @author Matthijs Laan, B3Partners
 */
class DXFEntry {
    public static final int TYPE_POINT = 0;
    public static final int TYPE_LINE = 1;
    public static final int TYPE_POLYGON = 2;

    private final int startingLineNumber;
    private final int type;
    private final String name;
    private final String key;
    private final String urlLink;
    private boolean parseError = false;
    private String errorDescription = null;
    
    private final GeometryFactory geometryFactory;
    
    private final Geometry geometry;
    
    public DXFEntry(LineNumberReader input, GeometryFactory geometryFactory) throws IOException, DXFParseException {
        String header;
        
        /* skip comments and empty lines */
        do {
            header = input.readLine();
            if(header == null) {
                throw new EOFException();
            }
        } while(header.length() == 0 || header.charAt(0) == ';');
        
        /* the current line number is the line of the header which has already
         * been read
         */
        this.startingLineNumber = input.getLineNumber();

        this.geometryFactory = geometryFactory;
        
        type = getEntryType(input, header);

        StringBuffer nameBuf = new StringBuffer(), keyBuf = new StringBuffer(), urlLinkBuf = new StringBuffer();
        int pos = 2;
        try {
            pos = readQuotedString(header, pos, nameBuf);
            pos = readQuotedString(header, pos, keyBuf);
            pos = readQuotedString(header, pos, urlLinkBuf);
        } catch(IndexOutOfBoundsException e) {
            throw new DXFParseException(this, "invalid entry header");
        }
        int numPts = -1;
        try {
            numPts = Integer.parseInt(header.substring(pos+1)); /* skip last ',' */
        } catch(Exception e) {
            throw new DXFParseException(this, "invalid entry header: can't parse NumPts");
        }
        
        name = nameBuf.toString();
        key = keyBuf.toString();
        urlLink = urlLinkBuf.toString();
        
        /* List of coordinates in order of appearance in the SDL entry. These 
         * coordinates may represent a point, polyline(s) or polygon(s) and may
         * contain a separator coordinate.
         * 
         */
        List<Coordinate> entryCoordinates = new ArrayList<Coordinate>();
        for(int i = 0; i < numPts; i++) {
            try {
                entryCoordinates.add(parseCoordinate(input.readLine()));
            } catch(NumberFormatException nfe) {
                throw new DXFParseException(input, "error parsing number: " + nfe.getMessage());
            } catch(Exception ioe) {
                throw new DXFParseException(input, ioe.getMessage());
            }
        }        

        geometry = createGeometry(entryCoordinates);     
    }

    private static int getEntryType(LineNumberReader lnr, String header) throws DXFParseException {
        if(header == null || header.length() == 0) {
            throw new DXFParseException(lnr, "Expected header, found empty line!");
        }
        char entryType = header.charAt(0);
        switch(entryType) {
            case 'M': return TYPE_POINT;
            case 'P': return TYPE_POLYGON; 
            case 'L': return TYPE_LINE; 
            default: throw new DXFParseException(lnr, "Unkown entry type (expected P, L or M): " + entryType);
        }        
    }

    public int getType() {
        return type;
    }
        
    private int readQuotedString(String s, int pos, StringBuffer output) throws IndexOutOfBoundsException, DXFParseException {
        if(s.charAt(pos) == ',') {
            ++pos;
        }
        if(s.charAt(pos++) != '"') {
            throw new DXFParseException(this, "invalid entry header");
        }
        int length = s.length();
        /* note that loop does not end at end of string, because a quoted string
         * must always end with a quote. This method may throw an
         * IndexOutOfBoundsException if there is no end quote
         */
        for(;;) {
            char c = s.charAt(pos++);
            if(c == '"') {
                /* check for stuffed double quote */
                if(pos < length) {
                    if(s.charAt(pos) == '"') {
                        output.append('"');
                        pos++;
                        continue;
                    }
                }
                /* not stuffed quote, end of quoted string */
                break;
            }
            output.append(c);
        }
        return pos;
    }
    
    private Coordinate parseCoordinate(String line) throws DXFParseException {
        if(line == null || line.trim().length() == 0) {
            throw new DXFParseException(this, "Unexpected end of input");
        }
        String[] coords = line.split(",");
        /* Y first, then X */
        return new Coordinate(Double.parseDouble(coords[1]), Double.parseDouble(coords[0]));
    }
    
    public int getStartingLineNumber() {
        return startingLineNumber;
    }
    
    public String getName() {
        return name;
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
    
    private Geometry createGeometry(List<Coordinate> entryCoordinates) throws DXFParseException {
        if(entryCoordinates.isEmpty()) {
            return new GeometryCollection(new Geometry[] {}, geometryFactory);
        } 

        try {
            switch(type) {
                case TYPE_POINT: return createPointGeometry(entryCoordinates);
                case TYPE_LINE: return createLineGeometry(entryCoordinates);
                case TYPE_POLYGON: return createPolygonGeometry(entryCoordinates);
                default: throw new IllegalStateException();
            }
        } catch(Exception e) {
            if(e instanceof DXFParseException) {
                throw (DXFParseException)e;
            } else {
                throw new DXFParseException(this, "Error creating geometry", e);
            }
        }
    }
    
    private Geometry createPointGeometry(List<Coordinate> entryCoordinates) throws DXFParseException {
        /* A SDL point entry is always one point, no multipoints */
        if(entryCoordinates.size() != 1) {
            throw new DXFParseException(this, "Point can have only one coordinate");
        }

        return geometryFactory.createMultiPoint(new Coordinate[]{entryCoordinates.get(0)});
    } 

    private Geometry createLineGeometry(List<Coordinate> entryCoordinates) throws DXFParseException {
        /* In SDL, a polyline entry may consist of multiple lines (polypolyline),
         * lines are separated by repeating the first coordinate of a line.
         * Lines are never closed, but may be constructed as such in a 
         * polyline of two lines with the last being the segment connecting
         * the last point of the first line to the first point of the first
         * line.
         * 
         * This SDL parser doesn't do anything special such as converting
         * those to a LinearRing, but just puts multiple lines in a 
         * MultiLineString.
         */

        final List<LineString> lineStrings = new ArrayList<LineString>();
        Iterator<Coordinate> coordinatesIterator = entryCoordinates.iterator();

        do {
            LineString lineString = createLineString(coordinatesIterator);
            if(!lineString.isEmpty()) {
                lineStrings.add(lineString);
            }
        } while(coordinatesIterator.hasNext());

        return geometryFactory.createMultiLineString((LineString[])lineStrings.toArray(new LineString[] {}));
    } 
    
    /* Create a single polyline from a SDL entry which may contain multiple
     * polylines (polypolyline).
     * The iterator is advanced to the first coordinate of the next polyline or
     * the end.
     */
    private LineString createLineString(Iterator<Coordinate> coordinatesIterator) throws DXFParseException {
        List<Coordinate> vertices = new ArrayList<Coordinate>();

        Coordinate first = coordinatesIterator.next();
        vertices.add(first);
        if(!coordinatesIterator.hasNext()) {
            addError("Polyline must have at least two coordinates");
            return geometryFactory.createLineString((Coordinate[])null);
        } 
        vertices.add(coordinatesIterator.next());

        /* Add points to coordinates list, except when next points equals
         * first point or there are no more points.
         */
        while(coordinatesIterator.hasNext()) {
            Coordinate c = coordinatesIterator.next();
            if(c.equals(first)) {
                /* end of polyline in a polypolyline entry */
                break;
            }
            vertices.add(c);
        }
        return geometryFactory.createLineString((Coordinate[])vertices.toArray(new Coordinate[] {}));
    }
    
    /**
     * Called when an error occurs but that error is constrained to a single
     * feature/subgeometry. Try to continue parsing features, but do set parseError
     * property to true and add and error message.
     * @param msg
     */
    private void addError(String msg) {
        if(!parseError) {
            parseError = true;
            errorDescription = "entry starting line " + startingLineNumber + ": " + msg;
        } else {
            errorDescription += "; " + msg;
        }
    }
    
    private Geometry createPolygonGeometry(List<Coordinate> entryCoordinates) throws DXFParseException {
        /* In practice, output from the SDF Loader does not follow the constraints
         * of the SDL file format very closely especially with polygons. Therefore
         * this parser ignores invalid polygons. This may result in empty or weird
         * features.
         * 
         * The attribute "parseError" is set to true if an entry contains parsing
         * errors, and the "errorDescription" attribute set to the details.
         */
        
        /* Note that this parser does not group polygons with the same key 
         * attribute into a multipolygon (only possible after parsing entire
         * file).
         */
        
        final List<LinearRing> rings = new ArrayList<LinearRing>();

        int index = 0;
        final int size = entryCoordinates.size();
        do {
            List<Coordinate> vertices = new ArrayList<Coordinate>(size);
            
            boolean closed = false;
            while(index < size) {
                Coordinate c = entryCoordinates.get(index++);
                if(vertices.size() != 0 && c.equals(vertices.get(vertices.size()-1))) {
                    /* ignore duplicate coordinate without setting error */
                    continue;
                }
                vertices.add(c);
                
                if(vertices.size() > 1 && c.equals(vertices.get(0))) {
                    closed = true;
                    break;
                }
            }
            
            if(!closed) { /* also end of entry */
                if(rings.isEmpty()) {
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
            if(vertices.size() <= 3) {
                boolean endOfEntry = index == size;
                boolean emptyEntry = rings.isEmpty();
                String error = "polygon " + (rings.size()+1) + " has insufficient vertices";
                if(endOfEntry) {
                    if(emptyEntry) {
                        error += " -- empty entry";
                    } else {
                        error += " -- entry has " + rings.size() + " valid polygons";
                    }
                }
                addError(error);
            } else {
                rings.add(geometryFactory.createLinearRing(vertices.toArray(new Coordinate[] {})));
            }
        } while(index < size);
        
        List<Polygon> polygons = foldHoles(rings);
        return geometryFactory.createMultiPolygon(GeometryFactory.toPolygonArray(polygons));      
    }
    
    /**
     * Convert a list of LinearRings to Polygons with holes. Uses the most naive
     * algorithm possible.
     */
    private List<Polygon> foldHoles(List<LinearRing> rings) {
        /* Convert LinearRings to Polygons with holes as follows:
         * for each polygon, check if it is within another polygon. Take the
         * first polygon that contains it (and is oriented differently ccw/cw),
         * and a it to the holes of that polygon and remove it from the list.
         * If a polygon already has holes, assume it is not within another 
         * polygon so don't check for that.
         */
        List<Polygon> polygons = new ArrayList<Polygon>(rings.size());
        List<Boolean> ccwCache = new ArrayList<Boolean>(rings.size());
        for(int i = 0; i < rings.size(); i++) {
            polygons.add(geometryFactory.createPolygon(rings.get(i), null));
            ccwCache.add(null);
        }
        
        int i = 0;
      
        while(i < polygons.size()) {
            Polygon p = polygons.get(i);
            if(p.getNumInteriorRing() == 0) {
                /* search for a polygon that contains p from the top */
                for(int j = 0; j < polygons.size(); j++) {
                    /* don't check if the polygon is within itself */
                    if(j != i) {
                        Polygon q = polygons.get(j);                    
                        try {
                            if(p.within(q) && (ccw(p,i,ccwCache) != ccw(q,j,ccwCache)) ) {
                                /* Recreate q with another hole */
                                LinearRing[] qHoles = new LinearRing[q.getNumInteriorRing()+1];
                                for(int k = 0; k < q.getNumInteriorRing(); k++) {
                                    qHoles[k] = (LinearRing) q.getInteriorRingN(k);
                                }
                                qHoles[qHoles.length-1] = (LinearRing)p.getExteriorRing();
                                q = geometryFactory.createPolygon((LinearRing)q.getExteriorRing(), qHoles);
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
                        } catch(TopologyException te) {
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
    
    private static boolean ccw(Polygon p, int index, List<Boolean> ccwCacheArray) {
        Boolean cached = ccwCacheArray.get(index);
        if(cached != null) {
            return cached;
        }
        Boolean ccw = CGAlgorithms.isCCW(p.getExteriorRing().getCoordinates());
        ccwCacheArray.set(index, ccw);
        return ccw;
    }
}