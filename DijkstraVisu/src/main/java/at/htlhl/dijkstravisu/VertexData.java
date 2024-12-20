package at.htlhl.dijkstravisu;

import java.util.Objects;

public class VertexData {

    private String name;

    public VertexData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VertexData that = (VertexData) obj;
        return Objects.equals(name, that.name); // Vergleicht die Namen
    }

}
