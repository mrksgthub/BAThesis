package Datastructures;



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

    private static int counter = 0;

    private boolean dummy = false;
    private final String name;
    ArrayList<Vertex> adjacentVertices = new ArrayList<>();
    private int id;

    public Vertex(String name) {

        this.name = name;
        id = counter++;
    }

    public Vertex(String name, boolean dummy) {
        this.name = name;
        this.dummy = dummy;
        id = counter++;
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


    public static void setCounter(int counter) {
        Vertex.counter = counter;
    }

    public boolean isDummy() {
        return dummy;
    }


    public ArrayList<Vertex> getAdjacentVertices() {
        return adjacentVertices;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
