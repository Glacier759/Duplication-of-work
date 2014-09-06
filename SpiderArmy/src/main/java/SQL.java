/**
 * Created by glacier on 14-9-1.
 */
import java.sql.*;

import org.apache.commons.codec.digest.DigestUtils;

public class SQL {

    private String DatabaseName = "Filter", TableName, FieldName = "URL";
    private String DatabaseUrl = "jdbc:mysql://222.24.63.100/"; 	//默认为操作本机数据库
    private Connection Conn;

    public void start() {
        setTableName("miercn");
        setDatabaseUrl("jdbc:mysql://127.0.0.1:3306/");
        ConnectionDB("root","linux");
        if ( !isUniqueTableName() )
            creatTable();
    }

    public boolean creatTable() {
        return creatTable(TableName);
    }
    public boolean creatTable( String TableName ) {

        try {
            if ( isUniqueTableName( TableName ) ) {
                return false; 	//存在返回false；
            }
            else {
                String SQL = "CREATE TABLE " + TableName + " (Url CHAR(32) PRIMARY KEY)";
                Conn.prepareStatement(SQL).executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; 	//建表成功返回true；
    }
    public boolean isUniqueTableName() {
        return isUniqueTableName(TableName);
    }
    public boolean isUniqueTableName( String TableName ) {
        String SQL = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='"+DatabaseName+"' AND TABLE_NAME='"+TableName+"'"; 	//检索是否存在改表
        ResultSet RS = null;
        try {
            RS = Conn.prepareStatement(SQL).executeQuery();
            if ( RS.next() ) {
                return true; 	//不存在返回false；
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setDatabaseName( String DatabaseName ) {
        this.DatabaseName = DatabaseName;
    }
    public void setTableName( String TableName ) {
        this.TableName = TableName;
    }
    public void setFieldName ( String FieldName ) {
        this.FieldName = FieldName;
    }
    public void setDatabaseUrl ( String DatabaseUrl ) {
        this.DatabaseUrl = DatabaseUrl;
    }
    public void ConnectionDB(String User, String Password) {  	//与数据库建立连接
        String Driver = "com.mysql.jdbc.Driver";
        DatabaseUrl = DatabaseUrl + DatabaseName;

        try {
            Class.forName(Driver);
            Conn = DriverManager.getConnection(DatabaseUrl, User, Password);
        } catch( SQLException e ) {
            System.err.println("SQLExcepion " + e);
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
    public void ConnectionDB( ) {
        ConnectionDB( "root", "linux" );
    }
    public void CutConnection() { 				//断开数据库连接
        try {
            if ( Conn == null ) {
                return;
            }
        } catch( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                Conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean isUniqueURL( String Data ) { 		//判断数据库中是否包含Data信息 true is have

        String SQL = "SELECT * FROM " + TableName + " WHERE " + FieldName + " = '" + DigestUtils.md5Hex(Data) + "'";
        if ( FieldName == null ) {
            try {
                throw new Exception("Field 不能为 null, 你可以调用setFieldName(String)方法指定 或者在构造函数中增加参数");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        ResultSet RS = null;
        try {
            RS = Conn.prepareStatement(SQL).executeQuery();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            if ( RS.next() ) {
                return true;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertUrl( String Url ) { 		//将Url插入数据库
        if ( isUniqueURL(Url) ) {
            return false;
        }
        String SQL = "INSERT INTO " + TableName + " (" + FieldName + ")values(?)";
        PreparedStatement PS = null;
        try {
            PS = Conn.prepareStatement(SQL);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            PS.setString(1, DigestUtils.md5Hex(Url));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int result = 0;
        try {
            result = PS.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if ( result > 0 )
            return true;
        return false;
    }
}