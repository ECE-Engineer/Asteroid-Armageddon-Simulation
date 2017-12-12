public class Time {
    private double realTime;
    private int discreteTime;

    public Time(double realTime, int discreteTime) {
        this.realTime = realTime;
        this.discreteTime = discreteTime;
    }

    public Time timeAdvance(Time currentTime) {//, int timeConstant
        Time newTime = new Time(this.realTime, this.discreteTime);

        if (currentTime.realTime != 0) {
            newTime.realTime += currentTime.realTime;//timeConstant
            newTime.discreteTime = 0;
        } else {
            newTime.discreteTime += currentTime.discreteTime;
        }

        return newTime;
    }

    public double getRealTime() {
        return realTime;
    }

    public int getDiscreteTime() {
        return discreteTime;
    }

    public void setRealTime(double realTime) {
        this.realTime = realTime;
    }

    public void setDiscreteTime(int discreteTime) {
        this.discreteTime = discreteTime;
    }
}
