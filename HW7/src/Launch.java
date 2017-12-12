import java.util.ArrayList;
import java.util.Random;

public class Launch extends Atomic {

    Launch() {
        MAX_INPUTS_VAL = 1;
        MAX_OUTPUTS_VAL = 1;

        internalStates = new ArrayList<>();

        outputs = new ArrayList<>();
        for (int i = 0; i < MAX_OUTPUTS_VAL; i++) {
            outputs.add(null);
        }
        remainingTime = 1000000;
        timeConstant = 1;
        random = new Random();
    }

    @Override
    public String lambda() {
        String result = "";
        if (outputs.get(0)==null) {
            result = "NULL";
        } else {
            Rocket rocket = (Rocket)outputs.get(0);
            if (rocket.getThermalStabilityFactor() < 50) {////////
                result = "rocket blows up!";
            } else if (rocket.getRocketOrientationDegrees() < 60 || rocket.getRocketOrientationDegrees() > 120) {
                result = "rocket misses asteroid!";
            } else if (rocket.getFuelTankAmount() < 500) {
                result = "rocket never reaches asteroid!";
            } else if (rocket.getInternalPressure() > 5) {////////
                result = "rocket blows up!";
            } else if (rocket.getPayload().getPayloadStability() > 75) {///////
                result = "rocket blows up!";
            } else {
                result = "SUCCESSFUL ROCKET LAUNCH!";
            }
        }

        return String.format("%-30s",(this.toString() + " output = " + result));
    }

    @Override
    public String deltaExternal(ArrayList b, int index, double e) {
        if (internalStates.size() < 1) {
            remainingTime = timeConstant;
        } else {
            remainingTime -= e;
        }
        internalStates.add((Rocket)b.get(0));

        //remove any null inputs
        int size = internalStates.size();
        for (int i = 0; i < size; i++) {
            if (internalStates.get(i)==null){
                internalStates.remove(i);
            }
        }

        return displayState();
    }

    @Override
    public String deltaConfluent(ArrayList b, int index, double e) {
        if (internalStates.size() < 1) {
            outputs.set(0, null);
        } else {
            Rocket rocket = ((Rocket)internalStates.get(0));
            internalStates.remove(0);
            outputs.set(0, rocket);
        }
        internalStates.add((Rocket)b.get(0));
        remainingTime = timeConstant;

        //remove any null inputs
        int size = internalStates.size();
        for (int i = 0; i < size; i++) {
            if (internalStates.get(i)==null){
                internalStates.remove(i);
            }
        }

        return displayState();
    }

    @Override
    public String deltaInternal() {
        if (internalStates.size() < 1) {
            outputs.set(0, null);
        } else {
            Rocket rocket = ((Rocket)internalStates.get(0));
            internalStates.remove(0);
            outputs.set(0, rocket);
        }

        //remove any null inputs
        int size = internalStates.size();
        for (int i = 0; i < size; i++) {
            if (internalStates.get(i)==null){
                internalStates.remove(i);
            }
        }

        return displayState();
    }
}
