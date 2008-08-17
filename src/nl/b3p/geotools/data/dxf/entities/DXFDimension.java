package nl.b3p.geotools.data.dxf.entities;

import java.io.EOFException;
import nl.b3p.geotools.data.dxf.DXFLineNumberReader;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFBlock;
import nl.b3p.geotools.data.dxf.header.DXFBlockReference;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;

public class DXFDimension extends DXFBlockReference {

    public double _angle = 0;//50
    public String _dimension = "<>";//1
    public DXFPoint _point_WCS = new DXFPoint();//10,20

    public DXFDimension(double a, String dim, double x, double y, DXFBlock refBlock, String nomBlock, DXFLayer l, int visibility, int c, DXFLineType lineType) {
        super(c, l, visibility, null, nomBlock, refBlock);
        _angle = a;
        _dimension = dim;
        _point_WCS = new DXFPoint(x, y, c, null, visibility, 1);
    }

    public DXFDimension() {
        super(-1, null, 0, null, "", null);
    }

    public static DXFDimension read(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        String dimension = "", nomBlock = "";
        DXFDimension d = null;
        DXFLayer l = null;
        DXFBlock refBlock = null;
        double angle = 0, x = 0, y = 0;
        int visibility = 0, c = -1;
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
                case LAYER_NAME: //"8"
                    l = univers.findLayer(cvp.getStringValue());
                    break;
                case TEXT: //"1"
                    dimension = cvp.getStringValue();
                    break;
                case ANGLE_1: //"50"
                    angle = cvp.getDoubleValue();
                    break;
                case NAME: //"2"
                    nomBlock = cvp.getStringValue();
                    refBlock = univers.findBlock(nomBlock);
                    break;
                case LINETYPE_NAME: //"6"
                    lineType = univers.findLType(cvp.getStringValue());
                    break;
                case X_1: //"10"
                    x = cvp.getDoubleValue();
                    break;
                case Y_1: //"20"
                    y = cvp.getDoubleValue();
                    break;
                case VISIBILITY: //"60"
                    visibility = cvp.getIntValue();
                    break;
                case COLOR: //"62"
                    c = cvp.getIntValue();
                    break;
                default:
                    break;
            }

        }

        d = new DXFDimension(angle, dimension, x, y, refBlock, nomBlock, l, visibility, c, lineType);
        d.setType(DXFEntity.TYPE_UNSUPPORTED);
 
        if ((refBlock == null)) {
            univers.addRefBlockForUpdate(d);
        }
        return d;
    }
}
