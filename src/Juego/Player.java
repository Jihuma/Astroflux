package Juego;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.logging.*;

public class Player {

    private int x_pos;
    private final int y_pos;
    private final int boxSize_x;
    BufferedImage image;

    public Player(int x, int y, int size_x) {
        x_pos = x;
        y_pos = y;
        boxSize_x = size_x;

        try {
            image = ImageIO.read(this.getClass().getResourceAsStream("/Imagenes/terraria1.png"));
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void moveX(int speed) {
        x_pos += speed;
        if (x_pos +speed > boxSize_x - 85 || x_pos + speed < -15) {
            x_pos -= speed; // para no cruzar la frontera
        }

    }

    public void setX(int x) {
        if (!(x > boxSize_x -45||x < 30)) {
            x_pos = x - 43; // dado que el tamaño del barco es de aproximadamente 90, lo dibujamos hacia la izquierda, de modo que el centro caiga donde está el mouse
        }
    }

    public int getX() {
        return x_pos;
    }

    public Shot generateShot() {
        Shot shot = new Shot(x_pos + 43, y_pos - 15); // 43 y 15 es que el tiro salio justo del medio de la nave
        return shot;
    }



    public void drawPlayer(Graphics g) {
        g.drawImage(image, x_pos, y_pos + 10, null);
    }
}
