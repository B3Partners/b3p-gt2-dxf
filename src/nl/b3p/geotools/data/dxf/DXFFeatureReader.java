/*
 * $Id: DXFFeatureReader.java 8672 2008-07-17 16:37:57Z Matthijs $
 */
package nl.b3p.geotools.data.dxf;

import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.referencing.CRS;
import org.geotools.referencing.NamedIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Matthijs Laan, B3Partners
 */
public class DXFFeatureReader implements FeatureReader {

    private static final Log log = LogFactory.getLog(DXFFeatureReader.class);
    private FeatureType ft;
    private DXFUnivers theUnivers;
    private Iterator<DXFEntity> entityIterator;

    public DXFFeatureReader(DXFUnivers theUnivers, String typeName, String srs) throws IOException, DXFParseException {
        this.theUnivers = theUnivers;

        if (theUnivers == null) {
            throw new IOException("No univers found!");
        }
        entityIterator = theUnivers.theEntities.iterator();

        createFeatureType(typeName, srs);
    }

    private void createFeatureType(String typeName, String srs) throws DataSourceException {
        CoordinateReferenceSystem crs = null;
        try {
            crs = CRS.decode(srs);
        } catch (Exception e) {
            throw new DataSourceException("Error parsing CoordinateSystem srs: \"" + srs + "\"");
        }

        int SRID = -1;
        if (crs != null) {
            try {
                Set ident = crs.getIdentifiers();
                if ((ident != null && !ident.isEmpty())) {
                    String code = ((NamedIdentifier) ident.toArray()[0]).getCode();
                    SRID = Integer.parseInt(code);
                }
            } catch (Exception e) {
                log.error("SRID could not be determined from crs!");
            }
        }
        log.info("SRID used by feature reader: " + SRID);

        try {
            GeometricAttributeType geometryType = new GeometricAttributeType("the_geom", Geometry.class, true, null, crs, null);
            ft = FeatureTypes.newFeatureType(
                    new AttributeType[]{
                        geometryType,
                        AttributeTypeFactory.newAttributeType("name", String.class),
                        AttributeTypeFactory.newAttributeType("key", String.class),
                        AttributeTypeFactory.newAttributeType("urlLink", String.class),
                        AttributeTypeFactory.newAttributeType("lineType", String.class),
                        AttributeTypeFactory.newAttributeType("color", String.class),
                        AttributeTypeFactory.newAttributeType("layer", String.class),
                        AttributeTypeFactory.newAttributeType("thickness", Double.class),
                        AttributeTypeFactory.newAttributeType("visible", Integer.class),
                        AttributeTypeFactory.newAttributeType("entryLineNumber", Integer.class),
                        AttributeTypeFactory.newAttributeType("parseError", Integer.class),
                        AttributeTypeFactory.newAttributeType("error", String.class)
                    }, typeName);
        } catch (Exception e) {
            throw new DataSourceException("Error creating FeatureType: " + typeName, e);
        }
    }

    public FeatureType getFeatureType() {
        return ft;
    }

    public Feature next() throws IOException, IllegalAttributeException, NoSuchElementException {
        DXFEntity ent = (DXFEntity) entityIterator.next();

        Geometry g = ent.getGeometry();

        Feature f = ft.create(new Object[]{
                    g,
                    ent.getName(),
                    ent.getKey(),
                    ent.getUrlLink(),
                    ent.getLineTypeName(),
                    ent.getColorRGB(),
                    ent.getRefLayerName(),
                    new Double(ent.getThickness()),
                    new Integer(ent.isVisible()?1:0),
                    new Integer(ent.getStartingLineNumber()),
                    new Integer(ent.isParseError()?1:0),
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
