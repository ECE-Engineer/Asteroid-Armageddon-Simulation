import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner kb = new Scanner(System.in);

        ArrayList<Network> networkArrayList = new ArrayList<>();
        ArrayList<MissionControl> missionControlArrayList = new ArrayList<>();
        ArrayList<BoosterFiringCheck> boosterFiringCheckArrayList = new ArrayList<>();
        ArrayList<LaunchPadSetUp> launchPadSetUpArrayList = new ArrayList<>();
        ArrayList<FuelCheck> fuelCheckArrayList = new ArrayList<>();
        ArrayList<ValveCheck> valveCheckArrayList = new ArrayList<>();
        ArrayList<AddPayload> addPayloadArrayList = new ArrayList<>();
        ArrayList<Launch> launchArrayList = new ArrayList<>();

        final int MAX_NETWORKS = 1;
        final int MAX_MISSION_CONTROL = 1;
        final int MAX_BOOSTER_FIRING_CHECK = 1;
        final int MAX_LAUNCH_PAD_SETUP = 1;
        final int MAX_FUEL_CHECK = 1;
        final int MAX_VALVE_CHECK = 1;
        final int MAX_ADD_PAYLOAD = 1;
        final int MAX_LAUNCH = 1;

        for (int i = 0; i < MAX_NETWORKS; i++) {
            HashMap<Pair, Pair> hashMap = new HashMap<>();

            for (int j = 0; j <MAX_MISSION_CONTROL ; j++) {
                missionControlArrayList.add(new MissionControl());
            }
            for (int j = 0; j < MAX_BOOSTER_FIRING_CHECK; j++) {
                boosterFiringCheckArrayList.add(new BoosterFiringCheck());
                //take output[0] -> to input[0]
                hashMap.put(new Pair<MissionControl>(missionControlArrayList.get(j), 0), new Pair<BoosterFiringCheck>(boosterFiringCheckArrayList.get(j), 0));

                //loop back to mission control is fail

                //take output[0] -> to input[0]
                hashMap.put(new Pair<BoosterFiringCheck>(boosterFiringCheckArrayList.get(j), 0), new Pair<MissionControl>(missionControlArrayList.get(j), 0));//feedback loop
            }
            for (int j = 0; j < MAX_LAUNCH_PAD_SETUP; j++) {
                launchPadSetUpArrayList.add(new LaunchPadSetUp());
                //take output[1] -> to input[0]
                hashMap.put(new Pair<BoosterFiringCheck>(boosterFiringCheckArrayList.get(j), 1), new Pair<LaunchPadSetUp>(launchPadSetUpArrayList.get(j), 0));

                //loop back to mission control is fail

                //take output[0] -> to input[0]
                hashMap.put(new Pair<LaunchPadSetUp>(launchPadSetUpArrayList.get(j), 0), new Pair<MissionControl>(missionControlArrayList.get(j), 0));//feedback loop
            }
            for (int j = 0; j <MAX_FUEL_CHECK ; j++) {
                fuelCheckArrayList.add(new FuelCheck());
                //take output[1] -> to input[0]
                hashMap.put(new Pair<LaunchPadSetUp>(launchPadSetUpArrayList.get(j), 1), new Pair<FuelCheck>(fuelCheckArrayList.get(j), 0));

                //loop back to SELF if fail

                //take output[0] -> to input[0]
                hashMap.put(new Pair<FuelCheck>(fuelCheckArrayList.get(j), 0), new Pair<FuelCheck>(fuelCheckArrayList.get(j), 0));//(SELF) feedback loop
            }
            for (int j = 0; j < MAX_VALVE_CHECK; j++) {
                valveCheckArrayList.add(new ValveCheck());
                //take output[1] -> to input[0]
                hashMap.put(new Pair<FuelCheck>(fuelCheckArrayList.get(j), 1), new Pair<ValveCheck>(valveCheckArrayList.get(j), 0));

                //loop back to mission control is fail

                //take output[0] -> to input[0]
                hashMap.put(new Pair<ValveCheck>(valveCheckArrayList.get(j), 0), new Pair<MissionControl>(missionControlArrayList.get(j), 0));//feedback loop
            }
            for (int j = 0; j < MAX_ADD_PAYLOAD; j++) {
                addPayloadArrayList.add(new AddPayload());
                //take output[1] -> to input[0]
                hashMap.put(new Pair<ValveCheck>(valveCheckArrayList.get(j), 1), new Pair<AddPayload>(addPayloadArrayList.get(j), 0));
            }
            for (int j = 0; j < MAX_LAUNCH; j++) {
                launchArrayList.add(new Launch());
                hashMap.put(new Pair<AddPayload>(addPayloadArrayList.get(j), 0), new Pair<Launch>(launchArrayList.get(j), 0));
            }

            Network<Integer> network = new Network.NetworkBuilder()
                    .missionControls(missionControlArrayList)
                    .boosterFiringChecks(boosterFiringCheckArrayList)
                    .lunchPadSetUps(launchPadSetUpArrayList)
                    .fuelChecks(fuelCheckArrayList)
                    .valveChecks(valveCheckArrayList)
                    .addPayloads(addPayloadArrayList)
                    .launches(launchArrayList)
                    .hm(hashMap)
                    .build();

            networkArrayList.add(network);
        }

        System.out.println("WELCOME TO THE ASTEROID ARMAGEDDON SIMULATION!");

        System.out.println("In the near distant future, the Earth is at war against a race of giant intelligent alien insects!");
        System.out.println("Little is known about the Bugs except that they are intent on the eradication of all human life.");
        System.out.println("A few humans go back in time to inform you that in the Bugs first wave of attack against humanity, ");
        System.out.println("a giant asteroid will impact the Earth wiping out more than half of Earth's total population.");
        System.out.println("Unfortunately you have only ONE year before the giant asteroid impacts the Earth!");
        System.out.println("In a desperate attempt to avert the asteroid's collision path away from Earth, you assemble ");
        System.out.println("all of the planet's Weapons of Mass Destruction and prepare rockets to launch the payloads into ");
        System.out.println("deep space.");

        System.out.println("WILL YOU SAVE THE WORLD FROM IMPEDING DOOM?!");

        System.out.println("COPYWRITE by Touchstone Pictures");
        System.out.println();

        for (int i = 0; i < MAX_NETWORKS; i++) {
            System.out.print("INPUTS: ->");
            String line = kb.nextLine();
            while (!line.equalsIgnoreCase("exit")) {
                String[] arr = line.split("\\s+");

                double realTime = Double.parseDouble(arr[0]);
                int modelInput = Integer.parseInt(arr[1]);
                int port = Integer.parseInt(arr[2]);

                networkArrayList.get(i).initScheduler(realTime, modelInput, port);
                System.out.print("INPUTS: ->");
                line = kb.nextLine();
            }
            networkArrayList.get(i).runEvents();
        }

    }
}
