package nl.b3p.geotools.data.dxf.entities;

import java.awt.BasicStroke;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTable;

public abstract class DXFEntity {

    public DXFLineType _lineType;
    public int _color;
    public DXFLayer _refLayer;
    public double _thickness;
    public boolean isVisible = true;
    public boolean selected = false;
    public boolean changing = false;
    public BasicStroke _stroke;

    public DXFEntity(int c, DXFLayer l, int visibility, DXFLineType lineType, double thickness) {
        _lineType = lineType;
        _refLayer = l;
        _color = c;
        _thickness = thickness;

        if (visibility == 0) {
            isVisible = true;
        } else {
            isVisible = false;
        }
        if (_lineType != null) {
            _stroke = new BasicStroke((float) _thickness, DXFTable.CAP, DXFTable.JOIN, 10.0f, DXFLineType.parseTxt(_lineType._value), 0.0f);
        } else {
            _stroke = new BasicStroke((float) _thickness, DXFTable.CAP, DXFTable.JOIN, 10.0f, DXFTable.defautMotif, 0.0f);
        }
    }

    public void setVisible(boolean bool) {
        isVisible = bool;
    }

    public void setSelected(boolean s) {
        this.selected = s;
    }

    public void setChanging(boolean b) {
        this.changing = b;
    }
}
