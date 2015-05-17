package nl.b3p.geotools.data.dxf.header;

import nl.b3p.geotools.data.dxf.entities.DXFEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class DXFBlockReference extends DXFEntity {

    private static final Log log = LogFactory.getLog(DXFBlockReference.class);
    public DXFBlock _refBlock;
    public String _blockName;

    public DXFBlockReference(int c, DXFLayer l, int visibility, DXFLineType lineType, String nomBlock, DXFBlock refBlock) {
        super(c, l, visibility, lineType, DXFTables.defaultThickness);

        _refBlock = refBlock;
        _blockName = nomBlock;
    }
}
