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
    public static final  TextColor BACKGROUND = TextColor.RGB.Factory.fromString("#000000");
    //20 MHz
    private static int frecuency = 2;
    private Terminal terminal;
    private Screen screen;

    private boolean key_left_pressed;
    private boolean key_right_pressed;
    private boolean key_exit;
    private boolean key_shoot;
    private Ship ship;
    private Enemy enemies[][];
    private Wall walls[];
    private int lifes = 3;

    private enum STATES {
        PLAY,
        GAME_OVER
    }
    private STATES state;

    /**
     * Constructor por defecto
     */
    public Game() {
        this.key_left_pressed = false;
        this.key_right_pressed = false;
        this.key_exit = false;
        this.key_shoot = false;

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

    private void init() {
        this.state = STATES.PLAY;
        this.initShip();
        this.initEnemies();
        this.initWalls();
    }

    private void initShip() {
        this.ship = new Ship(Game.COLUMNS / 2, ROWS - 3);

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
        int incx = 12;
        int incy = 4;
        int x = 6;
        int y = 1;
        int aleatorio;
        Enemy.HORIZONTAL_DIRECTION direction;
        Random PRNG = new Random();
        this.enemies = new Enemy[4][6];
        Enemy.EnemyType tipo_enemigo;
        Enemy.EnemyType[] enemytypes = Enemy.EnemyType.values();
        //se inicializan los enemigos
        for (int i = 0; i < this.enemies.length; i++) {
            //tipo de enemigo igual para toda la fila de forma aleatoria
            tipo_enemigo = enemytypes[PRNG.nextInt(enemytypes.length)];
            aleatorio = (int) (Math.random() * Enemy.getMax_animation_cicle());
            //para la direccion se mueve igual toda la fila
            direction = Enemy.HORIZONTAL_DIRECTION.values()[Math.random() < 0.5d ? 0 : 1];

            for (int j = 0; j < this.enemies[i].length; j++) {
                this.enemies[i][j] = new Enemy(new Point2D(x + j * incx, y));
                this.enemies[i][j].setEnemyType(tipo_enemigo);
                this.enemies[i][j].init();
                this.enemies[i][j].initAnimationTime(aleatorio);
                this.enemies[i][j].setHorizontaDirection(direction);
            }
            y = y + incy;

        }
    }

    /**
     * inicializar el juego mientra no se pulse la tecla escape
     */
    public void loop() {
        int counter = 0;
        int refresh = 100;
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

    private void paintLifes(Screen s) {
        int x = Game.COLUMNS - 3;
        int y = 23;
        for (int i = 0; i < this.lifes; i++) {
            s.setCharacter(x,
                    y, TextCharacter.fromCharacter(com.googlecode.lanterna.Symbols.HEART)[0].withForegroundColor(TextColor.ANSI.RED).withBackgroundColor(Game.BACKGROUND));
            x -= 2;
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
                            BACKGROUND));

                }
            }
            if (this.state == STATES.PLAY) {
                this.paintEnemies(s);
                this.paintWalls(s);
                this.ship.paint(s);
                this.paintLifes(s);
            } else if (this.state == STATES.GAME_OVER) {
                this.paintGameOver(s);
            }
            screen.refresh();
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void paintGameOver(Screen s) {
        //https://emojicombos.com/game-over-ascii-art
        //Game.BACKGROUND=TextColor.ANSI.BLACK;
        String game_over[] = {
            "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⡀⠀",
            "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣤⣤⠀⠀⠀⢀⣴⣿⡶⠀⣾⣿⣿⡿⠟⠛⠁",
            "⠀⠀⠀⠀⠀⠀⣀⣀⣄⣀⠀⠀⠀⠀⣶⣶⣦⠀⠀⠀⠀⣼⣿⣿⡇⠀⣠⣿⣿⣿⠇⣸⣿⣿⣧⣤⠀⠀⠀",
            "⠀⠀⢀⣴⣾⣿⡿⠿⠿⠿⠇⠀⠀⣸⣿⣿⣿⡆⠀⠀⢰⣿⣿⣿⣷⣼⣿⣿⣿⡿⢀⣿⣿⡿⠟⠛⠁⠀⠀",
            "⠀⣴⣿⡿⠋⠁⠀⠀⠀⠀⠀⠀⢠⣿⣿⣹⣿⣿⣿⣿⣿⣿⡏⢻⣿⣿⢿⣿⣿⠃⣼⣿⣯⣤⣴⣶⣿⡤⠀",
            "⣼⣿⠏⠀⣀⣠⣤⣶⣾⣷⠄⣰⣿⣿⡿⠿⠻⣿⣯⣸⣿⡿⠀⠀⠀⠁⣾⣿⡏⢠⣿⣿⠿⠛⠋⠉⠀⠀⠀",
            "⣿⣿⠲⢿⣿⣿⣿⣿⡿⠋⢰⣿⣿⠋⠀⠀⠀⢻⣿⣿⣿⠇⠀⠀⠀⠀⠙⠛⠀⠀⠉⠁⠀⠀⠀⠀⠀⠀⠀",
            "⠹⢿⣷⣶⣿⣿⠿⠋⠀⠀⠈⠙⠃⠀⠀⠀⠀⠀⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀",
            "⠀⠀⠈⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣤⣤⣴⣶⣦⣤⡀⠀",
            "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⡀⠀⠀⠀⠀⠀⠀⠀⣠⡇⢰⣶⣶⣾⡿⠷⣿⣿⣿⡟⠛⣉⣿⣿⣿⠆",
            "⠀⠀⠀⠀⠀⠀⢀⣤⣶⣿⣿⡎⣿⣿⣦⠀⠀⠀⢀⣤⣾⠟⢀⣿⣿⡟⣁⠀⠀⣸⣿⣿⣤⣾⣿⡿⠛⠁⠀",
            "⠀⠀⠀⠀⣠⣾⣿⡿⠛⠉⢿⣦⠘⣿⣿⡆⠀⢠⣾⣿⠋⠀⣼⣿⣿⣿⠿⠷⢠⣿⣿⣿⠿⢻⣿⣧⠀⠀⠀",
            "⠀⠀⠀⣴⣿⣿⠋⠀⠀⠀⢸⣿⣇⢹⣿⣷⣰⣿⣿⠃⠀⢠⣿⣿⢃⣀⣤⣤⣾⣿⡟⠀⠀⠀⢻⣿⣆⠀⠀",
            "⠀⠀⠀⣿⣿⡇⠀⠀⢀⣴⣿⣿⡟⠀⣿⣿⣿⣿⠃⠀⠀⣾⣿⣿⡿⠿⠛⢛⣿⡟⠀⠀⠀⠀⠀⠻⠿⠀⠀",
            "⠀⠀⠀⠹⣿⣿⣶⣾⣿⣿⣿⠟⠁⠀⠸⢿⣿⠇⠀⠀⠀⠛⠛⠁⠀⠀⠀⠀⠀⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀",
            "⠀⠀⠀⠀⠈⠙⠛⠛⠛⠋⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀"};
        for (int i = 0; i < game_over.length; i++) {
            for (int j = 0; j < game_over[i].length(); j++) {
                s.setCharacter(j + 20,
                        i + 4, TextCharacter.fromCharacter(
                                game_over[i].charAt(j)
                        )[0].withForegroundColor(TextColor.ANSI.values()[(int) (Math.random() * TextColor.ANSI.values().length)]));
            }

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
        if (this.state == STATES.PLAY) {
            if (this.key_left_pressed) {
                this.ship.moveHorizontal(-1, 0, COLUMNS - 1);
            }
            if (this.key_right_pressed) {
                this.ship.moveHorizontal(1, 0, COLUMNS - 1);
            }
            //se mueven las balas de la nave
            this.ship.moveBullets(0, ROWS);
            this.shoot_enemies();
            this.move_enemies();
            //se dispara si se ha pulsado la tecla
            if (this.key_shoot) {
                this.ship.shoot();
            }
            this.eval_colisionsWallShipBullets();
            this.eval_colisionsWallEnemisBullets();
            this.eval_colisionEnemiesShipBullets();
            if (this.eval_colisionShipEnemiesBullets()) {
                this.lifes--;
                this.ship.setXPosition(COLUMNS / 2);
                if (this.lifes <= 0) {
                    this.state = STATES.GAME_OVER;
                }
            }
        }
    }

    private boolean eval_colisionsWallShipBullets() {
        //muro contra balas de nave
        for (int i = 0; i < this.walls.length; i++) {
            for (int j = 0; j < this.ship.getBullets().length; j++) {
                if (this.walls[i].colision(this.ship.getBullets()[j])) {
                    this.ship.getBullets()[j] = null;
                }

            }
        }
        return true;
    }

    private boolean eval_colisionEnemiesShipBullets() {
        for (int i = 0; i < this.enemies.length; i++) {
            for (int j = 0; j < this.enemies[i].length; j++) {
                if (this.enemies[i][j] != null) {
                    for (int k = 0; k < this.ship.getBullets().length; k++) {
                        if (this.ship.getBullets()[k] != null && this.enemies[i][j].colision(this.ship.getBullets()[k])) {
                            this.enemies[i][j] = null;
                            this.ship.getBullets()[k] = null;
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    private boolean eval_colisionShipEnemiesBullets() {
        for (int i = 0; i < this.enemies.length; i++) {
            for (int j = 0; j < this.enemies[i].length; j++) {
                if (this.enemies[i][j] != null) {
                    for (int k = 0; k < this.enemies[i][j].getBullets().length; k++) {
                        if (this.enemies[i][j].getBullets()[k] != null
                                && this.ship.colision(this.enemies[i][j].getBullets()[k])) {
                            //this.enemies[i][j] = null;
                            this.enemies[i][j].getBullets()[k] = null;
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    private boolean eval_colisionsWallEnemisBullets() {
        //muro contra balas de enemigos
        //se miran los muros
        for (int i = 0; i < this.walls.length; i++) {
            if (this.walls[i] != null) {
                //se miran los enemigos
                for (int j = 0; j < this.enemies.length; j++) {
                    for (int k = 0; k < this.enemies[j].length; k++) {
                        if (this.enemies[j][k] != null) {
                            //se miran las balas
                            for (int l = 0; l < this.enemies[j][k].getBullets().length; l++) {
                                //se detecta la colision
                                if (this.enemies[j][k].getBullets()[l] != null && this.walls[i].colision(this.enemies[j][k].getBullets()[l])) {
                                    this.enemies[j][k].getBullets()[l] = null;
                                }
                            }
                        }
                    }

                }
            }
        }

        return true;
    }

    private void shoot_enemies() {
        for (int i = 0; i < this.enemies.length; i++) {
            for (int j = 0; j < this.enemies[i].length; j++) {
                if (this.enemies[i][j] != null) {
                    this.enemies[i][j].shoot();
                }
            }
        }
    }

    private void move_enemies() {
        double aleatorio;
        for (int i = 0; i < this.enemies.length; i++) {
            aleatorio = Math.random();
            for (int j = 0; j < this.enemies[i].length; j++) {
                if (this.enemies[i][j] != null) {//se mueve la fila
                    if (aleatorio < 0.01) {

                        this.enemies[i][j].moveHorizontal(0, Game.COLUMNS);
                        //se mueven las balas
                    }
                    this.enemies[i][j].moveBullets(0, Game.ROWS);

                }

            }
        }
    }

    private void setKey_left_pressed(boolean key_left_pressed) {
        this.key_left_pressed = key_left_pressed;
    }

    private boolean isKey_right_pressed() {
        return key_right_pressed;
    }

    private void setKey_right_pressed(boolean key_right_pressed) {
        this.key_right_pressed = key_right_pressed;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.loop();

    }

}
