package org.ordermanagement.bll;

import javafx.scene.control.TableView;
import org.ordermanagement.dao.OrderDAO;
import org.ordermanagement.model.Order;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderBLL {

    private final OrderDAO orderDAO = new OrderDAO();

    /**
     * Gaseste toate comenzile din baza de date
     * @return lista de comenzi
     */
    public ArrayList<Order> viewAllOrders() {
        return orderDAO.viewAll();
    }

    /**
     * Genereaza header-ul tabelului cu comenzi
     * @return tabel cu comenzi cu header-ul din baza de date obtinut folosind o metoda reflexiva
     * @throws SQLException in cazul in care nu se poate conecta la baza de date
     */
    public TableView<Order> setOrderHeader() throws SQLException {
        return orderDAO.setHeader();
    }

    /**
     * Adauga o noua comanda in baza de date
     * @param order - obiect de tipul order pentru a putea fi adaugat in tabel
     */
    public void addOrder(Order order) {
        orderDAO.add(order);
    }
}
