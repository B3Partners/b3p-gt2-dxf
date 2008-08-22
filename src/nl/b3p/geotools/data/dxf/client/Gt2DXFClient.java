package nl.b3p.geotools.data.dxf.client;

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import nl.b3p.geotools.data.dxf.DXFDataStore;
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
        String[] typeNames = dataStore.getTypeNames();
        String typeName = typeNames[0];

        log.info("Reading: " + typeName);

        FeatureSource featureSource = dataStore.getFeatureSource(typeName);
        FeatureCollection collection = featureSource.getFeatures();
        FeatureIterator iterator = collection.features();

        int count = 0;
        int emptyGeometries = 0;
        int parseErrors = 0;
        int invalid = 0;

        try {
            while (iterator.hasNext()) {
                Feature feature = iterator.next();

                Geometry geometry = feature.getDefaultGeometry();
                if (feature != null && geometry != null) {
                    log.info("----------------------------------------------------");
                    log.info("geomType: "+ geometry.getGeometryType());
                    log.info("name: "+ (String) feature.getAttribute(1));
                    log.info("key: "+ (String) feature.getAttribute(2));
                    log.info("urlLink: "+ (String) feature.getAttribute(3));
                    log.info("lineType: "+ (String) feature.getAttribute(4));
                    log.info("color: "+ (String) feature.getAttribute(5));
                    log.info("layer: "+ (String) feature.getAttribute(6));
                    log.info("thickness: "+ (Double) feature.getAttribute(7));
                    log.info("visible: "+ (Boolean) feature.getAttribute(8));
                    log.info("lineNumber: "+ (Integer) feature.getAttribute(9));
                    log.info("parseError: "+ (Boolean) feature.getAttribute(10));
                    log.info("error: "+ (String) feature.getAttribute(11));
                }

                if (geometry == null || geometry.isEmpty()) {
                    ++emptyGeometries;
                }

                if (((Boolean) feature.getAttribute(10)).booleanValue()) {
                    parseErrors++;
                }
                if (geometry == null || !geometry.isValid()) {
                    invalid++;
                }
                count++;
            }
        } finally {
            iterator.close();
        }

        log.info("count: " + count + ", emptyGeometries: " + emptyGeometries + ", parseErrors: " + parseErrors + ", invalid: " + invalid);
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

        DataStore dataStore = DataStoreFinder.getDataStore(params);

        String[] typeNames = dataStore.getTypeNames();
        for (int i = 0; i < typeNames.length; i++) {
            log.info(typeNames[i]);
        }
    }
}