package nl.b3p.geotools.data.dxf.entities;

import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import java.io.EOFException;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTables;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFSolid extends DXFEntity {

    private static final Log log = LogFactory.getLog(DXFSolid.class);
    public DXFPoint _p1 = new DXFPoint();
    public DXFPoint _p2 = new DXFPoint();
    public DXFPoint _p3 = new DXFPoint();
    public DXFPoint _p4 = null;

    public DXFSolid() {
        super(-1, null, 0, null, DXFTables.defaultThickness);
        setName("DXFSolid");
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
        setName("DXFSolid");
    }

    public DXFSolid(DXFSolid solid) {
        super(solid.getColor(), solid.getRefLayer(), 0, solid.getLineType(), solid.getThickness());

        _p1 = new DXFPoint(solid._p1);
        _p2 = new DXFPoint(solid._p2);
        _p3 = new DXFPoint(solid._p3);
        _p4 = new DXFPoint(solid._p4);
        setName("DXFSolid");
    }

    public static DXFEntity read(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        double p1_x = 0, p2_x = 0, p3_x = 0, p4_x = 0, p1_y = 0, p2_y = 0, p3_y = 0, p4_y = 0;
        double thickness = 0;
        int visibility = 0, c = -1;
        DXFLayer l = null;
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
                    c = cvp.getShortValue();
                    break;
                case LINETYPE_NAME: //"6"
                    lineType = univers.findLType(cvp.getStringValue());
                    break;
                case VISIBILITY: //"60"
                    visibility = cvp.getShortValue();
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
        e.setType(DXFEntity.TYPE_POLYGON);
        e.setStartingLineNumber(sln);
        e.setUnivers(univers);
        log.debug(e.toString(p1_x, p2_x, p3_x, p4_x, p1_y, p2_y, p3_y, p4_y, thickness, c, visibility));
        log.debug(">>Exit at line: " + br.getLineNumber());
        return e;
    }

    public String toString(double p1_x,
            double p2_x,
            double p3_x,
            double p4_x,
            double p1_y,
            double p2_y,
            double p3_y,
            double p4_y,
            double thickness,
            int c,
            int visibility) {
        StringBuffer s = new StringBuffer();
        s.append("DXFSolid [");
        s.append("p1_x: ");
        s.append(p1_x + ", ");
        s.append("p2_x: ");
        s.append(p2_x + ", ");
        s.append("p3_x: ");
        s.append(p3_x + ", ");
        s.append("p4_x: ");
        s.append(p4_x + ", ");
        s.append("p1_y: ");
        s.append(p1_y + ", ");
        s.append("p2_y: ");
        s.append(p2_y + ", ");
        s.append("p3_y: ");
        s.append(p3_y + ", ");
        s.append("p4_y: ");
        s.append(p4_y + ", ");
        s.append("thickness: ");
        s.append(thickness + ", ");
        s.append("color: ");
        s.append(c + ", ");
        s.append("visibility: ");
        s.append(visibility);
        s.append("]");
        return s.toString();
    }
}
