package com.mens.mutility.bungeecord.utils;

import java.util.TimerTask;
import java.util.function.BiConsumer;

public class Timer {
    private boolean running;
    private BiConsumer<Integer, TimerTask> onRunning;
    private BiConsumer<Integer, TimerTask> onFinish;

    public Timer() {
        running = false;
        onRunning = null;
        onFinish = null;
    }

    public void setOnRunning(BiConsumer<Integer, TimerTask> onRunning) {
        this.onRunning = onRunning;
    }

    public void setOnFinish(BiConsumer<Integer, TimerTask> onFinish) {
        this.onFinish = onFinish;
    }

    public void startTimer(int timeInSec) {
        running = true;
        new java.util.Timer().schedule(new TimerTask(){
            int seconds = 0;
            @Override
            public void run() {
                if(running) {
                    if(seconds == timeInSec) {
                        if(onFinish != null) {
                            onFinish.accept(seconds, this);
                        }
                        running = false;
                        this.cancel();
                    }
                    if(onRunning != null) {
                        onRunning.accept(seconds, this);
                    }
                    seconds++;
                } else {
                    this.cancel();
                }
            }
        },0,1000);
    }
}
