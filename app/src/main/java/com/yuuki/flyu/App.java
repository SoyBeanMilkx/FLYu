package com.yuuki.flyu;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.yuuki.flyu.ui.Strings;
import com.yuuki.flyu.utils.PrefHelper;

import io.github.libxposed.service.XposedService;
import io.github.libxposed.service.XposedServiceHelper;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

public class App extends Application implements XposedServiceHelper.OnServiceListener {

    private static final String TAG = Strings.MODULE_TAG;
    private static volatile XposedService sService;
    private static final CopyOnWriteArraySet<ServiceStateListener> sListeners = new CopyOnWriteArraySet<>();

    public interface ServiceStateListener {
        void onServiceStateChanged(XposedService service);
    }

    public static XposedService getService() {
        return sService;
    }

    public static void addServiceStateListener(ServiceStateListener listener, boolean notifyNow) {
        sListeners.add(listener);
        if (notifyNow) {
            listener.onServiceStateChanged(sService);
        }
    }

    public static void removeServiceStateListener(ServiceStateListener listener) {
        sListeners.remove(listener);
    }

    private static void notifyAll(XposedService service) {
        for (ServiceStateListener l : sListeners) {
            l.onServiceStateChanged(service);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        XposedServiceHelper.registerListener(this);
    }

    @Override
    public void onServiceBind(XposedService service) {
        sService = service;
        PrefHelper.syncFromRemote(this, service);
        ensureSystemUiScope(service);
        notifyAll(service);
    }

    @Override
    public void onServiceDied(XposedService service) {
        sService = null;
        notifyAll(null);
    }

    private void ensureSystemUiScope(XposedService service) {
        try {
            List<String> scope = service.getScope();
            if (scope != null && scope.contains(Strings.SYSTEMUI_PACKAGE)) {
                Log.i(TAG, "SystemUI already in scope");
                return;
            }
            Log.w(TAG, "SystemUI not in scope, requesting...");
            service.requestScope(
                    Collections.singletonList(Strings.SYSTEMUI_PACKAGE),
                    new XposedService.OnScopeEventListener() {
                        @Override
                        public void onScopeRequestApproved(List<String> approved) {
                            Log.i(TAG, "Scope approved: " + approved);
                        }

                        @Override
                        public void onScopeRequestFailed(String message) {
                            Log.e(TAG, "Scope request failed: " + message);
                            Toast.makeText(App.this,
                                    Strings.SCOPE_REQUEST_FAILED_TOAST,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );
        } catch (Throwable t) {
            Log.e(TAG, "ensureSystemUiScope failed", t);
        }
    }
}
