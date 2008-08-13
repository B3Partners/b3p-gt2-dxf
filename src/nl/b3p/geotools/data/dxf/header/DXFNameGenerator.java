package nl.b3p.geotools.data.dxf.header;

public class DXFNameGenerator {

    private static int blockIndx = 1;
    private static int insertIndx = 1;
    private static int dimensionIndx = 1;
    private static int layerIndx = 1;

    public static String getBlockName(String str) {
        return (str + (blockIndx++));
    }

    public static String getInsertName(String str) {
        return (str + (insertIndx++));
    }

    public static String getDimensionName(String str) {
        return (str + (dimensionIndx++));
    }

    public static String getLayerName(String str) {
        return (str + (layerIndx++));
    }

    public static void reset() {
        blockIndx = 1;
        insertIndx = 1;
        dimensionIndx = 1;
        layerIndx = 1;
    }
}
