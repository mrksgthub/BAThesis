package Datatypes;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.function.Supplier;

public class Vertex implements Serializable {

    public static Supplier<Vertex> getvSupplier = new Supplier<>() {
        private int id = 0;

        @Override
        public Vertex get() {

            return new Vertex("v" + id++);
        }
    };

    static int counter = 0;

    int depth;
    Vertex parent;
    boolean dummy = false;
    String name;
    ArrayList<Vertex> adjecentVertices = new ArrayList<>();
    int id;

    public Vertex(String name) {

        this.name = name;
        this.depth = 0;
        id = counter++;
    }

    public Vertex(String name, boolean dummy) {
        this.name = name;
        this.depth = 0;
        this.dummy = dummy;
        id = counter++;
    }

    public Vertex(String name, Vertex parent) {
        this.name = name;
        this.parent = parent;
        this.depth = 0;
        id = counter++;
    }


    public Vertex getParent() {
        return parent;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }

    public String getName() {
        if (name.length() != 0) {

            return name;
        } else {
            return name;
        }
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
       // if (!super.equals(o)) return false;
        Vertex that = (Vertex) o;
        return id == that.id;
    }


    public static Supplier<Vertex> getGetvSupplier() {
        return getvSupplier;
    }

    public static void setGetvSupplier(Supplier<Vertex> getvSupplier) {
        Vertex.getvSupplier = getvSupplier;
    }

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        Vertex.counter = counter;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }


    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Vertex> getAdjecentVertices() {
        return adjecentVertices;
    }

    public void setAdjecentVertices(ArrayList<Vertex> adjecentVertices) {
        this.adjecentVertices = adjecentVertices;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
