package nl.b3p.geotools.data.dxf.entities;

import nl.b3p.geotools.data.dxf.DXFLineNumberReader;
import java.awt.geom.GeneralPath;
import java.io.EOFException;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTables;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;

public class DXFSolid extends DXFEntity {

    private static final long serialVersionUID = 2567756283532200546L;
    public DXFPoint _p1 = new DXFPoint();
    public DXFPoint _p2 = new DXFPoint();
    public DXFPoint _p3 = new DXFPoint();
    public DXFPoint _p4 = null;
    public GeneralPath g;

    public DXFSolid() {
        super(-1, null, 0, null, DXFTables.defaultThickness);
    }

    public DXFSolid(DXFPoint p1, DXFPoint p2, DXFPoint p3, DXFPoint p4,
            double thickness, int c, DXFLayer l, int visibility, DXFLineType lineType) {
        super(c, l, visibility, lineType, thickness);

        _p1 = p1;
        _p2 = p2;
        _p3 = p3;

        if (p4 == null) {
            _p4 = p3;
        } else {
            _p4 = p4;
        }
    }

    public DXFSolid(DXFSolid solid) {
        super(solid._color, solid._refLayer, 0, solid._lineType, solid._thickness);

        _p1 = new DXFPoint(solid._p1);
        _p2 = new DXFPoint(solid._p2);
        _p3 = new DXFPoint(solid._p3);
        _p4 = new DXFPoint(solid._p4);
    }

    public static DXFEntity read(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        double p1_x = 0, p2_x = 0, p3_x = 0, p4_x = 0, p1_y = 0, p2_y = 0, p3_y = 0, p4_y = 0;
        double thickness = 0;
        int visibility = 0, c = -1;
        String ligne = "", ligne_tmp = "";
        DXFLayer l = null;
        DXFLineType lineType = null;

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
                    // geldt voor alle waarden van type
                    br.reset();
                    doLoop = false;
                    break;
                case X_1: //"10"
                    p1_x = cvp.getDoubleValue();
                    break;
                case X_2: //"11"
                    p2_x = cvp.getDoubleValue();
                    break;
                case X_3: //"12"
                    p3_x = cvp.getDoubleValue();
                    break;
                case X_4: //"13"
                    p4_x = cvp.getDoubleValue();
                    break;
                case Y_1: //"20"
                    p1_y = cvp.getDoubleValue();
                    break;
                case Y_2: //"21"
                    p2_y = cvp.getDoubleValue();
                    break;
                case Y_3: //"22"
                    p3_y = cvp.getDoubleValue();
                    break;
                case Y_4: //"23"
                    p4_y = cvp.getDoubleValue();
                    break;
                case THICKNESS: //"39"
                    thickness = cvp.getDoubleValue();
                    break;
                case LAYER_NAME: //"8"
                    l = univers.findLayer(cvp.getStringValue());
                    break;
                case COLOR: //"62"
                    c = cvp.getIntValue();
                    break;
                case LINETYPE_NAME: //"6"
                    lineType = univers.findLType(cvp.getStringValue());
                    break;
                case VISIBILITY: //"60"
                    visibility = cvp.getIntValue();
                    break;
                default:
                    break;
            }

        }
        DXFSolid e = new DXFSolid(
                new DXFPoint(p1_x, p1_y, c, null, visibility, 1),
                new DXFPoint(p2_x, p2_y, c, null, visibility, 1),
                new DXFPoint(p3_x, p3_y, c, null, visibility, 1),
                new DXFPoint(p4_x, p4_y, c, null, visibility, 1),
                thickness,
                c,
                l,
                visibility,
                lineType);
        e.setType(DXFEntity.TYPE_UNSUPPORTED);
        return e;
    }
}
