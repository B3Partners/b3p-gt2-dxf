package nl.b3p.geotools.data.dxf.parser;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
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
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

public class DXFUnivers implements DXFConstants {

    private static final Log log = LogFactory.getLog(DXFUnivers.class);
    private Vector<DXFBlockReference> _entForUpdate = new Vector<DXFBlockReference>();
    public Vector<DXFTables> theTables = new Vector<DXFTables>();
    public Vector<DXFBlock> theBlocks = new Vector<DXFBlock>();
    public Vector<DXFEntity> theEntities = new Vector<DXFEntity>();
    public DXFHeader _header;

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

    public DXFBlock findBlock(
            String nom) {
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
                if (theTables.elementAt(i).theLayers.elementAt(j)._nom.equals(nom)) {
                    return theTables.elementAt(i).theLayers.elementAt(j);
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

    public static void main(String[] args) throws Exception {

        Class c = DXFUnivers.class;
        URL url = c.getResource("/log4j.properties");
        Properties p = new Properties();
        p.load(url.openStream());
        PropertyConfigurator.configure(p);
        log.info("logging configured!");
        
        
        URL url2 = c.getResource("/VM50.dxf");
        CountingInputStream cis = new CountingInputStream(url2.openStream());
        DXFLineNumberReader lnr = new DXFLineNumberReader(new InputStreamReader(cis));
        DXFUnivers theUnivers = new DXFUnivers();
        theUnivers.read(lnr);
        Vector<DXFEntity> theEntities = theUnivers.theEntities;
        if (theEntities != null) {
            theEntities = new Vector<DXFEntity>();
        }

//        String version = theUnivers._header._ACADVER;

    }
}
