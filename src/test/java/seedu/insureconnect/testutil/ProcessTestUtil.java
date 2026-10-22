package seedu.insureconnect.testutil;

import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Runs lifecycle probes in isolated JVMs and collects their JaCoCo execution data.
 */
public final class ProcessTestUtil {
    private ProcessTestUtil() {
    }

    /**
     * Runs a probe with a temporary working directory and a bounded lifetime.
     */
    public static Result run(Class<?> entryPoint, Path directory, String reportName, String... arguments)
            throws Exception {
        Files.createDirectories(directory);
        Path output = directory.resolve("process-output.txt");
        List<String> command = createCommand(entryPoint, reportName, arguments);
        Process process = new ProcessBuilder(command).directory(directory.toFile())
                .redirectErrorStream(true).redirectOutput(output.toFile()).start();
        try {
            if (!process.waitFor(45, TimeUnit.SECONDS)) {
                throw new AssertionError("Lifecycle probe timed out:\n" + Files.readString(output));
            }
            return new Result(process.exitValue(), Files.readString(output));
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * Uses the current JDK and test classpath without relying on shell quoting or PATH.
     */
    private static List<String> createCommand(Class<?> entryPoint, String reportName, String[] arguments)
            throws Exception {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-ea");
        addCoverageAgent(command, reportName);
        command.add("-cp");
        command.add(System.getProperty("test.runtime.classpath", System.getProperty("java.class.path")));
        command.add(entryPoint.getName());
        command.addAll(Arrays.asList(arguments));
        return command;
    }

    /**
     * Gives each child its own coverage file; ordinary IDE runs may have no agent attached.
     */
    private static void addCoverageAgent(List<String> command, String reportName) throws Exception {
        Path reports = Path.of(System.getProperty("test.process.coverage.dir", "build/jacoco")).toAbsolutePath();
        Files.createDirectories(reports);
        ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                .filter(argument -> argument.startsWith("-javaagent:") && argument.contains("jacoco"))
                .findFirst().ifPresent(agent -> command.add(agent.split("=", 2)[0]
                        + "=destfile=" + reports.resolve(reportName + "-process.exec") + ",append=false"));
    }

    /**
     * Captures the process status and diagnostic output for assertions in the parent JVM.
     */
    public record Result(int exitCode, String output) {
    }
}
