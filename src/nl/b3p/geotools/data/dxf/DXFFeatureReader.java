/*
 * $Id: DXFFeatureReader.java 8672 2008-07-17 16:37:57Z Matthijs $
 */
package nl.b3p.geotools.data.dxf;

import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.ArrayList;
import java.net.URL;
import nl.b3p.geotools.data.GeometryType;
import nl.b3p.geotools.data.dxf.entities.DXFEntity;
import nl.b3p.geotools.data.dxf.entities.DXFInsert;
import nl.b3p.geotools.data.dxf.entities.DXFPoint;
import nl.b3p.geotools.data.dxf.entities.DXFText;
import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
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


import org.apache.commons.io.input.CountingInputStream;

/**
 * @author Matthijs Laan, B3Partners
 */
public class DXFFeatureReader implements FeatureReader {

    private static final Log log = LogFactory.getLog(DXFFeatureReader.class);
    private FeatureType ft;
    private Iterator<DXFEntity> entityIterator;
    private GeometryType geometryType = null;
    private Feature cache;
    private DXFUnivers theUnivers;
    private ArrayList dxfInsertsFilter;

    public DXFFeatureReader(URL url, String typeName, String srs, GeometryType geometryType, ArrayList dxfInsertsFilter) throws IOException, DXFParseException {
        CountingInputStream cis = null;
        DXFLineNumberReader lnr = null;

        try {
            cis = new CountingInputStream(url.openStream());
            lnr = new DXFLineNumberReader(new InputStreamReader(cis));
            theUnivers = new DXFUnivers(dxfInsertsFilter);
            theUnivers.read(lnr);
        } catch (IOException ioe) {
            log.error("Error reading data in datastore: ", ioe);
            throw ioe;
        } finally {
            if (lnr != null) {
                lnr.close();
            }
            if (cis != null) {
                cis.close();
            }
        }

        // Set filter point, line, polygon, defined in datastore typenames
        updateTypeFilter(typeName, geometryType, srs);
    }

    public void updateTypeFilter(String typeName, GeometryType geometryType, String srs) {
        this.geometryType = geometryType;
        entityIterator = theUnivers.theEntities.iterator();

        try {
            createFeatureType(typeName, srs);
        } catch (DataSourceException ex) {
            log.error(ex.getLocalizedMessage());
        }
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
            GeometricAttributeType geometryAttributeType = new GeometricAttributeType("the_geom", geometryType.getGeometryClass(), true, null, crs, null);
            ft = FeatureTypes.newFeatureType(
                    new AttributeType[]{
                        geometryAttributeType,
                        AttributeTypeFactory.newAttributeType("name", String.class),
                        AttributeTypeFactory.newAttributeType("key", String.class),
                        AttributeTypeFactory.newAttributeType("urlLink", String.class),
                        AttributeTypeFactory.newAttributeType("lineType", String.class),
                        AttributeTypeFactory.newAttributeType("color", String.class),
                        AttributeTypeFactory.newAttributeType("layer", String.class),
                        AttributeTypeFactory.newAttributeType("thickness", Double.class),
                        AttributeTypeFactory.newAttributeType("rotation", Double.class),
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
        return cache;
    }

    public boolean hasNext() throws IOException {
        if (!entityIterator.hasNext()) {
            return false;
        } else {
            Geometry g = null;
            DXFEntity entry = null;
            try {
                entry = null;
                boolean passedFilter = false;
                do {
                    entry = (DXFEntity) entityIterator.next();
                    passedFilter = passedFilter(entry);
                } while (!passedFilter && entityIterator.hasNext());

                if (passedFilter) {
                    g = entry.getGeometry();

                    cache = ft.create(new Object[]{
                                g,
                                entry.getName(),
                                entry.getKey(),
                                entry.getUrlLink(),
                                entry.getLineTypeName(),
                                entry.getColorRGB(),
                                entry.getRefLayerName(),
                                new Double(entry.getThickness()),
                                ((entry instanceof DXFText) ? new Double(((DXFText) entry)._rotation) : new Double(0.0)), // Text rotation
                                new Integer(entry.isVisible() ? 1 : 0),
                                new Integer(entry.getStartingLineNumber()),
                                new Integer(entry.isParseError() ? 1 : 0),
                                entry.getErrorDescription()
                            });
                    return true;
                } else {
                    // No next features found
                    return false;
                }
            } catch (IllegalAttributeException ex) {
                // TODO GJ DELETE
                log.error(ex.getLocalizedMessage() + "\n" + entry.getErrorDescription());
                return false;
            }
        }
    }

    /**
     * Check if geometry of entry is equal to filterType
     *
     * @param entry     Feature from iterator; entry to check it's geometryType from
     * @return          if entry.getType equals geometryType
     */
    private boolean passedFilter(DXFEntity entry) {
        // Entries who are null can never be wanted and will never pass the filter

        if (entry == null) {
            return false;
        } else {
            /**
             * Check if type of geometry is equal to geometryType of filter
             * If true, this entry should be added to the table
             */
            boolean isEqual = entry.getType().equals(geometryType) || (geometryType.equals(GeometryType.ALL) && !entry.getType().equals(GeometryType.UNSUPPORTED));

            try {
                // Filter invalid geometries
                if (!entry.getGeometry().isValid()) {
                    // Only display message for own featuretype, otherwise it will be displayed for every typename
                    if (isEqual) {
                        log.info("Invalid " + entry.getType() + " found while parsing table");
                    }
                    return false;
                }

                // Skip entryErrors from Inserts
                if(entry.isParseError()){
                    if(entry instanceof DXFInsert){
                        return false;
                    }
                }

            } catch (Exception ex) {
                // TODO GJ DELETE
                log.error("Skipping geometry; problem with " + entry.getName() + ": " + ex.getLocalizedMessage());
                return false;
            }

            return isEqual;
        }
    }

    public String getInfo() {
        return (theUnivers == null ? "Univers is null" : theUnivers.getInfo());
    }

    public void close() throws IOException {
    }
}
