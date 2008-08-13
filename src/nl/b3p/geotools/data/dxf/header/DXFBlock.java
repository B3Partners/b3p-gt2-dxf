package nl.b3p.geotools.data.dxf.header;

import java.io.IOException;
import java.util.Vector;


import nl.b3p.geotools.data.dxf.DXFColor;
import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.entities.DXFBufferedReader;
import nl.b3p.geotools.data.dxf.entities.DXFEntity;
import nl.b3p.geotools.data.dxf.entities.DXFPoint;

public class DXFBlock extends DXFEntity {

    private static final long serialVersionUID = -2195994318962049800L;
    public Vector<DXFEntity> _myEnt = new Vector<DXFEntity>();
    public DXFPoint _point = new DXFPoint();
    public String _name;
    public int _flag;
    public DXFUnivers _refUnivers;

    public DXFBlock(double x, double y, int flag, String name, Vector<DXFEntity> ent, int c, DXFLayer l, DXFUnivers univers) {
        super(c, l, 0, null, DXFTable.defaultThickness);
        _point = new DXFPoint(x, y, c, l, 0, 1);
        _name = name;
        _flag = flag;
        _refUnivers = univers;

        if (ent == null) {
            ent = new Vector<DXFEntity>();
        }
        _myEnt = ent;
    }

    public DXFBlock(DXFUnivers univers) {
        super(-1, null, 0, null, DXFTable.defaultThickness);
        _name = DXFNameGenerator.getBlockName("myBlock.0");
        _refUnivers = univers;
        _myEnt = new Vector<DXFEntity>();
    }

    public static DXFBlock read(DXFBufferedReader br, DXFUnivers univers) throws IOException {
        DXFEntity obj = null;
        Vector<DXFEntity> myEnt = new Vector<DXFEntity>();
        String ligne = "", ligne_temp = "", name = "";
        double x = 0, y = 0;
        int flag = 0;
        DXFLayer l = null;
        while ((ligne = br.readLine()) != null) {
            ligne_temp = ligne;
            ligne = br.readLine();

            if (ligne_temp.equalsIgnoreCase("8")) {
                l = univers.findLayer(ligne);
            } else if (ligne_temp.equalsIgnoreCase("2")) {
                name = ligne;
            } else if (ligne_temp.equalsIgnoreCase("70")) {
                flag = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("10")) {
                x = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("20")) {
                y = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("0")) {
                // Ajout des �l�ments du block
                while ((obj = univers.addEntity(br, ligne, false)) != null) {
                    myEnt.add(obj);
                    ligne = br.readLine();
                }
                break;
            } else {
//                myLog.writeLog("Unknown :" + ligne_temp + "(" + ligne + ")");
            }
        }
        return new DXFBlock(x, y, flag, name, myEnt, DXFColor.getDefaultColorIndex(), l, univers);
    }

    public String toString() {
        return _name + " (" + _myEnt.size() + ")";
    }

}
