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

    public DXFFeatureReader(DXFUnivers theUnivers, String typeName) throws IOException, DXFParseException {
        this.theUnivers = theUnivers;

        if (theUnivers == null) {
            throw new IOException("No univers found!");
        }
        entityIterator = theUnivers.theEntities.iterator();

        createFeatureType(typeName);
    }

    private void createFeatureType(String typeName) throws DataSourceException {
        CoordinateReferenceSystem crs = null;
        try {
            //TODO  er wordt nog geen crs opgehaald uit DXF, voorlopig 28992 default
//            CRSFactory crsFactory = ReferencingFactoryFinder.getCRSFactory(null);
            // Bij wkt is het belangrijk dat het een projectie betreft PROJCS en niet slechts een GEOCS, omdat dan de srid op -1 komt te staan in postgis
//            String PROJCS_RDNEW_WKT = "PROJCS[\"Amersfoort / RD New\",GEOGCS[\"Amersfoort\",DATUM[\"Amersfoort\",SPHEROID[\"Bessel 1841\",6377397.155,299.1528128,AUTHORITY[\"EPSG\",\"7004\"]],AUTHORITY[\"EPSG\",\"6289\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4289\"]],PROJECTION[\"Oblique_Stereographic\"],PARAMETER[\"latitude_of_origin\",52.15616055555555],PARAMETER[\"central_meridian\",5.38763888888889],PARAMETER[\"scale_factor\",0.9999079],PARAMETER[\"false_easting\",155000],PARAMETER[\"false_northing\",463000],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AUTHORITY[\"EPSG\",\"28992\"]]";
//            crs = crsFactory.createFromWKT(PROJCS_RDNEW_WKT);
            crs = CRS.decode("EPSG:28992");
        } catch (Exception e) {
            throw new DataSourceException("Error parsing CoordinateSystem!");
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
                        AttributeTypeFactory.newAttributeType("visible", Boolean.class),
                        AttributeTypeFactory.newAttributeType("entryLineNumber", Integer.class),
                        AttributeTypeFactory.newAttributeType("parseError", Boolean.class),
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
                    new Boolean(ent.isVisible()),
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
