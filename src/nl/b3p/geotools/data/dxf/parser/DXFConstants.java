package nl.b3p.geotools.data.dxf.parser;

/**
 *
 * @author Chris
 */
public interface DXFConstants {

    public static final String SECTION = "SECTION";
    public static final String HEADER = "HEADER";
    public static final String TABLES = "TABLES";
    public static final String TABLE = "TABLE";
    public static final String LAYER = "LAYER";
    public static final String LTYPE = "LTYPE";
    public static final String ENDTAB = "ENDTAB";
    public static final String BLOCKS = "BLOCKS";
    public static final String BLOCK = "BLOCK";
    public static final String ENDBLK = "ENDBLK";
    public static final String ENTITIES = "ENTITIES";
    public static final String CLASSES = "CLASSES";
    public static final String ENDSEC = "ENDSEC";
    
    // not supported
    public static final String OBJECTS = "OBJECTS";
    public static final String THUMBNAILIMAGE = "THUMBNAILIMAGE";
    
    /* Supported header variables */
    public static final String $LIMMIN = "$LIMMIN";
    public static final String $LIMMAX = "$LIMMAX";
    public static final String $EXTMIN = "$EXTMIN";
    public static final String $EXTMAX = "$EXTMAX";
    public static final String $ACADVER = "$ACADVER";
    public static final String $FILLMODE = "$FILLMODE";
    
    /* layer constants */
    public static final short LAYER_FROZEN = 1;  /* layer is frozen */
    public static final short LAYER_AUTO_FROZEN = 2;  /* layer automatically frozen in all VIEWPORTS */
    public static final short LAYER_LOCKED = 4;  /* layer is locked */
    public static final short LAYER_XREF = 8;  /* layer is from XREF */
    public static final short LAYER_XREF_FOUND = 16;  /* layer is from known XREF */
    public static final short LAYER_USED = 32;  /* layer was used */
    public static final short LAYER_INVISIBLE = 16384;  /* (own:) layer is invisible */
    
}
