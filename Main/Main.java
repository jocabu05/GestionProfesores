import java.util.List;
import java.util.Scanner;

public class Main {
    private static final GestorSustituciones gestor = new GestorSustituciones();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        mostrarMenu();
    }

    private static void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE SUSTITUCIONES DE PROFESORES ---");
            System.out.println("1. Detectar y Asignar Sustituto");
            System.out.println("2. Consultar Sustituciones de un Profesor");
            System.out.println("3. Mostrar Ranking de Sustituciones");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    gestionarAsignacion();
                    break;
                case 2:
                    consultarSustituto();
                    break;
                case 3:
                    gestor.mostrarRanking();
                    break;
                case 0:
                    System.out.println("Saliendo del programa. ¡Hasta pronto! 👋");
                    break;
                default:
                    System.out.println("Opción no válida. Inténtelo de nuevo.");
            }
        } while (opcion != 0);
    }

    private static void gestionarAsignacion() {
        System.out.print("\nNombre del profesor ausente: ");
        String profesorAusente = scanner.nextLine().trim().toUpperCase();

        // COMPROBACIÓN DE EXISTENCIA
        if (!gestor.existeArchivoProfesor(profesorAusente)) {
            System.out.println("❌ ERROR: No existe archivo de horario para '" + profesorAusente + "'.");
            return;
        }

        System.out.print("Día de la semana de la clase a sustituir (Ej: Martes): ");
        String dia = scanner.nextLine().trim();
        System.out.print("Hora de la clase a sustituir (Ej: 2ª): ");
        String hora = scanner.nextLine().trim();

        // 1. Obtener el listado ordenado
        List<Profesor> sustitutos = gestor.buscarSustitutosDisponibles(profesorAusente, dia, hora);

        if (sustitutos.isEmpty()) {
            System.out.println("❌ No se puede realizar la sustitución. No hay profesores libres o la franja es inválida.");
            return;
        }

        // 2. Mostrar listado
        System.out.println("\n--- Posibles Sustitutos para " + profesorAusente + " (" + dia + " " + hora + ") ---");
        for (int i = 0; i < sustitutos.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, sustitutos.get(i).toString());
        }
        System.out.println("---------------------------------------------------------");

        // 3. Elegir sustituto
        System.out.print("Elija sustituto (número) o 0 para cancelar: ");
        int opcion;
        try {
            opcion = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            opcion = 0;
        }

        if (opcion >= 1 && opcion <= sustitutos.size()) {
            String sustitutoElegido = sustitutos.get(opcion - 1).getNombre();
            gestor.registrarSustitucion(sustitutoElegido, dia, hora);
        } else {
            System.out.println("Sustitución cancelada.");
        }
    }

    private static void consultarSustituto() {
        System.out.print("\nNombre del profesor a consultar: ");
        String nombre = scanner.nextLine().trim().toUpperCase();
        gestor.consultarSustituciones(nombre);
    }
}
