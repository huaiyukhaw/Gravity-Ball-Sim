package ballsim_v2;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DropThread extends Thread {
    private final double PERCENT_ENERGY_LOST_BOUNCE = 0.8;
    private final double HEIGHT_METERS = 8;
    private final double EARTH_GRAVITY = 9.8;
    private final double WINDOW_HEIGHT = 420;
    private final double PX_PER_SEC2_Y = EARTH_GRAVITY * WINDOW_HEIGHT / HEIGHT_METERS; //m/s^2 * px / m
    private final double DECELERATION_DUE_TO_FRICTION = 200; //deceleration when x friction is applied
    private double xDeceleration = 0; //the active amount of x
    private Painter paint;
    private Timer time = new Timer();
    private boolean isDropping = true;
    private double initialXVelocity, initialYVelocity;

    public DropThread(Painter paint, double initialXVelocity, double initialYVelocity) {
        isDropping = true;
        this.paint = paint;
        this.initialXVelocity = initialXVelocity;
        this.initialYVelocity = initialYVelocity;
    }

    public void run() {
        System.out.println("NEW THREAD      " + isDropping);
        time.start();
        Point startPosition = (Point) paint.getBallLoc().clone();
        while (isDropping) {
            int xPos, yPos;
            xPos = paint.getBallLoc().x;
            int frictionDirection = initialXVelocity < 0 ? 1 : -1; //if x velocity is negative, friction is +, else friction is -
            yPos = (int) Math.round(startPosition.y + initialYVelocity * time.getSec() + 0.5 * PX_PER_SEC2_Y * time.getSec() * time.getSec()); //start position + V0t + + 1/2 a t^2. + is down
            xPos = (int) Math.round(startPosition.x + initialXVelocity * time.getSec() + frictionDirection * 0.5 * xDeceleration * time.getSec() * time.getSec()); //same as above. Accleration is 0 except when friction
            paint.setBallLoc(xPos, yPos);
            paint.repaint();
            
            //-----bounce X------
            if (paint.getBallLoc().x < 0) { //left bounce
                paint.setBallLoc(0, paint.getBallLoc().y);
                initialXVelocity = -PERCENT_ENERGY_LOST_BOUNCE * (initialXVelocity + frictionDirection * xDeceleration * time.getSec()); //bounce velocity
                initialYVelocity = (initialYVelocity + PX_PER_SEC2_Y * time.getSec()); //update y for time drop
                startPosition = (Point) paint.getBallLoc().clone();
                time.reset(); 
            } else if (paint.getBallLoc().x > 342) { //right bounce
                paint.setBallLoc(342, paint.getBallLoc().y);
                initialXVelocity = -PERCENT_ENERGY_LOST_BOUNCE * (initialXVelocity + frictionDirection * xDeceleration * time.getSec());
                initialYVelocity = (initialYVelocity + PX_PER_SEC2_Y * time.getSec());
                startPosition = (Point) paint.getBallLoc().clone();
                time.reset(); 
            }
            //-----bounce Y-----//
            if (paint.getBallLoc().y >= 420) { //bottom bounce
                paint.setBallLoc(paint.getBallLoc().x, 420);
                initialYVelocity = -PERCENT_ENERGY_LOST_BOUNCE *(initialYVelocity + PX_PER_SEC2_Y * time.getSec()); //v = v0 + at
                initialXVelocity = initialXVelocity + frictionDirection * xDeceleration * time.getSec();
                //----start over gravity falling time------
                startPosition = (Point) paint.getBallLoc().clone();
                time.reset(); 
                //isDropping = false;
            } 
            if (Math.abs(initialYVelocity) < 5 && paint.getBallLoc().y > 419){ //done bouncing
                xDeceleration = DECELERATION_DUE_TO_FRICTION;
            } else {
                xDeceleration = 0;
            }
            if (Math.abs(initialYVelocity) < 5 && Math.abs(initialXVelocity) < 5 && paint.getBallLoc().y > 419){ //if its not moving on the bottom
                isDropping = false;
            }
            System.out.println(xDeceleration);
           // System.out.println(initialYVelocity);
             //xDeceleration = (paint.getBallLoc().y >= 419) ? DECELERATION_DUE_TO_FRICTION : 0; //turn friction on and off based on y position
             
            paint.repaint();
            //---processor rest-----
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(DropThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("                        EXIT THREAD");
    }

    public void setIsDropping(boolean isDropping) {
        this.isDropping = isDropping;
    }
}
