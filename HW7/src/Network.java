import java.util.*;

public class Network<T> extends Atomic {
    //HAVE A COUNTER FOR THE NUMBER OF LAUNCHES
    //THE PROGRAM WILL START OUT SCHEDULING ONE LAUNCH AT A TIME AND AFTER 100 LAUNCHES -> PERFORM THE FINAL CHECK
    //HAVE THE PROGRAM PRINT OUT WHAT HAPPENS TO THE ROCKETS WHEN THEY FINALLY LAUNCH
    //HAVE THE PROGRAM KEEP A GLOBAL COUNTER OF THE NUMBER OF SUCCESSES, BLOWING UP, MISSES, AND NEVER HITTING TARGET

    ////////////NETWORK MUST SCHEDULE 1 ROCKET AT A TIME

    private int counter;
    private int rocketBlewUp, rocketMissed, rocketNeverReachedTarget, successfulLaunches;

    private ArrayList<Network> networkArrayList;
    private ArrayList<MissionControl> missionControlArrayList;
    private ArrayList<BoosterFiringCheck> boosterFiringCheckArrayList;
    private ArrayList<LunchPadSetUp> lunchPadSetUpArrayList;
    private ArrayList<FuelCheck> fuelCheckArrayList;
    private ArrayList<ValveCheck> valveCheckArrayList;
    private ArrayList<AddPayload> addPayloadArrayList;
    private ArrayList<Launch> launchArrayList;

    private HashMap<Pair, Pair> hashMap;
    private Node priorityQueue;
    private Pair<Atomic> initialNetworkPair;

    private Set set;
    private Iterator iterator;

    private static final int MAX_LAUNCHES = 1000;

    public void endEvaluation() {
        if (rocketNeverReachedTarget+rocketMissed+rocketBlewUp <= successfulLaunches) {
            System.out.println("CONGRATULATIONS HERO!!! YOU SAVED THE WORLD!!!");
        } else {
            System.out.println("SORRY! Looks like people will now be forced to evacuate!");
        }
        System.out.println();
        System.out.println("STATS: -> " + "successful launches = " + (successfulLaunches));
        System.out.println("STATS: -> " + "rockets blown up = " + rocketBlewUp);
        System.out.println("STATS: -> " + "rockets that missed the asteroid = " + rocketMissed);
        System.out.println("STATS: -> " + "rockets that ran never had enough fuel to hit the asteroid = " + rocketNeverReachedTarget);
    }

    public void statsChecker(String state) {
        if (!state.contains("NULL")) {
            if (state.contains("blows up")) {
                ++rocketBlewUp;
            } else if (state.contains("misses")) {
                ++rocketMissed;
            } else if (state.contains("never")) {
                ++rocketNeverReachedTarget;
            } else {
                ++successfulLaunches;
            }
        }
    }

    public void runEvents() throws InterruptedException {
//        System.out.println("TOTAL NUMBER OF LINES " + counter);
//        System.out.println("TOTAL NUMBER OF ROCKETS TO BE BUILT " + ((Atomic)hashMap.get(initialNetworkPair).getModel()).internalStates.get(0));

//        priorityQueue.displayTree();
/////////////////////////////////////////////////////////////////////


        while (priorityQueue != null) {
            //get the event at the top
            Node top = priorityQueue.getTop();
            double e = top.getTime().getRealTime() - ((Atomic)top.getPair().getModel()).remainingTime;
//            priorityQueue.displayTree();////////////////////////////////////////////////////////////////////////////////////////////////////////DEBUG

            Node opposite = null;

            Time topTime;
            Pair topPair;
            Event topEvent;

            Node temp;
            boolean confluentFlag = false;

            //check to see if there is another event in the tree with the same time & model_type & opposite event present
            if (priorityQueue.containsOppositeEventTypeConfluent(priorityQueue, top.getTime(), top.getPair(), top.getEvent())) {
                opposite = priorityQueue.getOppositeEventTypeConfluent(priorityQueue, top.getTime(), top.getPair(), top.getEvent());

                //keep the top node as the external event
                if (top.getEvent() instanceof InternalEvent) {
                    temp = top;
                    top = opposite;
                    opposite = temp;
                }

                //use opposite because it has the internal event
                if (priorityQueue.containsOppositePortExternalOrInternal(priorityQueue, opposite.getTime(), opposite.getPair(), opposite.getEvent())
                         || (((Atomic)opposite.getPair().getModel()).MAX_OUTPUTS_VAL == 1)) {
                    confluentFlag = true;
                } else {
                    temp = top;
                    top = opposite;
                    opposite = temp;
                }
            }

            //make sure it is actually a confluent event
            if (confluentFlag) {
                System.out.println("CONFLUENT");
                //true ----> double check the object's t and e for equality

                topTime = top.getTime();
                topPair = top.getPair();
                topEvent = top.getEvent();

                //run lambda
                String result = ((Atomic)topPair.getModel()).lambda();
                System.out.println(result);

                ArrayList tempArrList = new ArrayList<>();
                tempArrList.add(topEvent.getEvent());

                //run confluent delta
                System.out.println("AT TIME -> real -> " + topTime.getRealTime() + " and discrete -> " + topTime.getDiscreteTime());
                System.out.println(((Atomic)topPair.getModel()).deltaConfluent(tempArrList, topPair.getPort(), e));

                //check to see if the tree contains the "same" TIME & MODEL & "different" PORT0.0 true 0
                if (priorityQueue.containsOppositePortExternalOrInternal(priorityQueue, opposite.getTime(), opposite.getPair(), opposite.getEvent())) {///////if node has 2 outputs present (2 outputs --- confluent case)
                    Node portOppositeNode = priorityQueue.getOppositePortNode(priorityQueue, opposite.getTime(), opposite.getPair(), opposite.getEvent());
                    //outputs
                    priorityQueue = priorityQueue.remove(priorityQueue, opposite.getTime(), opposite.getPair(), opposite.getEvent());
                    priorityQueue = priorityQueue.remove(priorityQueue, portOppositeNode.getTime(), portOppositeNode.getPair(), portOppositeNode.getEvent());
                } else {//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////(1 output --- confluent case)
                    //output
                    priorityQueue = priorityQueue.remove(priorityQueue, opposite.getTime(), opposite.getPair(), opposite.getEvent());
                }
                //input
                priorityQueue = priorityQueue.remove(priorityQueue, topTime, topPair, topEvent);

                if ((topPair.getModel() instanceof Launch)) {
                    result = ((Atomic)topPair.getModel()).lambda();
                    this.statsChecker(result);
                    System.out.println(result);

                    if (priorityQueue != null) {
                        //HEAPIFY
                        priorityQueue = priorityQueue.heapify(priorityQueue);
                    }
                } else {
                    if (priorityQueue != null) {
                        //HEAPIFY
                        priorityQueue = priorityQueue.heapify(priorityQueue);

                        //add the nodes
                        while(iterator.hasNext()) {
                            Map.Entry me = (Map.Entry)iterator.next();
                            if ((((Pair)me.getKey()).getModel()) == (topPair.getModel())) {
                                Pair newPair = ((Pair)me.getValue());
                                int sendingFromOldOutputPort = ((Pair)me.getKey()).getPort();
                                Event newEvent = new ExternalEvent(null);

                                if (((Atomic)((Pair)me.getKey()).getModel()).outputs.get(sendingFromOldOutputPort) != null) {
                                    newEvent = new ExternalEvent(((Atomic)((Pair)me.getKey()).getModel()).outputs.get(sendingFromOldOutputPort));
                                }

                                priorityQueue.addNode(topTime, newPair, newEvent);
                            }
                        }
                    } else {
                        //add the nodes
                        int nodeCounter = 0;
                        while(iterator.hasNext()) {
                            Map.Entry me = (Map.Entry)iterator.next();
                            if ((((Pair)me.getKey()).getModel()) == (topPair.getModel())) {
                                Pair newPair = ((Pair)me.getValue());
                                int sendingFromOldOutputPort = ((Pair)me.getKey()).getPort();
                                Event newEvent = new ExternalEvent(null);

                                if (((Atomic)((Pair)me.getKey()).getModel()).outputs.get(sendingFromOldOutputPort) != null) {
                                    newEvent = new ExternalEvent(((Atomic)((Pair)me.getKey()).getModel()).outputs.get(sendingFromOldOutputPort));
                                }

                                if (nodeCounter == 0) {
                                    priorityQueue = new Node(topTime, newPair, newEvent);
                                } else {
                                    priorityQueue.addNode(topTime, newPair, newEvent);
                                }
                                ++nodeCounter;
                            }
                        }
                    }
                    //HEAPIFY
                    priorityQueue = priorityQueue.heapify(priorityQueue);

                    //reset the iterator
                    iterator = null;
                    iterator = set.iterator();
                }

                if (((topPair.getModel() instanceof MissionControl) && (((int)(((Atomic)topPair.getModel()).internalStates.get(0))) > 0)) || ((!(topPair.getModel() instanceof MissionControl)) && ((Atomic)topPair.getModel()).internalStates.size() > 0)) {
                    //time advance
                    Time currentTime = topTime.timeAdvance(new Time(((Atomic) topPair.getModel()).remainingTime, 1));//, ((Atomic) topPair.getModel()).timeConstant

                    //add internal events for this machine its machine time later
                    //add the nodes
                    if (priorityQueue == null) {
                        Event newEvent = new InternalEvent("---");

                        priorityQueue = new Node(currentTime, topPair, newEvent);
                    } else {
                        Event newEvent = new InternalEvent("---");

                        priorityQueue.addNode(currentTime, topPair, newEvent);
                    }
                    //HEAPIFY
                    priorityQueue = priorityQueue.heapify(priorityQueue);

                    //reset the iterator
                    iterator = null;
                    iterator = set.iterator();
                } else {
                    //reset remaining time
                    ((Atomic)topPair.getModel()).remainingTime = 1000000;
                }
            } else {
                top = priorityQueue.getTop();

                ////////false ----> check if to run internal
                if (top.getEvent() instanceof InternalEvent) {
                    System.out.println("INTERNAL");
                    //true ----> run internal & lambda

                    topTime = top.getTime();
                    topPair = top.getPair();
                    topEvent = top.getEvent();

                    //run lambda
                    String result = ((Atomic)topPair.getModel()).lambda();
                    System.out.println(result);

                    //run internal delta
                    System.out.println("AT TIME -> real -> " + topTime.getRealTime() + " and discrete -> " + topTime.getDiscreteTime());
                    System.out.println(((Atomic)topPair.getModel()).deltaInternal());

                    //check to see if the tree contains the "same" TIME & MODEL & "different" PORT0.0 true 0
                    if (priorityQueue.containsOppositePortExternalOrInternal(priorityQueue, topTime, topPair, topEvent)) {///////if node has 2 outputs present (2 outputs --- internal case)
                        Node portOppositeNode = priorityQueue.getOppositePortNode(priorityQueue, topTime, topPair, topEvent);
                        //outputs
                        priorityQueue = priorityQueue.remove(priorityQueue, topTime, topPair, topEvent);
                        priorityQueue = priorityQueue.remove(priorityQueue, portOppositeNode.getTime(), portOppositeNode.getPair(), portOppositeNode.getEvent());
                    } else {//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////(1 output --- internal case)
                        //output
                        priorityQueue = priorityQueue.remove(priorityQueue, topTime, topPair, topEvent);
                    }

                    if ((topPair.getModel() instanceof Launch)) {
                        result = ((Atomic)topPair.getModel()).lambda();
                        this.statsChecker(result);
                        System.out.println(result);

                        if (priorityQueue != null) {
                            //HEAPIFY
                            priorityQueue = priorityQueue.heapify(priorityQueue);
                        }
                    } else {
                        if (priorityQueue != null) {
                            //HEAPIFY
                            priorityQueue = priorityQueue.heapify(priorityQueue);

                            //add the nodes
                            while(iterator.hasNext()) {
                                Map.Entry me = (Map.Entry)iterator.next();
//                                System.out.println("(!null) iterator object -> " + (((Pair)me.getKey()).getModel()) + " top object -> " + topPair.getModel());/////////////////////////////////////////////INT
                                if ((((Pair)me.getKey()).getModel()) == (topPair.getModel())) {
                                    Pair newPair = ((Pair)me.getValue());
                                    int sendingFromOldOutputPort = ((Pair)me.getKey()).getPort();
                                    Event newEvent = new ExternalEvent(null);

                                    if (((Atomic)((Pair)me.getKey()).getModel()).outputs.get(sendingFromOldOutputPort) != null) {
                                        newEvent = new ExternalEvent(((Atomic)((Pair)me.getKey()).getModel()).outputs.get(sendingFromOldOutputPort));
                                    }

                                    priorityQueue.addNode(topTime, newPair, newEvent);
                                }
                            }
                        } else {
                            //add the nodes
                            int nodeCounter = 0;
                            while(iterator.hasNext()) {
                                Map.Entry me = (Map.Entry)iterator.next();
//                                System.out.println("(null) iterator object -> " + (((Pair)me.getKey()).getModel()) + " top object -> " + topPair.getModel());/////////////////////////////////////////////INT
                                if ((((Pair)me.getKey()).getModel()) == (topPair.getModel())) {
                                    Pair newPair = ((Pair)me.getValue());
                                    int sendingFromOldOutputPort = ((Pair)me.getKey()).getPort();
                                    Event newEvent = new ExternalEvent(null);

                                    if (((Atomic)((Pair)me.getKey()).getModel()).outputs.get(sendingFromOldOutputPort) != null) {
                                        newEvent = new ExternalEvent(((Atomic)((Pair)me.getKey()).getModel()).outputs.get(sendingFromOldOutputPort));
                                    }

                                    if (nodeCounter == 0) {
                                        priorityQueue = new Node(topTime, newPair, newEvent);
                                    } else {
                                        priorityQueue.addNode(topTime, newPair, newEvent);
                                    }
                                    ++nodeCounter;
                                }
                            }
                        }
                        //HEAPIFY
                        priorityQueue = priorityQueue.heapify(priorityQueue);

                        //reset the iterator
                        iterator = null;
                        iterator = set.iterator();
                    }

                    if (((topPair.getModel() instanceof MissionControl) && (((int)(((Atomic)topPair.getModel()).internalStates.get(0))) > 0)) || ((!(topPair.getModel() instanceof MissionControl)) && ((Atomic)topPair.getModel()).internalStates.size() > 0)) {
                        //time advance
                        Time currentTime = topTime.timeAdvance(new Time(((Atomic) topPair.getModel()).remainingTime, 1));//, ((Atomic) topPair.getModel()).timeConstant

                        //add internal events for this machine its machine time later
                        //add the nodes
                        if (priorityQueue == null) {
                            Event newEvent = new InternalEvent("---");

                            priorityQueue = new Node(currentTime, topPair, newEvent);
                        } else {
                            Event newEvent = new InternalEvent("---");

                            priorityQueue.addNode(currentTime, topPair, newEvent);
                        }
                        //HEAPIFY
                        priorityQueue = priorityQueue.heapify(priorityQueue);

                        //reset the iterator
                        iterator = null;
                        iterator = set.iterator();
                    } else {
                        //reset remaining time
                        ((Atomic)topPair.getModel()).remainingTime = 1000000;
                    }
                } else {

                    top = priorityQueue.getTop();

                    System.out.println("EXTERNAL");
                    topTime = top.getTime();
                    topPair = top.getPair();
                    topEvent = top.getEvent();
                    boolean toSchedule = Math.abs(((Atomic)topPair.getModel()).remainingTime - 1000000) < 0.01;

                    ArrayList tempArrList = new ArrayList<>();
                    tempArrList.add(topEvent.getEvent());

                    //run external delta
                    System.out.println("AT TIME -> real -> " + topTime.getRealTime() + " and discrete -> " + topTime.getDiscreteTime());
                    System.out.println(((Atomic)topPair.getModel()).deltaExternal(tempArrList, topPair.getPort(), e));

                    priorityQueue = priorityQueue.remove(priorityQueue, topTime, topPair, topEvent);

                    if (priorityQueue != null) {
                        //HEAPIFY
                        priorityQueue = priorityQueue.heapify(priorityQueue);
                    }

                    if (toSchedule) {
                        //time advance
                        Time currentTime = topTime.timeAdvance(new Time(((Atomic) topPair.getModel()).remainingTime, 1));//, ((Atomic) topPair.getModel()).timeConstant

                        //add internal events for this machine its machine time later
                        //add the nodes
                        if (priorityQueue == null) {
                            Event newEvent = new InternalEvent("---");

                            priorityQueue = new Node(currentTime, topPair, newEvent);
                        } else {
                            Event newEvent = new InternalEvent("---");

                            priorityQueue.addNode(currentTime, topPair, newEvent);
                        }
                        //HEAPIFY
                        priorityQueue = priorityQueue.heapify(priorityQueue);

                        //reset the iterator
                        iterator = null;
                        iterator = set.iterator();
                    }
                }
            }
            if (rocketNeverReachedTarget+rocketMissed+rocketBlewUp+successfulLaunches >= MAX_LAUNCHES) {
                break;
            }
//            System.out.println("--------------------END IS HERE--------------------");
        }
        this.endEvaluation();
    }

    public void initScheduler(double realTime, T input, int port) {
        //set the input to the first machine
        Pair<Atomic> networkPair = hashMap.get(initialNetworkPair);
        Atomic model = (Atomic) networkPair.getModel();

        int inputModelState = (int) input;
//        model.internalStates.set(port, (inputModelState + (int)input));

        //schedule an internal event for the first machine
        Time initTime = new Time(realTime, 0);
        ExternalEvent<Integer> initEvent = new ExternalEvent<>(inputModelState);

        //create initial nodes here
        if (counter == 0) {
            priorityQueue = new Node(initTime, networkPair, initEvent);//(TIME->real, discrete), (PAIR->model, port), (EVENT->input)
        } else {
            priorityQueue.addNode(initTime, networkPair, initEvent);//(TIME->real, discrete), (PAIR->model, port), (EVENT->input)

        }
        ++counter;
    }


    public Network(NetworkBuilder networkBuilder) {
        rocketBlewUp = 0;
        rocketMissed = 0;
        rocketNeverReachedTarget = 0;
        successfulLaunches = 0;
        counter = 0;
        MAX_INPUTS_VAL = 1;
        MAX_OUTPUTS_VAL = 1;

        internalStates = new ArrayList<>();
        for (int i = 0; i < MAX_INPUTS_VAL; i++) {
            internalStates.add(null);
        }
        outputs = new ArrayList<>();
        for (int i = 0; i < MAX_INPUTS_VAL; i++) {
            outputs.add(null);
        }

        timeConstant = 1;

        networkArrayList = networkBuilder.networks;
        missionControlArrayList = networkBuilder.missionControls;
        boosterFiringCheckArrayList = networkBuilder.boosterFiringChecks;
        lunchPadSetUpArrayList = networkBuilder.lunchPadSetUps;
        fuelCheckArrayList = networkBuilder.fuelChecks;
        valveCheckArrayList = networkBuilder.valveChecks;
        addPayloadArrayList = networkBuilder.addPayloads;
        launchArrayList = networkBuilder.launches;

        hashMap = networkBuilder.hm;
        initialNetworkPair = new Pair<Atomic>(this, 0);

        hashMap.put(initialNetworkPair, new Pair<Atomic>(missionControlArrayList.get(0), 0));
        set = hashMap.entrySet();
        iterator = set.iterator();
    }

    public static class NetworkBuilder {
        private ArrayList<MissionControl> missionControls;
        private ArrayList<BoosterFiringCheck> boosterFiringChecks;
        private ArrayList<LunchPadSetUp> lunchPadSetUps;
        private ArrayList<FuelCheck> fuelChecks;
        private ArrayList<ValveCheck> valveChecks;
        private ArrayList<AddPayload> addPayloads;
        private ArrayList<Launch> launches;
        private ArrayList<Network> networks;

        private HashMap<Pair, Pair> hm;

        public NetworkBuilder () {

            missionControls = new ArrayList<>();
            boosterFiringChecks = new ArrayList<>();
            lunchPadSetUps = new ArrayList<>();
            fuelChecks = new ArrayList<>();
            valveChecks = new ArrayList<>();
            addPayloads = new ArrayList<>();
            launches = new ArrayList<>();
            networks = new ArrayList<>();

            hm = new HashMap<>();
        }

        public NetworkBuilder hm(HashMap hm) {
            this.hm = hm;
            return this;
        }

        public NetworkBuilder missionControls(ArrayList<MissionControl> missionControls) {
            this.missionControls = missionControls;
            return this;
        }

        public NetworkBuilder boosterFiringChecks(ArrayList<BoosterFiringCheck> boosterFiringChecks) {
            this.boosterFiringChecks = boosterFiringChecks;
            return this;
        }

        public NetworkBuilder lunchPadSetUps(ArrayList<LunchPadSetUp> lunchPadSetUps) {
            this.lunchPadSetUps = lunchPadSetUps;
            return this;
        }

        public NetworkBuilder fuelChecks(ArrayList<FuelCheck> fuelChecks) {
            this.fuelChecks = fuelChecks;
            return this;
        }

        public NetworkBuilder valveChecks(ArrayList<ValveCheck> valveChecks) {
            this.valveChecks = valveChecks;
            return this;
        }

        public NetworkBuilder addPayloads(ArrayList<AddPayload> addPayloads) {
            this.addPayloads = addPayloads;
            return this;
        }

        public NetworkBuilder launches(ArrayList<Launch> launches) {
            this.launches = launches;
            return this;
        }

        public NetworkBuilder networks(ArrayList<Network> networks) {
            this.networks = networks;
            return this;
        }

        public Network build() {
            return new Network(this);
        }
    }

    @Override
    public String lambda() {
        return null;
    }

    @Override
    public String deltaExternal(ArrayList b, int index, double e) {
        return null;
    }

    @Override
    public String deltaConfluent(ArrayList b, int index, double e) {
        return null;
    }

    @Override
    public String deltaInternal() {
        return null;
    }
}
