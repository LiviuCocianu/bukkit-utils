package io.github.idoomful.bukkitutils.object;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class SQLiteDatabase {
    private Connection con = null;
    private final JavaPlugin plugin;

    public SQLiteDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isConnectionActive() {
        try {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Connection setupConnection(String databaseName) {
        try {
            if(isConnectionActive()) return con;
            else con = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + databaseName + ".db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public void setupTable(String tableName, String variables) {
        try(
            PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + tableName + "` (" + variables + ")")
        ) {
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void execute(String statement, Object... args) {
        try (PreparedStatement ps = con.prepareStatement(statement)) {
            if (args.length > 0) {
                for (int i = 1; i <= args.length; i++) {
                    ps.setObject(i, args[i - 1]);
                }
            }

            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public <T> T queryOne(String statement, Object... args) {
        try(PreparedStatement ps = con.prepareStatement(statement)) {
            if(args.length > 0) {
                for(int i = 1; i <= args.length; i++) {
                    ps.setObject(i, args[i - 1]);
                }
            }

            ResultSet rs = ps.executeQuery();
            if(rs.next()) return (T) rs.getObject(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public <T> List<T> queryMore(String statement, Object... args) {
        List<T> output = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(statement)) {
            if (args.length > 0) {
                for (int i = 1; i <= args.length; i++) {
                    ps.setObject(i, args[i - 1]);
                }
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) output.add((T) rs.getObject(1));
            return output;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }
}
