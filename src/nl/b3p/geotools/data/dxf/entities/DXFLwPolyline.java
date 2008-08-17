/*
 * [ 1719398 ] First shot at LWPOLYLINE
 * Peter Hopfgartner - hopfgartner
 *  
 */
package nl.b3p.geotools.data.dxf.entities;

import nl.b3p.geotools.data.dxf.DXFLineNumberReader;
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

public class DXFLwPolyline extends DXFEntity {

    private static final long serialVersionUID = 1L;
    public String _name = "myLwPolyline.0";
    public int _flag = 0;
    public Vector<DXFLwVertex> _myVertex = new Vector<DXFLwVertex>();
    GeneralPath poly = new GeneralPath();

    public DXFLwPolyline(String name, int flag, int c, DXFLayer l, Vector<DXFLwVertex> v, int visibility, DXFLineType lineType, double thickness) {
        super(c, l, visibility, lineType, thickness);
        _name = name;

        if (v == null) {
            v = new Vector<DXFLwVertex>();
        }
        _myVertex = v;
        _flag = flag;

    }

    public DXFLwPolyline(DXFLayer l) {
        super(-1, l, 0, null, DXFTables.defaultThickness);
    }

    public DXFLwPolyline() {
        super(-1, null, 0, null, DXFTables.defaultThickness);
    }

    public DXFLwPolyline(DXFLwPolyline orig) {
        super(orig._color, orig._refLayer, 0, orig._lineType, orig._thickness);
        _name = orig._name;

        for (int i = 0; i < orig._myVertex.size(); i++) {
            _myVertex.add(new DXFLwVertex((DXFLwVertex) orig._myVertex.elementAt(i), true));
        }
        _flag = orig._flag;
    }

    public static DXFLwPolyline read(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        String name = "";
        int visibility = 0, flag = 0, c = -1;
        DXFLineType lineType = null;
        Vector<DXFLwVertex> lv = new Vector<DXFLwVertex>();
//		DXFLwPolyline 			p		= null;	
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
                    String type = cvp.getStringValue(); // SEQEND ???
                    // geldt voor alle waarden van type
                    br.reset();
                    doLoop = false;
                    break;
//             if (ligne_temp.equals("10")) {
//                double x = Double.parseDouble(ligne);
//                double bulge = 0.0, y = 0.0;
//                while ((ligne_temp = br.readLine()) != null) {
//                    if (ligne_temp.equals("20")) {
//                        ligne = br.readLine();
//                        y = Double.parseDouble(ligne);
//                    } else if (ligne_temp.equals("40")) {
//                        ligne = br.readLine();
//                    // FIXME: Not used
//                    } else if (ligne_temp.equals("41")) {
//                        ligne = br.readLine();
//                    // FIXME: Not used
//                    }
//                    if (ligne_temp.equals("42")) {
//                        ligne = br.readLine();
//                        bulge = Double.parseDouble(ligne);
//                    } else {
//                        break;
//                    }
//                }
//                lv.addElement(new DXFLwVertex(x, y, bulge));
//            }
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
                    c = cvp.getIntValue();
                    break;
                case INT_1: //"70"
                    flag = cvp.getIntValue();
                    break;
                case VISIBILITY: //"60"
                    visibility = cvp.getIntValue();
                    break;
                default:
                    break;
            }

        }
        DXFLwPolyline e = new DXFLwPolyline(name, flag, c, l, lv, visibility, lineType, DXFTables.defaultThickness);
        e.setType(DXFEntity.TYPE_UNSUPPORTED);
        return e;
    }
}

