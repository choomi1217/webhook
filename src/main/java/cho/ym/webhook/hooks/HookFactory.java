package cho.ym.webhook.hooks;

import java.util.List;

public class HookFactory {

    private static HookFactory instance;
    private final List<Hook> hooks;


    private HookFactory() {
        hooks = List.of(new MergeRequestHook()
                , new PipelineHook()
                , new PushHook());
    }

    public static HookFactory getInstance() {
        if (instance == null) {
            synchronized (HookFactory.class) {
                if (instance == null) {
                    instance = new HookFactory();
                }
            }
        }
        return instance;
    }

    public Hook getHook(String header) {
        return hooks.stream()
                .filter(it -> it.isSupport(header))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid hook class"));
    }

}
