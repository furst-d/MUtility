package com.mens.mutility.bungeecord.commands.mparticle;

import com.mens.mutility.bungeecord.MUtilityBungeeCord;
import com.mens.mutility.bungeecord.database.Database;
import com.mens.mutility.bungeecord.database.DatabaseTables;
import com.mens.mutility.bungeecord.messages.MessageChannelListener;
import com.mens.mutility.bungeecord.requests.ParticleUpdateRequest;
import net.md_5.bungee.api.ProxyServer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MParticle {
    private final Database db;
    private final DatabaseTables tables;

    public MParticle() {
        db = MUtilityBungeeCord.getInstance().getDb();
        tables = new DatabaseTables();
    }

    public String getServerFromId(int id) {
        String server = null;
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("SELECT server FROM " + tables.getMParticleTable() + " WHERE id = ?");
            stm.setInt(1, id);
            ResultSet rs =  stm.executeQuery();
            if(rs.next()) {
                server = rs.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return server;
    }

    public void startSelectedPlace() {
        try {
            if(!db.getCon().isValid(0)) {
                db.openConnection();
            }
            PreparedStatement stm;
            stm = db.getCon().prepareStatement("SELECT id, server FROM " + tables.getMParticleTable() + " WHERE selected = 1 AND place = 1");
            ResultSet rs =  stm.executeQuery();
            while(rs.next()) {
                ParticleUpdateRequest request = new ParticleUpdateRequest(rs.getInt(1), ProxyServer.getInstance().getServerInfo(rs.getString(2)), true);
                ProxyServer.getInstance().getServers().values().forEach(s -> {
                    if(s.getName().equals(request.getServer().getName())) {
                        request.setRunClass(true);
                    }
                });
                MessageChannelListener.particleUpdateRequest.add(request);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
