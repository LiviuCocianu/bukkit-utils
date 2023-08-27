package io.github.idoomful.bukkitutils.object;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteDatabase {
    private Connection con = null;
    private final JavaPlugin plugin;

    public SQLiteDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Connection getConnection() {
        return con;
    }

    public boolean isConnectionActive() {
        try {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setupConnection(String databaseName) throws SQLException, IOException {
        final File dataFile = new File(plugin.getDataFolder() + File.separator + databaseName + ".db");

        if(!dataFile.exists()) {
            boolean made = dataFile.createNewFile();
            if(!made)
                Bukkit.getLogger().warning("Couldn't set up SQLite connection: cannot create '"
                        + plugin.getDataFolder() + File.separator + databaseName + ".db'");
        }

        if(!isConnectionActive())
            con = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + databaseName + ".db");
    }

    public void setupTable(String tableName, String variables) throws SQLException {
        try(
            PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + tableName + "` (" + variables + ")")
        ) {
            ps.execute();
        }
    }

    public void execute(String statement, Object... args) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(statement)) {
            if (args.length > 0) {
                for (int i = 1; i <= args.length; i++) {
                    ps.setObject(i, args[i - 1]);
                }
            }

            ps.execute();
        }
    }

    public <T> T queryOne(String statement, Object... args) throws SQLException {
        try(PreparedStatement ps = con.prepareStatement(statement)) {
            if(args.length > 0) {
                for(int i = 1; i <= args.length; i++) {
                    ps.setObject(i, args[i - 1]);
                }
            }

            final ResultSet rs = ps.executeQuery();
            if(rs.next()) return (T) rs.getObject(1);
        }

        return null;
    }

    public <T> List<T> queryMore(String statement, Object... args) throws SQLException {
        final List<T> output = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(statement)) {
            if (args.length > 0) {
                for (int i = 1; i <= args.length; i++) {
                    ps.setObject(i, args[i - 1]);
                }
            }

            final ResultSet rs = ps.executeQuery();
            while (rs.next()) output.add((T) rs.getObject(1));
        }

        return output;
    }

    public List<SQLRow> queryMoreColumns(String statement, String columns, Object... args) throws SQLException {
        final List<SQLRow> output = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(statement)) {
            if (args.length > 0) {
                for (int i = 1; i <= args.length; i++) {
                    ps.setObject(i, args[i - 1]);
                }
            }

            final ResultSet rs = ps.executeQuery();
            final String[] cols = columns.replace(" ", "").split(",");

            while(rs.next()) {
                final SQLRow row = new SQLRow();
                for(String col : cols) row.setColumn(col, rs.getObject(col));
                output.add(row);
            }
        }

        return output;
    }

    public static class SQLRow {
        private final Map<String, Object> columns;

        public SQLRow() {
            columns = new HashMap<>();
        }

        public Object getColumn(String name) {
            return columns.get(name);
        }

        public String getColumnString(String name) {
            return (String) columns.get(name);
        }

        public Long getColumnLong(String name) {
            return (Long) columns.get(name);
        }

        public Double getColumnDouble(String name) {
            return (Double) columns.get(name);
        }

        public Float getColumnFloat(String name) {
            return (Float) columns.get(name);
        }

        public Integer getColumnInt(String name) {
            return (Integer) columns.get(name);
        }

        public Short getColumnShort(String name) {
            return (Short) columns.get(name);
        }

        public Byte getColumnByte(String name) {
            return (Byte) columns.get(name);
        }

        public Boolean getColumnBoolean(String name) {
            return (Boolean) columns.get(name);
        }

        public void setColumn(String name, Object value) {
            columns.put(name, value);
        }
    }
}
