/*
 * $Id: DXFDataStore.java 8672 2008-07-17 16:37:57Z Matthijs $
 */

package nl.b3p.geotools.data.dxf;

import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.AbstractFileDataStore;
import org.geotools.data.FeatureReader;
import org.geotools.feature.FeatureType;

/**
 * DataStore for reading a DXF file produced by Autodesk.
 * 
 * Note that a single DXF file can contain point, line and polygon features.
 * Although many files will only contain a single type, the parser can only 
 * determine this by looking through the entire file - which is not advisable in 
 * a streaming API. The same is true for a file containing only polygons or also 
 * multipolygons etc. 
 * 
 * Therefore always the same feature schema is used:
 * the_geom_point: Point
 * the_geom_line: MultiLineString (getNumGeometries() can be 1) 
 * the_geom_polygon: MultiPolygons (getNumGeometries() can be 1, can contain holes)
 * Where only one of three is not null. The attributes are always the same:
 * key: String
 * name: String
 * urlLink: String
 * entryLineNumber: Integer
 * parseError: Boolean
 * error: String
 *  * 
 * @author Matthijs Laan, Chris van Lith B3Partners
 */
public class DXFDataStore extends AbstractFileDataStore {
    private static final Log log = LogFactory.getLog(DXFDataStore.class);
    private URL url;
    private String typeName;
    private FeatureReader featureReader;
    
    public DXFDataStore(URL url) throws IOException {
        this.url = url;
        this.typeName = getURLTypeName(url);
    }

    public String[] getTypeNames() throws IOException {
        return new String[] {getURLTypeName(url)};
    }
    
    static String getURLTypeName(URL url) throws IOException {
        String file = url.getFile();
        if(file.length() == 0) {
            return "unknown_dxf";
        } else {
            int i = file.lastIndexOf('/');
            if(i != -1) {
                file = file.substring(i+1);
            }
            if(file.toLowerCase().endsWith(".dxf")) {
                file = file.substring(0, file.length()-4);
            }
            return file;
        }
    }

    public FeatureType getSchema(String typeName) throws IOException {
        /* only one type */
        return getSchema();
    }
    
    public FeatureType getSchema() throws IOException {
        return getFeatureReader().getFeatureType();
    }
        
    public FeatureReader getFeatureReader(String typeName) throws IOException {
        /* only one type */
        return getFeatureReader();
    }
    
    public FeatureReader getFeatureReader() throws IOException {
        if(featureReader == null) {
            try {
                featureReader = new DXFFeatureReader(url, typeName);
            } catch (DXFParseException e) {
                throw new IOException("DXF parse exception", e);
            }
        }
        return featureReader;
    }
}
