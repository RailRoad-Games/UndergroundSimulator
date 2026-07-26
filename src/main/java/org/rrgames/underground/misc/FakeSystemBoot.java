package org.rrgames.underground.misc;

public class FakeSystemBoot {

    public static void startService(String serviceName) throws InterruptedException {

        System.out.println("● Starting " + serviceName + " service...");
        Thread.sleep(600);

        System.out.println("○ Checking dependencies...");
        Thread.sleep(800);

        System.out.println("○ Loading kernel modules...");
        Thread.sleep(700);

        System.out.println("○ Mounting virtual filesystem...");
        Thread.sleep(900);

        System.out.println("○ Applying configuration...");
        Thread.sleep(700);

        // Fake progress bar (boot style)
        int total = 40;

        for (int i = 0; i <= total; i++) {
            String bar = buildBar(i, total);
            int percent = (int) ((i / (double) total) * 100);

            System.out.print("\r[" + serviceName + "] initializing " + bar + " " + percent + "%");
            Thread.sleep(90);
        }

        System.out.println("\n");

        System.out.println("● Activating service: " + serviceName);
        Thread.sleep(800);

        System.out.println("✔ " + serviceName + " started successfully.");
        Thread.sleep(500);

        System.out.println("→ systemd: service is now running in background.");
        Thread.sleep(400);

        System.out.println("\n(no errors detected... suspicious 😏)");
    }

    private static String buildBar(int progress, int total) {
        int size = 25;
        int filled = (int) ((progress / (double) total) * size);

        return "[" + "=".repeat(filled) + " ".repeat(size - filled) + "]";
    }
}