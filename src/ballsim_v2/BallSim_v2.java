package ballsim_v2;

import javax.swing.JFrame;

public class BallSim_v2 {
    static JFrame window = new JFrame("Ball Sim");
    static Painter paint = new Painter(window);
    static Listener listen = new Listener(paint, window);
    
    public static void main(String[] args) {
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(700, 200, 400, 500);
        window.setResizable(true);
        
        window.addMouseListener(listen);
        window.addMouseMotionListener(listen);
        window.addComponentListener(listen);
        window.add(paint);
        
        window.setVisible(true);
    }
}
