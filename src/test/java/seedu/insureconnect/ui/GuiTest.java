package seedu.insureconnect.ui;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.parallel.ResourceLock;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Runs GUI assertions on the JavaFX thread and closes test windows between tests.
 * Requires a display; Linux CI supplies one through Xvfb.
 */
@ResourceLock("javafx")
public abstract class GuiTest {

    private static final int TIMEOUT_SECONDS = 15;
    private static boolean isToolkitStarted;

    @BeforeAll
    static synchronized void initializeToolkit() throws Exception {
        if (isToolkitStarted) {
            return;
        }
        startToolkit();
        isToolkitStarted = true;
    }

    /**
     * Starts JavaFX once and keeps it alive between independently created test windows.
     */
    private static void startToolkit() throws Exception {
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            ready.countDown();
        });
        if (!ready.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw new AssertionError("JavaFX toolkit did not start");
        }
    }

    @AfterEach
    void closeTestWindows() throws Exception {
        runOnFxThread(() -> List.copyOf(Window.getWindows()).forEach(Window::hide));
    }

    /**
     * Runs a test action on the application thread and propagates failures to JUnit.
     */
    protected static void runOnFxThread(FxAction action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            action.run();
            return null;
        });
        Platform.runLater(task);
        awaitTask(task);
    }

    /**
     * Waits for completion without hiding assertion failures behind concurrency exceptions.
     */
    private static void awaitTask(FutureTask<Void> task) throws Exception {
        try {
            task.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof Error error) {
                throw error;
            }
            if (e.getCause() instanceof Exception exception) {
                throw exception;
            }
            throw new AssertionError(e.getCause());
        }
    }

    /**
     * Attaches controls to a real scene, creating their CSS and layout-dependent skins.
     */
    protected static Stage show(Parent root) {
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
        root.applyCss();
        root.layout();
        return stage;
    }

    /**
     * Represents an application-thread test action that may throw a checked exception.
     */
    @FunctionalInterface
    protected interface FxAction {
        void run() throws Exception;
    }
}
