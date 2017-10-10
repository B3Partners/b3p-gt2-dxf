/*
 * $Id: DXFDataStore.java 8672 2008-07-17 16:37:57Z Matthijs $
 */
package nl.b3p.geotools.data.dxf;

import java.io.IOException;
import java.net.URL;
import nl.b3p.geotools.data.GeometryType;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.FeatureReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStore;
import org.geotools.data.LockingManager;
import org.geotools.data.Query;
import org.geotools.data.ServiceInfo;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.NameImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

/**
 * DataStore for reading a DXF file produced by Autodesk.
 *
 * The attributes are always the same: key: String name: String urlLink: String
 * entryLineNumber: Integer parseError: Boolean error: String *
 *
 * @author Chris van Lith B3Partners
 * @author mprins
 */
public class DXFDataStore implements FileDataStore {

    private static final Log log = LogFactory.getLog(DXFDataStore.class);
    private URL url;
    private FeatureReader featureReader;
    private String srs;
    private String strippedFileName;
    private String typeName;
    private ArrayList dxfInsertsFilter = new ArrayList();

    public DXFDataStore(URL url, String srs) throws IOException {
        this.url = url;
        this.strippedFileName = getURLTypeName(url);
        this.srs = srs;
    }

    public String[] getTypeNames() throws IOException {
        //return GeometryType.getTypeNames(strippedFileName, GeometryType.LINE, GeometryType.POINT, GeometryType.POLYGON);
        return GeometryType.getTypeNames(strippedFileName, GeometryType.ALL);
    }

    static String getURLTypeName(URL url) throws IOException {
        String file = url.getFile();
        if (file.length() == 0) {
            return "unknown_dxf";
        } else {
            int i = file.lastIndexOf('/');
            if (i != -1) {
                file = file.substring(i + 1);
            }
            if (file.toLowerCase().endsWith(".dxf")) {
                file = file.substring(0, file.length() - 4);
            }
            /* replace to make valid table names */
            file = file.replaceAll(" ", "_");
            return file;
        }
    }

    public void addDXFInsertFilter(String[] filteredNames) {
        dxfInsertsFilter.addAll(java.util.Arrays.asList(filteredNames));
    }

    public void addDXFInsertFilter(String filteredName) {
        dxfInsertsFilter.add(filteredName);
    }

    @Override
    public SimpleFeatureType getSchema(Name name) throws IOException {
        return this.getSchema(name.getLocalPart());
    }

    public SimpleFeatureType getSchema(String typeName) throws IOException {
        // Update featureReader with typename and return SimpleFeatureType
        return (SimpleFeatureType) getFeatureReader(typeName).getFeatureType();
    }

    public SimpleFeatureType getSchema() throws IOException {
        if (typeName == null) {
            log.warn("Typename is null, probably because of using getFeatureSource().\n"
                    + "\tPlease use getFeatureSource(typename)");
        }
        return getSchema(typeName);
    }

    public FeatureReader getFeatureReader(String typeName) throws IOException {
        // Update featureReader for this typename
        resetFeatureReader(typeName);
        return featureReader;
    }

    public FeatureReader getFeatureReader() throws IOException {
        if (featureReader == null) {
            resetFeatureReader(typeName);
        }
        return featureReader;
    }

    @Override
    public FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(Query query, Transaction transaction) throws IOException {
        // ignore query and transaction
        return this.getFeatureReader();
    }

    public void resetFeatureReader(String typeName) throws IOException {
        if (typeName == null) {
            log.error("No typeName given for featureReader");
        } else {
            this.typeName = typeName;

            // Get geometryType from typeName (GeometryType)(typeName - fileName)
            String extension = typeName.replaceFirst(strippedFileName, "");
            GeometryType geometryType = GeometryType.getTypeByExtension(extension);

            if (featureReader == null) {
                try {
                    featureReader = new DXFFeatureReader(url, typeName, srs, geometryType, dxfInsertsFilter);
                } catch (DXFParseException e) {
                    throw new IOException("DXF parse exception" + e.getLocalizedMessage());
                }
            } else {
                ((DXFFeatureReader) featureReader).updateTypeFilter(typeName, geometryType, srs);
            }
        }
    }

    @Override
    public List<Name> getNames() throws IOException {
        return Arrays.asList((Name) new NameImpl(getSchema().getTypeName()));
    }

    @Override
    public SimpleFeatureSource getFeatureSource() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SimpleFeatureSource getFeatureSource(String typeName) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SimpleFeatureSource getFeatureSource(Name typeName) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter(Filter filter, Transaction transaction) throws IOException {
        throw new UnsupportedOperationException("Functie niet ondersteund voor alleen-lezen databron.");
    }

    @Override
    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter(Transaction transaction) throws IOException {
        throw new UnsupportedOperationException("Functie niet ondersteund voor alleen-lezen databron.");
    }

    @Override
    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriterAppend(Transaction transaction) throws IOException {
        throw new UnsupportedOperationException("Functie niet ondersteund voor alleen-lezen databron.");
    }

    @Override
    public void updateSchema(SimpleFeatureType featureType) throws IOException {
        throw new UnsupportedOperationException("Functie niet ondersteund voor alleen-lezen databron.");
    }

    @Override
    public void updateSchema(String typeName, SimpleFeatureType featureType) throws IOException {
        throw new UnsupportedOperationException("Functie niet ondersteund voor alleen-lezen databron.");
    }

    @Override
    public void updateSchema(Name typeName, SimpleFeatureType featureType) throws IOException {
        throw new UnsupportedOperationException("Functie niet ondersteund voor alleen-lezen databron.");
    }

    @Override
    public void removeSchema(String typeName) throws IOException {
        throw new UnsupportedOperationException("Functie niet ondersteund voor alleen-lezen databron.");
    }

    @Override
    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter(String typeName, Filter filter, Transaction transaction) throws IOException {
        throw new UnsupportedOperationException("Functie niet ondersteund voor alleen-lezen databron.");
    }

    @Override
    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter(String typeName, Transaction transaction) throws IOException {
        throw new UnsupportedOperationException("Functie niet ondersteund voor alleen-lezen databron.");
    }

    @Override
    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriterAppend(String typeName, Transaction transaction) throws IOException {
        throw new UnsupportedOperationException("Functie niet ondersteund voor alleen-lezen databron.");
    }

    @Override
    public LockingManager getLockingManager() {
        throw new UnsupportedOperationException("Functie niet ondersteund voor alleen-lezen databron.");
    }

    @Override
    public void createSchema(SimpleFeatureType featureType) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSchema(Name typeName) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ServiceInfo getInfo() {
        try {
            return ((DXFFeatureReader) getFeatureReader()).getInfo();
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public void dispose() {
        try {
            this.featureReader.close();
        } catch (IOException ex) {
            // ignore
        }
        this.featureReader = null;
    }
}
