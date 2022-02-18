package org.ordermanagement.bll;

import javafx.scene.control.TableView;
import org.ordermanagement.dao.ProductDAO;
import org.ordermanagement.model.Product;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductBLL {

    private final ProductDAO productDAO = new ProductDAO();

    /**
     * Gaseste toate produsele din baza de date
     * @return lista cu produse
     */
    public ArrayList<Product> viewAllProducts() {
        return productDAO.viewAll();
    }

    /**
     * Genereaza header-ul tabelului cu produse
     * @return tabel cu produse cu header-ul din baza de date obtinut folosind o metoda reflexiva
     * @throws SQLException in cazul in care nu se poate conecta la baza de date
     */
    public TableView<Product> setProductHeader() throws SQLException {
        return productDAO.setHeader();
    }

    /**
     * Adauga un nou produs in tabel
     * @param product - obiect de tipul product pentru a putea fi adaugat in tabel
     */
    public void addProduct(Product product) {
        productDAO.add(product);
    }

    /**
     * Sterge un produs din baza de date
     * @param id - pentru a se putea sterge produsul in functie de id
     */
    public void removeProduct(int id) {
        productDAO.remove(id);
    }

    /**
     * Editeaza datele unui produs din tabel
     * @param product - obiect de tipul produs pentru a sti datele carui produs sunt editate
     * @param id - editam produsul in functie de id
     */
    public void editProduct(Product product, int id) {
        productDAO.edit(product, id);
    }

    public void editStock(int newStock, int id) {
        productDAO.editStock(newStock, id);
    }
}
