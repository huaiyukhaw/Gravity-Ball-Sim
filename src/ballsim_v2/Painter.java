package ballsim_v2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Painter extends JComponent {
    
    private Point ballPt = new Point(175, 402);
    private Point ballSize = new Point(50,50);
    private String labelHeight = "";
    private String labelEnergyLost = "";
    private String labelCumulativeEnergyLost = "";
    private JFrame window;
    
    public Painter(JFrame window) {
        this.window = window;
    }
    
    public void paintComponent(Graphics g) {
        //------gfx back buffer image for smoother graphics----
        
        /*removed back buffer for optimization. only drawing one object (the ball)
          so it doesnt make sense to render seperate frames.
          7/23/16               
        */
        
       // Image backBuffer = createImage(window.getWidth(), window.getHeight());
        Graphics2D g2 = (Graphics2D) g;//backBuffer.getGraphics();
         
        //---background--------
        g2.setColor(Color.black);
        g2.fillRect(0, 0, window.getWidth(), window.getHeight());
        
        //----------draw Ball-------
        g2.setColor(Color.red);
        g2.fillOval(ballPt.x, ballPt.y, ballSize.x, ballSize.y);
                
        //g.drawImage(backBuffer, 0, 0, this); //update screen
        g2.setColor(Color.white);
        g2.drawString(labelHeight, 20, 30);
        g2.drawString(labelEnergyLost, 20, 50);
        g2.drawString(labelCumulativeEnergyLost, 20, 70);
    }

    public void setLabelHeight(Double label) {
        labelHeight = "Height: " + label + " metres";
    }

    public void setLabelEnergyLost(Double string) {
        labelEnergyLost = "Energy Lost On Last Bounce: " + string + " m/s";
    }

    public void setLabelCumulativeEnergyLost(Double label) {
        labelCumulativeEnergyLost = "Cumulative Energy Lost: " + label + " m/s";
    }

    public void setBallLoc(Point in){
        ballPt = in;
    }
    public void setBallLoc(int x, int y){
        ballPt.x = x;
        ballPt.y = y;
    }
    
    public void setBallSize(Point in){
        ballSize = in;
    }
    
    public Point getBallLoc(){
        return ballPt;
    }
    
    public Point getBallSize(){
        return ballSize;
    }
    public JFrame getWindow(){
        return window;
    }
}