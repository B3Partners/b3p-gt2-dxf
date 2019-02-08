/*
 * $Id: DXFFeatureReader.java 8672 2008-07-17 16:37:57Z Matthijs $
 */
package nl.b3p.geotools.data.dxf;

import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import org.locationtech.jts.geom.Geometry;
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
import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import org.geotools.data.DataSourceException;
import org.geotools.data.FeatureReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.referencing.CRS;
import org.geotools.referencing.NamedIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.commons.io.input.CountingInputStream;
import org.geotools.data.DefaultServiceInfo;
import org.geotools.data.ServiceInfo;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * @author Matthijs Laan, B3Partners
 * @author mprins
 */
public class DXFFeatureReader implements FeatureReader {

    private static final Log LOG = LogFactory.getLog(DXFFeatureReader.class);
    private SimpleFeatureType ft;
    private Iterator<DXFEntity> entityIterator;
    private GeometryType geometryType = null;
    private SimpleFeature cache;
    private DXFUnivers theUnivers;
    private int featureID = 0;
    private ListFeatureCollection listFC;

    private Boolean hasNext = null;

    public DXFFeatureReader(URL url, String typeName, String srs, GeometryType geometryType, ArrayList dxfInsertsFilter) throws IOException, DXFParseException {
        CountingInputStream cis = null;
        DXFLineNumberReader lnr = null;

        try {
            cis = new CountingInputStream(url.openStream());
            lnr = new DXFLineNumberReader(new InputStreamReader(cis));
            theUnivers = new DXFUnivers(dxfInsertsFilter);
            theUnivers.read(lnr);
        } catch (IOException ioe) {
            LOG.error("Error reading data in datastore: ", ioe);
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
        hasNext = null;

        try {
            createFeatureType(typeName, srs);
        } catch (DataSourceException ex) {
            LOG.error(ex.getLocalizedMessage());
        }
    }

    private void createFeatureType(String typeName, String srs) throws DataSourceException {
        listFC = null;
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
                LOG.error("SRID could not be determined from crs!");
            }
        }
        LOG.info("SRID used by SimpleFeature reader: " + SRID);

        try {

            SimpleFeatureTypeBuilder ftb = new SimpleFeatureTypeBuilder();
            ftb.setName(typeName);
            ftb.setSRS(srs);

            ftb.add("the_geom", Geometry.class);
            ftb.add("layer", String.class);
            ftb.add("name", String.class);
            ftb.add("text", String.class);
            ftb.add("textposhorizontal", String.class);
            ftb.add("textposvertical", String.class);
            ftb.add("textheight", Double.class);
            ftb.add("textrotation", Double.class);
            ftb.add("color", String.class);
            ftb.add("linetype", String.class);
            ftb.add("thickness", Double.class);
            ftb.add("visible", Integer.class);
            ftb.add("linenumber", Integer.class);
            ftb.add("error", String.class);

            ft = ftb.buildFeatureType();
            LOG.debug("Created featuretype: " + ft);
        } catch (Exception e) {
            throw new DataSourceException("Error creating SimpleFeatureType: " + typeName, e);
        }
    }

    public SimpleFeatureType getFeatureType() {
        return ft;
    }

    public SimpleFeature next() throws IOException, IllegalAttributeException, NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        hasNext = null;
        return cache;
    }

    public boolean hasNext() throws IOException {
        if (hasNext != null) {
            return hasNext;
        }

        if (!entityIterator.hasNext()) {
            hasNext = false;
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

                    cache = SimpleFeatureBuilder.build(ft, new Object[]{
                        g,
                        entry.getRefLayerName(),
                        entry.getName(),
                        entry.getText(),
                        entry.getTextposhorizontal(),
                        entry.getTextposvertical(),
                        entry.getTextheight(),
                        entry.getTextrotation(),
                        entry.getColorRGB(),
                        entry.getLineTypeName(),
                        entry.getThickness(),
                        entry.isVisible() ? 1 : 0,
                        entry.getStartingLineNumber(),
                        entry.getErrorDescription()
                    }, Integer.toString(featureID++));

                    hasNext = true;
                    LOG.debug("Created feature: " + cache);
                } else {
                    // No next features found
                    hasNext = false;
                }
            } catch (IllegalAttributeException ex) {
                throw new IOException("Error accessing attribute", ex);
            }
        }
        return hasNext;
    }

    /**
     * Check if geometry of entry is equal to filterType
     *
     * @param entry SimpleFeature from iterator; entry to check it'serviceInfo
     * geometryType from
     * @return if entry.getType equals geometryType
     */
    private boolean passedFilter(DXFEntity entry) {
        // Entries who are null can never be wanted and will never pass the filter
        if (entry == null) {
            return false;
        } else {
            /**
             * Check if type of geometry is equal to geometryType of filter If
             * true, this entry should be added to the table
             */
            boolean isEqual = entry.getType().equals(geometryType) || (geometryType.equals(GeometryType.ALL) && !entry.getType().equals(GeometryType.UNSUPPORTED));

            try {
                // Filter invalid geometries
                if (entry.getGeometry() != null && !entry.getGeometry().isValid()) {
                    // Only display message for own SimpleFeatureType, otherwise it will be displayed for every typename
                    if (isEqual) {
                        LOG.info("Invalid " + entry.getType() + " found while parsing table");
                    }
                    return false;
                }

                // Skip entryErrors from Inserts
                if (entry.getErrorDescription() != null) {
                    if (entry instanceof DXFInsert) {
                        return false;
                    }
                }

            } catch (Exception ex) {
                LOG.error("Skipping geometry; problem with " + entry.getName() + ": " + ex.getLocalizedMessage());
                return false;
            }

            return isEqual;
        }
    }

    public ServiceInfo getInfo() {
        DefaultServiceInfo serviceInfo = new DefaultServiceInfo();
        serviceInfo.setTitle("DXF FeatureReader");
        serviceInfo.setDescription(theUnivers == null ? "Univers is null" : theUnivers.getInfo());

        return serviceInfo;
    }

    public void close() throws IOException {
        listFC = null;
    }

    public SimpleFeatureCollection getFeatureCollection() throws IOException {
        if (listFC == null || listFC.isEmpty()) {
            LOG.debug("Creating feature collection");

            listFC = new ListFeatureCollection(this.getFeatureType());
            while (hasNext()) {
                listFC.add(next());
            }
        }
        return listFC;
    }
}
