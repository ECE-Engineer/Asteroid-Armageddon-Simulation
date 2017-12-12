public class Node<T> {
    private Node left, right;
    private Time time;
    private Pair pair;
    private Event event;
    static Node answer;

    public Time getTime() {
        return time;
    }

    public Pair getPair() {
        return pair;
    }

    public Event getEvent() {
        return event;
    }

    public Node getLeft() {
        return this.left;
    }

    public Node getRight() {
        return this.right;
    }

    public Node getTop() {
        return this;
    }

    public int size() {
        return size(this);
    }

    public Node(Time time, Pair pair, Event event) {
        this.time = time;
        this.pair = pair;
        this.event = event;
    }

    public int size(Node node) {
        if (node == null)
            return 0;
        else
            return(size(node.left) + 1 + size(node.right));
    }


    public void addNode(Time time, Pair pair, Event event) {
        if (time.getRealTime() < this.time.getRealTime()
                || ((Math.abs(time.getRealTime()-this.time.getRealTime()) < 0.01) && time.getDiscreteTime() < this.time.getDiscreteTime())
                || ((Math.abs(time.getRealTime()-this.time.getRealTime()) < 0.01) && time.getDiscreteTime() == this.time.getDiscreteTime() && pair.getPort() < this.pair.getPort())) {
            if (this.left != null) {
                this.left.addNode(time, pair, event);
            } else {
                this.left = new Node(time, pair, event);
            }
        } else if (time.getRealTime() > this.time.getRealTime()
                || ((Math.abs(time.getRealTime()-this.time.getRealTime()) < 0.01) && time.getDiscreteTime() > this.time.getDiscreteTime())
                || ((Math.abs(time.getRealTime()-this.time.getRealTime()) < 0.01) && time.getDiscreteTime() == this.time.getDiscreteTime() && pair.getPort() > this.pair.getPort())) {
            if (this.right != null) {
                this.right.addNode(time, pair, event);
            } else {
                this.right = new Node(time, pair, event);
            }
        } else {
            if (this.right != null) {
                this.right.addNode(time, pair, event);
            } else {
                this.right = new Node(time, pair, event);
            }
        }
    }

    public void displayTree() {
        if (this.size() <= 0) {
            return;
        }
        displayTree(this, "root");
    }

    public void displayTree(Node node, String s) {
        if (node == null) {
            return;
        }
        displayTree(node.left, "LEFT");
        System.out.println(s + "\t---->" + node.time.getRealTime() + " " + node.time.getDiscreteTime() + " " + node.event.event + " " + node.pair.getModel() + " " + node.pair.getPort());
        displayTree(node.right, "RIGHT");
    }

    public void displayTreeOnLine() {
        if (this.size() <= 0) {
            return;
        }
        displayTreeOnLine(this, "root");
    }

    public void displayTreeOnLine(Node node, String s) {
        if (node == null) {
            return;
        }
        displayTreeOnLine(node.left, "LEFT");
        System.out.print(s + "\t---->" + node.time.getRealTime() + " " + node.time.getDiscreteTime() + " " + node.event.event + " " + node.pair.getModel() + " " + node.pair.getPort() + "-------------------------");
        displayTreeOnLine(node.right, "RIGHT");
    }

    public Node remove(Node t, Time time, Pair pair, Event event) {
        if( t == null ) {// Item not found; do nothing
            return t;
        }
        if (time.getRealTime() < t.time.getRealTime()
                || ((Math.abs(time.getRealTime()-t.time.getRealTime()) < 0.01) && time.getDiscreteTime() < t.time.getDiscreteTime())
                || ((Math.abs(time.getRealTime()-t.time.getRealTime()) < 0.01) && time.getDiscreteTime() == t.time.getDiscreteTime() && pair.getPort() < t.pair.getPort())) {
            t.left = remove(t.left, time, pair, event);
        } else if (time.getRealTime() > this.time.getRealTime()
                || ((Math.abs(time.getRealTime()-t.time.getRealTime()) < 0.01) && time.getDiscreteTime() > t.time.getDiscreteTime())
                || ((Math.abs(time.getRealTime()-t.time.getRealTime()) < 0.01) && time.getDiscreteTime() == t.time.getDiscreteTime() && pair.getPort() > t.pair.getPort())) {
            t.right = remove(t.right, time, pair, event);
        } else if(t.left != null && t.right != null) {// Two children
            t.time = findMin( t.right ).time;
            t.pair = findMin( t.right ).pair;
            t.event = findMin( t.right ).event;
            t.right = remove(t.right, t.time, t.pair, t.event);
        } else {
            t = (t.left != null) ? t.left : t.right;
        }
        return t;
    }

    private Node findMin( Node t ) {
        if( t == null )
            return null;
        else if( t.left == null )
            return t;
        return findMin( t.left );
    }

    public Node heapify(Node root) {
        if (root==null) {
            return null;
        } else if (root.size() <= 1) {
            return root;
        }

        Node minNode = findMin(root);
        Time nodeTime = minNode.time;
        Pair nodePair = minNode.pair;
        Event nodeEvent = minNode.event;

        if ((Math.abs(minNode.time.getRealTime()-root.time.getRealTime()) < 0.01) && minNode.time.getDiscreteTime()==root.time.getDiscreteTime()
                && minNode.pair.getModel()==root.pair.getModel() && minNode.pair.getPort()==root.pair.getPort()
                && (((minNode.event.getEvent() instanceof InternalEvent) && (root.event.getEvent() instanceof InternalEvent))
                || ((minNode.event.getEvent() instanceof ExternalEvent) && (root.event.getEvent() instanceof ExternalEvent)))) {
            return root;
        } else {
            Node newRoot = remove(root, nodeTime, nodePair, nodeEvent);
            answer = new Node(nodeTime, nodePair, nodeEvent);
            answer.right = newRoot;
            return answer;
        }
    }

        public boolean containsNode(Node node, Time time, Pair pair, Event event) {
        if (node != null) {
            if ((Math.abs(node.time.getRealTime()-time.getRealTime()) < 0.01) && node.time.getDiscreteTime()==time.getDiscreteTime()
                    && node.pair.getModel()==pair.getModel() && node.pair.getPort()==pair.getPort()
                    && (((node.event.getEvent() instanceof InternalEvent) && (event.getEvent() instanceof InternalEvent))
                    || ((node.event.getEvent() instanceof ExternalEvent) && (event.getEvent() instanceof ExternalEvent)))) {
                return true;
            } else {
                boolean foundNode = containsNode(node.left, time, pair, event);
                if(!foundNode) {
                    foundNode = containsNode(node.right, time, pair, event);
                }
                return foundNode;
            }
        } else {
            return false;
        }
    }

    public boolean containsOppositeEventTypeConfluent(Node node, Time time, Pair pair, Event event) {
        if (node != null) {
            if ((Math.abs(node.time.getRealTime()-time.getRealTime()) < 0.01) && node.time.getDiscreteTime()==time.getDiscreteTime()
                    && node.pair.getModel()==pair.getModel()
                    && (((node.event.getEvent() instanceof InternalEvent) && (event.getEvent() instanceof ExternalEvent))
                    || ((node.event.getEvent() instanceof ExternalEvent) && (event.getEvent() instanceof InternalEvent)))) {
                return true;
            } else {
                boolean foundNode = containsOppositeEventTypeConfluent(node.left, time, pair, event);
                if(!foundNode) {
                    foundNode = containsOppositeEventTypeConfluent(node.right, time, pair, event);
                }
                return foundNode;
            }
        } else {
            return false;
        }
    }

    public boolean containsOppositePortExternalOrInternal(Node node, Time time, Pair pair, Event event) {
        if (node != null) {
            if ((Math.abs(node.time.getRealTime()-time.getRealTime()) < 0.01) && node.time.getDiscreteTime()==time.getDiscreteTime()
                    && node.pair.getModel()==pair.getModel() && node.pair.getPort()!=pair.getPort()
                    && (((node.event.getEvent() instanceof InternalEvent) && (event.getEvent() instanceof InternalEvent))
                    || ((node.event.getEvent() instanceof ExternalEvent) && (event.getEvent() instanceof ExternalEvent)))) {
                return true;
            } else {
                boolean foundNode = containsOppositePortExternalOrInternal(node.left, time, pair, event);
                if(!foundNode) {
                    foundNode = containsOppositePortExternalOrInternal(node.right, time, pair, event);
                }
                return foundNode;
            }
        } else {
            return false;
        }
    }

    public boolean containsSameNodeAtAnyTime(Node node, Time time, Pair pair, Event event) {
        if (node != null) {
            if (node.pair.getModel()==pair.getModel() && node.pair.getPort()!=pair.getPort()
                    && (((node.event.getEvent() instanceof InternalEvent) && (event.getEvent() instanceof InternalEvent))
                    || ((node.event.getEvent() instanceof ExternalEvent) && (event.getEvent() instanceof ExternalEvent)))) {
                return true;
            } else {
                boolean foundNode = containsSameNodeAtAnyTime(node.left, time, pair, event);
                if(!foundNode) {
                    foundNode = containsSameNodeAtAnyTime(node.right, time, pair, event);
                }
                return foundNode;
            }
        } else {
            return false;
        }
    }

    public Node getOppositePortNode(Node node, Time time, Pair pair, Event event) {
        if (node != null) {
            if ((Math.abs(node.time.getRealTime()-time.getRealTime()) < 0.01) && node.time.getDiscreteTime()==time.getDiscreteTime()
                    && node.pair.getModel()==pair.getModel() && node.pair.getPort()!=pair.getPort()
                    && (((node.event.getEvent() instanceof InternalEvent) && (event.getEvent() instanceof InternalEvent))
                    || ((node.event.getEvent() instanceof ExternalEvent) && (event.getEvent() instanceof ExternalEvent)))) {
                return node;
            } else {
                Node foundNode = getOppositePortNode(node.left, time, pair, event);
                if(foundNode == null) {
                    foundNode = getOppositePortNode(node.right, time, pair, event);
                }
                return foundNode;
            }
        } else {
            return null;
        }
    }

    public Node getOppositeEventTypeConfluent(Node node, Time time, Pair pair, Event event) {
        if (node != null) {
            if ((Math.abs(node.time.getRealTime()-time.getRealTime()) < 0.01) && node.time.getDiscreteTime()==time.getDiscreteTime()
                    && node.pair.getModel()==pair.getModel()
                    && (((node.event.getEvent() instanceof InternalEvent) && (event.getEvent() instanceof ExternalEvent))
                    || ((node.event.getEvent() instanceof ExternalEvent) && (event.getEvent() instanceof InternalEvent)))) {
                return node;
            } else {
                Node foundNode = getOppositeEventTypeConfluent(node.left, time, pair, event);
                if(foundNode == null) {
                    foundNode = getOppositeEventTypeConfluent(node.right, time, pair, event);
                }
                return foundNode;
            }
        } else {
            return null;
        }
    }
}
