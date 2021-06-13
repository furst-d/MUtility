package com.mens.mutility.bungeecord.commands.anketa;

public class Option {
    private final int id;
    private final String option;
    private int numberOfVotes;

    public Option(int id, String option, int numberOfVotes) {
        this.id = id;
        this.option = option;
        this.numberOfVotes = numberOfVotes;
    }

    public int getId() {
        return id;
    }

    public String getOption() {
        return option;
    }

    public int getNumberOfVotes() {
        return numberOfVotes;
    }

    public void setNumberOfVotes(int numberOfVotes) {
        this.numberOfVotes = numberOfVotes;
    }
}
