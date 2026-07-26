package org.rrgames.underground.misc;

public class FakeAptInstaller {

    public static void runFakeInstall(String packageName) throws InterruptedException {

        System.out.println("Reading package lists... Done");
        Thread.sleep(600);

        System.out.println("Building dependency tree...");
        Thread.sleep(800);

        System.out.println("Reading state information... Done");
        Thread.sleep(600);

        System.out.println("\nThe following NEW packages will be installed:");
        System.out.println("  " + packageName);
        Thread.sleep(800);

        System.out.println("\n0 upgraded, 1 newly installed, 0 to remove and 0 not upgraded.");
        Thread.sleep(800);

        System.out.println("\nNeed to get 12.4 MB of archives.");
        System.out.println("After this operation, 42.1 MB of additional disk space will be used.\n");
        Thread.sleep(1000);

        // Progress bar simulation
        int total = 30;

        for (int i = 0; i <= total; i++) {
            String bar = buildBar(i, total);
            int percent = (int) ((i / (double) total) * 100);

            System.out.print("\rInstalling: " + packageName + " "
                    + bar + " " + percent + "%");

            Thread.sleep(120);
        }

        System.out.println("\n\nSetting up " + packageName + " ...");
        Thread.sleep(800);

        System.out.println("Processing triggers for systemd ... Done");
        Thread.sleep(600);

        System.out.println("\n✔ Installation complete.");
        System.out.println("(No actual changes were made 😏)");
    }

    private static String buildBar(int progress, int total) {
        int size = 20;
        int filled = (int) ((progress / (double) total) * size);

        return "[" + "#".repeat(filled) + "-".repeat(size - filled) + "]";
    }
} 
    
