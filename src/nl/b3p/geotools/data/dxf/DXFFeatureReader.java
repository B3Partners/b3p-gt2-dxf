/*
 * $Id: DXFFeatureReader.java 8672 2008-07-17 16:37:57Z Matthijs $
 */
package nl.b3p.geotools.data.dxf;

import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import nl.b3p.geotools.data.dxf.entities.DXFEntity;
import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
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
    private DXFLineNumberReader lnr;
    private String version;
    private DXFUnivers theUnivers;
    private Iterator<DXFEntity> entityIterator;

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
        this.lnr = new DXFLineNumberReader(new InputStreamReader(cis));

        createFeatureType(typeName);

        /* Read in complete dxf file
         * Required because blocks are defined upfront and later inserted
         **/
        theUnivers = new DXFUnivers();
        theUnivers.read(lnr);
        Vector<DXFEntity> theEntities = theUnivers.theEntities;
        if (theEntities != null) {
            theEntities = new Vector<DXFEntity>();
        }
        entityIterator = theEntities.iterator();

        version = theUnivers._header._ACADVER;
    }

    private void createFeatureType(String typeName) throws DataSourceException {
        CoordinateReferenceSystem crs = null;
        String[] csMetadata = null; //TODO hoe zit dit in dxf
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

    public Feature next() throws IOException, IllegalAttributeException, NoSuchElementException {
        DXFEntity ent = null;
        // TODO beter om helemaal niet op de vector te zetten indien unsupported
        do {
            ent = (DXFEntity) entityIterator.next();
        } while (ent.getType()== DXFEntity.TYPE_UNSUPPORTED && 
                entityIterator.hasNext());
        /* XXX use key as featureID? */
        MultiPoint point = null;
        MultiLineString line = null;
        MultiPolygon polygon = null;
        Geometry g = ent.getGeometry();
        switch (ent.getType()) {
            case DXFEntity.TYPE_POINT:
                point = (MultiPoint) g;
                break;
            case DXFEntity.TYPE_LINE:
                line = (MultiLineString) g;
                break;
            case DXFEntity.TYPE_POLYGON:
                polygon = (MultiPolygon) g;
                break;
        }
        Feature f = ft.create(new Object[]{
                    point,
                    line,
                    polygon,
                    ent.getName(),
                    ent.getKey(),
                    ent.getUrlLink(),
                    new Integer(ent.getStartingLineNumber()),
                    new Boolean(ent.isParseError()),
                    ent.getErrorDescription()
                });
        return f;
    }

    public boolean hasNext() throws IOException {
        return entityIterator.hasNext();
    }

    public void close() throws IOException {
        lnr.close();
    }

    public long getByteCount() {
        return cis.getByteCount();
    }
}
