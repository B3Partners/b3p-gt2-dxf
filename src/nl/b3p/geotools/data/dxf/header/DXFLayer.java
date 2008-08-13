package nl.b3p.geotools.data.dxf.header;

import java.io.IOException;
import java.util.Vector;


import nl.b3p.geotools.data.dxf.entities.DXFBufferedReader;
import nl.b3p.geotools.data.dxf.entities.DXFEntity;
import nl.b3p.geotools.data.dxf.DXFUnivers;

public class DXFLayer extends DXFEntity {

    // consts
    public static final short LAYER_FROZEN = 1;  /* layer is frozen */

    public static final short LAYER_AUTO_FROZEN = 2;  /* layer automatically frozen in all VIEWPORTS */

    public static final short LAYER_LOCKED = 4;  /* layer is locked */

    public static final short LAYER_XREF = 8;  /* layer is from XREF */

    public static final short LAYER_XREF_FOUND = 16;  /* layer is from known XREF */

    public static final short LAYER_USED = 32;  /* layer was used */

    public static final short LAYER_INVISIBLE = 16384;  /* (own:) layer is invisible */

    private static final long serialVersionUID = 1L;
    public DXFTable _refTable;
    public int _flag = 0;
    public String _nom;
    public Vector<DXFEntity> theEnt = new Vector<DXFEntity>();

    public DXFLayer(String nom, int c, DXFLineType lineType, int visibility, int flag) {
        super(c, null, visibility, lineType, DXFTable.defaultThickness);
        _nom = nom;
        _flag = flag;
    }

    public DXFLayer(String nom, int c, int flag) {
        super(c, null, 0, null, DXFTable.defaultThickness);
        _nom = nom;
        _flag = flag;
    }

    public DXFLayer(String nom, int c) {
        super(c, null, 0, null, DXFTable.defaultThickness);
        _nom = nom;
    }

    public void setVisible(boolean bool) {
        isVisible = bool;
        for (int i = 0; i < theEnt.size(); i++) {
            ((DXFEntity) theEnt.get(i)).setVisible(bool);
        }
    }

    public static DXFLayer read(DXFBufferedReader br, DXFUnivers u) throws NumberFormatException, IOException {
        String ligne, ligne_tmp, name = "";
        DXFLayer l = null;
        int f = 0, color = 0;

        while ((ligne = br.readLine().trim()) != null && !(ligne.equals("9") || ligne.equals("0"))) {
            ligne_tmp = ligne.trim();
            ligne = br.readLine().trim();

            if (ligne_tmp.equalsIgnoreCase("2")) {
                name = ligne;
            } else if (ligne_tmp.equalsIgnoreCase("62")) {
                color = Integer.parseInt(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("70")) {
                f = Integer.parseInt(ligne);
            }
        }

        l = new DXFLayer(name, color, f);
        if (color < 0) {
            l.setVisible(false);
        }
        u.currLayer = l;
        return l;
    }


}

