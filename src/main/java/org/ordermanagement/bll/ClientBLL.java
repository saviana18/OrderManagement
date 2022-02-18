package org.ordermanagement.bll;

import javafx.scene.control.TableView;
import org.ordermanagement.dao.ClientDAO;
import org.ordermanagement.model.Client;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientBLL {

    private final ClientDAO clientDAO = new ClientDAO();

    /**
     * Gaseste toti clientii din baza de date
     * @return lista de clienti
     */
    public ArrayList<Client> viewAllClients() {
        return clientDAO.viewAll();
    }

    /**
     * Genereaza header-ul tabelului cu clienti
     * @return tabel cu clienti cu header-ul din baza de date obtinut folosind o metoda reflexiva
     * @throws SQLException in cazul in care nu se poate face conexiunea cu baza de date
     */
    public TableView<Client> setClientHeader() throws SQLException {
        return clientDAO.setHeader();
    }

    /**
     * Adauga un nou client in tabel
     * @param client - obiect de tipul client pentru a putea fi adaugat in tabel
     */
    public void addClient(Client client) {
        clientDAO.add(client);
    }

    /**
     * Pentru stergerea unui client din baza de date
     * @param id - stergerea se face in functie de id-ul clientului
     */
    public void removeClient(int id) {
        clientDAO.remove(id);
    }

    /**
     * Pentru editarea datelor unui client
     * @param client - obiect de tipul client pentru a sti datele carui client sunt editate
     * @param id - editam clientul in functie de id
     */
    public void editClient(Client client, int id) {
        clientDAO.edit(client, id);
    }

}
