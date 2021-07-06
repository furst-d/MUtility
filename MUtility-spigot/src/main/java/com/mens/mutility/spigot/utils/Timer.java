package com.mens.mutility.spigot.utils;

import java.util.TimerTask;
import java.util.function.Consumer;

public class Timer {
    private boolean running;
    private Consumer<Integer> onRunning;
    private Consumer<Integer> onFinish;

    public Timer() {
        running = false;
        onRunning = null;
        onFinish = null;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setOnRunning(Consumer<Integer> onRunning) {
        this.onRunning = onRunning;
    }

    public void setOnFinish(Consumer<Integer> onFinish) {
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
                            onFinish.accept(seconds);
                        }
                        running = false;
                        this.cancel();
                    }
                    if(onRunning != null) {
                        onRunning.accept(seconds);
                    }
                    seconds++;
                } else {
                    this.cancel();
                }
            }
        },0,1000);
    }

    public void stopTimer() {
        running = false;
    }
}
