package org.rrgames.underground.misc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CrashReportLogger {
    private static List<CrashReportEntrance> crashReports = new ArrayList<>();

    public static void logCrashReport(String report) {
        // Hier könnte man die Crash-Report-Informationen in eine Datei schreiben oder
        // an einen Server senden
        System.out.println("Crash Report: " + report);
    }

    public static void logException(Exception e, Thread happenedIn) {
        logException(e, happenedIn, false);
    }

    public static void logException(Exception e, Thread happenedIn, boolean isFatal) {
        // Hier könnte man die Ausnahmeinformationen in eine Datei schreiben oder an
        // einen Server senden
        System.out.println("Exception: " + e.getMessage() + " happend in thread: " + happenedIn.getName()
                + " | Writing in Crash Report...");
        crashReports.add(new CrashReportEntrance(e));
        if (isFatal) {
            buildReport();
        }
    }

    public static void buildReport() {
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("Underground Simulator Crash Report - ").append(System.currentTimeMillis()).append("\n");
        reportBuilder.append("- Do you think Aliens woud have this problem? - Maybe...\n").append(
                "This is a crash report for Underground Simulator, a game developed by RailRoad Games. Below you can find details about the crashes that have occurred during the gameplay.\n");
                reportBuilder.append("---Stack Trace---\n");
        for (CrashReportEntrance entrance : crashReports) {
            reportBuilder.append("Timestamp: ").append(entrance.getTimestamp()).append("\n");
            reportBuilder.append("Exception: ").append(entrance.getReport().toString()).append("\n");
            reportBuilder.append("Stack Trace: \n");
            for (StackTraceElement element : entrance.getReport().getStackTrace()) {
                reportBuilder.append("\t").append(element.toString()).append("\n");
            }
            reportBuilder.append("\n");
        }
        try {

long millis = System.currentTimeMillis();

Instant instant = Instant.ofEpochMilli(millis);

DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("dd.MM.yy-HH:mm:ss")
        .withZone(ZoneId.systemDefault());

String formatted = formatter.format(instant);

            Path logDir = Path.of(System.getProperty("user.home"), ".config", "us", "logs");
            Files.write(Path.of(logDir.toString(), "crash_report_" + formatted + ".txt"), reportBuilder.toString().getBytes(), StandardOpenOption.CREATE_NEW);
            IO.println("Crash report saved to: " + logDir.toString()+File.separator+"crash_report_" + formatted + ".txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //logCrashReport(reportBuilder.toString());
    }

    public static class CrashReportEntrance {
        private Exception report;
        private long timestamp;

        public CrashReportEntrance(Exception report) {
            this.report = report;
            this.timestamp = System.currentTimeMillis();
        }

        public Exception getReport() {
            return report;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
    public static void main(String[] args) {
        // Beispiel für die Verwendung des CrashReportLoggers
        try {
            throw new RuntimeException("Dies ist ein Test-Crash!");
        } catch (Exception e) {
            logException(e, Thread.currentThread(), true);
        }
    }
}