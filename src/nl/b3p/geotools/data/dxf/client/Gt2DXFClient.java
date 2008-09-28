package nl.b3p.geotools.data.dxf.client;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import nl.b3p.geotools.data.dxf.DXFDataStore;
import nl.b3p.geotools.data.dxf.entities.DXFLwVertex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.postgis.PostgisDataStoreFactory;
import org.geotools.feature.FeatureType;
import org.geotools.util.logging.Logging;

public class Gt2DXFClient {

    private static final Log log = LogFactory.getLog(Gt2DXFClient.class);
    static final Logging logging = Logging.ALL;
    

    static {

        try {
            logging.setLoggerFactory("org.geotools.util.logging.CommonsLoggerFactory");
        } catch (ClassNotFoundException commonsException) {
            log.error("No commons logging for geotools");
            try {
                logging.setLoggerFactory("org.geotools.util.logging.Log4JLoggerFactory");
            } catch (ClassNotFoundException log4jException) {
                log.error("No logging at all for geotools");
            }
        }

    }

    public static void main(String[] args) throws Exception {
        Class c = Gt2DXFClient.class;
        URL log4j_url = c.getResource("/log4j.properties");
        Properties p = new Properties();
        p.load(log4j_url.openStream());
        PropertyConfigurator.configure(p);
        log.info("logging configured!");

        URL dbconfig_url = c.getResource("/dbconfig.properties");
        Properties p2 = new Properties();
        p2.load(dbconfig_url.openStream());
        Map dbconfig = new HashMap();
        dbconfig.put(PostgisDataStoreFactory.DBTYPE.key, p2.getProperty("dbtype", "postgis"));
        dbconfig.put(PostgisDataStoreFactory.HOST.key, p2.getProperty("host", "localhost"));
        dbconfig.put(PostgisDataStoreFactory.PORT.key, Integer.valueOf(p2.getProperty("port", "5432")));
        dbconfig.put(PostgisDataStoreFactory.SCHEMA.key, p2.getProperty("schema", "public"));
        dbconfig.put(PostgisDataStoreFactory.DATABASE.key, p2.getProperty("database", "upload"));
        dbconfig.put(PostgisDataStoreFactory.USER.key, p2.getProperty("user", "postgres"));
        dbconfig.put(PostgisDataStoreFactory.PASSWD.key, p2.getProperty("passwd", "postgres"));

        //Voor oracle support zie: http://geoserver.sourceforge.net/documentation/user/advanced.html#oracle
        log.info("db configured!");

        URL dxf_url = c.getResource("/Kadaster_2008.dxf");
        DXFDataStore dataStore2Read = new DXFDataStore(dxf_url);
        log.info("dxf file read!");
        write2DB(dataStore2Read, dbconfig);

    }

    private static void write2DB(DXFDataStore dataStore2Read, Map dbconfig) throws Exception {

        String[] typeNames = dataStore2Read.getTypeNames();
        String typeName2Read = typeNames[0];

        log.info("Reading: " + typeName2Read);

//        FeatureSource featureSource = dataStore2Read.getFeatureSource(typeName2Read);
//        FeatureCollection collection = featureSource.getFeatures();


        DataStore dataStore2Write = DataStoreFinder.getDataStore(dbconfig);

        boolean typeExists = false;
        String[] typeNames2Write = dataStore2Write.getTypeNames();
        for (int i = 0; i < typeNames2Write.length; i++) {
            if (typeName2Read.equals(typeNames2Write[i])) {
                typeExists = true;
                log.info("Found table with name: " + typeNames2Write[i] + " -> overwriting!");
                break;
            }
        }
        if (!typeExists) {
            FeatureType ft = dataStore2Read.getFeatureReader().getFeatureType();
            dataStore2Write.createSchema(ft);
            log.info("Creating new table with name: " + ft.getTypeName());
        }

        Transaction t = new DefaultTransaction("handle");
        try {
            FeatureStore featureStore = (FeatureStore) (dataStore2Write.getFeatureSource(typeName2Read));
            featureStore.addFeatureListener(new FeatureListener() {

                public int count = 1;

                public void changed(FeatureEvent featureEvent) {
                    String type;
                    switch (featureEvent.getEventType()) {
                        case FeatureEvent.FEATURES_ADDED:
                            type = "FEATURES_ADDED";
                            break;
                        case FeatureEvent.FEATURES_CHANGED:
                            type = "FEATURES_CHANGED";
                            break;
                        case FeatureEvent.FEATURES_REMOVED:
                            type = "FEATURES_REMOVED";
                            break;
                        default:
                            type = "UNKNOWN";
                            break;
                    }
                    /*
                    Feature feature = null; // waarom niet in event??
                    
                    Geometry geometry = feature.getDefaultGeometry();
                    if (feature != null && geometry != null) {
                    log.info("----------------------------------------------------");
                    log.info("geomType: " + geometry.getGeometryType());
                    log.info("name: " + (String) feature.getAttribute(1));
                    log.info("key: " + (String) feature.getAttribute(2));
                    log.info("urlLink: " + (String) feature.getAttribute(3));
                    log.info("lineType: " + (String) feature.getAttribute(4));
                    log.info("color: " + (String) feature.getAttribute(5));
                    log.info("layer: " + (String) feature.getAttribute(6));
                    log.info("thickness: " + (Double) feature.getAttribute(7));
                    log.info("visible: " + (Boolean) feature.getAttribute(8));
                    log.info("lineNumber: " + (Integer) feature.getAttribute(9));
                    log.info("parseError: " + (Boolean) feature.getAttribute(10));
                    log.info("error: " + (String) feature.getAttribute(11));
                    }
                     */

                    log.info("FeatureListener, count: " + count + ", type: " + type);
                    count++;
                }
            });
            featureStore.setTransaction(t);
//            featureStore.addFeatures(collection);
            featureStore.setFeatures(dataStore2Read.getFeatureReader());
            t.commit();
        } catch (IOException ioe) {
            log.error("IOException: ", ioe);
            t.rollback();
        } finally {
            t.close();
        }

        dataStore2Write.dispose();
    }
}