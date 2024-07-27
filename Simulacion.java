/*

Comandos para ejecutar en CMD:

javac -cp ".;libs/jfreechart-1.5.3.jar" Caja.java Molecula.java Simulacion.java

java -cp ".;libs/jfreechart-1.5.3.jar" Simulacion

*/

// Importa clases de la biblioteca AWT para manejar gráficos y dimensiones
import java.awt.Color;  // Para definir colores en el panel gráfico
import java.awt.Dimension;  // Para establecer el tamaño preferido del panel
import java.awt.Graphics;  // Para dibujar en el panel gráfico

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Importa clases de la biblioteca Swing para crear la interfaz gráfica
import javax.swing.JFrame;  // Para crear una ventana de aplicación
import javax.swing.JPanel;  // Para crear un panel dentro de la ventana
import javax.swing.JScrollPane;  // Para agregar una barra de desplazamiento al gráfico

// Importa clases de la biblioteca JFreeChart para generar gráficos
import org.jfree.chart.ChartFactory;  // Para crear gráficos, como el gráfico de líneas
import org.jfree.chart.ChartPanel;  // Para agregar el gráfico a un panel
import org.jfree.chart.JFreeChart;  // Representa el gráfico creado
import org.jfree.chart.plot.PlotOrientation;  // Para especificar la orientación del gráfico
import org.jfree.data.xy.XYSeries;  // Para almacenar series de datos en el gráfico de líneas
import org.jfree.data.xy.XYSeriesCollection;  // Para crear un conjunto de datos a partir de series

public class Simulacion extends JPanel {
    // Lista de moléculas en la simulación
    private List<Molecula> moleculas;

    // Caja que contiene las moléculas
    private Caja caja;

    // Paso de tiempo para la simulación
    private double dt = 0.1;

    // Número de moléculas en la simulación
    private int numMoleculas;

    // Serie de datos para el gráfico de fluctuaciones
    private XYSeries fluctuacionesSeries;

    // Tiempo actual de la simulación
    private int tiempo;

    // Tiempo máximo de simulación
    private int tiempoMaximo;

    // Masa de cada molécula en kg
    private double masaMolecula = 1e-26;

    // Constante de Boltzmann en J/K
    private double kBoltzmann = 1.38e-23;

    // Constructor de la clase Simulacion
    public Simulacion(int numMoleculas, double ancho, double alto, double radioMolecula, int tiempoMaximo) {
        this.caja = new Caja(ancho, alto);  // Inicializa la caja con el ancho y alto especificados
        this.moleculas = new ArrayList<>();  // Crea una lista vacía de moléculas
        this.numMoleculas = numMoleculas;  // Asigna el número de moléculas
        this.tiempoMaximo = tiempoMaximo;  // Asigna el tiempo máximo de simulación
        this.fluctuacionesSeries = new XYSeries("Fluctuaciones");  // Crea una serie para el gráfico de fluctuaciones
        Random random = new Random();  // Crea un generador de números aleatorios
        List<double[]> posicionesIniciales = caja.posicionInicial(numMoleculas);  // Obtiene posiciones iniciales de las moléculas

        // Inicializa las moléculas con posiciones y velocidades aleatorias
        for (double[] pos : posicionesIniciales) {
            double vx = 20*(-1 + 2 * random.nextDouble());  // Velocidad en x aleatoria entre -20 y 20
            double vy = 20*(-1 + 2 * random.nextDouble());  // Velocidad en y aleatoria entre -20 y 20
            moleculas.add(new Molecula(radioMolecula, pos[0], pos[1], vx, vy));  // Crea una nueva molécula y la agrega a la lista
        }

        // Calcula la energía cinética total del sistema
        double energiaCinEticaTotal = 0.0;
        for (Molecula mol : moleculas) {
            // Calcula la velocidad de la molécula
            double velocidad = Math.sqrt(Math.pow(mol.getVx(), 2) + Math.pow(mol.getVy(), 2));
            // Calcula la energía cinética de la molécula
            energiaCinEticaTotal += 0.5 * masaMolecula * Math.pow(velocidad, 2);
        }

        // Calcula la energía cinética promedio por molécula
        double energiaCinEticaPromedio = energiaCinEticaTotal / numMoleculas;
        // Calcula la energía cinética total del sistema
        double energiaCinEticaSistema = numMoleculas * energiaCinEticaPromedio;
        // Calcula el área de la caja
        double areaCaja = caja.ancho * caja.alto;
        // Calcula la presión del sistema usando la energía cinética
        double presion = (2.0 / 3.0) * (numMoleculas * energiaCinEticaPromedio) / areaCaja;
        // Calcula la temperatura del sistema usando la energía cinética promedio
        double temperatura = (2.0 / 3.0) * energiaCinEticaPromedio / kBoltzmann;

        // Muestra en consola las cantidades termodinámicas
        System.out.printf("---------------------------------------\n");
        System.out.printf("Fluctuación inicial: %.2f\n", Math.abs(0 - (numMoleculas/2.0)));
        System.out.printf("Energía Cinética Promedio por Molécula: %.2e J\n", energiaCinEticaPromedio);
        System.out.printf("Energía Cinética Total del Sistema: %.2e J\n", energiaCinEticaSistema);
        System.out.printf("Presión del Sistema: %.2e Pa\n", presion);
        System.out.printf("Temperatura del Sistema: %.2e K\n", temperatura);
        System.out.printf("---------------------------------------\n");

        // Configura las propiedades del panel
        setPreferredSize(new Dimension((int) ancho, (int) alto));
        setBackground(Color.WHITE);  // Establece el color de fondo del panel a blanco
    }

    // Actualiza la simulación en cada paso de tiempo
    public void actualizar() {
        int numEnIzquierda = 0;  // Contador de moléculas a la izquierda del centro

        // Actualiza la posición de cada molécula y verifica si está en el lado izquierdo de la caja
        for (Molecula mol : moleculas) {
            mol.actualizarPosicion(dt);  // Actualiza la posición de la molécula en función del tiempo
            mol.rebotePared(caja.ancho, caja.alto);  // Verifica si la molécula rebota en las paredes de la caja
            if (mol.getX() <= caja.ancho / 2) {
                numEnIzquierda++;  // Incrementa el contador si la molécula está a la izquierda del centro
            }
        }

        // Actualiza el gráfico de fluctuaciones con la diferencia entre el número de moléculas en la izquierda y en equilibrio
        fluctuacionesSeries.add(tiempo, Math.abs(numEnIzquierda - numMoleculas / 2.0));
        tiempo++;  // Incrementa el tiempo de simulación

        // Verifica si se ha alcanzado el tiempo máximo de simulación
        if (tiempo > tiempoMaximo) {
            System.out.println("Tiempo máximo alcanzado. La simulación ha terminado.");
            new java.util.Scanner(System.in).nextLine();  // Espera la entrada del usuario para cerrar
        }

        repaint();  // Vuelve a dibujar el panel para mostrar las nuevas posiciones de las moléculas
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);  // Establece el color para dibujar las moléculas
        for (Molecula mol : moleculas) {
            g.fillOval((int) (mol.getX() - mol.getRadio()), (int) (mol.getY() - mol.getRadio()),
                    (int) (mol.getRadio() * 2), (int) (mol.getRadio() * 2));  // Dibuja cada molécula como un círculo
        }
    }

    // Devuelve el conjunto de datos del gráfico de fluctuaciones
    public XYSeriesCollection getFluctuacionesDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(fluctuacionesSeries);  // Agrega la serie de fluctuaciones al conjunto de datos
        return dataset;
    }

    public static void main(String[] args) {
        int numMoleculas = 10000;  // Número de moléculas en la simulación
        double ancho = 800;  // Ancho de la caja
        double alto = 800;  // Alto de la caja
        double radioMolecula = 2;  // Radio de cada molécula
        int tiempoMaximo = 2000;  // Tiempo máximo de simulación

        // Crea la ventana principal para la simulación
        JFrame frame = new JFrame("Simulación de Gas");
        Simulacion simulacion = new Simulacion(numMoleculas, ancho, alto, radioMolecula, tiempoMaximo);
        frame.add(simulacion);  // Agrega el panel de simulación a la ventana
        frame.pack();  // Ajusta el tamaño de la ventana al contenido
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Cierra la aplicación cuando se cierra la ventana
        frame.setVisible(true);  // Muestra la ventana

        // Crea el gráfico de fluctuaciones
        XYSeriesCollection dataset = simulacion.getFluctuacionesDataset();  // Obtiene el conjunto de datos para el gráfico
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Fluctuaciones del Sistema",
                "Tiempo",
                "Δn",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );  // Crea un gráfico de líneas con la serie de fluctuaciones
        ChartPanel chartPanel = new ChartPanel(chart);  // Crea un panel para mostrar el gráfico
        chartPanel.setPreferredSize(new Dimension(600, 400));  // Establece el tamaño preferido del panel del gráfico
        JFrame chartFrame = new JFrame("Fluctuaciones");  // Crea una nueva ventana para el gráfico
        chartFrame.add(new JScrollPane(chartPanel));  // Agrega el panel del gráfico con una barra de desplazamiento
        chartFrame.pack();  // Ajusta el tamaño de la ventana al contenido
        chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Cierra la aplicación cuando se cierra la ventana
        chartFrame.setVisible(true);  // Muestra la ventana del gráfico

        // Inicia el temporizador para actualizar la simulación a intervalos regulares
        new javax.swing.Timer(16, e -> simulacion.actualizar()).start();  // Actualiza la simulación cada 16 ms (~60 FPS)
    }
}
