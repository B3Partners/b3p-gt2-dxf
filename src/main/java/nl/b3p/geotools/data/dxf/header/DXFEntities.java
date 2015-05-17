package nl.b3p.geotools.data.dxf.header;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import nl.b3p.geotools.data.dxf.entities.DXFArc;
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
import nl.b3p.geotools.data.dxf.entities.DXFSpLine;
import nl.b3p.geotools.data.dxf.entities.DXFText;
import nl.b3p.geotools.data.dxf.entities.DXFTrace;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFConstants;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFEntities implements DXFConstants {

    private static final Log log = LogFactory.getLog(DXFEntities.class);
    public Vector<DXFEntity> theEntities = new Vector<DXFEntity>();
    public Map<String,Integer> unsupportedEntitiesCounts;

    public DXFEntities() {
    }

    public DXFEntities(Vector<DXFEntity> sEntities) {
        if (sEntities == null) {
            sEntities = new Vector<DXFEntity>();
        }
        this.theEntities = sEntities;
    }

    public static DXFEntities readEntities(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        Vector<DXFEntity> sEnt = new Vector<DXFEntity>();

        DXFCodeValuePair cvp = null;
        DXFGroupCode gc = null;

        Map<String,Integer> unsupportedEntitiesCounts = new HashMap<String,Integer>();
        
        int sln = br.getLineNumber();
        log.debug(">Enter at line: " + sln);
        boolean doLoop = true;
        while (doLoop) {
            cvp = new DXFCodeValuePair();
            try {
                gc = cvp.read(br);
            } catch (DXFParseException ex) {
                throw new IOException("DXF parse error" + ex.getLocalizedMessage());
            } catch (EOFException e) {
                doLoop = false;
                break;
            }

            switch (gc) {
                case TYPE:
                    DXFEntity dxfe = null;
                    String type = cvp.getStringValue();
                    if (type.equals(ENDSEC) || type.equals(ENDBLK)) {
                        doLoop = false;
                        break;
                    } else if (type.equals(LINE)) {
                        dxfe = DXFLine.read(br, univers);
                    } else if (type.equals(ARC)) {
                        dxfe = DXFArc.read(br, univers);
                    } else if (type.equals(CIRCLE)) {
                        dxfe = DXFCircle.read(br, univers);
                    } else if (type.equals(POLYLINE)) {
                             dxfe = DXFPolyline.read(br, univers);
                    } else if (type.equals(LWPOLYLINE)) {
                               dxfe = DXFLwPolyline.read(br, univers);
                    } else if (type.equals(POINT)) {
                        dxfe = DXFPoint.read(br, univers);
                    } else if (type.equals(SOLID)) {
                        dxfe = DXFSolid.read(br, univers);
                    } else if (type.equals(TEXT) || type.equals(MTEXT) || type.equals(ATTRIB)) {
                        dxfe = DXFText.read(br, univers, type.equals(MTEXT));
                    } else if (type.equals(INSERT)) {
                        dxfe = DXFInsert.read(br, univers);
                    } else if (type.equals(DIMENSION)) {
                        dxfe = DXFDimension.read(br, univers);
                    } else if (type.equals(TRACE)) {
                        dxfe = DXFTrace.read(br, univers);
                    } else if (type.equals(ELLIPSE)) {
                        dxfe = DXFEllipse.read(br, univers);
                    } else if (type.equals(SPLINE)) {
                        dxfe = DXFSpLine.read(br, univers);
                    } 
                    if (dxfe != null) {
                        sEnt.add(dxfe);
                    } else {
                        Integer count = unsupportedEntitiesCounts.get(type);
                        if(count == null) {
                            count = 0;
                        }
                        count++;
                        unsupportedEntitiesCounts.put(type, count);
                    }
                    break;
                default:
                    break;
            }

        }
        DXFEntities e = new DXFEntities(sEnt);
        e.unsupportedEntitiesCounts = unsupportedEntitiesCounts;

        log.debug(e.toString(sEnt.size()));
        log.debug(">Exit at line: " + br.getLineNumber());

        return e;
    }

    public String toString(int numEntities) {
        StringBuffer s = new StringBuffer();
        s.append("DXFEntities [");
        s.append("numEntities: ");
        s.append(numEntities);
        s.append("]");
        return s.toString();
    }
}
