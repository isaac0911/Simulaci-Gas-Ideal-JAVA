public class Molecula {
    private double radio;  // Radio de la molécula
    private double x, y;  // Posición de la molécula
    private double vx, vy;  // Velocidad de la molécula

    // Constructor de la clase Molecula
    public Molecula(double radio, double x, double y, double vx, double vy) {
        this.radio = radio;  // Asigna el radio de la molécula
        this.x = x;  // Asigna la posición x de la molécula
        this.y = y;  // Asigna la posición y de la molécula
        this.vx = vx;  // Asigna la velocidad en x de la molécula
        this.vy = vy;  // Asigna la velocidad en y de la molécula
    }

    public double getX() { return x; }  // Obtiene la posición x de la molécula
    public double getY() { return y; }  // Obtiene la posición y de la molécula
    public double getVx() { return vx; }  // Obtiene la velocidad en x de la molécula
    public double getVy() { return vy; }  // Obtiene la velocidad en y de la molécula
    public double getRadio() { return radio; }  // Obtiene el radio de la molécula

    // Actualiza la posición de la molécula en función del tiempo
    public void actualizarPosicion(double dt) {
        x += vx * dt;
        y += vy * dt;
    }

    // Verifica si la molécula rebota en las paredes de la caja
    public void rebotePared(double ancho, double alto) {
        if (x - radio < 0 || x + radio > ancho) vx *= -1;
        if (y - radio < 0 || y + radio > alto) vy *= -1;
    }
}