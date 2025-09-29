import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GestorSustituciones
{
    // 📌 Ruta fija (pero con File.separator para independencia del SO)
    private static final String HORARIOS_DIR = "C:" + File.separator + "EjercicioAD";
    private static final String CONTROL_FILE_NAME = "sustituciones_control.csv";
    private static final String CONTROL_FILE_PATH = HORARIOS_DIR + File.separator + CONTROL_FILE_NAME;

    // Cabecera para el fichero de control
    private static final String CONTROL_HEADER = "Profesor,FechaHora,Total\n";

    // Diccionarios para mapa de días y horas
    private static final Map<String, Integer> DIA_TO_ROW = Map.of(
            "LUNES", 0, "MARTES", 1, "MIERCOLES", 2, "JUEVES", 3, "VIERNES", 4
    );
    private static final Map<String, Integer> HORA_TO_COL = Map.of(
            "1", 0, "2", 1, "3", 2, "4", 3, "5", 4, "6", 5
    );

    /**
     * Comprueba si el archivo de horario del profesor existe en la carpeta fija.
     */
    public boolean existeArchivoProfesor(String profesor) {
        String filePath = HORARIOS_DIR + File.separator + profesor.toLowerCase() + ".csv";
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Carga sustituciones acumuladas desde el fichero de control.
     */
    public Map<String, Integer> cargarSustituciones() {
        Map<String, Integer> sustituciones = new HashMap<>();
        if (!Files.exists(Paths.get(CONTROL_FILE_PATH))) return sustituciones;

        try (BufferedReader br = new BufferedReader(new FileReader(CONTROL_FILE_PATH))) {
            br.readLine(); // saltar cabecera
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(";", -1); // <- ahora separador ;
                if (campos.length >= 3) {
                    String nombre = campos[0].trim().toUpperCase();
                    try {
                        int total = Integer.parseInt(campos[2].trim());
                        sustituciones.put(nombre, total);
                    } catch (NumberFormatException ignore) {}
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ No se pudo leer el archivo de control: " + e.getMessage());
        }
        return sustituciones;
    }

    /**
     * Busca profesores disponibles (excluyendo al ausente).
     */
    public List<Profesor> buscarSustitutosDisponibles(String ausente, String dia, String hora) {
        int filaDia = DIA_TO_ROW.getOrDefault(dia.toUpperCase(), -1);
        int colHora = HORA_TO_COL.getOrDefault(hora.replace("ª",""), -1);
        if (filaDia == -1 || colHora == -1) return Collections.emptyList();

        Map<String, Integer> acumulados = cargarSustituciones();
        List<Profesor> candidatos = new ArrayList<>();

        File carpeta = new File(HORARIOS_DIR);
        File[] archivos = carpeta.listFiles((d, name) ->
                name.toLowerCase().endsWith(".csv") && !name.equalsIgnoreCase(CONTROL_FILE_NAME));

        if (archivos == null) return Collections.emptyList();

        for (File f : archivos) {
            String nombreProfesor = f.getName().replace(".csv", "").toUpperCase();
            if (nombreProfesor.equalsIgnoreCase(ausente)) continue; // excluir ausente

            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String linea;
                int fila = 0;
                while ((linea = br.readLine()) != null && fila <= filaDia) {
                    if (fila == filaDia) {
                        String[] columnas = linea.split(";", -1);
                        if (columnas.length > colHora) {
                            String clase = columnas[colHora].trim().toUpperCase();
                            if (clase.equals("LIBRE") || clase.equals("GUARDIA") || clase.contains("ED") || clase.contains("SX")) {
                                int total = acumulados.getOrDefault(nombreProfesor, 0);
                                candidatos.add(new Profesor(nombreProfesor, total));
                            }
                        }
                        break;
                    }
                    fila++;
                }
            } catch (IOException e) {
                System.err.println("Error leyendo " + nombreProfesor + ": " + e.getMessage());
            }
        }

        candidatos.sort(Comparator.comparingInt(Profesor::getNumSustituciones));
        return candidatos;
    }

    /**
     * Registra sustitución en el archivo de control.
     */
    public void registrarSustitucion(String profesor, String dia, String hora) {
        Map<String, Integer> acumulados = cargarSustituciones();
        String nombre = profesor.toUpperCase();
        int nuevoTotal = acumulados.getOrDefault(nombre, 0) + 1;

        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        String linea = nombre + ";" + fecha + " (" + dia + " " + hora + ");" + nuevoTotal + "\n";

        boolean existe = Files.exists(Paths.get(CONTROL_FILE_PATH));

        try (PrintWriter pw = new PrintWriter(new FileWriter(CONTROL_FILE_PATH, true))) {
            if (!existe) pw.write(CONTROL_HEADER);
            pw.write(linea);
            System.out.println("✔️ Sustitución registrada: " + nombre + " → total " + nuevoTotal);
        } catch (IOException e) {
            System.err.println("Error al guardar sustitución: " + e.getMessage());
        }
    }

    /**
     * Muestra ranking descendente.
     */
    public void mostrarRanking() {
        Map<String, Integer> acumulados = cargarSustituciones();
        if (acumulados.isEmpty()) {
            System.out.println("⚠️ No hay sustituciones registradas.");
            return;
        }

        List<Profesor> ranking = new ArrayList<>();
        acumulados.forEach((n, t) -> ranking.add(new Profesor(n, t)));
        ranking.sort((a, b) -> Integer.compare(b.getNumSustituciones(), a.getNumSustituciones()));

        System.out.println("\n=== Ranking de Sustituciones ===");
        int pos = 1;
        for (Profesor p : ranking) {
            System.out.println(pos++ + ". " + p);
        }
    }

    /**
     * Consulta sustituciones de un profesor.
     */
    public void consultarSustituciones(String profesor) {
        Map<String, Integer> acumulados = cargarSustituciones();
        int total = acumulados.getOrDefault(profesor.toUpperCase(), 0);
        System.out.println("ℹ️ El profesor " + profesor.toUpperCase() + " lleva " + total + " sustituciones.");
    }
}
