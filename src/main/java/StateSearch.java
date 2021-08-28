public class StateSearch extends State {

    protected StateSearch(Server server) {
        super(server);
        System.out.println("wait..\n");
    }

    @Override
    public String onSearch() {
        return "";
    }

    @Override
    public String onFinish() {
        server.changeState(new StateFinish(server));
        return "Done";
    }
}