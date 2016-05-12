package ballsim_v2;

public class Timer {

    long startTime;
    long acumTime;
    boolean isRunning;

    public Timer(){
    }
    public Timer(double startTimeSec) {
        acumTime = (int) startTimeSec * 1000;
    }
    public void start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            isRunning = true;
        }
    }

    public long get() {
        return acumTime + (isRunning ? System.currentTimeMillis() - startTime : 0);
    }

    public double getSec() {
        return get() / 1000.0;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        if (isRunning) {
            acumTime += System.currentTimeMillis() - startTime;
            isRunning = false;
        }
    }

    public void reset() {
        acumTime = 0;
        startTime = System.currentTimeMillis();
    }
}
