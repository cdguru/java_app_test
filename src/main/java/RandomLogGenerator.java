import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RandomLogGenerator {
    private static final Logger logger = Logger.getLogger(RandomLogGenerator.class.getName());
    private static final List<String> infoMessages = new ArrayList<>();
    private static final List<String> warningMessages = new ArrayList<>();
    private static final List<String> criticalMessages = new ArrayList<>();
    private static final Random random = new Random();

    public static void main(String[] args) {
        // Set up a custom logging format to avoid duplicate prefixes
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(java.util.logging.LogRecord lr) {
                return String.format("%1$tF %1$tT %2$s: %3$s%n", lr.getMillis(), lr.getLevel().getLocalizedName(), lr.getMessage());
            }
        });
        logger.setUseParentHandlers(false);  // Disable default console handler
        logger.addHandler(handler);

        // Load log messages from file
        loadLogMessages("/log_messages.txt");

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int messageType = random.nextInt(3); // 0: INFO, 1: WARNING, 2: CRITICAL
                String randomMessage = null;

                switch (messageType) {
                    case 0:
                        if (!infoMessages.isEmpty()) {
                            randomMessage = infoMessages.get(random.nextInt(infoMessages.size()));
                        }
                        break;
                    case 1:
                        if (!warningMessages.isEmpty()) {
                            randomMessage = warningMessages.get(random.nextInt(warningMessages.size()));
                        }
                        break;
                    case 2:
                        if (!criticalMessages.isEmpty()) {
                            randomMessage = criticalMessages.get(random.nextInt(criticalMessages.size()));
                        }
                        break;
                }

                if (randomMessage != null) {
                    if (randomMessage.startsWith("INFO:")) {
                        logger.log(Level.INFO, randomMessage.substring(5));
                    } else if (randomMessage.startsWith("WARNING:")) {
                        logger.log(Level.WARNING, randomMessage.substring(8));
                    } else if (randomMessage.startsWith("ERROR:")) {
                        logger.log(Level.SEVERE, randomMessage.substring(9));
                    }
                }
            }
        }, 0, 10000); // Run every 5 seconds
    }

    private static void loadLogMessages(String resourceName) {
        try (InputStream is = RandomLogGenerator.class.getResourceAsStream(resourceName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("INFO:")) {
                    infoMessages.add(line);
                } else if (line.startsWith("WARNING:")) {
                    warningMessages.add(line);
                } else if (line.startsWith("ERROR:")) {
                    criticalMessages.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
