import java.util.Random;

public class Payload {
    private int payloadSize;
    private int payloadStability;
    private Random random;

    Payload() {
        random = new Random();
        payloadSize = random.nextInt(100);
        //make stability worse for larger payload
    }

    public int getPayloadStability() {
        return payloadStability;
    }
}
