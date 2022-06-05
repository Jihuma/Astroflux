package Juego;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseInput implements MouseListener {


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

        int mx = e.getX();
        int my = e.getY();

        /*
        public Rectangle playButton = new Rectangle(230,110,100,50);
        public Rectangle quitButton = new Rectangle(230,170,100,50);
        public Rectangle helpButton = new Rectangle(230,230,100,50);
        */

        if (Main.State == Main.STATE.MENU) {
            //Boton de jugar
            if (mx >= 230 && mx <= 330) {
                if (my >= 110 && my <= 160) {
                    //Presionar Boton Jugar
                    Main.State = Main.STATE.GAME;
                }
            }


            //Boton de salir
            if (mx >= 230 && mx <= 330) {
                if (my >= 170 && my <= 220) {
                    //Salir del juego
                    System.exit(1);
                }
            }

            //Boton de ayuda
            if (mx >= 230 && mx <= 330) {
                if (my >= 230 && my <= 280) {
                    //Muestra los controles
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
