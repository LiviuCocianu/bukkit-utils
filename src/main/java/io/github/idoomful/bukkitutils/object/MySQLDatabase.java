package io.github.idoomful.bukkitutils.object;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.idoomful.bukkitutils.statics.TextUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLDatabase {
    public static class Credentials {
        private final String host, database, username, password;
        private final int port, maxPoolSize;
        private final boolean useSSL;

        public Credentials(String host, int port, String database, String username, String password, boolean useSSL, int maxPoolSize) {
            this.host = host;
            this.port = port;
            this.database = database;
            this.username = username;
            this.password = password;
            this.useSSL = useSSL;
            this.maxPoolSize = maxPoolSize;
        }

        public String getHost() {
            return host;
        }
        public String getDatabase() {
            return database;
        }
        public String getUsername() {
            return username;
        }
        public String getPassword() {
            return password;
        }
        public int getPort() {
            return port;
        }
        public int getMaxPoolSize() {
            return maxPoolSize;
        }
        public boolean usesSSL() {
            return useSSL;
        }
    }

    private static TaskChainFactory taskChainFactory;
    private static HikariDataSource dataSource;

    public MySQLDatabase(JavaPlugin main, Credentials credentials) {
        taskChainFactory = BukkitTaskChainFactory.create(main);

        Logger.getLogger("com.zaxxer.hikari.pool.PoolBase").setLevel(Level.OFF);
        Logger.getLogger("com.zaxxer.hikari.pool.HikariPool").setLevel(Level.OFF);
        Logger.getLogger("com.zaxxer.hikari.HikariDataSource").setLevel(Level.OFF);

        HikariConfig config = new HikariConfig();

        String url = "jdbc:mysql://" +
                credentials.getHost() + ":" +
                credentials.getPort() + "/" +
                credentials.getDatabase();

        try {
            config.setJdbcUrl(url);
            config.setUsername(credentials.getUsername());
            config.setPassword(credentials.getPassword());
            config.setMaximumPoolSize(credentials.getMaxPoolSize());
            config.setConnectionTestQuery("SELECT 1");

            config.setMinimumIdle(0);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(35000);
            config.setMaxLifetime(45000);

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useSSL", credentials.usesSSL());
            dataSource = new HikariDataSource(config);
        } catch(Exception e) {
            main.getLogger().warning(TextUtils.color("Couldn't connect to MySQL, verify the credentials and try again"));
        }
    }

    /**
     * Check if the plugin is connected to the database
     */
    public boolean isConnectionValid() {
        if(dataSource == null) return false;

        try(Connection connection = dataSource.getConnection()) {
            return connection.isValid(0);
        } catch (SQLException e) {
            return false;
        }
    }

    private Connection connect() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Disconnect from the database
     */
    public void disconnect() {
        dataSource.close();
    }

    /**
     * Create a new table in the database, if it doesn't exist
     * @param tableName The name of the new table
     * @param variables The format of the variables that the table will have
     *                  <p/>Example: "name VARCHAR(16), cookies INTEGER DEFAULT 0"
     */
    public void setupTable(String tableName, String variables) {
        try(Connection con = connect();
            PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + tableName + "` (" + variables + ")")
        ) {
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Execute an SQL statement asynchronously.
     * @param statement The SQL statement to be executed
     * @param after A Runnable that will be executed after the statement, synchronously
     * @param args The arguments of the statement
     */
    public void execute(String statement, Runnable after, Object... args) {
        MySQLDatabase.newChain().async(() -> {
            try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(statement)) {
                if (args.length > 0) {
                    for (int i = 1; i <= args.length; i++) {
                        ps.setObject(i, args[i - 1]);
                    }
                }

                ps.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).sync(after::run).execute();
    }

    /**
     * Execute an SQL statement asynchronously. Appends the task to a shared TaskChain.
     * Useful for when you want to run multiple async tasks one after another
     *
     * @param chain The name of the shared chain
     * @param statement The SQL statement to be executed
     * @param after A Runnable that will be executed after the statement, synchronously
     * @param args The arguments of the statement
     */
    public void execute(String chain, String statement, Runnable after, Object... args) {
        MySQLDatabase.newSharedChain(chain).async(() -> {
            try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(statement)) {
                if (args.length > 0) {
                    for (int i = 1; i <= args.length; i++) {
                        ps.setObject(i, args[i - 1]);
                    }
                }

                ps.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).sync(after::run).execute();
    }

    /**
     * Queries one element using an SQL statement
     *
     * @param statement The SQL statement to be executed
     * @param after A Consumer that will be executed after the statement, synchronously, with the result of the query as an argument
     * @param args The arguments of the statement
     * @param <T> The type of the queried data
     */
    public <T> void queryOne(String statement, Consumer<T> after, Object... args) {
        MySQLDatabase.newChain().asyncFirst(() -> {
            try(Connection con = connect(); PreparedStatement ps = con.prepareStatement(statement)) {
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
        }).abortIfNull().syncLast(after::accept).execute();
    }

    /**
     * Queries one element using an SQL statement. Appends the task to a shared TaskChain.
     * Useful for when you want to run multiple async tasks one after another
     *
     * @param chain The name of the shared chain
     * @param statement The SQL statement to be executed
     * @param after A Consumer that will be executed after the statement, synchronously, with the result of the query as an argument
     * @param args The arguments of the statement
     * @param <T> The type of the queried data
     */
    public <T> void queryOne(String chain, String statement, Consumer<T> after, Object... args) {
        MySQLDatabase.newSharedChain(chain).asyncFirst(() -> {
            try(Connection con = connect(); PreparedStatement ps = con.prepareStatement(statement)) {
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
        }).abortIfNull().syncLast(after::accept).execute();
    }

    /**
     * Queries more matches for the SQL statement, if any, and puts them in a list.
     * The returned list may be empty if no matches were found.
     *
     * @param statement The SQL statement to be executed
     * @param after A Consumer that will be executed after the statement, synchronously, with the result of the query as an argument
     * @param args The arguments of the statement
     * @param <T> The type of the queried data
     */
    public <T> void queryMore(String statement, Consumer<List<T>> after, Object... args) {
        MySQLDatabase.newChain().asyncFirst(() -> {
            try(Connection con = connect(); PreparedStatement ps = con.prepareStatement(statement)) {
                if(args.length > 0) {
                    for(int i = 1; i <= args.length; i++) {
                        ps.setObject(i, args[i - 1]);
                    }
                }

                ResultSet rs = ps.executeQuery();
                List<T> output = new ArrayList<>();

                while(rs.next()) output.add((T) rs.getObject(1));
                return output;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return null;
        }).abortIfNull().syncLast(after::accept).execute();
    }

    /**
     * Queries more matches for the SQL statement, if any, and puts them in a list.
     * The returned list may be empty if no matches were found. Appends the task to a shared TaskChain.
     * Useful for when you want to run multiple async tasks one after another
     *
     * @param chain The name of the shared chain
     * @param statement The SQL statement to be executed
     * @param after A Consumer that will be executed after the statement, synchronously, with the result of the query as an argument
     * @param args The arguments of the statement
     * @param <T> The type of the queried data
     */
    public <T> void queryMore(String chain, String statement, Consumer<List<T>> after, Object... args) {
        MySQLDatabase.newSharedChain(chain).asyncFirst(() -> {
            try(Connection con = connect(); PreparedStatement ps = con.prepareStatement(statement)) {
                if(args.length > 0) {
                    for(int i = 1; i <= args.length; i++) {
                        ps.setObject(i, args[i - 1]);
                    }
                }

                ResultSet rs = ps.executeQuery();
                List<T> output = new ArrayList<>();

                while(rs.next()) output.add((T) rs.getObject(1));
                return output;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return null;
        }).abortIfNull().syncLast(after::accept).execute();
    }

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }
    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }
}
