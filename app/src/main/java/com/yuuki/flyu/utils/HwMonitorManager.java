package com.yuuki.flyu.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HwMonitorManager {

    private static final String TAG = "[HwMonitor]";
    private static final String APK_BINARY_NAME = "libhw_monitor.so";
    private static final String DEPLOY_DIR = "/data/local/tmp/flyu";
    private static final String DEPLOY_PATH = DEPLOY_DIR + "/hw_monitor";

    private final String sourcePath;

    public HwMonitorManager(Context context) {
        sourcePath = context.getApplicationInfo().nativeLibraryDir + "/" + APK_BINARY_NAME;
    }

    /**
     * 部署二进制文件到 /data/local/tmp/flyu/hw_monitor
     */
    private void deploy() {
        try {
            String cmd = String.format(
                    "mkdir -p %s && cp %s %s && chmod 777 %s",
                    DEPLOY_DIR, sourcePath, DEPLOY_PATH, DEPLOY_PATH
            );
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
            p.waitFor();
            Log.i(TAG, "deployed to " + DEPLOY_PATH);
        } catch (Exception e) {
            Log.e(TAG, "deploy failed", e);
        }
    }

    /**
     * 部署并以 root 后台启动 daemon
     */
    public void start() {
        if (isRunning()) {
            Log.i(TAG, "already running");
            return;
        }
        deploy();
        try {
            String cmd = "nohup " + DEPLOY_PATH + " > /dev/null 2>&1 &";
            Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
            Log.i(TAG, "daemon started");
        } catch (Exception e) {
            Log.e(TAG, "start failed", e);
        }
    }

    /**
     * 停止 daemon 进程
     */
    public void stop() {
        try {
            Runtime.getRuntime().exec(new String[]{
                    "su", "-c", "pkill -f " + DEPLOY_PATH
            });
            Log.i(TAG, "daemon stopped");
        } catch (Exception e) {
            Log.e(TAG, "stop failed", e);
        }
    }

    /**
     * 检测 daemon 是否正在运行
     */
    public boolean isRunning() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{
                    "su", "-c", "pidof hw_monitor"
            });
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = reader.readLine();
            p.waitFor();
            return line != null && !line.trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
