package org.hjujgfg;

import org.hjujgfg.history.DoNotForgetNotification;

import java.util.Collection;

public interface HistoryLoadedHandler {

    void handle(Collection<DoNotForgetNotification> notifications);
}
