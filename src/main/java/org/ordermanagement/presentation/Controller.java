package org.ordermanagement.presentation;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.ordermanagement.App;
import org.ordermanagement.bll.ClientBLL;
import org.ordermanagement.bll.OrderBLL;
import org.ordermanagement.bll.ProductBLL;
import org.ordermanagement.connection.ConnectionFactory;
import org.ordermanagement.dao.AbstractDAO;
import org.ordermanagement.dao.ClientDAO;
import org.ordermanagement.model.Client;
import org.ordermanagement.model.Order;
import org.ordermanagement.model.Product;

public class Controller {

    @FXML private TableView<Client> clientTable = new TableView<>();
    @FXML private TableColumn<Client, Integer> idClientColumn;
    @FXML private TableColumn<Client, String> clientNameColumn;
    @FXML private TableColumn<Client, String> emailColumn;
    @FXML private TableColumn<Client, String> addressColumn;
    @FXML private TextField newClientName;
    @FXML private TextField newClientEmail;
    @FXML private TextField newClientAddress;
    @FXML private TextField newClientId;
    ClientBLL clientBLL = new ClientBLL();

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Integer> idOrderColumn;
    @FXML private TableColumn<Product, Integer> idProductColumn;
    @FXML private TableColumn<Order, Integer> quantityColumn;
    @FXML private TextField newOrderIdClient;
    @FXML private TextField newOrderIdProduct;
    @FXML private TextField newOrderQuantity;
    @FXML private TextArea errorMessagesOrder;
    OrderBLL orderBLL = new OrderBLL();

    @FXML private TableView<Product> productTable = new TableView<>();
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private TableColumn<Product, Integer> priceColumn;
    @FXML private TextField newProductId;
    @FXML private TextField newProductName;
    @FXML private TextField newProductStock;
    @FXML private TextField newProductPrice;
    ProductBLL productBLL = new ProductBLL();

    ObservableList<Client> observableClientList = FXCollections.observableArrayList();
    ObservableList<Order> observableOrderList = FXCollections.observableArrayList();
    ObservableList<Product> observableProductList = FXCollections.observableArrayList();

    int actualStock = 0;
    int newStock = 0;

    @FXML
    private void showClientView() throws IOException {
        App.setRoot("client");
    }

    @FXML
    private void showProductView() throws IOException {
        App.setRoot("product");
    }

    @FXML
    private void showOrderView() throws IOException {
        App.setRoot("order");
    }

    @FXML
    private void addClient() {
        clientTable.getItems().clear();
        clientBLL.addClient(new Client(newClientName.getText(), newClientEmail.getText(), newClientAddress.getText()));
    }

    @FXML
    private void editClient() {
        clientTable.getItems().clear();
        clientBLL.editClient(new Client(Integer.parseInt(newClientId.getText()), newClientName.getText(),
                        newClientEmail.getText(), newClientAddress.getText()), Integer.parseInt(newClientId.getText()));
    }

    @FXML
    private void removeClient() {
        clientTable.getItems().clear();
        clientBLL.removeClient(Integer.parseInt(newClientId.getText()));
    }

    @FXML
    private void viewAllClients() throws SQLException {
        clientTable.getItems().clear();
        observableClientList.addAll(clientBLL.viewAllClients());
        clientTable.getColumns().setAll(clientBLL.setClientHeader().getColumns());
        clientTable.getItems().setAll(observableClientList);
        clientTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void viewAllProducts() throws SQLException {
        productTable.getItems().clear();
        observableProductList.addAll(productBLL.viewAllProducts());
        productTable.getColumns().setAll(productBLL.setProductHeader().getColumns());
        productTable.getItems().setAll(observableProductList);
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void addProduct() {
        productTable.getItems().clear();
        productBLL.addProduct(new Product(newProductName.getText(), Integer.parseInt(newProductStock.getText()),
                Integer.parseInt(newProductPrice.getText())));
    }

    @FXML
    private void editProduct() {
        productTable.getItems().clear();
        productBLL.editProduct(new Product(Integer.parseInt(newProductId.getText()), newProductName.getText(),
                        Integer.parseInt(newProductStock.getText()), Integer.parseInt(newProductPrice.getText())),
                Integer.parseInt(newProductId.getText()));
    }

    @FXML
    private void removeProduct() {
        productTable.getItems().clear();
        productBLL.removeProduct(Integer.parseInt(newProductId.getText()));
    }

    @FXML
    private void addOrder() throws SQLException {
        viewAllProducts();
        viewAllClients();
        boolean clientExists = true;
        boolean productExists = true;

        try {
            actualStock = Integer.parseInt(productTable.getColumns().get(2).getCellObservableValue(Integer.parseInt(
                    newOrderIdProduct.getText()) + 1).getValue().toString());
        } catch (NullPointerException e) {
            errorMessagesOrder.clear();
            errorMessagesOrder.setText("Nonexistent client and product");
        }
        ArrayList<Integer> productIds = new ArrayList<>();
        for (Product item : productTable.getItems()) {
            productIds.add((Integer) productTable.getColumns().get(0).getCellObservableValue(item).getValue());
        }

        ArrayList<Integer> clientIds = new ArrayList<>();
        for(Client item : clientTable.getItems()) {
            clientIds.add((Integer) clientTable.getColumns().get(0).getCellObservableValue(item).getValue());
        }

        for (int id : clientIds) {
            if (id != Integer.parseInt(newOrderIdClient.getText())) {
                errorMessagesOrder.clear();
                errorMessagesOrder.setText("This client doesn't exist!");
                clientExists = false;
            }
        }

        for (int id : productIds) {
            if (id != Integer.parseInt(newOrderIdProduct.getText())) {
                errorMessagesOrder.clear();
                errorMessagesOrder.setText("This product doesn't exist!");
                productExists = false;
            }
        }

        newStock = actualStock - Integer.parseInt(newOrderQuantity.getText());

        if (newStock < 0) {
            newStock = 0;
            errorMessagesOrder.clear();
            errorMessagesOrder.setText("Oh no! Insufficient stock for this specific product. :(");
        }
        else if (newStock != 0 && !clientExists && !productExists){
            errorMessagesOrder.clear();
            orderTable.getItems().clear();
            orderBLL.addOrder(new Order(Integer.parseInt(newOrderIdClient.getText()), Integer.parseInt(
                    newOrderIdProduct.getText()), Integer.parseInt(newOrderQuantity.getText())));
        }
        productBLL.editStock(newStock, Integer.parseInt(newOrderIdProduct.getText()));
    }


    @FXML
    private void viewAllOrders() throws SQLException {
        orderTable.getItems().clear();
        observableOrderList.addAll(orderBLL.viewAllOrders());
        orderTable.getColumns().setAll(orderBLL.setOrderHeader().getColumns());
        orderTable.getItems().setAll(observableOrderList);
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}