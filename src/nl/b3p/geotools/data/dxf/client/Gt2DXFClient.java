package nl.b3p.geotools.data.dxf.client;

/*
package nl.b3p.geotools.data.dxf.client;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.File;
import java.io.FilenameFilter;
import org.geotools.feature.*;
import nl.b3p.geotools.data.dxf.DXFDataStore;
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
    private static final int numberOfFeaturesPrinted = 0; // TODO GJ Delete or may be edited
    private static boolean toDatabase = true; // TODO GJ DELETE


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
        URL log4j_url = c.getResource("log4j.properties");
        Properties p = new Properties();
        p.load(log4j_url.openStream());
        PropertyConfigurator.configure(p);
        log.info("logging configured!");


        URL dbconfig_url = c.getResource("dbconfig.properties");
        Properties p2 = new Properties();
        p2.load(dbconfig_url.openStream());


        File dir = new File("C:\\Documents and Settings\\Gertjan\\Mijn documenten\\DataStores\\DXF\\Samples\\Test");
        //File dir = new File("C:\\Documents and Settings\\Gertjan\\Mijn documenten\\dev\\data\\DXF\\samples");
        File[] files = dir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".dxf");
            }
        });

        DataStore dataStore2Write = createPostGIS();

        for (int i = 0; i < files.length; i++) {
            try {
                System.out.println("Process " + files[i].getName());
                processFile(files[i], dataStore2Write);
            } catch (Exception ex) {
                log.error("Unable to process file \"" + files[i].getName() + "\"");
            }
        }
        dataStore2Write.dispose();
    }

    private static void processFile(File file, DataStore dataStore2Write) {
        try {
            Map map = new HashMap();
            map.put("url", file.toURL());

            URL dxf_url = file.toURL();
            DXFDataStore dataStore2Read = new DXFDataStore(dxf_url, "EPSG:28992");

            //"GEWAESS", "LAUBNEU", "NADELNEU", 
            //String[] filters = {"BRUNNEN", "GRENZPU", "HOEHENKR", "HYDRANT", "KANALGIT", "MAST", "SCHACHT", "WIESENSY"};


            String[] filters = {"*U6", "AI9-BLK61", "House14_sitemap", "House24_sitemap", "House26_sitemap", "House37_sitemap"};
            // dataStore2Read.addDXFInsertFilter(filters);

            log.info("dxf file read!");

            String[] typeNames = dataStore2Read.getTypeNames();
            for (int i = 0; i < typeNames.length; i++) {
                write2DB(dataStore2Read, dataStore2Write, typeNames[i]);
            }

        // System.out.println(dataStore2Read.getInfo());

        } catch (Exception ex) {
            log.info(ex.getLocalizedMessage());
        }
    }

    private static DataStore createPostGIS() throws IOException {
        Map params = new HashMap();
        params.put(PostgisDataStoreFactory.DBTYPE.key, "postgis");
        params.put(PostgisDataStoreFactory.HOST.key, "b3p-demoserver");
        params.put(PostgisDataStoreFactory.PORT.key, 5432);
        params.put(PostgisDataStoreFactory.SCHEMA.key, "public");
        params.put(PostgisDataStoreFactory.DATABASE.key, "uploadDXF");
        params.put(PostgisDataStoreFactory.USER.key, "dev");
        params.put(PostgisDataStoreFactory.PASSWD.key, "b3p");

        return DataStoreFinder.getDataStore(params);
    }

    private static void write2DB(DXFDataStore dataStore2Read, DataStore dataStore2Write, String typeName2Read) throws Exception {
        log.info("Reading: " + typeName2Read);


        if (!toDatabase) {
            // Get iterator of given typeName
            FeatureIterator iterator = dataStore2Read.getFeatureSource(typeName2Read).getFeatures().features();
            printFeatureIterator(iterator, typeName2Read, numberOfFeaturesPrinted);

        } else {
            // Check if table exists
            boolean typeExists = false;
            String[] typeNames2Write = dataStore2Write.getTypeNames();
            for (int i = 0; i < typeNames2Write.length; i++) {
                if (typeName2Read.equals(typeNames2Write[i])) {
                    typeExists = true;
                    log.info("Found table with name: " + typeNames2Write[i] + " -> overwriting!");
                    break;
                }
            }

            // If table does not exist, create new
            if (!typeExists) {
                FeatureType ft = dataStore2Read.getFeatureReader(typeName2Read).getFeatureType();
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

                        log.info("FeatureListener, count: " + count + ", type: " + type);
                        count++;
                    }
                });

                featureStore.setTransaction(t);
                featureStore.setFeatures(dataStore2Read.getFeatureReader(typeName2Read));
                t.commit();

                // Just to print info
                FeatureIterator iterator = featureStore.getFeatures().features();
                printFeatureIterator(iterator, typeName2Read, numberOfFeaturesPrinted);

            } catch (IOException ioe) {
                log.error("IOException: ", ioe);
                t.rollback();
            } finally {
                t.close();
            }
        }
    }

    public static void printFeatureIterator(FeatureIterator iterator, String typeName2Read, int numberOfFeaturesToPrint) {
        // Loop through iterator and count occurances
        String buffer = "";
        int counter = 0;
        while (iterator.hasNext()) {
            Feature feature = iterator.next();
            if (counter < numberOfFeaturesToPrint || numberOfFeaturesToPrint == -1) {
                buffer += " " + feature.getDefaultGeometry().toText() + "\n";
            } else if (counter == numberOfFeaturesToPrint) {
                buffer += " ...\n";
            }
            counter++;
        }

        // TODO Evil System.out.println(...)
        System.out.print("Type " + typeName2Read + " has " + counter + " features");
        if (counter > 0 && numberOfFeaturesToPrint != 0) {
            System.out.print(":\n" + buffer);
        } else {
            System.out.println();
        }
    }
}
 * */