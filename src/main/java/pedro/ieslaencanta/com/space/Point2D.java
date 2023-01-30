/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pedro.ieslaencanta.com.space;

/**
 *
 * @author Pedro
 */
public class Point2D {
    private int x;
    private int y;
    public Point2D(){
        this.x=-1;
        this.y=-1;
    }
    public Point2D(int x,int y){
        this.x=x;
        this.y=y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    public void addX(int incx){
        this.x+=incx;
    }
      public void addY(int incy){
        this.y+=incy;
    }
}
