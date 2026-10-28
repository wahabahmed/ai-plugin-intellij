package com.turbo.dev.plugin.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

@Service(Service.Level.APP)
@State(
        name = "TurboDevSettings",
        storages = @Storage("turbodev.xml")
)
public final class TurboDevSettings implements PersistentStateComponent<TurboDevSettings.State> {

    public static class State {
        public String accessToken;
        public String expiry;
    }

    private State state = new State();

    @Override
    public State getState() {
        if (state == null) {
            state = new State();
        }
        return state;
    }

    @Override
    public void loadState(State state) {
        this.state = state != null ? state : new State();
    }

/*    public static TurboDevSettings getInstance() {
        return com.intellij.openapi.application.ApplicationManager
                .getApplication()
                .getService(TurboDevSettings.class);
    }*/

    public static TurboDevSettings getInstance() {
        return ApplicationManager.getApplication().getService(TurboDevSettings.class);
    }
}
