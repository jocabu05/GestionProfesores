public class Profesor implements Comparable<Profesor> {
    private final String nombre;
    private int numSustituciones;

    public Profesor(String nombre, int numSustituciones) {
        this.nombre = nombre;
        this.numSustituciones = numSustituciones;
    }

    public String getNombre() {
        return nombre;
    }

    public int getNumSustituciones() {
        return numSustituciones;
    }

    /**
     * Compara por el número de sustituciones para ordenar de menor a mayor.
     */
    @Override
    public int compareTo(Profesor otro) {
        return Integer.compare(this.numSustituciones, otro.numSustituciones);
    }

    @Override
    public String toString() {
        return String.format("%-10s (Sustituciones: %d)", nombre, numSustituciones);
    }
}