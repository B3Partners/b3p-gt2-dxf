package nl.b3p.geotools.data.dxf.header;

import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;


import nl.b3p.geotools.data.dxf.parser.DXFColor;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.DXFLineNumberReader;
import nl.b3p.geotools.data.dxf.entities.DXFEntity;
import nl.b3p.geotools.data.dxf.entities.DXFPoint;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFConstants;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;

public class DXFBlock extends DXFEntity implements DXFConstants {

    public Vector<DXFEntity> theEntities = new Vector<DXFEntity>();
    public DXFPoint _point = new DXFPoint();
    public String _name;
    public int _flag;

    public DXFBlock() {
        super(-1, null, 0, null, DXFTables.defaultThickness);
        _name = DXFNameGenerator.getBlockName("myBlock.0");
        theEntities = new Vector<DXFEntity>();
    }

    public DXFBlock(double x, double y, int flag, String name, Vector<DXFEntity> ent, int c, DXFLayer l) {
        super(c, l, 0, null, DXFTables.defaultThickness);
        _point = new DXFPoint(x, y, c, l, 0, 1);
        _name = name;
        _flag = flag;

        if (ent == null) {
            ent = new Vector<DXFEntity>();
        }
        theEntities = ent;
    }

    public static DXFBlock read(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        DXFEntity obj = null;
        Vector<DXFEntity> sEnt = new Vector<DXFEntity>();
        String name = "";
        double x = 0, y = 0;
        int flag = 0;
        DXFLayer l = null;


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
                    if (ENDBLK.equals(type)) {
                        doLoop = false;
                        break;
                    } else if (ENDSEC.equals(type)) {
                        // hack voor als ENDBLK ontbreekt
                        doLoop = false;
                        br.reset();
                        break;
                    } else {
                        // check of dit entities zijn
                        br.reset();
                        sEnt.addAll(DXFEntities.readEntities(br, univers).theEntities);
                        break;
                    }
                case LAYER_NAME:
                    l = univers.findLayer(cvp.getStringValue());
                    break;
                case NAME:
                    name = cvp.getStringValue();
                    break;
                case INT_1:
                    flag = cvp.getIntValue();
                    break;
                case X_1:
                    x = cvp.getDoubleValue();
                    break;
                case Y_1:
                    y = cvp.getDoubleValue();
                    break;
                default:
                    //                myLog.writeLog("Unknown :" + ligne_temp + " (" + ligne + ")");
                    break;
            }

        }
        return new DXFBlock(x, y, flag, name, sEnt, DXFColor.getDefaultColorIndex(), l);
    }
}
