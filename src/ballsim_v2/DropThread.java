package ballsim_v2;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class DropThread extends Thread {

    private final double PERCENT_ENERGY_LOST_BOUNCE = 0.8;
    private final double RATIO_OF_PIXEL_TO_HEIGHT = 0.01;
    private final double HEIGHT_METERS = 8;
    private final double EARTH_GRAVITY = 9.8;
    private final double WINDOW_HEIGHT = 420;
    private final double WINDOW_HEIGHT_OFFSET = 98;
    private final double WINDOW_WIDTH_OFFSET = 73;
    private final double PX_PER_SEC2_Y = EARTH_GRAVITY * WINDOW_HEIGHT / HEIGHT_METERS; //m/s^2 * px / m
    private final double DECELERATION_DUE_TO_FRICTION = 200; //deceleration when x friction is applied
    private double xDeceleration = 0; //the active amount of x
    private double resizeXVelocity = 0, resizeYVelocity = 0;
    private double velocity = 0;
    private double energyLost = 0;
    private double cumulativeEnergyLost = 0;
    private int xPos;
    private int yPos;
    private Painter paint;
    private Timer time = new Timer();
    private double currentTime = time.getSec();
    private boolean isDropping = true;
    private double initialXVelocity, initialYVelocity;
    private JFrame window;

    public DropThread(Painter paint, double initialXVelocity, double initialYVelocity, JFrame window) {
        isDropping = true;
        this.paint = paint;
        this.initialXVelocity = initialXVelocity;
        this.initialYVelocity = initialYVelocity;
        //System.out.println(initialYVelocity);
        this.window = window;
    }

    public void run() {
        System.out.println("NEW THREAD      " + isDropping);
        time.start();
        Point startPosition = (Point) paint.getBallLoc().clone();
        while (isDropping) {
            double timeChanged = time.getSec() - currentTime;
            System.out.println(timeChanged);
            double xVelocity = paint.getBallLoc().x - xPos / timeChanged;
            double yVelocity = paint.getBallLoc().y - yPos / timeChanged;
            velocity = Math.sqrt(Math.pow(xVelocity, 2) + Math.pow(yVelocity, 2));

            currentTime = time.getSec();
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
                energyLost = pixelToMetre((1 - PERCENT_ENERGY_LOST_BOUNCE) / PERCENT_ENERGY_LOST_BOUNCE * Math.sqrt(Math.pow(initialXVelocity, 2) + Math.pow(initialYVelocity, 2)));
                cumulativeEnergyLost = cumulativeEnergyLost + energyLost;
                paint.setLabelEnergyLost(roundUp(energyLost));
                paint.setLabelCumulativeEnergyLost(roundUp(cumulativeEnergyLost));
                paint.setBallSize(new Point(35,50));
                time.reset();
            } else if (paint.getBallLoc().x > window.getWidth() - WINDOW_WIDTH_OFFSET) { //right bounce
                paint.setBallLoc((int) (window.getWidth() - WINDOW_WIDTH_OFFSET), paint.getBallLoc().y); //342
                initialXVelocity = -PERCENT_ENERGY_LOST_BOUNCE * (initialXVelocity + frictionDirection * xDeceleration * time.getSec());
                initialYVelocity = (initialYVelocity + PX_PER_SEC2_Y * time.getSec());
                startPosition = (Point) paint.getBallLoc().clone();
                energyLost = pixelToMetre((1 - PERCENT_ENERGY_LOST_BOUNCE) / PERCENT_ENERGY_LOST_BOUNCE * Math.sqrt(Math.pow(initialXVelocity, 2) + Math.pow(initialYVelocity, 2)));
                cumulativeEnergyLost = cumulativeEnergyLost + energyLost;
                paint.setLabelEnergyLost(roundUp(energyLost));
                paint.setLabelCumulativeEnergyLost(roundUp(cumulativeEnergyLost));
                paint.setBallSize(new Point(35,50));
                time.reset();
            }
            //-----bounce Y-----//
            if (paint.getBallLoc().y >= window.getHeight() - WINDOW_HEIGHT_OFFSET) { //bottom bounce
                paint.setBallLoc(paint.getBallLoc().x, (int) (window.getHeight() - WINDOW_HEIGHT_OFFSET)); //420
                initialYVelocity = -PERCENT_ENERGY_LOST_BOUNCE * (initialYVelocity + PX_PER_SEC2_Y * time.getSec()) + resizeYVelocity; //v = v0 + at
                initialXVelocity = initialXVelocity + frictionDirection * xDeceleration * time.getSec();
                //----start over gravity falling time------
                startPosition = (Point) paint.getBallLoc().clone();
                energyLost = pixelToMetre((1 - PERCENT_ENERGY_LOST_BOUNCE) / PERCENT_ENERGY_LOST_BOUNCE * Math.sqrt(Math.pow(initialXVelocity, 2) + Math.pow(initialYVelocity, 2)));
                cumulativeEnergyLost = cumulativeEnergyLost + energyLost;
                paint.setLabelEnergyLost(roundUp(energyLost));
                paint.setLabelCumulativeEnergyLost(roundUp(cumulativeEnergyLost));
                paint.setBallSize(new Point(50,35));
                time.reset();
                //isDropping = false;
            }
            else if (paint.getBallLoc().y <0) { //top bounce
                paint.setBallLoc(paint.getBallLoc().x, 0); //420
                initialYVelocity = -PERCENT_ENERGY_LOST_BOUNCE * (initialYVelocity + PX_PER_SEC2_Y * time.getSec()) + resizeYVelocity; //v = v0 + at
                initialXVelocity = initialXVelocity + frictionDirection * xDeceleration * time.getSec();
                //----start over gravity falling time------
                startPosition = (Point) paint.getBallLoc().clone();
                energyLost = pixelToMetre((1 - PERCENT_ENERGY_LOST_BOUNCE) / PERCENT_ENERGY_LOST_BOUNCE * Math.sqrt(Math.pow(initialXVelocity, 2) + Math.pow(initialYVelocity, 2)));
                cumulativeEnergyLost = cumulativeEnergyLost + energyLost;
                paint.setLabelEnergyLost(roundUp(energyLost));
                paint.setLabelCumulativeEnergyLost(roundUp(cumulativeEnergyLost));
                paint.setBallSize(new Point(50,35));
                time.reset();
                //isDropping = false;
            }
            //---reset resize velocity after one pass---
            resizeXVelocity = 0;
            resizeYVelocity = 0;

            //---turn on x frixtion---//
            if (Math.abs(initialYVelocity) < 5 && paint.getBallLoc().y > window.getHeight() - WINDOW_HEIGHT_OFFSET - 1) { //done bouncing
                xDeceleration = DECELERATION_DUE_TO_FRICTION;
            } else {
                xDeceleration = 0;
            }

            //----STOP Thread----
            if (Math.abs(initialYVelocity) < 5 && Math.abs(initialXVelocity) < 5 && paint.getBallLoc().y > window.getHeight() - WINDOW_HEIGHT_OFFSET - 1) { //if its not moving on the bottom
                isDropping = false;
            }
            paint.setBallSize(new Point(50,50));
            paint.setLabelHeight(roundUp((pixelToMetre(window.getHeight() - paint.getBallLoc().y - paint.getBallSize().y))));
            paint.setLabelVelocity(roundUp(pixelToMetre(velocity)));
            paint.setLabelPosition("(" + roundUp(pixelToMetre(xPos)) + ", " + roundUp(pixelToMetre(yPos)) + ")");
            paint.repaint();

            //---processor rest-----
            try {
                Thread.sleep(4);
            } catch (InterruptedException ex) {
                Logger.getLogger(DropThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("                        EXIT THREAD");
    }

    public void setIsDropping(boolean isDropping) {
        this.isDropping = isDropping;
    }

    public void setResizeVelocity(double x, double y) {
        resizeXVelocity = x;
        resizeYVelocity = scaleVelocity(y);
    }

    //scale the velocity from the window resize
    private double scaleVelocity(double vel) {
        int sign = (vel < 0) ? -1 : 1; //store direction
        vel = Math.abs(vel); //get scalar quantity
        if (vel < 200) { //piece wise
            return sign * vel;
        } else { //logrithmic scaling
            return sign * (212.53 * Math.log(vel) - 941.32); //equation from excel
        }
    }

    //convert from px per sec to metre per sec
    private double pixelToMetre(double pixel) {
        return RATIO_OF_PIXEL_TO_HEIGHT * pixel;
    }

    //round up to four digits
    private double roundUp(double number) {
        return (Math.round(number * 10000.0) / 10000.0);
    }
}
