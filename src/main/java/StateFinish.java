public class StateFinish extends State {

    protected StateFinish(Server server) {
        super(server);
    }

    @Override
    public String onSearch() {
        server.changeState(new StateSearch(server));
        return "searching";
    }

    @Override
    public String onFinish() {
        return "";
    }
}
