import java.util.ArrayList;

public class MissionControl extends Atomic<Integer, Rocket>{

    MissionControl() {
        MAX_INPUTS_VAL = 1;
        MAX_OUTPUTS_VAL = 1;

        internalStates = new ArrayList<>();
        for (int i = 0; i < MAX_INPUTS_VAL; i++) {
            internalStates.add(0);
        }

        outputs = new ArrayList<>();
        for (int i = 0; i < MAX_OUTPUTS_VAL; i++) {
            outputs.add(null);
        }

        remainingTime = 1000000;
        timeConstant = 5;
    }

    @Override
    public String lambda() {
        String result = "";
        if (outputs.get(0)==null) {
            result = "NULL";
        } else {
            Rocket rocket = (Rocket)outputs.get(0);
            result = rocket.toString();
        }

        return String.format("%-30s",(this.toString() + " output = " + result));
    }

    @Override
    public String deltaExternal(ArrayList b, int index, double e) {
        if ((int)internalStates.get(0) > 0) {
            remainingTime -= e;
        } else if ((int)internalStates.get(0) == 0) {
            remainingTime = timeConstant;
        }
        internalStates.set(index, ((int)internalStates.get(index))+((int)b.get(0)));

        return displayState();
    }

    @Override
    public String deltaConfluent(ArrayList b, int index, double e) {
        Rocket rocket = new Rocket();
        outputs.set(0, rocket);

        int input = (int)b.get(0);
        internalStates.set(index, ((int)internalStates.get(index))+(input - 1));
        remainingTime = timeConstant;

        return displayState();
    }

    @Override
    public String deltaInternal() {//sets 1 new rocket to the output and decrements the input bag
        if ((int)internalStates.get(0) > 0) {
            Rocket rocket = new Rocket();
            outputs.set(0, rocket);

            internalStates.set(0, (int)internalStates.get(0) - 1);
            remainingTime = timeConstant;
        } else {
            outputs.set(0, null);
            remainingTime = timeConstant;
        }
        return displayState();
    }
}
