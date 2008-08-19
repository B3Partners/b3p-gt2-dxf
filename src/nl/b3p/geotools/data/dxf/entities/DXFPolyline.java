package nl.b3p.geotools.data.dxf.entities;

import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import java.awt.geom.GeneralPath;
import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;


import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTables;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFPolyline extends DXFEntity {

    private static final Log log = LogFactory.getLog(DXFPolyline.class);
    private static final long serialVersionUID = 1L;
    public String _name = "myPolyline.0";
    public int _flag = 0;
    public Vector<DXFVertex> theVertex = new Vector<DXFVertex>();
    GeneralPath poly = new GeneralPath();

    public DXFPolyline(String name, int flag, int c, DXFLayer l, Vector<DXFVertex> v, int visibility, DXFLineType lineType, double thickness) {
        super(c, l, visibility, lineType, thickness);
        _name = name;

        if (v == null) {
            v = new Vector<DXFVertex>();
        }
        theVertex = v;
        _flag = flag;
    }

    public DXFPolyline(DXFLayer l) {
        super(-1, l, 0, null, DXFTables.defaultThickness);
    }

    public DXFPolyline() {
        super(-1, null, 0, null, DXFTables.defaultThickness);
    }

    public DXFPolyline(DXFPolyline orig) {
        super(orig._color, orig._refLayer, 0, orig._lineType, orig._thickness);
        _name = orig._name;

        for (int i = 0; i < orig.theVertex.size(); i++) {
            theVertex.add(new DXFVertex((DXFVertex) orig.theVertex.elementAt(i), true));
        }
        _flag = orig._flag;
    }

    public static DXFPolyline read(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        String name = "";
        int visibility = 0, flag = 0, c = -1;
        DXFLineType lineType = null;
        Vector<DXFVertex> lv = new Vector<DXFVertex>();
        DXFLayer l = null;

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
                    if (SEQEND.equals(type)) {
                        doLoop = false;
                    } else if (VERTEX.equals(type)) {
                        lv.add(DXFVertex.read(br, univers));
                    } else {
                        br.reset();
                        doLoop = false;
                    }
                    break;
                case NAME: //"2"
                    name = cvp.getStringValue();
                    break;
                case LAYER_NAME: //"8"
                    l = univers.findLayer(cvp.getStringValue());
                    break;
                case LINETYPE_NAME: //"6"
                    lineType = univers.findLType(cvp.getStringValue());
                    break;
                case COLOR: //"62"
                    c = cvp.getShortValue();
                    break;
                case INT_1: //"70"
                    flag = cvp.getShortValue();
                    break;
                case VISIBILITY: //"60"
                    visibility = cvp.getShortValue();
                    break;
                default:
                    break;
            }

        }
        DXFPolyline e = new DXFPolyline(name, flag, c, l, lv, visibility, lineType, DXFTables.defaultThickness);
        e.setType(DXFEntity.TYPE_LINE);
        e.setStartingLineNumber(sln);
        log.debug(e.toString(name, flag, lv.size(), c, visibility, DXFTables.defaultThickness));
        log.debug(">>Exit at line: " + br.getLineNumber());
        return e;
    }

    public String toString(String name, int flag, int numVert, int c, int visibility, double thickness) {
        StringBuffer s = new StringBuffer();
        s.append("DXFPolyline [");
        s.append("name: ");
        s.append(name + ", ");
        s.append("flag: ");
        s.append(flag + ", ");
        s.append("numVert: ");
        s.append(numVert + ", ");
        s.append("color: ");
        s.append(c + ", ");
        s.append("visibility: ");
        s.append(visibility + ", ");
        s.append("thickness: ");
        s.append(thickness);
        s.append("]");
        return s.toString();
    }
}

