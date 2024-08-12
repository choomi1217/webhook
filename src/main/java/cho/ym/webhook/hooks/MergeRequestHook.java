package cho.ym.webhook.hooks;

public class MergeRequestHook implements Hook {
    public MergeRequestHook() {
        super();
    }

    @Override
    public void execute(String json) {
        System.out.println(json);
    }

    @Override
    public boolean isSupport(String header) {
        return header.equals("Merge Request Hook");
    }

}
