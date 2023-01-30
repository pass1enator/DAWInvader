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
    private int width = 7;
    private int height = 4;
    private static int bullets_size = 2;
    private Bullet[] bullets;
    //por la frecuencia
    private static int max_paint_counter = 35;
    private static int max_animation_cicle = 250;
    private int paint_counter = 0;
    private int animation = 250;
    private boolean animate = false;
    private EnemyType enemytype;

    public enum EnemyType {
        A,
        B,
        C,
        D
    }
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
        this.init();
    }

    public Enemy(Point2D p) {
        this.position = p;
        this.init();
    }

    public Enemy(int x, int y) {
        this.position = new Point2D(x, y);
        this.init();
    }

    public void initAnimationTime(int i) {
        this.animation = i;
    }
    public void setEnemyType(EnemyType e){
        this.enemytype=e;
    }
    public static int getMax_animation_cicle() {
        return max_animation_cicle;
    }

    private void init() {
        this.color = TextColor.ANSI.GREEN;
        this.backgroundcolor = TextColor.ANSI.BLACK;
        this.bullets = new Bullet[Enemy.bullets_size];

    }

    public void moveVertical(int incy, int min_y, int max_y) {
        if (this.position.getY() + incy >= min_y && this.position.getY() + incy < max_y) {
            this.position.addY(incy);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void moveHorizontal(int intx, int min_x, int max_x) {
        if (this.position.getX() + intx - this.width / 2 >= min_x && this.position.getX() + intx + this.width / 2 < max_x) {
            this.position.addX(intx);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void moveBullets(int min_y, int max_y) {
        this.paint_counter++;

        //para que se pueda ver el disparo
        if (this.paint_counter >= Enemy.max_paint_counter) {
            this.paint_counter = 0;
            for (int i = 0; i < this.bullets.length; i++) {
                if (this.bullets[i] != null) {
                    this.bullets[i].moveVertical(-1, min_y, max_y);
                    //en caso de llegar a la parte superior se elimina
                    if (this.bullets[i].getPosition().getY() <= min_y) {
                        this.bullets[i] = null;
                    }
                }
            }
        }
    }

    /**
     * Dibuja ^ _/ \_ !#####!
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
        for (int i = 0; i < this.bullets.length; i++) {
            if (this.bullets[i] != null) {
                this.bullets[i].paint(s);
            }
        }
    }

    private void paintOne(Screen s) {

        if (this.animate) {
            s.setCharacter(this.position.getX() - 2, this.position.getY() - 1, new TextCharacter('/', color, this.backgroundcolor));
            s.setCharacter(this.position.getX() - 1, this.position.getY() - 1, new TextCharacter('-', color, this.backgroundcolor));
            s.setCharacter(this.position.getX(), this.position.getY() - 1, new TextCharacter('-', color, this.backgroundcolor));
            s.setCharacter(this.position.getX() + 1, this.position.getY() - 1, new TextCharacter('-', color, this.backgroundcolor));
            s.setCharacter(this.position.getX() + 2, this.position.getY() - 1, new TextCharacter('\\', color, this.backgroundcolor));

            s.setCharacter(this.position.getX() - 2, this.position.getY(), new TextCharacter(' ', color, this.backgroundcolor));
            s.setCharacter(this.position.getX() - 1, this.position.getY(), new TextCharacter('/', color, this.backgroundcolor));
            s.setCharacter(this.position.getX(), this.position.getY(), new TextCharacter(' ', color, this.backgroundcolor));
            s.setCharacter(this.position.getX() + 1, this.position.getY(), new TextCharacter('\\', color, this.backgroundcolor));
            s.setCharacter(this.position.getX() + 2, this.position.getY(), new TextCharacter(' ', color, this.backgroundcolor));
        } else {
            s.setCharacter(this.position.getX() - 2, this.position.getY() - 1, new TextCharacter('\\', color, this.backgroundcolor));
            s.setCharacter(this.position.getX() - 1, this.position.getY() - 1, new TextCharacter('_', color, this.backgroundcolor));
            s.setCharacter(this.position.getX(), this.position.getY() - 1, new TextCharacter('_', color, this.backgroundcolor));
            s.setCharacter(this.position.getX() + 1, this.position.getY() - 1, new TextCharacter('_', color, this.backgroundcolor));
            s.setCharacter(this.position.getX() + 2, this.position.getY() - 1, new TextCharacter('/', color, this.backgroundcolor));

            s.setCharacter(this.position.getX() - 2, this.position.getY(), new TextCharacter(' ', color, this.backgroundcolor));
            s.setCharacter(this.position.getX() - 1, this.position.getY(), new TextCharacter('\\', color, this.backgroundcolor));
            s.setCharacter(this.position.getX(), this.position.getY(), new TextCharacter(' ', color, this.backgroundcolor));
            s.setCharacter(this.position.getX() + 1, this.position.getY(), new TextCharacter('/', color, this.backgroundcolor));
            s.setCharacter(this.position.getX() + 2, this.position.getY(), new TextCharacter(' ', color, this.backgroundcolor));

        }

    }

    public void shoot() {
        Bullet tempo;
        boolean shooted = false;
        //solo  dispara si tiene un disparo libre
        for (int i = 0; i < this.bullets.length && !shooted; i++) {
            if (this.bullets[i] == null) {
                tempo = new Bullet(this.position.getX(), this.position.getY() - 2);
                this.bullets[i] = tempo;
                shooted = true;
            }
        }
    }
}
