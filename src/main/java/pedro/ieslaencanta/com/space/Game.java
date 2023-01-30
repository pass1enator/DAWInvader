package pedro.ieslaencanta.com.space;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pedro
 */
public class Game {

    //dimensiones de un terminal
    private static int COLUMNS = 80;
    private static int ROWS = 24;
    //20 MHz
    private static int frecuency = 2;
    private Terminal terminal;
    private Screen screen;
    private TextColor background;
    private boolean key_left_pressed;
    private boolean key_right_pressed;
    private boolean key_exit;
    private boolean key_shoot;
    private Ship ship;
    private Enemy enemies[][];
    private Wall walls[];

    public Game() {
        this.key_left_pressed = false;
        this.key_right_pressed = false;
        this.key_exit = false;
        this.key_shoot = false;
        //se crea la nave
        this.background = TextColor.ANSI.BLACK;
        this.ship = new Ship(Game.COLUMNS / 2, ROWS - 3);
        this.init();
        try {
            this.terminal = new DefaultTerminalFactory().createTerminal();
            this.screen = new TerminalScreen(this.terminal);
            //no se muestra el cursor
            screen.setCursorPosition(null);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void init() {
        this.initEnemies();
        this.initWalls();
    }

    private void initWalls() {
        int incx = 18;

        int x = 10;
        int y = 18;

        this.walls = new Wall[4];
        //se inicializan los enemigos
        for (int i = 0; i < this.walls.length; i++) {
            //tipo de enemigo igual para toda la fila de forma aleatoria

            this.walls[i] = new Wall(new Point2D(x + i * incx, y));

        }

    }

    private void initEnemies() {
        int incx = 9;
        int incy = 4;
        int x = 10;
        int y = 1;
        int aleatorio;
        Random PRNG = new Random();
        this.enemies = new Enemy[4][8];
        Enemy.EnemyType tipo_enemigo;
        Enemy.EnemyType[] enemytypes = Enemy.EnemyType.values();
        //se inicializan los enemigos
        for (int i = 0; i < this.enemies.length; i++) {
            //tipo de enemigo igual para toda la fila de forma aleatoria
            tipo_enemigo = enemytypes[PRNG.nextInt(enemytypes.length)];
            aleatorio = (int) (Math.random() * Enemy.getMax_animation_cicle());
            for (int j = 0; j < this.enemies[i].length; j++) {
                this.enemies[i][j] = new Enemy(new Point2D(x + j * incx, y));
                this.enemies[i][j].setEnemyType(tipo_enemigo);
                this.enemies[i][j].init();
                this.enemies[i][j].initAnimationTime(aleatorio);
            }
            y = y + incy;

        }
    }

    public void loop() {
        try {
            screen.startScreen();
            screen.clear();
            this.terminal.setBackgroundColor(TextColor.ANSI.CYAN);

            while (!this.key_exit) {
                try {
                    //se procesa la entrada
                    this.process_input();
                    //se actualiza el juego
                    this.update();
                    //se pinta
                    this.paint(this.screen);
                    //1000 es un segundo, frecuenca de 10 Hz son 10 veces por segundo
                    //frecuenca de 20 Hz son 20 veces por segundo, una vez cada 0,05 segundos
                    Thread.sleep((1 / Game.frecuency) * 1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //fin del bucle
            screen.stopScreen();
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void paintEnemies(Screen s) {
        for (int i = 0; i < this.enemies.length; i++) {
            for (int j = 0; j < this.enemies[i].length; j++) {
                if (this.enemies[i][j] != null) {
                    this.enemies[i][j].paint(s);
                }
            }

        }
    }

    private void paintWalls(Screen s) {
        for (int i = 0; i < this.walls.length; i++) {
            if (this.walls[i] != null) {
                this.walls[i].paint(s);
            }

        }
    }

    private void paint(Screen s) {
        try {
            TerminalSize terminalSize = s.getTerminalSize();
            for (int column = 0; column < terminalSize.getColumns(); column++) {
                for (int row = 0; row < terminalSize.getRows(); row++) {
                    s.setCharacter(column, row, new TextCharacter(
                            ' ',
                            TextColor.ANSI.DEFAULT,
                            this.background));

                }
            }
            this.paintEnemies(s);
            this.paintWalls(s);
            this.ship.paint(s);
            screen.refresh();
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Borrar el buffer de teclado para evitar saltos en el movimiento
     *
     */
    private void clear_keyboard_input() {
        KeyStroke keyStroke = null;
        do {
            try {
                keyStroke = screen.pollInput();
            } catch (IOException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (keyStroke != null);
    }

    private void process_input() {
        this.key_left_pressed = false;
        this.key_right_pressed = false;
        this.key_shoot = false;
        try {
            //la lectura es no bloqueante
            KeyStroke keyStroke = screen.pollInput();

            if (keyStroke != null) {
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    this.key_exit = true;
                } else if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
                    this.key_left_pressed = true;
                } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
                    this.key_right_pressed = true;
                } else {
                    KeyType c = keyStroke.getKeyType();

                    // System.out.println(String.format("%2x", (int) c.toString().charAt(0))+" ....");
                    if ((int) c.toString().charAt(0) == 67) {
                        this.key_shoot = true;
                    }
                }
                //se borra el buffer
                //this.clear_keyboard_input();
            }

        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void update() {
        if (this.key_left_pressed) {
            this.ship.moveHorizontal(-1, 0, COLUMNS - 1);
        }
        if (this.key_right_pressed) {
            this.ship.moveHorizontal(1, 0, COLUMNS - 1);
        }
        //se mueven las balas
        this.ship.moveBullets(0, ROWS);
        //se dispara si se ha pulsado la tecla
        if (this.key_shoot) {
            this.ship.shoot();
        }
        this.eval_colisions();
        
    }
    public boolean eval_colisions(){
        for(int i=0;i<this.walls.length;i++){
            for(int j=0;j<this.ship.getBullets().length;j++){
                 if(this.walls[i].colision(this.ship.getBullets()[j]))
                     this.ship.getBullets()[j]=null;
                 
            }
        }
        return true;
    }
    public boolean isKey_left_pressed() {
        return key_left_pressed;
    }

    public void setKey_left_pressed(boolean key_left_pressed) {
        this.key_left_pressed = key_left_pressed;
    }

    public boolean isKey_right_pressed() {
        return key_right_pressed;
    }

    public void setKey_right_pressed(boolean key_right_pressed) {
        this.key_right_pressed = key_right_pressed;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.loop();

    }

}
