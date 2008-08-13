/*
 * $Id: DXFDataStoreFactory.java 8672 2008-07-17 16:37:57Z Matthijs $
 */

package nl.b3p.geotools.data.dxf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFactorySpi.Param;
import org.geotools.data.FileDataStoreFactorySpi;

/**
 * @author Matthijs Laan, B3Partners
 */
public class DXFDataStoreFactory implements FileDataStoreFactorySpi {

    public static final DataStoreFactorySpi.Param PARAM_URL = new Param("url", URL.class, "url to a .dxf file");    
    
    public String getDisplayName() {
        return "DXF File";
    }

    public String getDescription() {
        return "Autodesk DXF format";
    }

    public String[] getFileExtensions() {
        return new String[] {".dxf"};
    }

    /**
     * @return true if the file of the f parameter exists
     */
    public boolean canProcess(URL f) {
        return f.getFile().toLowerCase().endsWith(".dxf");  
    }

    /**
     * @return true if the file in the url param exists
     */
    public boolean canProcess(Map params) {
        boolean result = false;
        if (params.containsKey(PARAM_URL.key)) {
            try {
                URL url = (URL)PARAM_URL.lookUp(params);
                result = canProcess(url);
            } catch (IOException ioe) {
                /* return false on any exception */
            }
        }
        return result;
    }

    /*
     * Always returns true, no additional libraries needed
     */
    public boolean isAvailable() {
        return true;
    }

    public Param[] getParametersInfo() {
        return new Param[] {PARAM_URL};
    }

    public Map getImplementationHints() {
        /* XXX do we need to put something in this map? */
        return Collections.EMPTY_MAP;
    }

    public String getTypeName(URL url) throws IOException {
        return DXFDataStore.getURLTypeName(url);
    }

    public DataStore createDataStore(URL url) throws IOException {
        Map params = new HashMap();
        params.put(PARAM_URL.key, url);
        
        boolean isLocal = url.getProtocol().equalsIgnoreCase("file");
        if(isLocal && !(new File(url.getFile()).exists())){
            throw new UnsupportedOperationException("Specified DXF file \"" + url + "\" does not exist, this plugin is read-only so no new file will be created");
        } else {
            return createDataStore(params);
        }        
    }

    public DataStore createDataStore(Map params) throws IOException {
        if(!canProcess(params)) {
            throw new FileNotFoundException( "DXF file not found: " + params);
        }
        /* XXX use param for GeometryFactory?... */
        return new DXFDataStore((URL)params.get(PARAM_URL.key));
    }

    public DataStore createNewDataStore(Map params) throws IOException {
        throw new UnsupportedOperationException("This plugin is read-only");
    }
}