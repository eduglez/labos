package jsf.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConnector {
    private static MysqlConnector instance=null;

    public synchronized static MysqlConnector getInstance() throws Exception{
        if(instance==null)
            instance=new MysqlConnector();

        return instance;
    }



    private MysqlConnector() throws Exception{
        Class.forName("com.mysql.jdbc.Driver").newInstance();

    }

    public Connection getConnection() throws SQLException
    {
        String ip="";
        String user="";
        String db="";
        String password="";

        return DriverManager.getConnection("jdbc:mysql://"+ip+"/"+db+"?user="+user+"&password="+password);
    }
}
