public class Pair<T> {
    private T model;
    private int port;

    public Pair(T m, int p) {
        model = m;
        port = p;
    }

    public int getPort() {
        return port;
    }

    public T getModel() {
        return model;
    }
}