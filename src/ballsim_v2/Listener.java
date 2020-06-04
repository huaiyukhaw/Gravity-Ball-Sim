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
    private Timer mouseTime = new Timer();
    private Timer sizeTime = new Timer();
    private Point prevLocation;
    private int prevWidth = 0, prevHeight = 0;
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
        mouseTime.stop();

        if (mouseTime.getSec() > 0) { //divide by 0 protect
            mouseXVelocity = (double) (mouseLoc.x - prevLocation.x) / mouseTime.getSec();
            mouseYVelocity = (double) (mouseLoc.y - prevLocation.y) / mouseTime.getSec();
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
            paint.setLabelHeight((Math.round((0.01 * (window.getHeight() - paint.getBallLoc().y - paint.getBallSize().y)) * 10000.0) / 10000.0));
        }
        prevLocation = (Point) mouseLoc.clone();
        mouseTime.reset();
        mouseTime.start();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        //---init---
        if (prevHeight == 0 || prevWidth == 0) {
            prevHeight = window.getHeight();
            prevWidth = window.getWidth();
        }
        sizeTime.stop();
        double resizeXVelocity = 0, resizeYVelocity = 0;
        if (sizeTime.getSec() > 0) { //divide by 0 protect
            resizeXVelocity = (double) (window.getWidth() - prevWidth) / sizeTime.getSec();
            resizeYVelocity = (double) (window.getHeight() - prevHeight) / sizeTime.getSec();
        }
        
        if (!dropThread.isAlive()) {
            System.out.println("    NEW");
            dropThread = new DropThread(paint, 0, resizeYVelocity, paint.getWindow());//mouse cant be moving ball
            dropThread.start();
        } else {
            dropThread.setResizeVelocity(resizeXVelocity, resizeYVelocity);
        }

        prevHeight = window.getHeight();
        prevWidth = window.getWidth();
        sizeTime.reset();
        sizeTime.start();
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
