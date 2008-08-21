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
import java.io.IOException;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Matthijs Laan, B3Partners
 */
public class DXFFeatureReader implements FeatureReader {

    private static final Log log = LogFactory.getLog(DXFFeatureReader.class);
    private FeatureType ft;
    private DXFUnivers theUnivers;
    private Iterator<DXFEntity> entityIterator;

    public DXFFeatureReader(DXFUnivers theUnivers) throws IOException, DXFParseException {
        this.theUnivers = theUnivers;

        if (theUnivers == null) {
            throw new IOException("No univers found!");
        }
        entityIterator = theUnivers.theEntities.iterator();

        createFeatureType();


    }

    private void createFeatureType() throws DataSourceException {
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
            GeometricAttributeType geometryType = new GeometricAttributeType("the_geom", Geometry.class, true, null, crs, null);
            ft = FeatureTypes.newFeatureType(
                    new AttributeType[]{
                        geometryType,
                        AttributeTypeFactory.newAttributeType("name", String.class),
                        AttributeTypeFactory.newAttributeType("key", String.class),
                        AttributeTypeFactory.newAttributeType("urlLink", String.class),
                        AttributeTypeFactory.newAttributeType("entryLineNumber", Integer.class),
                        AttributeTypeFactory.newAttributeType("parseError", Boolean.class),
                        AttributeTypeFactory.newAttributeType("error", String.class)
                    }, "dxf");
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
        } while (ent.getType() == DXFEntity.TYPE_UNSUPPORTED &&
                entityIterator.hasNext());
        
        Geometry g = ent.getGeometry();

        Feature f = ft.create(new Object[]{
                    g,
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
    }
}
