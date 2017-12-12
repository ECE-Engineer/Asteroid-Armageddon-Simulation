import java.util.ArrayList;
import java.util.Random;

public class AddPayload extends Atomic<Rocket, Rocket> {

    AddPayload() {
        MAX_INPUTS_VAL = 1;
        MAX_OUTPUTS_VAL = 1;

        internalStates = new ArrayList<>();

        outputs = new ArrayList<>();
        for (int i = 0; i < MAX_OUTPUTS_VAL; i++) {
            outputs.add(null);
        }
        remainingTime = 1000000;
        timeConstant = 3;
        random = new Random();
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
            rocket.setPayload(new Payload());
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
            rocket.setPayload(new Payload());
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
