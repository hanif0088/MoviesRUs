import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class AddGameDialog extends JDialog implements ActionListener,DocumentListener
{
    public static void main(String[] x)
    {
		try
		{
			Class.forName( "com.mysql.jdbc.Driver" );
			Connection connection = DriverManager.getConnection( "jdbc:mysql://localhost/sys", "root", "root");

			Statement statement = connection.createStatement();
			new AddGameDialog(statement);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

    }
    //--------------------------------------------------------------------------------
	JLabel			titleLabel;
	JLabel			quantityLabel;
	JLabel			dateLabel;
	JLabel			versionLabel;
	JLabel			platformLabel;
	JLabel			genreLabel;

	JTextField		titleField;
	JTextField		quantityField;
	JTextField		dateField;
	JTextField		versionField;

	JComboBox<String> platformSelection, genreSelection;

	String[] genreNames = {"Action","Sport","Fighting","Role-Playing", "Adventure","Racing"};
	String[] platformFilters ={"Xbox 360", "Xbox One", "PS3","PS4", "Wii", "Wii U","PC"};

	JButton         addButton;
	JButton         cancelButton;
	String			title;
	String			quantityString;
	String			date;
	String			version;
	String			platform;
	String			genre;

	Statement 		myStatement;
	ResultSet 		resultSet;

	//====================================================================================
	AddGameDialog(Statement urStatement)
	{
		myStatement = urStatement;

		titleLabel = new JLabel("Game Title:");
		titleLabel.setBounds(100, 20, 200, 20);
		titleField = new JTextField(30);
		titleField.setBounds(300, 20, 280, 20);

		quantityLabel = new JLabel("In Stock Quantity:");
		quantityLabel.setBounds(100, 50, 200, 20);
		quantityField = new JTextField(30);
		quantityField.setBounds(300, 50, 280, 20);

		dateLabel = new JLabel("Released Date: (e.g. YYYY-MM-DD)");
		dateLabel.setBounds(100, 80, 200, 20);
		dateField = new JTextField(30);
		dateField.setBounds(300, 80, 280, 20);

		versionLabel = new JLabel("Game Version:");
		versionLabel.setBounds(100, 110, 200, 20);
		versionField = new JTextField(30);
		versionField.setBounds(300, 110, 280, 20);

		platformLabel = new JLabel("Select a Platform:");
		platformLabel.setBounds(100, 140, 200, 20);
		platformSelection = new JComboBox<String>(platformFilters);
		platformSelection.setBounds(300, 140, 280, 20);

		genreLabel = new JLabel("Select a Genre:");
		genreLabel.setBounds(100, 170, 200, 20);
		genreSelection = new JComboBox<String>(genreNames);
		genreSelection.setBounds(300, 170, 280, 20);

		addButton = new JButton("Add Game");
		addButton.setBounds(240, 260, 100, 30);
		addButton.addActionListener(this);
		addButton.setEnabled(false);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
        cancelButton.setBounds(360, 260, 80, 30);

		titleField.getDocument().addDocumentListener(this);
		quantityField.getDocument().addDocumentListener(this);
        dateField.getDocument().addDocumentListener(this);
        versionField.getDocument().addDocumentListener(this);

		setLayout(null);
		add(titleLabel);
		add(titleField);
		add(quantityLabel);
		add(quantityField);

		add(dateLabel);
		add(dateField);
		add(versionLabel);
		add(versionField);
		add(platformLabel);
		add(platformSelection);

		add(genreLabel);
		add(genreSelection);
		add(addButton);
		add(cancelButton);
		setupMainFrame();
	}
    //========================================================================================================
    public void insertUpdate(DocumentEvent de)
    {
		title = titleField.getText().trim();
		quantityString = quantityField.getText().trim();
		date = dateField.getText().trim();
        version = versionField.getText().trim();

        if( !title.equals("") && !quantityString.equals("") && !date.equals("") && !version.equals("") )
        {
            addButton.setEnabled(true);//enable the add button if all fields aren't empty
        }
    }
    //========================================================================================================
    public void changedUpdate(DocumentEvent de){}
    //=======================================================================================
    public void removeUpdate(DocumentEvent de)
    {
		addButton.setEnabled(true);
		title = titleField.getText().trim();
		quantityString = quantityField.getText().trim();
		date = dateField.getText().trim();
        version = versionField.getText().trim();

        if( title.equals("") || quantityString.equals("") || date.equals("") || version.equals("") )
        {
            addButton.setEnabled(false);//disable the add button if all fields are empty
        }
    }
    //=====================================================================================
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == addButton)
		{
			addGame();
		}
		else if(e.getSource() == cancelButton)
		{
			this.dispose();
		}
	}
	//============================================================================================
	//function to add the game on the database
	void addGame ()
	{
		title = titleField.getText().trim();
		quantityString = quantityField.getText().trim();
		date = dateField.getText().trim();
        version = versionField.getText().trim();

        platform = platformSelection.getSelectedItem().toString();
        genre = genreSelection.getSelectedItem().toString();
        try
        {
			if ( Validate.isQuantityValid(quantityString) == false )//throw exception if it isn't valid
				throw new MyException("Invalid quantity! Numeric digits only (maximum 8 digits)");

			else if ( Validate.isValidDate(date) == false )//throw exception if it isn't valid
					throw new MyException("Invalid date! Date format is YYYY-MM-DD (e.g. 2015-11-28)");

			title = "'"+ title.replaceAll("'", "\\\\'") +"'"; //replace occurrences of ' with \'
			date = "'"+ date +"'";
			version = "'"+ version.replaceAll("'", "\\\\'") +"'";
			platform = "'"+ platform.replaceAll("'", "\\\\'") +"'";
			genre = "'"+ genre.replaceAll("'", "\\\\'") +"'";

			String keyQuery = "SELECT * "+
						      "FROM KeysTable k "+
						      "WHERE k.developer_ID = 'abc123' ";

			resultSet = myStatement.executeQuery(keyQuery);
			resultSet.first();
			int gameID = resultSet.getInt("game_ID"); //run a query to get game ID key from KeysTable
			resultSet.close();

			int newGameID = gameID + 1 ;
			String updateKeyQuery = "UPDATE KeysTable"
						      	 + " SET game_ID = "+ newGameID
						      	 + " WHERE developer_ID = 'abc123' ";
			myStatement.executeUpdate(updateKeyQuery);//first update the new gameID key on KeysTable (to be on save side )

			String gameIDString = "'"+gameID+"'";

			String insertQuery1 = "INSERT INTO items " +
								 "VALUES (" +gameID+ "," +date+ "," +quantityString+ "," + title+" )";
			myStatement.executeUpdate( insertQuery1);//insert the new game in Items table first

			String insertQuery2 = "INSERT INTO games " +
								 "VALUES (" +gameID+ "," +version+ "," +platform+ "," + genre+" )";
			int m = myStatement.executeUpdate( insertQuery2); //then add the new game in Games table

			if( m > 0)
			{
				this.dispose();//dispose the dialog
				JOptionPane.showMessageDialog(this,"New game has been successfully added!");
			}

		}//end try
		catch(MyException myEx)
		{
			JOptionPane.showMessageDialog(this,myEx.getMessage());
		}
		catch ( SQLException sqlException )
		{
			sqlException.printStackTrace();
		}
		catch ( Exception exception )
		{
			exception.printStackTrace();
		}
	}

    //======================================================================================================================
    void setupMainFrame()
    {
        Toolkit tk;
        Dimension d;
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(d.width/2, d.height / 2);
        setLocation(d.width / 5, d.height / 5);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Movies-R-Us");
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setVisible(true);
    }
}//end of class
