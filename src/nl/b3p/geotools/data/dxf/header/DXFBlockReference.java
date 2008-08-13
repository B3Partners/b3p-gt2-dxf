package nl.b3p.geotools.data.dxf.header;



import nl.b3p.geotools.data.dxf.entities.DXFEntity;

public abstract class DXFBlockReference extends DXFEntity {

    public DXFBlock _refBlock;
    public String _blockName;


    public DXFBlockReference(int c, DXFLayer l, int visibility, DXFLineType lineType, String nomBlock, DXFBlock refBlock) {
        super(c, l, visibility, lineType, DXFTable.defaultThickness);

        _refBlock = refBlock;
        _blockName = nomBlock;
    }

}
