    package com.example.finalpro.database;

    /**
     * An example using JDBC
     * This example requires SQLite installed
     * and the driver sqlite-jdbc-3.36.0.3.jar
     * Compilation and execution with the option -cp .:./sqlite-jdbc-3.36.0.3.jar
     */

    import com.example.finalpro.Peerr;
    import org.sqlite.JDBC;

    import java.sql.*;
    import java.util.HashMap;
    import java.util.Properties;


    public class DatabaseConnection {
        // Name of the file with the data
        public static final String database="jdbc:sqlite:finalpro.db";



        /**
         * A connection to the database
         */
        private static Connection conn;


        /**
         * Opening the connection
         */
        public static void connect() throws SQLException {
            conn = new JDBC().connect(DatabaseConnection.database, new Properties());
            System.out.println("Connection OK");
        }

        /**
         * Closing the connection
         */
        public static void close() throws SQLException {
            conn.close();
            System.out.println("Disconnected OK");
        }



        /**
         * delete peer to the database
         * @return is the operation succeed
         */
        public static boolean deletePeer(int port) {
            PreparedStatement stmt = null;
            try {
                stmt = conn.prepareStatement("DELETE FROM peers WHERE port = ?");
                stmt.setInt(1, port);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * Add or update peer to the database
         * @return is the operation succeed
         */
        public static boolean createOrUpdatePeer(Peerr peerr) {
            PreparedStatement stmt = null;
            try{
                if(isPeerExist(peerr.getPortNumber())){
                    stmt = conn.prepareStatement("UPDATE peers SET username = ? AND SET hostname = ? WHERE port = ?");
                    stmt.setString(1, peerr.getUsername());
                    stmt.setString(2, peerr.getHostName());
                    stmt.setInt(3, peerr.getPortNumber());
                }
                else{
                    stmt = conn.prepareStatement("INSERT INTO peers(port, username, hostname) VALUES (?,?,?)");
                    stmt.setInt(1, peerr.getPortNumber());
                    stmt.setString(2, peerr.getUsername());
                    stmt.setString(3, peerr.getHostName());
                }
                    return stmt.executeUpdate() > 0;

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * check if a peer exist
         * @param port
         * @return is operation succeed
         * @throws SQLException
         */
        public static boolean isPeerExist(int port) throws SQLException{
            PreparedStatement stmt = conn.prepareStatement("select exists (select 1 from peers where port = ?)");
            stmt.setInt(1,port );
            ResultSet rs = stmt.executeQuery();


            if ( rs.next() ) {
                return   rs.getInt(1) == 1;
            }

            return false;
        }

        /**
         * Listing the peers in the database
         * @return is operation succeed
         */
        public static HashMap<Integer, Peerr> getPeers() {
            HashMap<Integer, Peerr> listPeers = new HashMap<>();
            Statement stmt  = null;
            try {
                stmt = conn.createStatement();
                ResultSet rs    = stmt.executeQuery("SELECT * FROM peers");
                while (rs.next()){
                    listPeers.put(rs.getInt("port"),
                            new Peerr(
                                    rs.getInt("port"),
                                    rs.getString("hostname"),
                                    rs.getString("username")
                            ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return listPeers;
        }

    }
