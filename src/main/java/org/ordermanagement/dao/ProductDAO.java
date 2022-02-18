package org.ordermanagement.dao;

import org.ordermanagement.connection.ConnectionFactory;
import org.ordermanagement.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductDAO extends AbstractDAO<Product> {

    /**
     * Creeaza un query pentru a edita doar stock-ul unui produs
     * @param newStock intregul care va reprezenta noua valoare a stock-ului
     * @param id id-ul produsului asupra caruia se produce modificarea
     * @return string-ul cu query-ul de update
     */
    private String createUpdateStockQuery(int newStock, int id) {
        return "UPDATE `product` SET stock = '" + newStock + "' WHERE id = " + id;
    }

    /**
     * Creeaza conexiunea cu baza de date si updateaza stock-ul unui produs in functie de id
     * @param newStock intregul care va reprezenta noua valoare a stock-ului
     * @param id id-ul produsului supra caruia se produce modificarea
     */
    public void editStock(int newStock, int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = createUpdateStockQuery(newStock, id);
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            assert statement != null;
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }
}
