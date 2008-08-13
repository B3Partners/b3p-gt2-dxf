/*
 * $Id: DXFFeatureReader.java 8672 2008-07-17 16:37:57Z Matthijs $
 */
package nl.b3p.geotools.data.dxf;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.geotools.data.DataSourceException;
import org.geotools.data.FeatureReader;
import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypes;
import org.geotools.feature.IllegalAttributeException;
import org.geotools.feature.type.GeometricAttributeType;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.commons.io.input.CountingInputStream;

/**
 * @author Matthijs Laan, B3Partners
 */
public class DXFFeatureReader implements FeatureReader {

    private GeometryFactory gf;
    private FeatureType ft;
    private CountingInputStream cis;
    private LineNumberReader lnr;
    private String version;
    private Map<String, String[]> metadata = new HashMap<String, String[]>();
    private static final int MARK_SIZE = 8 * 1024;

    public DXFFeatureReader(URL url, String typeName) throws IOException, DXFParseException {

        /* TODO for loading large files, obtain a total stream size from somewhere
         * and use an apache commons CountingInputStream to provide current
         * progress info.
         */

        /* Note that a LineNumberReader may read more bytes than are strictly
         * returned as characters of lines read.
         */
        this.cis = new CountingInputStream(url.openStream());

        /* TODO provide param to override encoding! This uses the platform
         * default encoding, SDF Loader Help doesn't specify encoding
         */
        this.lnr = new LineNumberReader(new InputStreamReader(cis));

        parseHeader();
        skipCommentsCheckEOF();
        createFeatureType(typeName);
    }

    private void parseHeader() throws IOException {
        skipCommentsCheckEOF();
        for (;;) {
            /* mark the start of the next line */
            lnr.mark(MARK_SIZE);
            String line = lnr.readLine();
            if (line == null) {
                /* eof in or before header, empty file? */
                break;
            }
            if (line.trim().length() != 0) {
                int firstChar = line.charAt(0);
                if (firstChar != '#') {
                    /* end of headers, reset stream */
                    lnr.reset();
                    break;
                }

                /* handle header line */
                String lcline = line.toLowerCase();
                if (lcline.startsWith("#version")) {
                    version = line.substring(line.indexOf('=') + 1);
                } else if (lcline.startsWith("#metadata_begin")) {
                    /* use the lowercase name as map key, case insensitive */
                    String name = lcline.substring(line.indexOf('=') + 1);
                    List<String> contents = new ArrayList<String>();
                    String headerLine;
                    while ((headerLine = lnr.readLine()) != null) {
                        if (headerLine.toLowerCase().startsWith("#metadata_end")) {
                            break;
                        }
                        contents.add(headerLine.substring(1));
                    }
                    if (!contents.isEmpty()) {
                        metadata.put(name, contents.toArray(new String[]{}));
                    }
                }
            } else {
                /* skip empty line */
            }
        }
    }

    private void createFeatureType(String typeName) throws DataSourceException {
        CoordinateReferenceSystem crs = null;
        String[] csMetadata = metadata.get("coordinatesystem");
        if (csMetadata != null) {
            String wkt = csMetadata[0];
            try {
                /* parse WKT */
                CRSFactory crsFactory = ReferencingFactoryFinder.getCRSFactory(null);
                crs = crsFactory.createFromWKT(wkt);
            } catch (Exception e) {
                throw new DataSourceException("Error parsing CoordinateSystem WKT: \"" + wkt + "\"");
            }
        }

        try {
            /* GeometricAttributeType creates the GeometryFactory */
            GeometricAttributeType pointType = new GeometricAttributeType("the_geom_point", MultiPoint.class, true, null, crs, null);
            GeometricAttributeType lineType = new GeometricAttributeType("the_geom_line", MultiLineString.class, true, null, crs, null);
            GeometricAttributeType polygonType = new GeometricAttributeType("the_geom_polygon", MultiPolygon.class, true, null, crs, null);
            gf = pointType.getGeometryFactory(); /* XXX does it matter which GF is used? All have the same CRS... */
            ft = FeatureTypes.newFeatureType(
                    new AttributeType[]{
                        pointType,
                        lineType,
                        polygonType,
                        AttributeTypeFactory.newAttributeType("name", String.class),
                        AttributeTypeFactory.newAttributeType("key", String.class),
                        AttributeTypeFactory.newAttributeType("urlLink", String.class),
                        AttributeTypeFactory.newAttributeType("entryLineNumber", Integer.class),
                        AttributeTypeFactory.newAttributeType("parseError", Boolean.class),
                        AttributeTypeFactory.newAttributeType("error", String.class)
                    }, typeName);
        } catch (Exception e) {
            throw new DataSourceException("Error creating FeatureType", e);
        }
    }

    public FeatureType getFeatureType() {
        return ft;
    }

    /**
     * Skip empty and comment lines and return EOF status
     * @return true if EOF
     * @throws java.io.IOException
     */
    private boolean skipCommentsCheckEOF() throws IOException {
        String line;
        do {
            /* mark the start of the next line */
            lnr.mark(MARK_SIZE);
            line = lnr.readLine();
            if (line == null) {
                /* skipped comments till end of file */
                return true;
            }
        } while (line.length() == 0 || line.charAt(0) == ';');

        /* EOF or the last line we read wasn't a comment or empty line. reset 
         * the stream so the next readLine() call will return the line we just
         * read
         */
        lnr.reset();
        return false;
    }

    public Feature next() throws IOException, IllegalAttributeException, NoSuchElementException {
        try {
            DXFEntry entry = new DXFEntry(lnr, gf);
            /* XXX use key as featureID? */
            MultiPoint point = null;
            MultiLineString line = null;
            MultiPolygon polygon = null;
            Geometry g = entry.getGeometry();
            switch (entry.getType()) {
                case DXFEntry.TYPE_POINT:
                    point = (MultiPoint) g;
                    break;
                case DXFEntry.TYPE_LINE:
                    line = (MultiLineString) g;
                    break;
                case DXFEntry.TYPE_POLYGON:
                    polygon = (MultiPolygon) g;
                    break;
            }
            Feature f = ft.create(new Object[]{
                        point,
                        line,
                        polygon,
                        entry.getName(),
                        entry.getKey(),
                        entry.getUrlLink(),
                        new Integer(entry.getStartingLineNumber()),
                        new Boolean(entry.isParseError()),
                        entry.getErrorDescription()
                    });
            return f;
        } catch (DXFParseException ex) {
            throw new IOException("SDL parse error", ex);
        } catch (EOFException e) {
            return null;
        }
    }

    public boolean hasNext() throws IOException {
        /* this method should be fast as it will probably be called before each 
         * next(). skipCommentsCheckEOF will mark()/reset() the stream
         */
        return !skipCommentsCheckEOF();
    }

    public void close() throws IOException {
        lnr.close();
    }

    public long getByteCount() {
        return cis.getByteCount();
    }
}
