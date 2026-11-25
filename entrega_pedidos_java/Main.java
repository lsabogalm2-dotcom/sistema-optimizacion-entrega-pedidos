import java.util.List;
import java.util.Scanner;

/**
 * Sistema de optimizacion de rutas para la entrega de pedidos.
 * Los pesos de las aristas representan tiempos estimados en minutos.
 */
public class Main {
    private static final int BODEGA_CENTRAL = 0;

    private Main() {
    }

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            Grafo grafo = crearGrafo();

            System.out.println("=================================================");
            System.out.println("   SISTEMA DE OPTIMIZACION DE ENTREGA DE PEDIDOS");
            System.out.println("=================================================");
            System.out.println("Este programa calcula rutas con Dijkstra y Floyd-Warshall.");
            System.out.println("Los tiempos son hipoteticos y se expresan en minutos.\n");

            mostrarUbicaciones(grafo);
            System.out.print("\nIngrese el nombre del cliente: ");
            String cliente = entrada.nextLine().trim();
            if (cliente.isEmpty()) {
                cliente = "Cliente sin nombre";
            }

            int destino = solicitarDestino(entrada, grafo);
            String numeroPedido = "PED-001";

            System.out.println("\n---------------- DATOS DEL PEDIDO ----------------");
            System.out.println("Numero de pedido: " + numeroPedido);
            System.out.println("Cliente: " + cliente);
            System.out.println("Origen: " + grafo.getNombreCiudad(BODEGA_CENTRAL));
            System.out.println("Destino: " + grafo.getNombreCiudad(destino));

            // Dijkstra: mejor ruta desde la bodega al destino elegido.
            ResultadoDijkstra resultadoDijkstra =
                new Dijkstra().calcular(grafo, BODEGA_CENTRAL);

            List<Integer> rutaDijkstra = resultadoDijkstra.getRutaHasta(destino);
            int tiempoDijkstra = resultadoDijkstra.getDistanciaHasta(destino);

            System.out.println("\n--------------- RESULTADO DIJKSTRA ---------------");
            imprimirRuta(grafo, rutaDijkstra, tiempoDijkstra);

            // Floyd-Warshall: mejores rutas entre todos los pares de ubicaciones.
            ResultadoFloydWarshall resultadoFloyd =
                new FloydWarshall().calcular(grafo);

            List<Integer> rutaFloyd =
                resultadoFloyd.getRuta(BODEGA_CENTRAL, destino);
            int tiempoFloyd =
                resultadoFloyd.getDistancia(BODEGA_CENTRAL, destino);

            System.out.println("\n------------- RESULTADO FLOYD-WARSHALL ------------");
            imprimirRuta(grafo, rutaFloyd, tiempoFloyd);

            System.out.println("\n---------------- COMPARACION FINAL ----------------");
            if (tiempoDijkstra == tiempoFloyd) {
                System.out.println("Los dos algoritmos encontraron el mismo tiempo minimo: "
                    + tiempoDijkstra + " minutos.");
            } else {
                System.out.println("Los algoritmos reportaron resultados diferentes.");
            }

            System.out.println("\nEl pedido " + numeroPedido + " queda planificado para " + cliente + ".");
            System.out.println("Nota: el sistema realiza una simulacion; no consulta trafico en tiempo real.");
        }
    }

    /** Construye el grafo dirigido con las ubicaciones y tiempos del ejercicio. */
    private static Grafo crearGrafo() {
        Grafo grafo = new Grafo(
            "Bodega Central",
            "Teusaquillo",
            "Chapinero",
            "Centro",
            "Usaquen"
        );

        // Cada arista representa un trayecto permitido y su tiempo estimado.
        // Al ser dirigido, si se necesita el regreso hay que agregar otra arista.
        grafo.agregarArista(0, 1, 12); // Bodega -> Teusaquillo
        grafo.agregarArista(0, 2, 18); // Bodega -> Chapinero
        grafo.agregarArista(1, 3, 10); // Teusaquillo -> Centro
        grafo.agregarArista(3, 4, 25); // Centro -> Usaquen
        grafo.agregarArista(2, 4, 14); // Chapinero -> Usaquen
        grafo.agregarArista(1, 2, 8);  // Teusaquillo -> Chapinero
        grafo.agregarArista(2, 3, 9);  // Chapinero -> Centro

        return grafo;
    }

    /** Muestra las ubicaciones disponibles para la entrega. */
    private static void mostrarUbicaciones(Grafo grafo) {
        System.out.println("\nUBICACIONES DISPONIBLES");
        for (int i = 0; i < grafo.getCantidadCiudades(); i++) {
            System.out.printf("%d. %s%s%n", i,
                grafo.getNombreCiudad(i),
                i == BODEGA_CENTRAL ? " (origen)" : "");
        }
    }

    /** Solicita un destino valido, distinto de la bodega de origen. */
    private static int solicitarDestino(Scanner entrada, Grafo grafo) {
        while (true) {
            System.out.print("\nSeleccione el numero de la zona de entrega: ");
            String dato = entrada.nextLine().trim();
            try {
                int destino = Integer.parseInt(dato);
                if (destino <= BODEGA_CENTRAL || destino >= grafo.getCantidadCiudades()) {
                    System.out.println("Opcion no valida. Seleccione un destino entre 1 y "
                        + (grafo.getCantidadCiudades() - 1) + ".");
                } else {
                    return destino;
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un numero entero.");
            }
        }
    }

    /** Imprime la secuencia de ubicaciones y el tiempo total de la ruta. */
    private static void imprimirRuta(Grafo grafo, List<Integer> ruta, int tiempo) {
        if (ruta == null || ruta.isEmpty() || tiempo == Grafo.INFINITO) {
            System.out.println("No existe una ruta disponible para este destino.");
            return;
        }

        System.out.print("Recorrido: ");
        for (int i = 0; i < ruta.size(); i++) {
            if (i > 0) {
                System.out.print(" -> ");
            }
            System.out.print(grafo.getNombreCiudad(ruta.get(i)));
        }
        System.out.println("\nTiempo estimado total: " + tiempo + " minutos.");
    }
}
