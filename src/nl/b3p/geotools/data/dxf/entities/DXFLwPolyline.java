/*
 * [ 1719398 ] First shot at LWPOLYLINE
 * Peter Hopfgartner - hopfgartner
 *  
 */
package nl.b3p.geotools.data.dxf.entities;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.Vector;


import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTable;

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
        super(-1, l, 0, null, DXFTable.defaultThickness);
    }

    public DXFLwPolyline() {
        super(-1, null, 0, null, DXFTable.defaultThickness);
    }

    public DXFLwPolyline(DXFLwPolyline orig) {
        super(orig._color, orig._refLayer, 0, orig._lineType, orig._thickness);
        _name = orig._name;

        for (int i = 0; i < orig._myVertex.size(); i++) {
            _myVertex.add(new DXFLwVertex((DXFLwVertex) orig._myVertex.elementAt(i), true));
        }
        _flag = orig._flag;
    }

    public static DXFLwPolyline read(DXFBufferedReader br, DXFUnivers univers) throws IOException {
        String ligne, ligne_temp;
        String name = "";
        int visibility = 0, flag = 0, color = -1;
        DXFLineType lineType = null;
        Vector<DXFLwVertex> lv = new Vector<DXFLwVertex>();
//		DXFLwPolyline 			p		= null;	
        DXFLayer l = null;

        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("SEQEND")) {
            ligne_temp = ligne;
            ligne = br.readLine();
            if (ligne_temp.equals("10")) {
                double x = Double.parseDouble(ligne);
                double bulge = 0.0, y = 0.0;
                while ((ligne_temp = br.readLine()) != null) {
                    if (ligne_temp.equals("20")) {
                        ligne = br.readLine();
                        y = Double.parseDouble(ligne);
                    } else if (ligne_temp.equals("40")) {
                        ligne = br.readLine();
                    // FIXME: Not used
                    } else if (ligne_temp.equals("41")) {
                        ligne = br.readLine();
                    // FIXME: Not used
                    }
                    if (ligne_temp.equals("42")) {
                        ligne = br.readLine();
                        bulge = Double.parseDouble(ligne);
                    } else {
                        break;
                    }
                }
                lv.addElement(new DXFLwVertex(x, y, bulge));
            }
            if (ligne_temp.equalsIgnoreCase("2")) {
                name = ligne;
            } else if (ligne_temp.equalsIgnoreCase("8")) {
                l = univers.findLayer(ligne);
            } else if (ligne_temp.equalsIgnoreCase("6")) {
                lineType = univers.findLType(ligne);
            } else if (ligne_temp.equalsIgnoreCase("62")) {
                color = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("70")) {
                flag = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("60")) {
                visibility = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("0")) {
                break;
            } else {
//                myLog.writeLog("Unknown :" + ligne_temp + "(" + ligne + ")");
            }
        }
        /*if(l==null)
        univers.findLayer("default");*/

        return new DXFLwPolyline(name, flag, color, l, lv, visibility, lineType, DXFTable.defaultThickness);
    }


}

