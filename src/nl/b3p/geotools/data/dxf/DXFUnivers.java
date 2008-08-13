package nl.b3p.geotools.data.dxf;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Vector;

import nl.b3p.geotools.data.dxf.entities.DXFArc;
import nl.b3p.geotools.data.dxf.entities.DXFBufferedReader;
import nl.b3p.geotools.data.dxf.entities.DXFCircle;
import nl.b3p.geotools.data.dxf.entities.DXFDimension;
import nl.b3p.geotools.data.dxf.entities.DXFEllipse;
import nl.b3p.geotools.data.dxf.entities.DXFEntity;
import nl.b3p.geotools.data.dxf.entities.DXFInsert;
import nl.b3p.geotools.data.dxf.entities.DXFLine;
import nl.b3p.geotools.data.dxf.entities.DXFLwPolyline;
import nl.b3p.geotools.data.dxf.entities.DXFPoint;
import nl.b3p.geotools.data.dxf.entities.DXFPolyline;
import nl.b3p.geotools.data.dxf.entities.DXFSolid;
import nl.b3p.geotools.data.dxf.entities.DXFText;
import nl.b3p.geotools.data.dxf.entities.DXFTrace;
import nl.b3p.geotools.data.dxf.header.DXFBlock;
import nl.b3p.geotools.data.dxf.header.DXFBlockReference;
import nl.b3p.geotools.data.dxf.header.DXFHeader;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFNameGenerator;
import nl.b3p.geotools.data.dxf.header.DXFTable;

public class DXFUnivers {

    public static Color _bgColor = Color.BLACK;
    public static boolean antialiasing = true;
    private Vector<DXFEntity> _entForUpdate = new Vector<DXFEntity>();
    public Rectangle2D.Double lastView = new Rectangle2D.Double();
    public Vector<DXFTable> theTables = new Vector<DXFTable>();
    public Vector<DXFBlock> theBlocks = new Vector<DXFBlock>();
    public DXFLayer currLayer;
    public DXFEntity currEntity;
    public DXFBlock currBlock;
    public double currThickness = 1.0f;
    public DXFHeader _header;
    public String _filename;
    public double strayX = 0;
    public double strayY = 0;

    public DXFUnivers(DXFHeader header) {
        reset();
        _header = header;
        if (header == null) {
            header = new DXFHeader();
        }
    }

    public void reset() {
        _filename = null;
        //_bgColor 	= DXFColor.getColor(255);

        DXFNameGenerator.reset();

        theTables.removeAllElements();
        theBlocks.removeAllElements();

        currLayer = new DXFLayer(DXFNameGenerator.getLayerName("DXFUnivers.0"), DXFColor.getDefaultColorIndex());

        DXFTable t = new DXFTable();
        t.theLayers.addElement(currLayer);
        theTables.addElement(t);
    }

    public DXFBlock findBlock(String nom) {
        DXFBlock b = null;
        for (int i = 0; i < theBlocks.size(); i++) {
            if (theBlocks.elementAt(i)._name.equals(nom)) {
                return theBlocks.elementAt(i);
            }
        }
        return b;
    }

    public DXFLayer findLayer(String nom) {
        DXFLayer l = null;
        for (int i = 0; i < theTables.size(); i++) {
            for (int j = 0; j < theTables.elementAt(i).theLayers.size(); j++) {
                if (theTables.elementAt(i).theLayers.elementAt(j)._nom.equals(nom)) {
                    return theTables.elementAt(i).theLayers.elementAt(j);
                }
            }
        }

        l = new DXFLayer(nom, DXFColor.getDefaultColorIndex());

        if (theTables.size() < 1) {
            theTables.add(new DXFTable());
        }

        theTables.elementAt(0).theLayers.add(l);

        return l;
    }

    public DXFLineType findLType(String name) {
        for (int i = 0; i < theTables.size(); i++) {
            for (int j = 0; j < theTables.elementAt(i).theLineTypes.size(); j++) {
                if (theTables.elementAt(i).theLineTypes.elementAt(j)._name.equals(name)) {
                    return theTables.elementAt(i).theLineTypes.elementAt(j);
                }
            }
        }
        return null;
    }

    public Vector getLTypes() {
        Vector<DXFLineType> v = new Vector<DXFLineType>();

        for (int i = 0; i < theTables.size(); i++) {
            for (int j = 0; j < theTables.elementAt(i).theLineTypes.size(); j++) {
                v.add(theTables.elementAt(i).theLineTypes.elementAt(j));
            }
        }
        return v;
    }

    public void readTables(DXFBufferedReader br) throws IOException {
        String ligne;
        DXFTable table = null;
        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("ENDSEC")) {
            if (ligne.toUpperCase().trim().equals("TABLE")) {
                table = new DXFTable(br, this);
                theTables.add(table);
            }
        }
    }

    public void readBlocks(DXFBufferedReader br) throws IOException {
        String ligne;
        DXFBlock b = null;

        while ((ligne = br.readLine()) != null && !ligne.trim().equalsIgnoreCase("ENDSEC")) {
            ligne = ligne.trim();
            if (ligne.equalsIgnoreCase("BLOCK")) {
                b = DXFBlock.read(br, this);
                theBlocks.add(b);
            }
        }
    }

    public void readEntities(DXFBufferedReader br) throws IOException {
        String ligne;
        while ((ligne = br.readLine()) != null && !ligne.equals("ENDSEC")) {
            addEntity(br, ligne, true);
        }
    }

    public DXFEntity addEntity(DXFBufferedReader br, String Element, boolean addToLayer) throws IOException {
        DXFEntity o = null;
        Element = Element.trim();

        if (Element.equals("0")) {
            Element = br.readLine();
        }
        if (Element.equals("LINE")) {
            o = DXFLine.read(br, this);
        } else if (Element.equals("ARC")) {
            o = DXFArc.read(br, this);
        } else if (Element.equals("CIRCLE")) {
            o = DXFCircle.read(br, this);
        } else if (Element.equals("POLYLINE")) {
            o = DXFPolyline.read(br, this);
        } else if (Element.equals("LWPOLYLINE")) {
            o = DXFLwPolyline.read(br, this);
        } else if (Element.equals("POINT")) {
            o = DXFPoint.read(br, this);
        } else if (Element.equals("SOLID")) {
            o = DXFSolid.read(br, this);
        } else if (Element.equals("TEXT")) {
            o = DXFText.read(br, this);
        } else if (Element.equals("MTEXT")) {
            o = DXFText.read(br, this);
        } else if (Element.equals("INSERT")) {
            o = DXFInsert.read(br, this);
        } else if (Element.equals("DIMENSION")) {
            o = DXFDimension.read(br, this);
        } else if (Element.equals("TRACE")) {
            o = DXFTrace.read(br, this);
        } else if (Element.equals("ELLIPSE")) {
            o = DXFEllipse.read(br, this);
        } else if (Element.equals("ATTDEF") || Element.equals("ENDBLK")) {
            o = new DXFPoint();
            addToLayer = false;
        } else {
//            DXFLog.writeLog(DXF_Loader.res.getString("DXFUnivers.87") + Element);
        }

        if (o != null && o._refLayer != null && addToLayer) {
            o._refLayer.theEnt.addElement(o);
        }
        return o;
    }

    public void addRefBlockForUpdate(DXFEntity obj) {
        _entForUpdate.addElement(obj);
    }

    public void updateRefBlock() {
        DXFEntity obj = null;
        for (int i = 0; i < _entForUpdate.size(); i++) {
            obj = _entForUpdate.get(i);
            if (obj instanceof DXFBlockReference) {
                changeBlock((DXFBlockReference) obj, ((DXFBlockReference) obj)._blockName);
            }
        }
        _entForUpdate.removeAllElements();
    }

    public void changeBlock(DXFBlockReference obj, String nom) {
        DXFBlock b;
        if (obj == null) {
            return;
        }
        if (obj._refBlock == null) {
            b = findBlock(nom);
            if (b != null) {
                if (nom.equalsIgnoreCase(b._name)) {
                    obj._blockName = nom;
                    obj._refBlock = b;
                }
            }
        } else {
            if (!nom.equalsIgnoreCase(obj._refBlock._name)) {
                b = findBlock(nom);
                if (nom.equalsIgnoreCase(b._name)) {
                    obj._blockName = nom;
                    obj._refBlock = b;
                }
            }
        }
    }
}
