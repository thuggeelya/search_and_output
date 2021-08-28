public abstract class State {

    protected Server server;

    protected State(Server server) {
        this.server = server;
    }

    public abstract String onSearch();
    public abstract String onFinish();
}