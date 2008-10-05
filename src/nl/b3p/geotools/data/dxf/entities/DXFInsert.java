package nl.b3p.geotools.data.dxf.entities;

import java.io.EOFException;
import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import java.io.IOException;

import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFBlock;
import nl.b3p.geotools.data.dxf.header.DXFBlockReference;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFInsert extends DXFBlockReference {

    private static final Log log = LogFactory.getLog(DXFInsert.class);
    public DXFPoint _point = new DXFPoint();

    public DXFInsert(double x, double y, String nomBlock, DXFBlock refBlock, DXFLayer l, int visibility, int c, DXFLineType lineType) {
        super(c, l, visibility, lineType, nomBlock, refBlock);
        _point = new DXFPoint(x, y, c, null, visibility, 1);
        setName("DXFInsert");
    }

    public static DXFInsert read(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        String nomBlock = "";
        DXFInsert m = null;
        DXFLayer l = null;
        double x = 0, y = 0;
        int visibility = 0, c = -1;
        DXFBlock refBlock = null;
        DXFLineType lineType = null;

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
                    // geldt voor alle waarden van type
                    br.reset();
                    doLoop = false;
                    break;
                case LAYER_NAME: //"8"
                    l = univers.findLayer(cvp.getStringValue());
                    break;
                case NAME: //"2"
                    nomBlock = cvp.getStringValue();
                    break;
                case X_1: //"10"
                    x = cvp.getDoubleValue();
                    break;
                case Y_1: //"20"
                    y = cvp.getDoubleValue();
                    break;
                case COLOR: //"62"
                    c = cvp.getShortValue();
                    break;
                case VISIBILITY: //"60"
                    visibility = cvp.getShortValue();
                    break;
                case LINETYPE_NAME: //"6"
                    lineType = univers.findLType(cvp.getStringValue());
                    break;
                default:
                    break;
            }
        }

        m = new DXFInsert(x, y, nomBlock, refBlock, l, visibility, c, lineType);
        m.setType(DXFEntity.TYPE_POINT);
        m.setStartingLineNumber(sln);
        m.setUnivers(univers);
        univers.addRefBlockForUpdate(m);
        log.debug(m.toString(x, y, visibility, c, lineType));
        log.debug(">>Exit at line: " + br.getLineNumber());
        return m;
    }

    public String toString(double x, double y, int visibility, int c, DXFLineType lineType) {
        StringBuffer s = new StringBuffer();
        s.append("DXFInsert [");
        s.append("x: ");
        s.append(x + ", ");
        s.append("y: ");
        s.append(y + ", ");
        s.append("visibility: ");
        s.append(visibility + ", ");
        s.append("color: ");
        s.append(c + ", ");
        s.append("line type: ");
        if (lineType != null) {
            s.append(lineType._name);
        }
        s.append("]");
        return s.toString();
    }
    
    @Override
    public DXFEntity translate(double x, double y) {
        return this;
    }
    
}
