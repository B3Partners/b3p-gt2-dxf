package nl.b3p.geotools.data.dxf.client;

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import nl.b3p.geotools.data.dxf.DXFDataStore;
import nl.b3p.geotools.data.dxf.entities.DXFEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.data.postgis.PostgisDataStoreFactory;

public class Gt2DXFClient {
    
    private static final Log log = LogFactory.getLog(Gt2DXFClient.class);

    public static void main(String[] args) throws Exception {   
        Class c = Gt2DXFClient.class;
        URL url = c.getResource("/log4j.properties");
        Properties p = new Properties();
        p.load(url.openStream());
        PropertyConfigurator.configure(p);
        log.info("logging configured!");
        
        //createPostGIS();

        URL url2 = c.getResource("/VM50.dxf");
        processFile(url2);
        
    }
    
    private static void processFile(URL url) throws Exception {
        
        DXFDataStore dataStore = new DXFDataStore(url);
        String[] typeNames = dataStore.getTypeNames ();
        String typeName = typeNames[0];

        log.info( "Reading: "+ typeName );

        FeatureSource featureSource = dataStore.getFeatureSource( typeName );
        FeatureCollection collection = featureSource.getFeatures();
        FeatureIterator iterator = collection.features();

        int count = 0;        
        int emptyGeometries = 0; int parseErrors = 0;
        int invalid = 0;
        int[] typeCounts = new int[3];
        int[] multiGeometriesTypeCounts = new int[3];
        
        try {
           while( iterator.hasNext() ){
                Feature feature = iterator.next();
                
                Geometry geometry = null;
                //Geometry geometry = feature.getDefaultGeometry();
                int i;
                for(i = 0; geometry == null && i < 3; i++) {
                    geometry = (Geometry)feature.getAttribute(i);
                }
                typeCounts[i-1] = typeCounts[i-1]+1;
                if(geometry.getNumGeometries() != 1) {
                    multiGeometriesTypeCounts[i-1] = multiGeometriesTypeCounts[i-1]+1;
                }
                if(geometry.isEmpty()) {
                    ++emptyGeometries;
                }                

                if(((Boolean)feature.getAttribute(7)).booleanValue()) {
                    parseErrors++;
                }
                if(!geometry.isValid()) {
                    invalid++;
                }
                count++;
           }
        } finally {
           iterator.close();
        }

    }
    
    private static void createPostGIS() throws IOException {
        Map params = new HashMap();
        params.put(PostgisDataStoreFactory.DBTYPE.key, "postgis");
        params.put(PostgisDataStoreFactory.HOST.key, "localhost");
        params.put(PostgisDataStoreFactory.PORT.key, 5432);
        params.put(PostgisDataStoreFactory.SCHEMA.key, "public");
        params.put(PostgisDataStoreFactory.DATABASE.key, "geoobis");
        params.put(PostgisDataStoreFactory.USER.key, "postgres");
        params.put(PostgisDataStoreFactory.PASSWD.key, "***REMOVED***");

        DataStore dataStore=DataStoreFinder.getDataStore(params);
                
        String[] typeNames = dataStore.getTypeNames();
        for(int i = 0; i < typeNames.length; i++) {
            System.out.println(typeNames[i]);
        }        
    }
}