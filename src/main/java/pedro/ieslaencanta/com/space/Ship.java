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
public class Ship {

    private Point2D position;
    private TextColor color;
    private TextColor backgroundcolor;
    private int width = 7;
    private int height = 4;
    private static int bullets_size = 2;
    private Bullet[] bullets;
    //por la frecuencia
    private static int max_paint_counter = 35;
    private int paint_counter = 0;
    private String cartoon[] = {
        "⢀⣀⣾⣿⣷⣀⡀",
        "⣿⣿⣿⣿⣿⣿⣿"
    };

    public Ship() {
        this.position = new Point2D();
        this.init();
    }

    public Ship(Point2D p) {
        this.position = p;
        this.init();
    }

    public Ship(int x, int y) {
        this.position = new Point2D(x, y);
        this.init();
    }

    private void init() {
        this.color = TextColor.ANSI.GREEN;
        this.backgroundcolor = TextColor.ANSI.BLACK;
        this.bullets = new Bullet[Ship.bullets_size];

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
        if (this.paint_counter >= Ship.max_paint_counter) {
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

    public void paint(Screen s) {
        //se pinta la matriz
        for (int i = 0; i < this.cartoon.length; i++) {
            for (int j = -this.cartoon[i].length() / 2; j < this.cartoon[i].length() - this.cartoon[i].length() / 2; j++) {
                s.setCharacter(this.position.getX() + j,
                        this.position.getY() + i,
                        new TextCharacter(this.cartoon[i].charAt(j + this.cartoon[i].length() / 2),
                                color, this.backgroundcolor));
            }
        }
        //se pintan las balas
        for (int i = 0; i < this.bullets.length; i++) {
            if (this.bullets[i] != null) {
                this.bullets[i].paint(s);
            }
        }
    }

    public void shoot() {
        Bullet tempo;
        boolean shooted = false;
        //solo  dispara si tiene un disparo libre
        for (int i = 0; i < this.bullets.length && !shooted; i++) {
            if (this.bullets[i] == null) {
                tempo = new Bullet(this.position.getX(), this.position.getY());
                this.bullets[i] = tempo;
                shooted = true;
            }
        }
    }
}
