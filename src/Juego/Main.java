package Juego;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Random;
import java.util.logging.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Main<menu> extends JPanel implements Runnable, KeyListener, MouseMotionListener, MouseListener, ActionListener {
    // variables

    public static enum STATE {
        MENU,
        GAME
    };
    public static STATE State = STATE.MENU;

    private final Menu menu;

    public int boxSize_x = 596;
    public int boxSize_y = 340;//335
    private Thread th;
    BufferedImage icono2 = null;
    JMenuBar menuBar;
    JMenu options, about;
    JMenuItem astroflux, ayuda;
    JCheckBoxMenuItem sound;

    private Player player;
    private Shot[] shots;
    private final Asteroid[] asteroid = new Asteroid[5];
    private final int max_delta = 20;   // distancia máxima de los asteroides desde la posición normal

    // constantes de velocidad
    private final int shotSpeed = -10;
    private final int playerSpeed = 3;

    // mover de izquierda a derecha?
    private boolean playerMoveLeft, playerMoveRight;

    JFrame f;
    BufferedImage espacio = null;

    private int num_colecciones, suma, suma_actual;

    private int points = 0;// cuantos puntos tiene, cada vez que tira saca 10 puntos, si pierde le sacan 5

    private String msj = "";
    private String msj_mostrado = "";
    private String estado1 = "Nivel 1";
    private String timer = "";
    private int level = 1;
    long tiempoactual;

    public Main() {

        menu = new Menu();
        f = new JFrame();
        f.setResizable(false);

        //el jugador / barco se crea en la posición de inicio respectiva
        player = new Player(((int) (boxSize_x / 2)) - 45, boxSize_y - 100, boxSize_x); //player = new Player(150, 280, boxSize);

        //creamos asteroides en las posiciones respectivas
        generarNumeros();
        shots = new Shot[8];
        tiempoactual = System.currentTimeMillis();

        f.setBackground(Color.black);
        f.setVisible(true);
        f.setTitle("Astroflux");
        f.setLocation(-3, 0);


        // menus
        menuBar = new JMenuBar();

        options = new JMenu("Opciones");
        options.setMnemonic(KeyEvent.VK_O); // Alt + O este menú se abre
        options.addActionListener(this);

        sound = new JCheckBoxMenuItem("Sonido ON/OFF");
        sound.setState(false);
        sound.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));   // Tecla S activa/desactiva el sonido
        options.add(sound);

        menuBar.add(options);

        about = new JMenu("Ayuda/Info");
        about.setMnemonic(KeyEvent.VK_P);   // Alt + P este menú se abre
        astroflux = new JMenuItem("Astroflux");
        astroflux.addActionListener(this);

        ayuda = new JMenuItem("Ayuda");
        ayuda.addActionListener(this);
        ayuda.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)); // con F1 se abre la ayuda
        about.add(astroflux);
        about.add(ayuda);
        menuBar.add(about);

        f.setJMenuBar(menuBar);
        // fin del menu

        try {
            BufferedImage iconos = ImageIO.read(this.getClass().getResourceAsStream("/Imagenes/icono.png"));
            f.setIconImage(iconos);      //tomamos el icono y lo colocamos en el marco
            icono2 = ImageIO.read(this.getClass().getResourceAsStream("/Imagenes/icono2.png"));  // icono pequeño, en la ventana de ayuda
            espacio = ImageIO.read(this.getClass().getResourceAsStream("/Imagenes/espacio.jpg")); //obtenemos el fondo
        }
        catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

        f.getContentPane().add(this);  // insertamos este panel en la ventana
        f.setSize(boxSize_x, boxSize_y + 65 + 20);  // tamaño de la ventana, por 65 debido a la barra negra en la parte inferior, 20 por el menú


        this.addMouseListener(new MouseInput());

        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);

        if (State == STATE.GAME) {
            //  Escondemos el ratón
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
            f.getContentPane().setCursor(blankCursor);
            // end
        }
        setFocusable(true);    // este panel puede tener foco
        requestFocusInWindow(); // inmediatamente establecer el enfoque

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        th = new Thread(this);
        th.start();

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == astroflux) {
            JOptionPane.showMessageDialog(this, "¡Astroflux es un juego de disparos en el que tendras\n  que probar tu agilidad mental con las sumas! \n\n ¡Esperemos que te encante!");
        } else if (e.getSource() == ayuda) {
            JOptionPane.showMessageDialog(this, "Movimiento: \nCon flechas en el teclado, o con el mouse \n\nDisparo: \nCon BARRA ESPACIADORA o clic izquierdo del ratón ", "Ayuda", JOptionPane.PLAIN_MESSAGE, new ImageIcon(icono2));

        }
    }

    public void checkLevel() {
        if (points >= 1 && points <= 10) {
            level = 1;
            estado1 = "Nivel " + level;
        } else if (points > 10) {
            level = 2;
            estado1 = "Nivel " + level;
        }
    }

    private void generarNumeros() {

        int[] asteroid_number = new int[5];  // 5 numeros para los 5 asteroides

        asteroid_number[0] = (int) (Math.random() * 15 + 1);  // le estamos dando un valor al primero
        // otros se generan aleatoriamente, pero no para repetir ninguno que se haya generado anteriormente
        for (int i = 1; i != 5; i++) {
            int random = 0;
            boolean ok = false;
            while (!ok) { //siempre y cuando no generemos un número que no se haya generado antes
                ok = true;  // El número generado no es repetido
                random = (int) (Math.random() * 15 + 1);
                for (int j = 0; j != i; j++) {
                    if (asteroid_number[j] == random) {
                        ok = false; //El numero esta repetido
                    }
                }
            }
            asteroid_number[i] = random;
        }  // Fin del for

        //si no hay asteroides (cuando comienza el juego), los estamos creando como objetos, de lo contrario ya cambiamos los números
        if (asteroid[0] == null) {
            int next = 20;
            for (int i = 0; i != 5; i++) {
                asteroid[i] = new Asteroid(next, 30, asteroid_number[i], max_delta);
                next += 120; // la distancia entre ellos es de 120
            }
        } else {
            for (int i = 0; i != 5; i++) {
                // cambiamos las imagenes
                asteroid[i].setNumber(asteroid_number[i]);
            }
        }
        // ahora generamos num_colecciones
        num_colecciones = (int) (Math.random() * 2 + 2);

        // seleccionamos aleatoriamente los números de 0-4 (índices de asteroides) a los "num_colecciones", es decir, elegimos qué asteroides recolectaremos
        int[] recolecciones = new int[num_colecciones];
        recolecciones[0] = (int) (Math.random() * 5);
        for (int i = 1; i != num_colecciones; i++) {
            int random = 0;
            boolean ok = false;
            while (!ok) {
                ok = true;
                random = (int) (Math.random() * 5);
                for (int j = 0; j != i; j++) {
                    if (recolecciones[j] == random) {
                        ok = false;
                    }
                }

            }
            recolecciones[i] = random;
        }

        // ahora recogemos las adiciones, y calculamos la cantidad
        for (int i = 0; i != num_colecciones; i++) {
            suma += asteroid_number[recolecciones[i]];
            System.out.print(asteroid_number[recolecciones[i]] + ", ");
        }
        System.out.println(" Estos números da la suma = " + suma);

        //a veces pasa que por ejemplo la suma 9, porque los sumadores son 8 y 1  ,
        //pero sucede que se genera la cantidad en sí, por ejemplo tenemos asteroides con el número 9, en ese caso llamamos de nuevo a este método
        boolean ok = true;
        for (int i = 0; i != 5; i++) {
            if (suma == asteroid_number[i]) {
                ok = false;
            }
        }
        if (!ok) {
            suma = 0;
            generarNumeros();
        } else {
            msj = "Golpea dos o más asteroides, de modo que su suma sea : " + suma;
            msj_mostrado = "";
        }

    }

    void shuffle2Arrays(int[] ar, boolean[] br) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);

            int a = ar[index];
            boolean b = br[index];
            ar[index] = ar[i];
            br[index] = br[i];
            ar[i] = a;
            br[i] = b;
        }
    }

    public void shuffleAsteroids() {
        long tiemponuevo = System.currentTimeMillis();
        if (tiemponuevo - tiempoactual > 5000) {
            // obtenemos los números y estados de los asteroides
            int[] asteroid_numbers = new int[5];
            boolean[] asteroid_hits = new boolean[5];

            for (int i = 0; i != 5; i++) {
                asteroid_numbers[i] = asteroid[i].getNumber();
                asteroid_hits[i] = asteroid[i].getHit();
            }
            shuffle2Arrays(asteroid_numbers, asteroid_hits);

            //ahora colocamos los numeros y aciertos en los asteroides
            for (int i = 0; i != 5; i++) {
                asteroid[i].setNumber(asteroid_numbers[i]);
                asteroid[i].setHit(asteroid_hits[i]);
            }

            tiempoactual = tiemponuevo;
        } else {
            long diferenca = 5000 - (tiemponuevo - tiempoactual);
            timer = diferenca / 1000 + "." + ((diferenca) % 1000) / 100 + "";
        }
    }

    public void run() {
//        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        while (true) {

            //movimiento del asteroide
            for (int i = 0; i != 5; i++) {
                asteroid[i].move();
            }
            if (level == 2) {
                // miramos el temporizador, si debemos barajarlo
                shuffleAsteroids();
            }
            // Observamos cada disparo
            for (int i = 0; i < shots.length; i++) {
                //Ahora observamos si el disparo impactó en un asteroide
                // no es nulo, es decir, hay un disparo en el i-ésimo índice de la cadena
                if (shots[i] != null) {

                    shots[i].moveShot(shotSpeed); // le decimos que dispare
                    //para el disparo j-ésimo vemos para cada asteroide si hay un golpe, 5 = número de asteroides
                    for (int j = 0; j != 5; j++) {

                        if (shots[i].getYPos() < asteroid[j].getYPos() + (int) (asteroid[j].getRadius() / 2 + 10) && shots[i].getYPos() > asteroid[j].getYPos()
                                && shots[i].getXPos() > asteroid[j].getXPos() && shots[i].getXPos() < asteroid[j].getXPos() + asteroid[j].getRadius() && !asteroid[j].getHit()) {
                            // si llegamos aquí, la i-ésima bala ha impactado en el j-ésimo asteroide
                            shots[i] = null; // borró la bala que impacto
                            asteroid[j].setHit(true);  // decirle al asteroide que ha sido golpeado, es decir, en el siguiente dibujo desaparece (no dibujado)
                            suma_actual += asteroid[j].getNumber();  //sumamos a la suma, el número en la cara del asteroide

                            //vemos si ha acertado los números que dan la suma
                            if (suma_actual == suma) {
                                //ha ganado, anunciar y reiniciar el juego
                                //estado1 = "Nivel 1";
                                points += 1;
                                checkLevel();
                                reiniciarJuego();
                                generarNumeros(); //generar números para el siguiente nivel
                            } else if (suma_actual > suma) {
                                // ha excedido la cantidad requerida
                                // reportar pérdida y reiniciar el juego
                                estado1 = "Volviste al principio";
                                points = 0;
                                level = 1;
                                timer = ""; //resetear el tiempo
                                reiniciarJuego();
                                generarNumeros();
                            } else {
                                msj_mostrado = "Impactos de asteroides es : \t" + suma_actual;
                            }
                            break;

                        } else if (shots[i].getYPos() < 0) {
                            // ha cruzado la parte superior del marco
                            shots[i] = null;
                            break;
                        }
                    }

                }
            }

            //ver si los jugadores necesitan ser movidos
            if (playerMoveLeft) {
                player.moveX(playerSpeed * (-1));
            } else if (playerMoveRight) {
                player.moveX(playerSpeed);
            }

            // redibujar la pantalla del juego
            repaint();

            try {
                Thread.sleep(15);
            } catch (InterruptedException ex) {

            }

//            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        }


    }

    public void reiniciarJuego() {

        msj = "";
        msj_mostrado = "";
        suma = 0;
        suma_actual = 0;
        tiempoactual = System.currentTimeMillis();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (State == STATE.GAME) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT) {
                playerMoveLeft = true;
            } else if (key == KeyEvent.VK_RIGHT) {
                playerMoveRight = true;
            } else if (key == KeyEvent.VK_SPACE) {
                // ha tocado "FUEGO", generamos la viñeta
                for (int i = 0; i < shots.length; i++) {
                    if (shots[i] == null) { // encontramos el primer lugar vacío en la cadena e insertamos el disparo
                        shots[i] = player.generateShot();
                        if (sound.getState()) {
                            playAudio();
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            playerMoveLeft = false;
        } else if (key == KeyEvent.VK_RIGHT) {
            playerMoveRight = false;
        }
    }

    public void playAudio() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/sounds/laser.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
        }
    }

    @Override
    public void paint(Graphics g) {

        super.repaint();
        f.repaint();

        g.drawImage(espacio, 0, 0, null);
        if (State == STATE.GAME) {
            player.drawPlayer(g);
            for (Asteroid a : asteroid) {
                a.drawAsteroid(g); // para cada asteroide, dibuja
            }
            for (Shot shot : shots) {
                if (shot != null) {
                    shot.drawShot(g);
                }
            }
            g.setColor(Color.white);
            g.drawString(msj, 10, boxSize_y + 7);
            g.drawString(msj_mostrado, 10, boxSize_y + 20);
            g.drawString(estado1, 470, boxSize_y + 7);
            g.drawString("Puntos : " + points, 470, boxSize_y + 20);
            g.drawString(timer, 550, boxSize_y + 20);
        }else if(State == STATE.MENU){
            menu.paint(g);
        }
    }

    /**
     * ¡Mueve la nave espacial cuando se mueva el ratón!
     * @para event
     */

    @Override
    public void mouseMoved(MouseEvent event) {

        player.setX(event.getX());
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        player.setX(event.getX());
    }

    //------------------------------------------------------------------------
    //  Fuegooo!
    //------------------------------------------------------------------------
    @Override
    public void mousePressed(MouseEvent event) {
        if (State == STATE.GAME) {
            // System.out.println(event.getX());
            for (int i = 0; i < shots.length; i++) {
                if (shots[i] == null) { // encontramos el primer lugar vacío en la cadena e insertamos el disparo
                    shots[i] = player.generateShot();
                    if (sound.getState()) {
                        playAudio();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
    }


    @Override
    public void mouseClicked(MouseEvent event) {
    }


    @Override
    public void mouseExited(MouseEvent event) {

    }

    @Override
    public void mouseEntered(MouseEvent event) {

    }

    public static void main(String[] args) {
        new Main();

    }

}
