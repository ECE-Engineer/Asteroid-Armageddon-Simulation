import java.util.Random;

public class Rocket {
    private double thermalStabilityFactor;
    private int rocketOrientationDegrees;
    private int fuelTankAmount;
    private int internalPressure;
    private Payload payload;
    private Random random;

    Rocket() {
        random = new Random();
        thermalStabilityFactor = random.nextDouble()*100;
        rocketOrientationDegrees = random.nextInt(180);
        fuelTankAmount = random.nextInt(1000);
        internalPressure = random.nextInt(10);
        payload = null;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public double getThermalStabilityFactor() {
        return thermalStabilityFactor;
    }

    public int getFuelTankAmount() {
        return fuelTankAmount;
    }

    public int getInternalPressure() {
        return internalPressure;
    }

    public int getRocketOrientationDegrees() {
        return rocketOrientationDegrees;
    }

    public void setFuelTankAmount(int fuelTankAmount) {
        this.fuelTankAmount = fuelTankAmount;
    }
}
