package Juego;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.logging.*;

public class Asteroid {

    private int x_pos;          // posiciones actuales
    private int y_pos;
    private final int x_pos_default;  //posiciones normales (no se mueve más que delta desde esta posición
    private final int y_pos_default;
    public boolean hit;         // ha sido golpeado

    BufferedImage image;        //la "cara" del asteroide, es decir, la imagen numerada en él


    private int num;          //numero en asteroide

    private int delta;         // distancias desde la posición predeterminada
    private int deltaY;
    private final int maxDelta; // distancia máxima
    private int unidad;         // cuántas "unidades" se mueve la pelota (generalmente +1 o -1)
    private int unidadY;

    public Asteroid(int x, int y, int face, int max_delta) {

        x_pos_default = x;
        y_pos_default = y;
        hit = false;
        num = face;
        unidad = 1;
        unidadY = 1;
        maxDelta = max_delta;
        delta = (int) (Math.random() * 2 * max_delta - max_delta);  // comienza a una distancia aleatoria de la posición predeterminada


        deltaY = (int) (Math.random() * 2 * max_delta - max_delta);

        x_pos = x_pos_default + delta;
        y_pos = y_pos_default + deltaY;

        try {
            image = ImageIO.read(this.getClass().getResourceAsStream("/Imagenes/" + num + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int getYPos() {
        return y_pos;
    }

    public int getNumber() {
        return num;
    }
    int i = 0;
    int espera = 1;   // con estos definimos la velocidad de movimiento, por ejemplo en este caso la espera = 1, es decir un ciclo de movimiento, un no



    /**
     * Se utiliza para mover el asteroide
     */
    public void move() {
        // System.out.println("delta "+delta +" asteroide " + numero);
        if (Math.abs(delta) != maxDelta) {
            if (i == espera) {
                // si la distancia desde la posición predeterminada no es tanta como la distancia máxima, movemos la pelota
                i = 0;
                x_pos += unidad;    // nos movemos en la posición x
                delta = x_pos - x_pos_default;
                //ahora lo mismo en y
                if (Math.abs(deltaY) != maxDelta) {
                    y_pos += unidadY;
                    deltaY = y_pos - y_pos_default;
                } else if (deltaY == maxDelta) {
                    unidadY = -1;
                    y_pos += unidadY;
                    deltaY = y_pos - y_pos_default;
                } else if (deltaY == (-1) * maxDelta) {
                    unidadY = 1;
                    y_pos += unidadY;
                    deltaY = y_pos - y_pos_default;
                }

            } else {
                i++;
            }
        } else  {
            // si ha llegado al borde, cambie de dirección y muévase
            unidad = (-1)* unidad;
            x_pos += unidad;
            delta = x_pos - x_pos_default;
        }
    }



    public void setNumber(int i) {
        // tenemos el nuevo número, así que ahora restablecemos el hit y obtenemos una nueva imagen
        hit = false;
        num = i;
        try {
            image = ImageIO.read(this.getClass().getResourceAsStream("/Imagenes/" + num + ".png"));

        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getRadius() {
        return image.getHeight();
    }

    public void setHit(boolean b) {
        hit = b;
    }

    public boolean getHit() {
        return hit;
    }

    public int getXPos() {
        return x_pos;
    }

    public void drawAsteroid(Graphics g) {
        if (!hit) {
            g.drawImage(image, x_pos, y_pos, null);
        }

    }
}
