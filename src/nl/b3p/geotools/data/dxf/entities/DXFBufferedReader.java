package nl.b3p.geotools.data.dxf.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class DXFBufferedReader extends BufferedReader {

    public DXFBufferedReader(Reader r) {
        super(r);
    }

    @Override
    public String readLine() throws IOException {
        String value;

        value = super.readLine();

        if (value != null) {
            value = value.trim();
        }
        return value;
    }
}
