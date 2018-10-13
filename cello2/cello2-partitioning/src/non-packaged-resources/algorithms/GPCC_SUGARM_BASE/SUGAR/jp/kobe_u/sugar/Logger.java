package jp.kobe_u.sugar;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class Logger {
    private static final long MEGA = 1024*1024;
    public static int verboseLevel = 0;

    public static void print(String message) {
        System.out.print(message);
    }

    public static void println(String message) {
        System.out.println(message);
    }

    public static void info(String message) {
        if (verboseLevel >= 1) {
            println("c " + message);
        }
    }

    public static void fine(String message) {
        if (verboseLevel >= 2) {
            println("c " + message);
        }
    }

    public static void status() {
        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = mbean.getHeapMemoryUsage();
//      long heapInit = heapUsage.getInit() / MEGA;
        long heapUsed = heapUsage.getUsed() / Logger.MEGA;
//      long heapCommitted = heapUsage.getCommitted() / MEGA;
        long heapMax = heapUsage.getMax() / Logger.MEGA;
        MemoryUsage nonHeapUsage = mbean.getNonHeapMemoryUsage();
//      long nonHeapInit = nonHeapUsage.getInit() / MEGA;
        long nonHeapUsed = nonHeapUsage.getUsed() / Logger.MEGA;
//      long nonHeapCommitted = nonHeapUsage.getCommitted() / MEGA;
        long nonHeapMax = nonHeapUsage.getMax() / Logger.MEGA;
        fine(
                "Heap : " + heapUsed + " MiB used (max " + heapMax + " MiB), " +
                "NonHeap : " + nonHeapUsed + " MiB used (max " + nonHeapMax + " MiB)"
        );
    }

}
