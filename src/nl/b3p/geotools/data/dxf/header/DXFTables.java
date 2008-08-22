package nl.b3p.geotools.data.dxf.header;

import java.awt.BasicStroke;
import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;

import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFConstants;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFTables implements DXFConstants {

    private static final Log log = LogFactory.getLog(DXFTables.class);
    public static final double defaultThickness = 1.0f;
    public static final float defautMotif[] = {1.0f, 0.0f};
     public Vector<DXFLayer> theLayers = new Vector<DXFLayer>();
    public Vector<DXFLineType> theLineTypes = new Vector<DXFLineType>();

    public DXFTables() {
    }

    public DXFTables(Vector<DXFLayer> sLayers, Vector<DXFLineType> sLineTypes) {
        this.theLayers = sLayers;
        this.theLineTypes = sLineTypes;
    }

    public static DXFTables readTables(DXFLineNumberReader br) throws IOException {
        Vector<DXFLayer> sLayers = new Vector<DXFLayer>();
        Vector<DXFLineType> sLineTypes = new Vector<DXFLineType>();

        int sln = br.getLineNumber();
        log.debug(">Enter at line: " + sln);
        DXFCodeValuePair cvp = null;
        DXFGroupCode gc = null;

        boolean doLoop = true;
        while (doLoop) {
            cvp = new DXFCodeValuePair();
            try {
                gc = cvp.read(br);
            } catch (DXFParseException ex) {
                throw new IOException("DXF parse error", ex);
            } catch (EOFException e) {
                doLoop = false;
                break;
            }

            switch (gc) {
                case TYPE:
                    String type = cvp.getStringValue();
                    if (type.equals(ENDSEC)) {
                        doLoop = false;
                        break;
                    } else if (type.equals(TABLE)) {
                        readTable(br, sLayers, sLineTypes);
                    }
                    break;
                default:
                    break;
            }
        }
        DXFTables e = new DXFTables(sLayers, sLineTypes);
        log.debug(e.toString(sLayers.size(), sLineTypes.size()));
        log.debug(">Exit at line: " + br.getLineNumber());
        return e;
    }

    public static void readTable(DXFLineNumberReader br, Vector<DXFLayer> sLayers, Vector<DXFLineType> sLineTypes) throws IOException {

        int sln = br.getLineNumber();
        log.debug(">>Enter at line: " + sln);
        DXFCodeValuePair cvp = null;
        DXFGroupCode gc = null;

        boolean doLoop = true;
        while (doLoop) {
            cvp = new DXFCodeValuePair();
            try {
                gc = cvp.read(br);
            } catch (DXFParseException ex) {
                throw new IOException("DXF parse error", ex);
            } catch (EOFException e) {
                doLoop = false;
                break;
            }

            switch (gc) {
                case TYPE:
                    String type = cvp.getStringValue();
                    if (type.equals(ENDSEC)) {
                        // hack om einde zonder ENDTAB te werken
                        br.reset();
                        doLoop = false;
                        break;
                    } else if (type.equals(ENDTAB)) {
                        doLoop = false;
                        break;
                    } else if (type.equals(LAYER)) {
                        DXFLayer layer = DXFLayer.read(br);
                        sLayers.add(layer);
                    } else if (type.equals(LTYPE)) {
                        DXFLineType lt = DXFLineType.read(br);
                        sLineTypes.add(lt);
                    }
                    break;
                default:
                    break;
            }

        }
    }

    public String toString(int numLayers, int numLineTypes) {
        StringBuffer s = new StringBuffer();
        s.append("DXFTables [");
        s.append("numLayers: ");
        s.append(numLayers + ", ");
        s.append("numLineTypes: ");
        s.append(numLineTypes);
        s.append("]");
        return s.toString();
    }
}
