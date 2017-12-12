public class ExternalEvent<T> extends Event{
    ExternalEvent(T event) {
        this.event = event;
    }
}