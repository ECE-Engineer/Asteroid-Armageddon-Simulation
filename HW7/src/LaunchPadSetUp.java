import java.util.ArrayList;
import java.util.Random;

public class LaunchPadSetUp extends Atomic {//////////////output could be int or rocket

    LaunchPadSetUp() {
        MAX_INPUTS_VAL = 1;
        MAX_OUTPUTS_VAL = 2;

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
        String result1 = "";
        String result2 = "";

        if (outputs.get(0) == null) {
            result1 = "NULL";
        } else {
            Integer value = (int)outputs.get(0);
            result1 = value.toString();
        }

        if (outputs.get(1) == null) {
            result2 = "NULL";
        } else {
            Rocket rocket = (Rocket)outputs.get(1);
            result2 = rocket.toString();
        }

        return String.format("%-30s",(this.toString() + " output = (" + result1 + "," + result2 + ")"));
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
            outputs.set(0, 0);
            outputs.set(1, null);
        } else {
            Rocket rocket = ((Rocket)internalStates.get(0));
            internalStates.remove(0);
            if (rocket.getRocketOrientationDegrees() >= (60+(random.nextInt(30))-(random.nextInt(30))) && rocket.getRocketOrientationDegrees() <= (120+(random.nextInt(30))-(random.nextInt(30)))) {//pass (0, rocket)(rocket.getRocketOrientationDegrees() >= (60+(random.nextInt(30))-(random.nextInt(30))) && rocket.getRocketOrientationDegrees() <= (120+(random.nextInt(25))-(random.nextInt(25))))
                outputs.set(0, 0);
                outputs.set(1, rocket);
            } else {//fail (1, null)
                outputs.set(0, 1);
                outputs.set(1, null);
            }
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
            outputs.set(0, 0);
            outputs.set(1, null);
        } else {
            Rocket rocket = ((Rocket)internalStates.get(0));
            internalStates.remove(0);
            if (rocket.getRocketOrientationDegrees() >= (60+(random.nextInt(30))-(random.nextInt(30))) && rocket.getRocketOrientationDegrees() <= (120+(random.nextInt(30))-(random.nextInt(30)))) {//pass (0, rocket)(rocket.getRocketOrientationDegrees() >= (60+(random.nextInt(30))-(random.nextInt(30))) && rocket.getRocketOrientationDegrees() <= (120+(random.nextInt(25))-(random.nextInt(25))))
                outputs.set(0, 0);
                outputs.set(1, rocket);
            } else {//fail (1, null)
                outputs.set(0, 1);
                outputs.set(1, null);
            }
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
