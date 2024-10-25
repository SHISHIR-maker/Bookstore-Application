package bookstoreapplication; 

//JavaFX Imports

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//File IO Imports

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/*
 *  BOOKSTORE APPLICATION W2023
 *
 *  By Abdullah Arif, Sebastian D'Costa, Abhinav Gupta, Shishir Prakash, and Latheeshan Suthaharan
 */

public class BookStoreApplication extends Application
{
    private final String bookFilePath = "books.txt"; //Filepath for book database
    private final String customerFilePath = "customers.txt"; //Filepath for customer database
    
    private Stage primaryStage; //Object for main window
    private ObservableList<Book> books; //Create observable list to store book database
    private ObservableList<Customer> customers; //Create observable list to store customer database
    
    //Login Screen
    @Override
    public void start(Stage primaryStage)
    {
        //Login screen initialization
        loadCustomers(); //Load customer database
        this.primaryStage = primaryStage; //Set the stage
        
        //Create welcome message label
        Label welcomeMessage = new Label("Welcome to the BookStore App!"); //Create label to welcome the user to the program
        
        //Text Fields
        TextField userField = new TextField(); //Text field for user to enter their username
        PasswordField passField = new PasswordField(); //Text field for user to enter their password
        
        //Login Notification Label
        Label loginFail = new Label(); //Label to notify the customer if login was unsuccessful
        
        //Login Button
        Button loginButton = new Button("Login"); //Create login button for the user to attemp a login
        
        //Create master vertical box containing all window elements
        VBox vbox = new VBox(40, welcomeMessage, new Label("Username:"), userField, new Label("Password:"), passField, loginButton, loginFail); //Create vertical box containing all elements
        vbox.setStyle("-fx-padding: 10"); //Set padding to 10 pixels
        
        //Button Actions
        
        loginButton.setOnAction(event -> //Detect if "Login" button is clicked
        {
            String username = userField.getText(); //Get user input from username text field
            String password = passField.getText(); //Get user input from password text field

            if(username.equals("admin") && password.equals("admin")) //Check if the owner is logging in
                showOwnerStartScreen(primaryStage); //Go to OwnerStartScreen
            else //Check if a customer is logging in
            {
                for(Customer user: customers) //Loop through customer database
                {
                    if(username.equals(user.getUsername()) && password.equals(user.getPassword())) //Check if customer is in database
                    {
                        showCustomerStartScreen(primaryStage, user); //Go to customer's CustomerStartScreen
                        break; //Break for loop (since customer was in database)
                    }

                    //Customer does not exist OR incorrect login credentials were used
                    loginFail.setText("Incorrect username or password! Please try again."); //Print error
                    userField.clear(); //Clear username text field for next login attempt
                    passField.clear(); //Clear password text field for next login attempt
                }
            }
        });
        
        //Create the scene
        Scene scene = new Scene(vbox, 500, 500); //Draw scene with window resolution of 500x500 using master vbox
        primaryStage.setTitle("Bookstore App Login"); //Set window title
        primaryStage.setScene(scene); //Set the scene
        primaryStage.show(); //Show the scene
    }
    
    //OwnerStartScreen
    private void showOwnerStartScreen(Stage primaryStage)
    {
        //Create buttons
        Button booksButton = new Button("Books"); //Create button to take owner to OwnerBooksScreen
        Button customersButton = new Button("Customers"); //Create button to take owner to OwnerCustomersScreen
        Button logoutButton = new Button("Logout"); //Create button to take owner back to login screen
        
        //Create master vertical box containing all window elements
        VBox vbox = new VBox(10, booksButton, customersButton, logoutButton); //Create vertical box containing all buttons
        vbox.setAlignment(Pos.CENTER); //Center the buttons on the screen
        
        //Button Actions
        booksButton.setOnAction(event -> showOwnerBooksScreen(primaryStage)); //Go to OwnerBooksScreen if "Books" button is clicked
        customersButton.setOnAction(event -> showOwnerCustomerScreen(primaryStage)); //Go to OwnerCustomerScreen if "Customers" button is clicked
        logoutButton.setOnAction(event -> start(primaryStage)); //Return owner to login screen

        //Set the scene
        Scene scene = new Scene(vbox, 500, 500); //Draw scene with window resolution of 500x500 using master vbox
        primaryStage.setTitle("Owner-Start Screen"); //Set window title
        primaryStage.setScene(scene); //Set the scene
        primaryStage.show(); //Show the scene
    }
    
    //OwnerBooksScreen
    private void showOwnerBooksScreen(Stage primaryStage)
    {
        loadBooks(); //Refresh book database
        
        //Create table with columns for name and price
        TableView<Book> bookList = new TableView<>(); //Create table to display book database
        TableColumn<Book, String> nameColumn = new TableColumn<>("Name"); //Create book name column
        TableColumn<Book, Float> priceColumn = new TableColumn<>("Price ($)"); //Create book price column
        bookList.getColumns().addAll(nameColumn, priceColumn); //Add columns to table

        //Bind columns to the Book object's properties
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); //Bind name
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price")); //Bind price

        //Populate table using book database
        bookList.setItems(books); //Add book database to the table
        
        //Create "Add Book" section
        TextField bookNameField = new TextField(); //Create text field to enter name of book to be added
        bookNameField.setPromptText("Enter Book Name"); //Set hint text
        
        TextField bookPriceField = new TextField(); //Create text field to enter price of book to be added
        bookPriceField.setPromptText("Enter Book Price"); //Set hint text
        
        Button addButton = new Button("Add"); //Create "Add" button to add the book to the database
        
        HBox addBook = new HBox(); //Create horizontal box
        addBook.getChildren().addAll(bookNameField, bookPriceField, addButton); //Add text fields and "Add" button to horizontal box
        
        //Create horizontal box for "Delete" and "Back" button
        
        Button backButton = new Button("Back"); //Create "Back" button to return owner to OwnerStartScreen
        Button deleteButton = new Button("Delete"); //Create "Delete" button to delete the currently selected book
        
        HBox deleteOrBack = new HBox(backButton, deleteButton); //Create horizontal box containing "Back" and "Delete" button
        
        //Create error notification for adding books
        Label addFail = new Label(); //Label to notify the owner if adding the book was unsuccessful
        
        //Create master vertical box containing all window elements
        VBox vbox = new VBox(5, bookList, addBook, deleteOrBack, addFail); //Create vertical box containing all elements
        vbox.setStyle("-fx-padding: 10"); //Set padding to 10 pixels
        
        //Button Actions
        
        addButton.setOnAction(event -> //Detect if "Add" button is clicked
        {
            String bookName = bookNameField.getText(); //Get book name from book name text field
            float bookPrice; //Variable to store book price to be added
            
            try
            {
                for(Book b: books) //Loop through all books in the database
                {
                    if(bookName.equals(b.getName())) //Check if book already exists in database
                        throw new NumberFormatException(); //Throw exception to show error message
                }
                
                bookPrice = Float.parseFloat(bookPriceField.getText()); //Attempt to convert String to float
                books.add(new Book(bookNameField.getText(), Float.parseFloat(bookPriceField.getText()))); //Add new book using data from text fields
                addFail.setText(""); //Reset error text if book is added successfully
                bookNameField.clear(); //Clear book name text field
                bookPriceField.clear(); //Clear book price text field
            }
            catch(NumberFormatException e) //Exception handling
            {
                addFail.setText("Please enter a valid name/price for the book to be added!"); //Print error
                bookNameField.clear(); //Clear book name text field
                bookPriceField.clear(); //Clear book price text field
            }
        });
        
        backButton.setOnAction(event -> //Detect if "Back" button is clicked
        {
            saveBooks(); //Save book database
            showOwnerStartScreen(primaryStage); //Return to OwnerStartScreen
        });
        
        deleteButton.setOnAction(event -> //Detect if "Delete" button is clicked
        {
            Book selectedBook = bookList.getSelectionModel().getSelectedItem(); //Get book object from selected row

            if (selectedBook != null) //Check if selected row is not empty
                books.remove(selectedBook); //Delete selected book from database if the row is not empty
        });
        
        //Create the scene
        Scene scene = new Scene(vbox, 500, 500); //Draw scene with window resolution of 500x500 using master vbox
        primaryStage.setTitle("Owner-Book Screen"); //Set window title
        primaryStage.setScene(scene); //Set the scene
        primaryStage.show(); //Show the scene
    }
    
    //OwnerCustomerScreen
    private void showOwnerCustomerScreen(Stage primaryStage)
    {
        loadCustomers(); //Refresh customer database
        
        //Create table with columns for username, password, and points
	TableView<Customer> customerList = new TableView<>(); //Create table
	TableColumn<Customer, String> usernameColumn = new TableColumn<>("Username"); //Create username column
	TableColumn<Customer, String> passwordColumn = new TableColumn<>("Password"); //Create password column
        TableColumn<Customer, Integer> pointsColumn = new TableColumn<>("Points"); //Create points column
	customerList.getColumns().addAll(usernameColumn, passwordColumn, pointsColumn); //Add columns to table

        //Bind columns to the Customer object's properties
	usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
	passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
        
        //Populate table using customer database
	customerList.setItems(customers); //Add customer database to the table
        
        //Create "Add Customer" section
	TextField customerUsernameField = new TextField(); //Create text field to enter username of customer to be added
	customerUsernameField.setPromptText("Enter Customer Username"); //Set hint text
        
        TextField customerPasswordField = new TextField(); //Create text field to enter password of customer to be added
	customerPasswordField.setPromptText("Enter Customer Password"); //Set hint text
        
        Button addButton = new Button("Add"); //Create "Add" button to add the customer to the database
        
        HBox addCustomer = new HBox(); //Create horizontal box
	addCustomer.getChildren().addAll(customerUsernameField, customerPasswordField, addButton); //Add text fields and "Add" button to horizontal box
        
        //Create horizontal box for "Delete" and "Back" button
        
        Button backButton = new Button("Back"); //Create "Back" button to return owner to OwnerStartScreen
	Button deleteButton = new Button("Delete"); //Create "Delete" button to delete the currently selected customer
	
	HBox deleteOrBack = new HBox(); //Create horizontal box
	deleteOrBack.getChildren().addAll(backButton, deleteButton); //Add "Back" and "Delete" button to horizontal box
        
        //Create error notification for adding a customer
        Label addFail = new Label(); //Label to notify the owner if adding the user was unsuccessful
        
        //Create master vertical box containing all window elements
        
        VBox vbox = new VBox(customerList, addCustomer, deleteOrBack, addFail); //Create vertical box containing all elements
        vbox.setStyle("-fx-padding: 10"); //Set padding to 10 pixels
        
        //Button Actions
	
	addButton.setOnAction(event -> //Detect if "Add" button is clicked
	{
            String username = customerUsernameField.getText(); //Store username of customer about to be added
            boolean allowAdd = true; //Allow creation by default
            
            if(username.equals("admin")) //Check if owner is trying to add another owner
            {
                allowAdd = false; //Do not allow creation of new user
            } 
            else //Check if customer is alreadt in database
            {
                for(Customer c: customers) //Loop through all customers in the database
                {
                    if(username.equals(c.getUsername())) //Check if book already exists in database
                        allowAdd = false; //Do not allow creation of new customer
                }
            }
            
            if(allowAdd) //Check if added user has a unique username
            {
                customers.add(new Customer(username, customerPasswordField.getText(), 0)); //Add new customer using data from text fields
                customerUsernameField.clear(); //Clear username text field
                customerPasswordField.clear(); //Clear password text field
                addFail.setText("");
            }
            else //Show error message if username was not unique
            {
                customerUsernameField.clear(); //Clear username text field
                customerPasswordField.clear(); //Clear password text field
                addFail.setText("User already exists or attempting to add another owner!"); //Print error
            }
	});
	
	backButton.setOnAction(event -> //Detect if "Back" button is clicked
	{
            saveCustomers(); //Save customer database
            showOwnerStartScreen(primaryStage); //Return to OwnerStartScreen
	});
	
	deleteButton.setOnAction(event -> //Detect if "Delete" button is clicked
	{
            Customer selectedCustomer = customerList.getSelectionModel().getSelectedItem(); //Get customer object from selected row

            if (selectedCustomer != null) //Check if selected row is not empty
                    customers.remove(selectedCustomer); //Delete selected customer from database if the row is not empty
	});
        
        //Create the scene
	Scene scene = new Scene(vbox, 500, 500); //Draw scene with window resolution of 500x500 using master vbox
	primaryStage.setTitle("Owner-Customer Screen"); //Set window title
	primaryStage.setScene(scene); //Set the scene
	primaryStage.show(); //Show the scene
    }
    
    //CustomerStartScreen
    private void showCustomerStartScreen(Stage primaryStage, Customer customer)
    {
        loadBooks(); //Refresh book database
	
        //Create welcome message
        String name = customer.getUsername(); //Store customer username
        Label welcomeMessage = new Label("Welcome " + name + ". You have " + customer.getPoints() + " points. Your status is " + customer.getStatus() + "."); //Show welcome message for customer
        
	//Create table with columns for name and price
	TableView<Book> bookList = new TableView<>(); //Create table
	TableColumn<Book, String> nameColumn = new TableColumn<>("Name"); //Create book name column
	TableColumn<Book, Float> priceColumn = new TableColumn<>("Price ($)"); //Create book price column
        TableColumn checkBox = new TableColumn("Selected"); //Create checkbox column
	bookList.getColumns().addAll(nameColumn, priceColumn, checkBox); //Add columns to table
        
        //Bind columns to the Book object's properties
	nameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); //Bind name
	priceColumn.setCellValueFactory(new PropertyValueFactory<>("price")); //Bind price
        checkBox.setCellValueFactory(new PropertyValueFactory<>("selected")); //Bind checkbox
        
        //Populate table using book database
	bookList.setItems(books); //Add book database to the table
        
	//Create horizontal box for "Buy" "Redeem Points & Buy", and "Logout" button
	
	Button buyButton = new Button("Buy"); //Create "Buy" button to purchase selected books by CAD
	Button redeemButton = new Button("Redeem Points & Buy"); //Create "Redeem Points & Buy" button to purchase selected books by redeeming points
        Button logoutButton = new Button("Logout"); //Create "Logout" button to log the customer out
	
	HBox options = new HBox(); //Create horizontal box
	options.getChildren().addAll(buyButton, redeemButton, logoutButton); //Add "Back" and "Delete" button to horizontal box
	
        //Create label for redeem errors
        Label redeemFail = new Label(); //Label to notify the customer if the redeem was unsuccessful
        
	//Create master vertical box containing all window elements
	
	VBox vbox = new VBox(welcomeMessage, bookList, options, redeemFail); //Create vertical box containing all elements
        vbox.setStyle("-fx-padding: 10"); //Set padding to 10 pixels
        
	//Button Actions
	
	buyButton.setOnAction(event -> //Detect if "Buy" button is clicked
	{
            float totalCost = 0; //Stores total cost of purchase
            
            for(Book b: books)
            {
                if (b.getSelected().isSelected()) //Check if book was selected in the checkbox column
                {
                    totalCost += b.getPrice(); //Add price of selected book to the total cost
                }
            }
            
            Iterator<Book> iterator = books.iterator(); //Create iterator to iterate through book database
            
            while(iterator.hasNext()) //Iterate while there are still books that have not been checked
            {
                Book b = iterator.next(); //Get next book
                
                if (b.getSelected().isSelected()) //Check if book was selected in the checkbox column
                    iterator.remove(); //Remove book from the database
            }
            
            saveBooks(); //Save book database once all removes have been completed
            
            customer.addPoints((int)(totalCost * 10.00)); //Update points
            showCustomerCostScreen(primaryStage, customer, totalCost); //Go to CustomerCostScreen
	});
	
	redeemButton.setOnAction(event -> //Detect if "Redeem Points & Buy" button is clicked
	{
            float totalCost = 0; //Stores total cost of purchase
            
            for(Book b: books)
            {
                if (b.getSelected().isSelected()) //Check if book was selected in the checkbox column
                {
                    totalCost += b.getPrice(); //Add price of selected book to the total cost
                }
            }
            
            int pointsTotal = (int)(totalCost * 100.00); //Variable to store points equivalent of purchase
            
            //Update Points
            if(customer.getPoints() >= pointsTotal) //Check if customer has enough points
            {
                Iterator<Book> iterator = books.iterator(); //Create iterator to iterate through book database
            
                while (iterator.hasNext()) //Iterate while there are still books that have not been checked
                {
                    Book b = iterator.next(); //Get next book

                    if (b.getSelected().isSelected()) //Check if book was selected in the checkbox column
                        iterator.remove(); //Remove book from the database
                }

                saveBooks(); //Save book database once all removes have been completed
                
                customer.removePoints(pointsTotal); //Remove points from customer's account
                totalCost = 0; //Set cost to 0
                saveCustomers(); //Save customer database
                showCustomerCostScreen(primaryStage, customer, totalCost); //Go to CustomerCostScreen (checkout)
            }
            else //Customer does not have enough points
                redeemFail.setText("Not enough points!"); //Print error
	});
	
	logoutButton.setOnAction(event -> start(primaryStage)); //Return user to login screen
	
	//Create the scene
	Scene scene = new Scene(vbox, 500, 500); //Draw scene with window resolution of 500x500 using master vbox
	primaryStage.setTitle("Customer-Start Screen"); //Set window title
	primaryStage.setScene(scene); //Set the scene
	primaryStage.show(); //Show the scene
    }
    
    //CustomerCostScreen
    private void showCustomerCostScreen(Stage primaryStage, Customer customer, float totalCost)
    {
        //Create label for totalCost
        Label totalCostMessage = new Label("Total Cost: " + totalCost);
        
        //Create label for customer points and status
        Label pointsAndStatusMessage = new Label("Points: " + customer.getPoints() + "\tStatus: " + customer.getStatus());
        
        //Create logout button to log the customer out
        Button logoutButton = new Button("Logout");
        
	//Create master vertical box containing all window elements
	
	VBox vbox = new VBox(totalCostMessage, pointsAndStatusMessage, logoutButton); //Create vertical box
        vbox.setStyle("-fx-padding: 10"); //Set padding to 10 pixels

	//Button Actions
	
	logoutButton.setOnAction(event -> //Detect if "Logout" button is clicked
	{
            saveCustomers(); //Save customer database
            start(primaryStage); //Return user to login screen
	});
	
	//Create the scene
	Scene scene = new Scene(vbox, 500, 500); //Draw scene with window resolution of 500x500 using master vbox
	primaryStage.setTitle("Customer-Cost Screen"); //Set window title
	primaryStage.setScene(scene); //Set the scene
	primaryStage.show(); //Show the scene
    }
    
    //Method to load books from text file to program database
    private void loadBooks()
    {
        books = FXCollections.observableArrayList(); //Clear book database to re-read text file
        
        try(BufferedReader reader = new BufferedReader(new FileReader(bookFilePath))) //Create reader
        {
            String line1, line2; //Create empty strings to store book name (line1) and price (line2)
            while ((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null) //Ensure line is not blank in text file
                books.add(new Book(line1, Float.parseFloat(line2))); //Create new book using data from file and add said book to database
        }
        catch(IOException e) //Exception handling
        {
            System.out.println("Error reading book database: " + e.getMessage()); //Print error if text file could not be read
            System.exit(1); //Exit program
        }
    }
    
    //Method to save books from database to text file
    private void saveBooks()
    {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(bookFilePath, false))) //Create writer in overwrite mode (not append)
        {
            for(Book book: books) //Loop through book database
            {
                writer.write(book.getName()); //Write book name to text file
                writer.newLine(); //Go to next line
                
                writer.write("" + book.getPrice()); //Write book price to text file
                writer.newLine(); //Go to next line
            }
        }
        catch(IOException e) //Exception handling
        {
            System.out.println("Error saving book database: " + e.getMessage()); //Print error if text file could not be written to
            System.exit(1); //Exit program
        }
    }
    
    //Method to load customers from text file to program database
    private void loadCustomers()
    {
        customers = FXCollections.observableArrayList(); //Clear customer database to re-read text file
        
        try(BufferedReader reader = new BufferedReader(new FileReader(customerFilePath))) //Create reader
        {
            String line1, line2, line3; //Create empty strings to store customer username (line1), password (line2), and price (line3)
            while((line1 = reader.readLine()) != null && (line2 = reader.readLine()) != null && (line3 = reader.readLine()) != null) //Ensure line is not blank in text file
                customers.add(new Customer(line1, line2, Integer.parseInt(line3))); //Create new customer using data from file and add said customer to database
        }
        catch(IOException e) //Exception handling
        {
            System.out.println("Error reading user database: " + e.getMessage()); //Print error if text file could not be read
            System.exit(1); //Exit program
        }
    }
    
    //Method to save customers from database to text file
    private void saveCustomers()
    {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(customerFilePath, false))) //Create writer in overwrite mode (not append)
        {
            for(Customer user: customers) //Loop through customer database
            {
                writer.write(user.getUsername()); //Write customer username to text file
                writer.newLine(); //Go to next line
                
                writer.write(user.getPassword()); //Write customer password to text file
                writer.newLine(); //Go to next line
                
                writer.write("" + user.getPoints()); //Write customer points to text file
                writer.newLine(); //Go to next line
            }
        }
        catch (IOException e) //Exception handling
        {
            System.out.println("Error saving customer database: " + e.getMessage()); //Print error if text file could not be written to
            System.exit(1); //Exit program
        }
    }
    
    public static void main(String[] args)
    {
        launch(args); //Go to start method (login screen)
    }
}