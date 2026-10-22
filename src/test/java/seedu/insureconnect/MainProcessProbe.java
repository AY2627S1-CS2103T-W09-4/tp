package seedu.insureconnect;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Launches the real entry point and closes its window after verifying startup completed.
 */
public final class MainProcessProbe {
    private MainProcessProbe() {
    }

    /**
     * Runs only in a child JVM because Application.launch owns the JavaFX lifecycle.
     */
    public static void main(String[] args) {
        AtomicBoolean hasClosedWindow = new AtomicBoolean();
        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor(
                Thread.ofPlatform().daemon(true).factory());
        monitor.scheduleAtFixedRate(() -> inspectWhenReady(hasClosedWindow), 0, 100, TimeUnit.MILLISECONDS);
        try {
            Main.main(args);
        } finally {
            monitor.shutdownNow();
        }
    }

    /**
     * Retries until JavaFX is initialized, without accessing controls from a background thread.
     */
    private static void inspectWhenReady(AtomicBoolean hasClosedWindow) {
        try {
            Platform.runLater(() -> inspectAndCloseWindow(hasClosedWindow));
        } catch (IllegalStateException e) {
            // The launcher has not initialized JavaFX yet; the next scheduled check will retry.
        }
    }

    /**
     * Checks the real customer list and sends the normal window-close request.
     */
    private static void inspectAndCloseWindow(AtomicBoolean hasClosedWindow) {
        for (Window window : List.copyOf(Window.getWindows())) {
            if (window instanceof Stage stage && stage.isShowing() && !hasClosedWindow.get()) {
                var list = stage.getScene().lookup("#personListView");
                if (list instanceof ListView<?> customers) {
                    System.out.println("STARTUP_CUSTOMERS=" + customers.getItems().size());
                    hasClosedWindow.set(true);
                    Event.fireEvent(stage, new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                }
            }
        }
    }
}
