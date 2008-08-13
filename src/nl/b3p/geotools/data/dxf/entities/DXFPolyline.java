package nl.b3p.geotools.data.dxf.entities;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.Vector;


import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTable;

public class DXFPolyline extends DXFEntity {

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
        super(-1, l, 0, null, DXFTable.defaultThickness);
    }

    public DXFPolyline() {
        super(-1, null, 0, null, DXFTable.defaultThickness);
    }

    public DXFPolyline(DXFPolyline orig) {
        super(orig._color, orig._refLayer, 0, orig._lineType, orig._thickness);
        _name = orig._name;

        for (int i = 0; i < orig.theVertex.size(); i++) {
            theVertex.add(new DXFVertex((DXFVertex) orig.theVertex.elementAt(i), true));
        }
        _flag = orig._flag;
    }

    public static DXFPolyline read(DXFBufferedReader br, DXFUnivers univers) throws IOException {
        String ligne, ligne_temp;
        String name = "";
        int visibility = 0, flag = 0, color = -1;
        DXFLineType lineType = null;
        Vector<DXFVertex> lv = new Vector<DXFVertex>();
        DXFPolyline p = null;
        DXFLayer l = null;

//        myLog.writeLog("> new myPolyline");
        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("SEQEND")) {
            ligne_temp = ligne;
            while ((ligne = br.readLine()) != null && ligne.equalsIgnoreCase("VERTEX")) {
                lv.addElement(DXFVertex.read(br, univers, p));
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

        return new DXFPolyline(name, flag, color, l, lv, visibility, lineType, DXFTable.defaultThickness);
    }
}

