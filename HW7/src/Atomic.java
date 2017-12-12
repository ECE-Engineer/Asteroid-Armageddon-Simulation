import java.util.ArrayList;
import java.util.Random;

public abstract class Atomic<K, V> {
    Random random;
    ArrayList<K> internalStates;
    ArrayList<V> outputs;
    int timeConstant, MAX_INPUTS_VAL, MAX_OUTPUTS_VAL;
    double remainingTime;/////////////////////////////////////////USE THIS!!!!!

    public abstract String lambda();
    public abstract String deltaExternal(ArrayList<K> b, int index, double e);
    public abstract String deltaConfluent(ArrayList<K> b, int index, double e);
    public abstract String deltaInternal();

    public String displayState() {
        String result = "";
        if (internalStates.size() < 1) {
            result = String.format("%-30s",(this.toString() + " state = " + "NULL"));
        } else {
            for (int i = 0; i < internalStates.size(); i++) {
                result += String.format("%-30s",(this.toString() + " state = " + internalStates.get(0)));
                if (i != internalStates.size()-1) {
                    result += "\n";
                }
            }
        }
        return result;
    }
}