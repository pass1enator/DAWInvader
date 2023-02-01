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
public class Bullet {

    private Point2D position;

    private TextColor color;
    private TextColor backgroundcolor;
    private int width = 1;
    private int height = 1;

    public Bullet() {
        this.position = new Point2D();
        this.init();
    }

    public Bullet(Point2D p) {
        this.position = p;
        this.init();
    }

    public Bullet(int x, int y) {
        this.position = new Point2D(x, y);
        this.init();
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    private void init() {
        this.color = TextColor.ANSI.GREEN;
        this.backgroundcolor = TextColor.ANSI.GREEN;

    }

    public void moveVertical(int incy, int min_y, int max_y) {
        if (this.getPosition().getY() + incy >= min_y && this.getPosition().getY() + incy <= max_y) {
            this.getPosition().addY(incy);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    public void paint(Screen s) {
        s.setCharacter(this.getPosition().getX(), this.getPosition().getY(), new TextCharacter('⣿', color, this.backgroundcolor));

    }
}
