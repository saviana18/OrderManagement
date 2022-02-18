package org.ordermanagement.dao;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.ordermanagement.connection.ConnectionFactory;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AbstractDAO<T> {

    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());
    private final Class<T> type;

    public AbstractDAO() {
        type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Creeaza un query pentru select
     * @return string cu query-ul creat
     */
    private String createSelectQuery() {
        return "SELECT * FROM `" + type.getSimpleName().toLowerCase() + "`";
    }

    /**
     * Creeaza un query pentru insert
     * @param t - instanta a clasei generice T
     * @return string cu query-ul creat
     */
    private String createInsertQuery(T t) {
        StringBuilder insertString = new StringBuilder();
        insertString.append("INSERT INTO `");
        insertString.append(type.getSimpleName().toLowerCase()).append("` (");
        boolean flag1 = true;
        boolean flag2 = true;
        for (Field field : type.getDeclaredFields()) {
            if (flag1) {
                flag1 = false;
            } else if(flag2) {
                insertString.append(field.getName());
                flag2 = false;
            } else {
                insertString.append(", ").append(field.getName());
            }
        }
        insertString.append(") VALUES (");

        try {
            flag1 = true;
            flag2 = true;
            for (Field field : type.getDeclaredFields()) {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
                Method method = propertyDescriptor.getReadMethod();
                Object value = method.invoke(t);
                if (flag1) {
                    flag1 = false;
                } else if(flag2) {
                    insertString.append("'").append(value.toString()).append("'");
                    flag2 = false;
                } else {
                    insertString.append(", '").append(value.toString()).append("'");
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | IntrospectionException | InvocationTargetException e) {
            e.printStackTrace();
        }
        insertString.append(")");
        System.out.println(insertString.toString());
        return insertString.toString();
    }

    /**
     * Creeaza un query pentru delete
     * @param id - id-ul obiectului care se sterge
     * @return string cu query-ul creat
     */
    private String createDeleteQuery(int id) {
        return "DELETE FROM " + type.getSimpleName() + " WHERE " + type.getDeclaredFields()[0].getName() + "=" + id;
    }

    /**
     * Creeaza un query pentru update
     * @param t - instanta a clasei generice T
     * @param id - id-ul obiectului care se udpateaza
     * @return string cu query-ul creat
     */
    private String createUpdateQuery(T t, int id) {
        StringBuilder updateString = new StringBuilder();
        updateString.append("UPDATE ").append(type.getSimpleName()).append(" SET ");
        try {
            String auxString = "";
            boolean flag = true;
            for (Field field : type.getDeclaredFields()) {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
                Method method = propertyDescriptor.getReadMethod();
                Object value = method.invoke(t);
                if (flag) {
                    updateString.append(field.getName()).append(" = '").append(value.toString()).append("' ");
                    auxString = field.getName() + " = '" + id + "' ";
                    flag = false;
                } else {
                    updateString.append(", ").append(field.getName()).append(" = '").append(value.toString()).append("' ");
                }
            }
            updateString.append(" WHERE ").append(auxString);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            e.printStackTrace();
        }
        System.out.println(updateString.toString());
        return updateString.toString();
    }

    /**
     * Insereaza un obiect din clasa T in baza de date
     * @param t - instanta a clasei T
     */
    public void add(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createInsertQuery(t);
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if(resultSet.next()) {
                resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:add " + e.getMessage());
        } finally {
            assert statement != null;
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Sterge un obiect din clasa T din baza de date
     * @param id - id-ul obiectului care se sterge
     */
    public void remove(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createDeleteQuery(id);
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:remove " + e.getMessage());
        } finally {
            assert statement != null;
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

    }

    /**
     * Updateaza un obiect din clasa T in baza de date
     * @param t - instanta a clasei T
     * @param id - id-ul obiectului care este updatat
     */
    public void edit(T t, int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createUpdateQuery(t, id);
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:edit " + e.getMessage());
        } finally {
            assert statement != null;
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Creeaza obiectele prin metode reflexive
     * @param resultSet - obtinut in urma efectuarii query-ului
     * @return lista de obiecte create
     */
    private ArrayList<T> createObjects(ResultSet resultSet) {
        ArrayList<T> list = new ArrayList<T>();
        try {
            while (resultSet.next()) {
                T instance = type.newInstance();

                for (Field field : type.getDeclaredFields()) {
                    Object value = resultSet.getObject(field.getName());
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | SecurityException | SQLException | IntrospectionException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Se conecteaza la baza de date si apeleaza metoda care creeaza obiectele
     * @return lista cu obiecte ale clasei T
     */
    public ArrayList<T> viewAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery();
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "AbstractDAO:viewAll " + e.getMessage());
        } finally {
            assert resultSet != null;
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    /**
     * Genereaza header-ul tabelului in mod reflexiv
     * @return tabelul de tip TableView specific JavaFX
     * @throws SQLException in cazul in care nu se poate face conexiunea la baza de date
     */
    public TableView<T> setHeader() throws SQLException {
        Connection connection = ConnectionFactory.getConnection();
        String query = createSelectQuery();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery(query);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        System.out.println(query);

        TableView<T> tableView = new TableView<>();
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        int index = 1;
        while(index <= resultSetMetaData.getColumnCount()) {
            TableColumn<T, ?> tableColumn = new TableColumn<>();
            tableColumn.setCellValueFactory(new PropertyValueFactory<>(resultSetMetaData.getColumnName(index)));
            tableColumn.setText(resultSetMetaData.getColumnName(index));
            tableView.getColumns().add(tableColumn);

            index++;
        }
        ConnectionFactory.close(resultSet);
        ConnectionFactory.close(statement);
        ConnectionFactory.close(connection);
        return tableView;
    }
}
