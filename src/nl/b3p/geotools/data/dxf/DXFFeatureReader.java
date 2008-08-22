/*
 * $Id: DXFFeatureReader.java 8672 2008-07-17 16:37:57Z Matthijs $
 */
package nl.b3p.geotools.data.dxf;

import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
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
    public static String GEOCS_RDNEW_WKT;
    public static String GEOCS_WSG84_WKT;
    

    static {
        StringBuffer t = new StringBuffer();
        t.append("GEOGCS[\"Amersfoort\",");
        t.append("DATUM[\"Amersfoort\",");
        t.append("SPHEROID[\"Bessel 1841\",6377397.155,299.1528128],");
        t.append("TOWGS84[565.04,49.91,465.84,-0.40939438743923684,-0.35970519561431136,1.868491000350572,0.8409828680306614]");
        t.append("],");
        t.append("PRIMEM[\"Greenwich\",0],");
        t.append("UNIT[\"degree\",0.01745329251994328]");
        t.append("],");
        t.append("PROJECTION[\"Oblique_Stereographic\"],");
        t.append("PARAMETER[\"latitude_of_origin\",52.15616055555555],");
        t.append("PARAMETER[\"central_meridian\",5.38763888888889],");
        t.append("PARAMETER[\"scale_factor\",0.9999079],");
        t.append("PARAMETER[\"false_easting\",155000],");
        t.append("PARAMETER[\"false_northing\",463000],");
        t.append("UNIT[\"metre\",1]");
        t.append("]");
        GEOCS_RDNEW_WKT = t.toString();

        GEOCS_WSG84_WKT = "GEOGCS[\"LL84\",DATUM[\"WGS 84\",SPHEROID[\"WGS 84\",6378137,0],TOWGS84[0,0,0,0,0,0,0]],PRIMEM[\"Greenwich\",0],UNIT[\"Degrees\",0.01745329252]]";
    }

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
        //TODO  er wordt nog geen crs opgehaald uit DXF, voorlopig 28992 default
        String wkt = GEOCS_RDNEW_WKT;
        log.debug("CRS WKT: " + wkt);

        try {
            /* parse WKT */
            CRSFactory crsFactory = ReferencingFactoryFinder.getCRSFactory(null);
            crs = crsFactory.createFromWKT(wkt);
        } catch (Exception e) {
            throw new DataSourceException("Error parsing CoordinateSystem WKT: \"" + wkt + "\"");
        }

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
                    }, "dxf");
        } catch (Exception e) {
            throw new DataSourceException("Error creating FeatureType", e);
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
