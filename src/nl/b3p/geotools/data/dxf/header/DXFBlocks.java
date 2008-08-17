package nl.b3p.geotools.data.dxf.header;

import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;

import nl.b3p.geotools.data.dxf.DXFLineNumberReader;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFConstants;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import nl.b3p.geotools.data.dxf.parser.DXFUnivers;

public class DXFBlocks implements DXFConstants {

    public Vector<DXFBlock> theBlocks = new Vector<DXFBlock>();

    public DXFBlocks() {
        theBlocks = new Vector<DXFBlock>();
    }

    public DXFBlocks(Vector<DXFBlock> blocks) {
        if (blocks == null) {
            blocks = new Vector<DXFBlock>();
        }
        theBlocks = blocks;
    }

    public static DXFBlocks readBlocks(DXFLineNumberReader br, DXFUnivers univers) throws IOException {

        Vector<DXFBlock> sBlocks = new Vector<DXFBlock>();


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
                    String tableType = cvp.getStringValue();
                    if (tableType.equals(ENDSEC)) {
                        break;
                    } else if (tableType.equals(BLOCK)) {
                        DXFBlock block = DXFBlock.read(br, univers);
                        sBlocks.add(block);
                    }
                    break;
                default:
                    break;
            }

        }
        return new DXFBlocks(sBlocks);
    }
}
