package nl.b3p.geotools.data.dxf.parser;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;

import nl.b3p.geotools.data.dxf.entities.DXFEntity;
import nl.b3p.geotools.data.dxf.header.DXFBlock;
import nl.b3p.geotools.data.dxf.header.DXFBlockReference;
import nl.b3p.geotools.data.dxf.header.DXFBlocks;
import nl.b3p.geotools.data.dxf.header.DXFEntities;
import nl.b3p.geotools.data.dxf.header.DXFHeader;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTables;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFUnivers implements DXFConstants {

    private static final Log log = LogFactory.getLog(DXFUnivers.class);
    public static final PrecisionModel precisionModel = new PrecisionModel(PrecisionModel.FLOATING);
    public static final int NUM_OF_SEGMENTS = 36;
    private Vector<DXFBlockReference> _entForUpdate = new Vector<DXFBlockReference>();
    public Vector<DXFTables> theTables = new Vector<DXFTables>();
    public Vector<DXFBlock> theBlocks = new Vector<DXFBlock>();
    public Vector<DXFEntity> theEntities = new Vector<DXFEntity>();
    private DXFHeader _header;
    private GeometryFactory geometryFactory = null;
    private Geometry errorGeometry = null;

    public DXFUnivers() {
    }

    public void read(DXFLineNumberReader br) throws IOException {
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
                    if (type.equals(SECTION)) {
                        readSection(br);
                    }
                    break;
                default:
                    break;
            }
        }

        updateRefBlock();

    }

    public void readSection(DXFLineNumberReader br) throws IOException {
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
                    if (type.equals(ENDSEC)) {
                        doLoop = false;
                        break;
                    }
                    break;
                case NAME:
                    String name = cvp.getStringValue();
                    if (name.equals(HEADER)) {
                        _header = DXFHeader.read(br);
                        if (_header._EXTMAX == null || _header._EXTMIN == null) {
                            _header = new DXFHeader();
                        }
                        /* construct geometry factory */
                        geometryFactory = new GeometryFactory(precisionModel, _header._SRID);
                    } else if (name.equals(TABLES)) {
                        DXFTables at = DXFTables.readTables(br);
                        theTables.add(at);
                    } else if (name.equals(BLOCKS)) {
                        DXFBlocks ab = DXFBlocks.readBlocks(br, this);
                        theBlocks.addAll(ab.theBlocks);
                    } else if (name.equals(ENTITIES)) {
                        DXFEntities dxfes = DXFEntities.readEntities(br, this);
                        theEntities.addAll(dxfes.theEntities);
                    // toevoegen aan layer doen we even niet, waarschijnlijk niet nodig
//                        if (o != null && o._refLayer != null) {
//                            o._refLayer.theEnt.add(o);
//                        }
                    }
                    break;
                default:
                    break;
            }

        }

        updateRefBlock();

    }

    public DXFBlock findBlock(String nom) {
        DXFBlock b = null;
        for (int i = 0; i <
                theBlocks.size(); i++) {
            if (theBlocks.elementAt(i)._name.equals(nom)) {
                return theBlocks.elementAt(i);
            }
        }
        return b;
    }

    public DXFLayer findLayer(String nom) {
        DXFLayer l = null;
        for (int i = 0; i <
                theTables.size(); i++) {
            for (int j = 0; j <
                    theTables.elementAt(i).theLayers.size(); j++) {
                if (theTables.elementAt(i).theLayers.elementAt(j).getName().equals(nom)) {
                    l = theTables.elementAt(i).theLayers.elementAt(j);
                    return l;
                }

            }
        }

        l = new DXFLayer(nom, DXFColor.getDefaultColorIndex());

        if (theTables.size() < 1) {
            theTables.add(new DXFTables());
        }

        theTables.elementAt(0).theLayers.add(l);

        return l;
    }

    public DXFLineType findLType(
            String name) {
        for (int i = 0; i <
                theTables.size(); i++) {
            for (int j = 0; j <
                    theTables.elementAt(i).theLineTypes.size(); j++) {
                if (theTables.elementAt(i).theLineTypes.elementAt(j)._name.equals(name)) {
                    return theTables.elementAt(i).theLineTypes.elementAt(j);
                }

            }
        }
        return null;
    }

    public void addRefBlockForUpdate(DXFBlockReference obj) {
        _entForUpdate.add(obj);
    }

    public void updateRefBlock() {
        DXFBlockReference bro = null;
        for (int i = 0; i < _entForUpdate.size(); i++) {
            bro = _entForUpdate.get(i);
            DXFBlock b = findBlock(bro._blockName);
            if (b != null) {
                bro._refBlock = b;
            }
        }
        _entForUpdate.removeAllElements();
    }

    public GeometryFactory getGeometryFactory() {
        return geometryFactory;
    }

    public void setGeometryFactory(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    public Geometry getErrorGeometry() {
        if (errorGeometry == null && geometryFactory != null) {
            errorGeometry = geometryFactory.createPoint(new Coordinate(0.0, 0.0));
        }
        return errorGeometry;
    }

    public void setErrorGeometry(Geometry errorGeometry) {
        this.errorGeometry = errorGeometry;
    }
}
