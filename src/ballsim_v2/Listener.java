package ballsim_v2;

import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;

public class Listener implements MouseListener, MouseMotionListener, ComponentListener {

    private Painter paint;
    private JFrame window;
    private boolean isHolding = false; //if you are clicked and holding on the ball
    private Point delta = new Point(0, 0);
    private DropThread dropThread;
    private Timer time = new Timer();
    private Point prevLocation;
    private double mouseXVelocity, mouseYVelocity;
    private final double WINDOW_HEIGHT_OFFSET = 98;
    private final double WINDOW_WIDTH_OFFSET = 73;

    public Listener(Painter paint, JFrame window) {
        this.paint = paint;
        dropThread = new DropThread(paint, 0, 0, paint.getWindow());
        this.window = window;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        Point mouseLoc = me.getPoint();
        //System.out.println(mouseLoc);
        mouseXVelocity = 0;
        mouseYVelocity = 0;
        //if the click is inside the ball, you are holding it. 
        //System.out.println("Mouse LOC: " + mouseLoc + "      " + "Ball LOC: " + paint.getBallLoc());
        isHolding = (ShapeUtils.isPtInCircle(new Point(mouseLoc.x - 12, mouseLoc.y - 33), paint.getBallLoc(), paint.getBallSize())); //20 is offset to align mouse and ball
        dropThread.setIsDropping(!isHolding); //if it is holding stop dropping, else keep dropping
        delta.x = (paint.getBallLoc().x - mouseLoc.x);
        delta.y = (paint.getBallLoc().y - mouseLoc.y);
        System.out.println(isHolding);
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (isHolding) { //if you were holding and just let go
            isHolding = false;
            dropThread.setIsDropping(false); //kill old drop before starting new one
            dropThread = new DropThread(paint, mouseXVelocity, mouseYVelocity, paint.getWindow());
            dropThread.start();
            mouseXVelocity = 0;
            mouseYVelocity = 0;
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        Point mouseLoc = me.getPoint();
        if (prevLocation == null) {
            prevLocation = (Point) mouseLoc.clone();
        }
        time.stop();

        if (time.getSec() > 0) { //divide by 0 protect
            mouseXVelocity = (double) (mouseLoc.x - prevLocation.x) / time.getSec();
            mouseYVelocity = (double) (mouseLoc.y - prevLocation.y) / time.getSec();
        }

        if (isHolding) {
            paint.setBallLoc(delta.x + mouseLoc.x, delta.y + mouseLoc.y);
            if (paint.getBallLoc().x > window.getWidth() - WINDOW_WIDTH_OFFSET) {
                paint.setBallLoc((int) (window.getWidth() - WINDOW_WIDTH_OFFSET), paint.getBallLoc().y);
            }
            if (paint.getBallLoc().x < 0) {
                paint.setBallLoc(0, paint.getBallLoc().y);
            }
            if (paint.getBallLoc().y > window.getHeight() - WINDOW_HEIGHT_OFFSET) {
                paint.setBallLoc(paint.getBallLoc().x, (int) (window.getHeight() - WINDOW_HEIGHT_OFFSET));
            }
            paint.repaint();
        }
        prevLocation = (Point) mouseLoc.clone();
        time.reset();
        time.start();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        if (!dropThread.isAlive()) {
            dropThread = new DropThread(paint, mouseXVelocity, mouseYVelocity, paint.getWindow());
            dropThread.start();
        }
    }

    @Override
    public void componentMoved(ComponentEvent ce) {
    }

    @Override
    public void componentShown(ComponentEvent ce) {
    }

    @Override
    public void componentHidden(ComponentEvent ce) {
    }

}
