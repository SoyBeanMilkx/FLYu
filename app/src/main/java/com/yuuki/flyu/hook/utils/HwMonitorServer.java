package com.yuuki.flyu.hook.utils;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HwMonitorServer {

    private static final String P = "[HwMonitor] ";
    private static final String SOCKET_NAME = "flyu_hw_monitor";

    public interface Callback {
        void onData(HwData data);
    }

    public static class HwData {
        public float cpuTemp;
        public float batTemp;
        public float batPower;
        public boolean batCharging;
        public float memUsed;
        public float memTotal;
    }

    private final Callback callback;
    private final Handler mainHandler;
    private volatile boolean running;
    private Thread thread;
    private LocalServerSocket serverSocket;

    public HwMonitorServer(Callback callback) {
        this.callback = callback;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(this::serverLoop, "HwMonitorServer");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (Exception ignored) {}
        if (thread != null) thread.interrupt();
    }

    private void serverLoop() {
        try {
            serverSocket = new LocalServerSocket(SOCKET_NAME);
            HookLog.i(P + "server listening on @" + SOCKET_NAME);
        } catch (Exception e) {
            HookLog.e(P + "failed to create server socket: " + e.getMessage());
            return;
        }

        while (running) {
            try {
                LocalSocket client = serverSocket.accept();
                HookLog.i(P + "daemon connected");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));

                String line;
                while (running && (line = reader.readLine()) != null) {
                    try {
                        JSONObject json = new JSONObject(line);
                        HwData data = new HwData();
                        data.cpuTemp = (float) json.optDouble("cpu_temp", -1);
                        data.batTemp = (float) json.optDouble("bat_temp", -1);
                        data.batPower = (float) json.optDouble("bat_power", 0);
                        data.batCharging = json.optBoolean("bat_charging", false);
                        data.memUsed = (float) json.optDouble("mem_used", 0);
                        data.memTotal = (float) json.optDouble("mem_total", 0);

                        HookLog.d(P + "data: cpu=" + data.cpuTemp
                                + " bat=" + data.batTemp + " power=" + data.batPower
                                + " mem=" + data.memUsed + "/" + data.memTotal);
                        mainHandler.post(() -> callback.onData(data));
                    } catch (Exception e) {
                        HookLog.w(P + "parse error: " + e.getMessage());
                    }
                }

                HookLog.w(P + "daemon disconnected");
                client.close();
            } catch (Exception e) {
                if (running) {
                    HookLog.w(P + "accept error: " + e.getMessage());
                }
            }
        }

        try {
            serverSocket.close();
        } catch (Exception ignored) {}
    }
}
