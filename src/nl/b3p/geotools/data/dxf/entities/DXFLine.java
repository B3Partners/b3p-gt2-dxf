package nl.b3p.geotools.data.dxf.entities;

import java.io.EOFException;
import nl.b3p.geotools.data.dxf.DXFLineNumberReader;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTables;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;

public class DXFLine extends DXFEntity {

    private static final long serialVersionUID = 1L;
    public DXFPoint _a = new DXFPoint();
    public DXFPoint _b = new DXFPoint();

    public DXFLine(DXFPoint a, DXFPoint b, int c, DXFLayer l, DXFLineType lineType, double thickness, int visibility) {
        super(c, l, visibility, lineType, thickness);
        _a = a;
        _b = b;

    }

    public DXFLine() {
        super(-1, null, 0, null, DXFTables.defaultThickness);
    }

    public DXFLine(DXFLine original) {
        super(original._color, original._refLayer, 0, original._lineType, original._thickness);
        _a = new DXFPoint(original._a);
        _b = new DXFPoint(original._b);

    }

    public static DXFLine read(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        DXFLayer l = null;
        double x1 = 0, y1 = 0, x2 = 0, y2 = 0, thickness = 0;
        DXFLineType lineType = null;
        int visibility = 0, c = -1;

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
                    x1 = cvp.getDoubleValue();
                    break;
                case Y_1: //"20"
                    y1 = cvp.getDoubleValue();
                    break;
                case X_2: //"11"
                    x2 = cvp.getDoubleValue();
                    break;
                case Y_2: //"21"
                    y2 = cvp.getDoubleValue();
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
                case THICKNESS: //"39"
                    thickness = cvp.getDoubleValue();
                    break;
                case VISIBILITY: //"60"
                    visibility = cvp.getIntValue();
                    break;
                default:
                    break;
            }

        }
        DXFLine e = new DXFLine(new DXFPoint(x1, y1, c, l, visibility, 1),
                new DXFPoint(x2, y2, c, l, visibility, 1),
                c,
                l,
                lineType,
                thickness,
                visibility);
        e.setType(DXFEntity.TYPE_LINE);
        return e;
    }
}
