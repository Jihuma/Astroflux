package Juego;

import java.awt.*;

public class Menu {

    public Rectangle playButton = new Rectangle(230,110,100,50); //caja rectangular para el boton de Jugar
    public Rectangle quitButton = new Rectangle(230,230,100,50); //caja rectangular para el boton de de salir
    public Rectangle helpButton = new Rectangle(230,170,100,50); //caja rectangular para el boton de Ayuda


    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        Font fnt0 = new Font("arial", Font.BOLD,40); // tamaño para el nombre del juego
        g.setFont(fnt0);
        g.setColor(Color.white);
        g.drawString("ASTROFLUX", 160,70); //posicion x, y para el nombre del juego

        Font fnt1 = new Font("arial", Font.BOLD,30); // tamaño para los titulos de los botones
        g.setFont(fnt1);
        g.drawString("Jugar", playButton.x+10, playButton.y+35); //posicion x, y para el nombre del boton Jugar
        g2d.draw(playButton);
        g.drawString("Salir", helpButton.x+17, helpButton.y+35); //posicion x, y para el nombre del boton Salir
        g2d.draw(helpButton);
        g.drawString("Ayuda", quitButton.x+5, quitButton.y+35); //posicion x, y para el nombre del boton Ayuda
        g2d.draw(quitButton);
    }
}
