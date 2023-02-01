/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pedro.ieslaencanta.com.space;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import java.awt.Toolkit;

/**
 *
 * @author Pedro
 */
public class Enemy {

    private Point2D position;
    private TextColor color;
    private TextColor backgroundcolor;

    private static int bullets_size = 1;
    private Bullet[] bullets;
    //por la frecuencia
    private static int max_paint_counter = 35;
    private static int max_animation_cicle = 250;
    private int paint_counter = 0;
    private int animation = 250;
    private boolean animate = false;
    private float prob;
    private EnemyType enemytype;

    /*+
    tipos de enemigo
     */
    public enum EnemyType {
        A,
        B,
        C,
        D
    }
    /**
     * matriz de dibujos
     */
    private static String cartoon[][] = {
        {
            "⢀⡴⣿⢦⡀ ",
            "⢈⢝⠭⡫⡁ "
        },
        {
            "⢀⡴⣿⢦⡀ ",
            "⠨⡋⠛⢙⠅ "
        },
        {
            "⢀⡵⣤⡴⣅ ",
            "⠏⢟⡛⣛⠏⠇",
            "      "

        },
        {
            "⣆⡵⣤⡴⣅⡆",
            "⢘⠟⠛⠛⢟⠀"
        },
        {
            "⣴⡶⢿⡿⢶⣦",
            "⠩⣟⠫⠝⣻⠍"
        },
        {
            "⣴⡶⢿⡿⢶⣦",
            "⣉⠽⠫⠝⠯⣉"
        },
        {
            "⢀⡴⣾⢿⡿⣷⢦⡀",
            "⠉⠻⠋⠙⠋⠙⠟⠉"
        }

    };

    public Enemy() {
        this.position = new Point2D();
        this.prob = (float) Math.random() / 10000f;
    }

    public Enemy(Point2D p) {
        this.position = p;
        this.prob = (float) Math.random() / 10000f;

    }

    public Enemy(int x, int y) {
        this.position = new Point2D(x, y);
        this.prob = (float) (Math.random() / 10000f);

    }

    public void initAnimationTime(int i) {
        this.animation = i;
    }

    public void setEnemyType(EnemyType e) {
        this.enemytype = e;
    }

    public static int getMax_animation_cicle() {
        return max_animation_cicle;
    }

    public Bullet[] getBullets() {
        return bullets;
    }

    public void setBullets(Bullet[] bullets) {
        this.bullets = bullets;
    }

    /**
     * Inicialización del objeto
     */
    public void init() {

        if (null != this.enemytype) {
            switch (this.enemytype) {
                case A:
                    this.color = TextColor.ANSI.RED;
                    break;
                case B:
                    this.color = TextColor.ANSI.BLUE;
                    break;
                case C:
                    this.color = TextColor.ANSI.YELLOW;
                    break;
                case D:
                    this.color = TextColor.ANSI.WHITE;
                    break;
                default:
                    this.color = TextColor.ANSI.GREEN;
            }

        }
        this.backgroundcolor = TextColor.ANSI.BLACK;
        this.setBullets(new Bullet[Enemy.bullets_size]);

    }

    /**
     * Moverl el enemigo de forma vertical
     *
     * @param incy incremento en el eje y
     * @param min_y margen superior
     * @param max_y margen inferior
     */
    public void moveVertical(int incy, int min_y, int max_y) {
        if (this.position.getY() + incy >= min_y && this.position.getY() + incy < max_y) {
            this.position.addY(incy);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    /**
     * mueve el elemnto en el eje x
     *
     * @param intx incremento o decremento
     * @param min_x límite izquierdo
     * @param max_x límite derecho
     */
    public void moveHorizontal(int intx, int min_x, int max_x) {
        if (this.position.getX() + intx - Enemy.cartoon[0].length / 2 >= min_x && this.position.getX() + intx + Enemy.cartoon[0].length / 2 < max_x) {
            this.position.addX(intx);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    /**
     * vuelve las balas asociadas al enemigo
     *
     * @param min_y límite superior de la pantalla
     * @param max_y límite inferior de la pantalla
     */
    public void moveBullets(int min_y, int max_y) {
        this.paint_counter++;

        //para que se pueda ver el disparo
        if (this.paint_counter >= Enemy.max_paint_counter) {
            this.paint_counter = 0;
            for (int i = 0; i < this.getBullets().length; i++) {
                if (this.getBullets()[i] != null) {
                    this.getBullets()[i].moveVertical(1, min_y, max_y);
                    //en caso de llegar a la parte superior se elimina
                    if (this.getBullets()[i].getPosition().getY() >= max_y) {
                        this.getBullets()[i] = null;
                    }
                }
            }
        }
    }

    /**
     * Dibuja al enemigo en funciónde los 4 tipos existentes
     *
     * @param s
     */
    public void paint(Screen s) {
        int enemy_index;

        //para activar la animacion y los disparos
        if (this.animation >= Enemy.getMax_animation_cicle()) {
            this.animate = !this.animate;
            this.animation = 0;

        }
        if (this.enemytype == EnemyType.A) {
            if (this.animate) {
                enemy_index = 0;
            } else {
                enemy_index = 1;
            }
        } else if (this.enemytype == EnemyType.B) {

            if (this.animate) {
                enemy_index = 2;
            } else {
                enemy_index = 3;
            }

        } else if (this.enemytype == EnemyType.C) {

            if (this.animate) {
                enemy_index = 4;
            } else {
                enemy_index = 5;
            }

        } else {
            enemy_index = 6;

        }
        for (int i = 0; i < this.cartoon[enemy_index].length; i++) {
            for (int j = -this.cartoon[enemy_index][i].length() / 2; j < this.cartoon[enemy_index][i].length() - this.cartoon[enemy_index][i].length() / 2; j++) {
                s.setCharacter(this.position.getX() + j,
                        this.position.getY() + i,
                        new TextCharacter(this.cartoon[enemy_index][i].charAt(j + this.cartoon[enemy_index][i].length() / 2),
                                color, this.backgroundcolor));
            }
        }
        this.animation++;
        for (int i = 0; i < this.getBullets().length; i++) {
            if (this.getBullets()[i] != null) {
                this.getBullets()[i].paint(s);
            }
        }
    }

    /**
     * Dispara de forma aleatorio, en función del valor de probabilidad [0,1]
     */
    public void shoot() {
        Bullet tempo;
        boolean shooted = false;
        //solo se dispara si la probabilidad es menor
        if (Math.random() < this.prob) {
            //solo  dispara si tiene un disparo libre
            for (int i = 0; i < this.getBullets().length && !shooted; i++) {
                if (this.getBullets()[i] == null) {
                    tempo = new Bullet(this.position.getX(), this.position.getY() + 2);
                    this.getBullets()[i] = tempo;
                    shooted = true;
                }
            }
        }
    }

    public boolean colision(Bullet b) {
        int x, y;
        if (b != null) {
            //se encuentra en el eje x
            if (this.position.getX() - this.cartoon[0].length / 2 <= b.getPosition().getX()
                    && this.position.getX() + this.cartoon[0].length / 2 >= b.getPosition().getX()) {
                //se encuentra en el eje y
                if (this.position.getY() - this.cartoon.length / 2 < b.getPosition().getY() && this.position.getY() + this.cartoon.length / 2 > b.getPosition().getY()) {
                    x = b.getPosition().getX() - (this.position.getX() - this.cartoon[0].length / 2);
                    y = this.position.getY() - b.getPosition().getY();
                    return true;

                }
            }
        }
        return false;
    }

}
