package com.coinport.odin.network;

import com.coinport.odin.network.NetworkAsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarrierTaskSet {
    private Map<String, NetworkAsyncTask> tasks = null;
    private Map<String, Map<String, String>> params = null;
    private Map<String, NetworkRequest> finishedTasks = new HashMap<>();

    private OnPostRenderListener renderListener = null;

    public void setRenderListener(OnPostRenderListener renderListener) {
        this.renderListener = renderListener;
    }

    public BarrierTaskSet(final Map<String, NetworkAsyncTask> tasks, Map<String, Map<String, String>> params) {
        assert(tasks != null && tasks.size() > 0);

        this.tasks = tasks;
        this.params = params;

        for (final String taskName: tasks.keySet()) {
            tasks.get(taskName).setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                @Override
                public void onRender(NetworkRequest s) {
                    finishedTasks.put(taskName, s);
                    if (finishedTasks.size() == tasks.size() && renderListener != null) {
                        renderListener.onRender(finishedTasks);
                    }
                }
            });
        }
    }

    public void execute() {
        for (String taskName: tasks.keySet()) {
            if (params != null && params.containsKey(taskName))
                tasks.get(taskName).execute(params.get(taskName));
            else
                tasks.get(taskName).execute();
        }
    }

    public interface OnPostRenderListener {
        public void onRender(Map<String, NetworkRequest> s);
    }
}