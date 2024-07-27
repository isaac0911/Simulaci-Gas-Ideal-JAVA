import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Caja {
    public double ancho;  // Ancho de la caja, accesible públicamente
    public double alto;   // Alto de la caja, accesible públicamente
    private Random random;  // Generador de números aleatorios para generar posiciones iniciales

    // Constructor de la clase Caja
    public Caja(double ancho, double alto) {
        this.ancho = ancho;  // Inicializa el ancho de la caja
        this.alto = alto;   // Inicializa el alto de la caja
        this.random = new Random();  // Crea una nueva instancia del generador de números aleatorios
    }

    // Verifica si una posición está dentro de los límites de la caja
    public boolean verificarDentroCaja(double x, double y) {
        return x >= 0 && x <= this.ancho && y >= 0 && y <= this.alto;  // Comprueba si las coordenadas (x, y) están dentro de los límites de la caja
    }

    // Calcula el tiempo hasta que una molécula choque con una pared de la caja
    public double calcularParedChoque(double x, double y, double vx, double vy) {
        List<Double> tiemposChoque = new ArrayList<>();  // Lista para almacenar los tiempos hasta el choque con cada pared

        // Calcula el tiempo hasta que la molécula choque con la pared derecha de la caja
        if (vx > 0) {
            tiemposChoque.add((this.ancho - x) / vx);  // Si la velocidad en x es positiva, calcula el tiempo hasta que choque con la pared derecha
        } 
        // Calcula el tiempo hasta que la molécula choque con la pared izquierda de la caja
        else if (vx < 0) {
            tiemposChoque.add(x / -vx);  // Si la velocidad en x es negativa, calcula el tiempo hasta que choque con la pared izquierda
        }

        // Calcula el tiempo hasta que la molécula choque con la pared superior de la caja
        if (vy > 0) {
            tiemposChoque.add((this.alto - y) / vy);  // Si la velocidad en y es positiva, calcula el tiempo hasta que choque con la pared superior
        } 
        // Calcula el tiempo hasta que la molécula choque con la pared inferior de la caja
        else if (vy < 0) {
            tiemposChoque.add(y / -vy);  // Si la velocidad en y es negativa, calcula el tiempo hasta que choque con la pared inferior
        }

        // Devuelve el tiempo mínimo hasta el choque con una pared
        return tiemposChoque.stream().min(Double::compare).orElse(Double.MAX_VALUE);  // Devuelve el tiempo mínimo de choque, o el máximo posible si no hay choque
    }

    // Genera una lista de posiciones iniciales para las moléculas
    public List<double[]> posicionInicial(int numMoleculas) {
        List<double[]> posiciones = new ArrayList<>();  // Lista para almacenar las posiciones iniciales de las moléculas

        // Genera posiciones aleatorias para cada molécula
        for (int i = 0; i < numMoleculas; i++) {
            double x = this.ancho / 2 + random.nextDouble() * this.ancho / 2;  // Genera una posición aleatoria en la mitad derecha de la caja
            double y = random.nextDouble() * this.alto;  // Genera una posición aleatoria en la altura de la caja
            posiciones.add(new double[]{x, y});  // Agrega la posición a la lista
        }
        return posiciones;  // Devuelve la lista de posiciones iniciales
    }
}