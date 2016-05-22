import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class AddMovieDialog extends JDialog implements ActionListener, DocumentListener, ListSelectionListener
{
    public static void main(String[] x)
    {
    	try{
    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	Class.forName("com.mysql.jdbc.Driver");
    	//Connection connection = DriverManager.getConnection( "jdbc:mysql://localhost/moviesrus", "root", "Password123");
    	Connection connection = DriverManager.getConnection( "jdbc:mysql://localhost/sys", "root", "root");
    	Statement statement = connection.createStatement();
		new AddMovieDialog(statement);
    	}
    	catch(Exception e)
    	{
    		e.getStackTrace();
    	}
    }

	JLabel movieTitleLabel,
		   quantityLabel,
		   releaseDateLabel,
		   genreLabel,
		   directorLabel,
		   awardLabel,
		   awardYearLabel,
		   sequelLabel,
		   viewAllLabel,
		   searchLabel,
		   addActorsLabel,
		   newActorLabel;

	JTextField movieTitleTF,
	   		   quantityTF,
	   		   releaseDateTF,
	   		   directorTF,
	   		   awardTF,
	   		   awardYearTF,
	   		   sequelTF,
	   		   searchTF,
	   		   newActorTF;

	JComboBox<String> genreSelection;

	String[] genreNames = {"Action","Comedy","Drama","Sci-Fi","Adventure","Thriller","Fantasy","Crime"};

	String movieTitle,
		   releaseDate,
		   genre,
		   director,
		   awardName,
		   predecessorID,
		   errorMessage,
		   insertStatement;

	JButton addActorButton,
			saveButton,
			cancelButton,
			searchButton,
			viewAllButton,
			addNewButton,
			removeActorButton;

	JTable existingActorsTable,
		   actorsInMovieTable;

	DefaultTableModel dtm, dtm2;

	ListSelectionModel lsm, lsm2;

	JScrollPane scrollPane1,
				scrollPane2;

	Hashtable<Integer, String> actorsInMovie,
						       newActorsAdded;

	Statement myStatement;

	Font font;

	int quantityAvailable,
	    awardYear,
	    newMovieID,
	    directorID;

	boolean successfulAdd = true,
			addNewDirector = false;

	AddMovieDialog(Statement urStatement)
	{
		myStatement = urStatement;

		setLayout(null);

		font = new Font("Arial", Font.BOLD,11);

		movieTitleLabel = new JLabel("Movie Title");
		movieTitleLabel.setBounds(40, 20, 150, 20);
		movieTitleLabel.setFont(font);
		add(movieTitleLabel);
		movieTitleTF = new JTextField(20);
		movieTitleTF.setBounds(240, 20, 200, 20);
		movieTitleTF.getDocument().addDocumentListener(this);
		add(movieTitleTF);

		quantityLabel = new JLabel("Quantity Available");
		quantityLabel.setBounds(40, 60, 150, 20);
		quantityLabel.setFont(font);
		add(quantityLabel);
		quantityTF = new JTextField(20);
		quantityTF.setBounds(240, 60, 200, 20);
		quantityTF.getDocument().addDocumentListener(this);
		add(quantityTF);

		releaseDateLabel = new JLabel("Release Date, YYYY-MM-DD");
		releaseDateLabel.setBounds(40, 100, 150, 20);
		releaseDateLabel.setFont(font);
		add(releaseDateLabel);
		releaseDateTF = new JTextField(20);
		releaseDateTF.setBounds(240,100,200,20);
		releaseDateTF.getDocument().addDocumentListener(this);
		add(releaseDateTF);

		genreLabel = new JLabel("Genre");
		genreLabel.setBounds(40, 140, 150, 20);
		genreLabel.setFont(font);
		add(genreLabel);
		genreSelection = new JComboBox<String>(genreNames);
		genreSelection.setBounds(240,140,200,20);
		add(genreSelection);

		directorLabel = new JLabel("Director Name");
		directorLabel.setBounds(500,20,150,20);
		directorLabel.setFont(font);
		add(directorLabel);
		directorTF = new JTextField(20);
		directorTF.setBounds(700,20,200,20);
		directorTF.getDocument().addDocumentListener(this);
		add(directorTF);

		awardLabel = new JLabel("Award (Optional)");
		awardLabel.setBounds(500,60,150,20);
		awardLabel.setFont(font);
		add(awardLabel);
		awardTF = new JTextField(20);
		awardTF.setBounds(700,60,200,20);
		awardTF.getDocument().addDocumentListener(this);
		add(awardTF);

		awardYearLabel = new JLabel("Award Year");
		awardYearLabel.setBounds(500,100,150,20);
		awardYearLabel.setFont(font);
		add(awardYearLabel);
		awardYearTF = new JTextField(20);
		awardYearTF.setBounds(700,100,200,20);
		awardYearTF.setEnabled(false);
		awardYearTF.getDocument().addDocumentListener(this);
		add(awardYearTF);

		sequelLabel = new JLabel("Sequel? Original movie ID in here");
		sequelLabel.setBounds(500, 140, 205, 20);
		sequelLabel.setFont(font);
		add(sequelLabel);
		sequelTF = new JTextField(20);
		sequelTF.setBounds(700,140,200,20);
		add(sequelTF);

		addActorButton = new JButton(">");
		addActorButton.setBounds(450,310,40,30);
		addActorButton.setEnabled(false);
		addActorButton.addActionListener(this);
		add(addActorButton);

		saveButton = new JButton("Add Movie");
		saveButton.setBounds(590, 550, 100, 30);
		saveButton.addActionListener(this);
		saveButton.setEnabled(false);
		add(saveButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setBounds(710, 550, 100, 30);
		cancelButton.addActionListener(this);
		add(cancelButton);

		searchLabel = new JLabel("Search By Actor's Name:");
		searchLabel.setBounds(40,460,205,20);
		searchLabel.setFont(font);
		add(searchLabel);
		searchTF = new JTextField(20);
		searchTF.setBounds(40, 490, 200, 20);
		add(searchTF);
		searchButton = new JButton("Search");
		searchButton.setBounds(260,485,75,30);
		searchButton.addActionListener(this);
		add(searchButton);

		viewAllButton = new JButton("View All");
		viewAllButton.setBounds(345,485,75,30);
		viewAllButton.addActionListener(this);
		add(viewAllButton);

		addActorsLabel = new JLabel("Select Actors from the left to add to the movie");
		addActorsLabel.setBounds(315,190,500,20);
		addActorsLabel.setFont(new Font("Arial", Font.BOLD, 13));
		add(addActorsLabel);

		newActorLabel = new JLabel("Can't find the actor you need? Add them here:");
		newActorLabel.setBounds(500,460,400,20);
		newActorLabel.setFont(font);
		add(newActorLabel);

		newActorTF = new JTextField(20);
		newActorTF.setBounds(500,490, 200, 20);
		newActorTF.getDocument().addDocumentListener(this);
		add(newActorTF);

		addNewButton = new JButton("Add New");
		addNewButton.setBounds(720,485,75,30);
		addNewButton.addActionListener(this);
		addNewButton.setEnabled(false);
		add(addNewButton);

		removeActorButton = new JButton("Remove");
		removeActorButton.setBounds(805,485,75,30);
		removeActorButton.addActionListener(this);
		removeActorButton.setEnabled(false);
		add(removeActorButton);

		actorsInMovie = new Hashtable<Integer, String>();
		newActorsAdded = new Hashtable<Integer, String>();

		buildActorDatabaseTable();
		buildAddActorTable();

        setupMainFrame();

	}// end AddMovieDialog() constructor

//==================================================================================================================

	private void buildActorDatabaseTable()
	{
        dtm = new DefaultTableModel()
        {
			 //Override the method to make all cells non-editable
			 @Override
			 public boolean isCellEditable(int row, int col)
			 {
				 return false;
			 }
		};

		populateTable("SELECT castID AS 'Actor ID Number', actor_name AS 'Name' FROM Actors");
		existingActorsTable = new JTable(dtm);
		existingActorsTable.setFont(new Font("Arial", Font.BOLD,12));
		existingActorsTable.setMinimumSize(new Dimension(10, 10));
		existingActorsTable.setPreferredScrollableViewportSize(new Dimension(420, 60));
		existingActorsTable.setRowHeight(18);
		existingActorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsm=existingActorsTable.getSelectionModel();
        lsm.addListSelectionListener(this);

		scrollPane1 = new  JScrollPane(existingActorsTable);
		scrollPane1.setBounds(40,225,400,225);
		add(scrollPane1);
	}// end buildActorDatabaseTable()

//==================================================================================================================

	private void populateTable(String query)
	{
		try{
			ResultSet myResultSet = myStatement.executeQuery(query);
			if(!myResultSet.next())
			{
				JOptionPane.showMessageDialog(null,"No results found.");
				viewAllButton.doClick(); // show all again
			}

			else
			{
				myResultSet.previous();//move Result set cursor to previous row

				ResultSetMetaData metaData = myResultSet.getMetaData();// process query results
				int numberOfColumns = metaData.getColumnCount();

				Vector<Object> columnNames = new Vector<Object>();// columnNames holds the column names of the query result
				Vector<Object> rows = new Vector<Object>();//rows is a vector of vectors, each vector is a vector of values representing a certain row of the query result

				for ( int i = 1; i <= numberOfColumns; i++ )
				{
					columnNames.addElement(metaData.getColumnLabel(i));
				}

				while ( myResultSet.next() )
				{
					Vector<Object> currentRow = new Vector<Object>();
					for ( int i = 1; i <= numberOfColumns; i++ )
					{
					   currentRow.addElement(myResultSet.getObject(i));
					}
					rows.addElement(currentRow);
				}
				dtm.setDataVector(rows, columnNames);

				myResultSet.close();
			}
		}
		catch(SQLException exception)
		{
			System.out.println( exception.getMessage() +"****");
			exception.printStackTrace();
		}
	} // end populateTable()

//==================================================================================================================

	private void buildAddActorTable()
	{

			String[] columnNames = {"Actor ID Number", "Actor Name"};
			Object[][] data = {};

			dtm2 = new DefaultTableModel(data, columnNames)
							 {
								 //Override the method to make all cells non-editable
								 @Override
								 public boolean isCellEditable(int row, int col)
								 {
									 return false;
								 }
							 };

			actorsInMovieTable = new JTable(dtm2);
			actorsInMovieTable.setFont(new Font("Arial", Font.BOLD,12));
			actorsInMovieTable.setMinimumSize(new Dimension(10, 10));
			actorsInMovieTable.setPreferredScrollableViewportSize(new Dimension(420, 60));
			actorsInMovieTable.setRowHeight(18);
       		lsm2=actorsInMovieTable.getSelectionModel();
       		lsm2.addListSelectionListener(this);

			scrollPane2 = new  JScrollPane(actorsInMovieTable);
			scrollPane2.setBounds(500,225,400,225);
			add(scrollPane2);
	} // end buildAddActorTable()

//==================================================================================================================

	public void changedUpdate(DocumentEvent arg0)
	{}

	public void insertUpdate(DocumentEvent arg0)
	{
		populateFields();

		if (!awardName.equals(""))
			awardYearTF.setEnabled(true);


		if (!movieTitle.equals("") && quantityAvailable > 0 &&
				releaseDate.length() == 10 && !director.equals("") && dtm2.getRowCount() > 0)
		{
			if (awardName.equals("")) // if there's nothing, there's no award year
				saveButton.setEnabled(true);
			else
			{
				if (awardYear < 1800 || awardYear > 2100) // if there is something, make sure the year is valid
					saveButton.setEnabled(false);
				else
					saveButton.setEnabled(true);
			}
		}

		if (newActorTF.getText().trim().length() >= 6)
			addNewButton.setEnabled(true);
	}

	public void removeUpdate(DocumentEvent arg0)
	{
		populateFields();

		if (awardName.equals(""))
		{
			awardYearTF.setEnabled(false);
			awardYearTF.setText("");
		}

		if (movieTitle.equals("") || quantityAvailable <= 0 ||
				releaseDate.length() != 10 || director.equals("") || dtm2.getRowCount() < 1)
		{
			saveButton.setEnabled(false);
		}
		else
		{
			if (awardName.equals(""))
				saveButton.setEnabled(true);
			else if (awardYear < 1800 || awardYear > 2100)
				saveButton.setEnabled(false);
		}

		if (newActorTF.getText().trim().length() < 6)
			addNewButton.setEnabled(false);
	}

//==================================================================================================================

	public void actionPerformed(ActionEvent arg0)
	{
		if (arg0.getSource() == cancelButton)
		{
			if (JOptionPane.OK_OPTION ==
					JOptionPane.showConfirmDialog(this, "Are you sure you want to quit? "
																	       + "Any new info will be discarded.",
																	       	 "Add Movie Cancel",
																	       	 JOptionPane.OK_CANCEL_OPTION))
				dispose();
		}
		else if (arg0.getSource() == saveButton)
			addMovie();

		else if (arg0.getSource() == searchButton)
			doSearch();

		else if (arg0.getSource() == viewAllButton)
		{
			populateTable("SELECT castID AS 'Actor ID Number', actor_name AS 'Name' FROM Actors");
			System.out.println("View All Clicked");
			dtm.fireTableDataChanged();
		}

		else if (arg0.getSource() == addActorButton)
		{
			int actorID = Integer.parseInt((String)dtm.getValueAt(existingActorsTable.getSelectedRow(), 0));
			String actorName = (String)dtm.getValueAt(existingActorsTable.getSelectedRow(), 1);


			dtm2.addRow(new Object[]{actorID, actorName});
				// This takes the selected item in the first table, then
				// adds it to the 2nd

			dtm2.fireTableDataChanged(); // update table

			actorsInMovie.put(actorID, actorName); // add actor to hashtable

			addActorButton.setEnabled(false); // actor cannot be added twice,
											  // will be reenabled once selection changes

			insertUpdate(null); // fire checks for validation including at least 1 actor
		}

		else if (arg0.getSource() == removeActorButton)
		{
			Integer actorID = (Integer)dtm2.getValueAt(actorsInMovieTable.getSelectedRow(), 0);

			if(newActorsAdded.containsKey(actorID))
			{
				System.out.println(newActorsAdded.get(actorID) + " removed from New Actors Hashtable.");
				newActorsAdded.remove(actorID); // if it was a new actor removed, we don't add it to Actors in DB anymore
			}

			actorsInMovie.remove(actorID); // this actor will not be added to the cast in DB

			dtm2.removeRow(actorsInMovieTable.getSelectedRow()); // remove from table model

			dtm2.fireTableDataChanged(); // update table

			removeActorButton.setEnabled(false); // now there are no actors selected, no removing


			addActorButton.setEnabled(true);

			removeUpdate(null); // fire validation checks for removal including at least 1 actor check

			// if the actor selected in table 1 is not in the table on the right after deletion
		}

		else if (arg0.getSource() == addNewButton)
		{
			String actorName = newActorTF.getText().trim();
			actorName = actorName.replaceAll("'", "\\\\'");

			if (checkIfValueExists("actors", "actor_name", actorName))
			{
				JOptionPane.showMessageDialog(this, "Actor already exists in database!");
				searchTF.setText(actorName); // point user to actor in database
				searchButton.doClick();
			}

			else
			{
				int actorID = generateActorID();

				if (actorID == -1)
					new MyException("Error generating new actor ID");
				else
				{

					newActorsAdded.put(actorID, actorName); // add to list of new actors to be added to Actors Table
					actorsInMovie.put(actorID, actorName); // add to list of actors to be added to castedby in DB
					dtm2.addRow(new Object[] {actorID, actorName}); // add to table
					dtm2.fireTableDataChanged();

					newActorTF.setText(""); // clear text field
				}

				insertUpdate(null); // fire the text field checks and validation for > 1 actors in movie
			}
		}
	}

//==================================================================================================================

	private void populateFields()
	{
		movieTitle = movieTitleTF.getText().trim();

		quantityAvailable = Validate.validateNumber(quantityTF.getText().trim());
		awardYear = Validate.validateNumber(awardYearTF.getText().trim());

		releaseDate = releaseDateTF.getText().trim();
		genre = (String)genreSelection.getSelectedItem();
		director = directorTF.getText().trim();
		awardName = awardTF.getText().trim();
		predecessorID = sequelTF.getText().trim();
	}

//==================================================================================================================

	private void doSearch()
	{
		String searchQuery = searchTF.getText().trim();
		searchQuery.replaceAll("'", "\\\\'"); // account for '

		dtm.setRowCount(0); // clear off the table

		populateTable("SELECT castID AS 'Actor ID Number', actor_name AS 'Name' "
				    + "FROM Actors WHERE actor_name LIKE '%" + searchQuery + "%';");
	}

//==================================================================================================================

	private void addMovie()
	{
		populateFields();

		movieTitle = movieTitle.replaceAll("'", "\\\\'");
		director = director.replaceAll("'", "\\\\'");
		awardName = awardName.replaceAll("'", "\\\\'");
		
		if (quantityAvailable < 0)
		{
			successfulAdd = false;
			errorMessage = errorMessage + "     - Invalid data for quantity available \n";
		}

		if (!Validate.isValidDate(releaseDate))
		{
			successfulAdd = false;
			errorMessage = errorMessage + "     - Incorrect release date format \n";
		}

		if(!predecessorID.equals("") && !checkIfValueExists("items", "itemID", predecessorID))
		{
			successfulAdd = false;
			errorMessage = errorMessage + "     - Predecessor movie does not exist in database \n";
		}

		if(!checkIfValueExists("directors", "dir_name", director))
		{
			if(JOptionPane.NO_OPTION ==
							JOptionPane.showConfirmDialog(null, "Director '" + director + "' "
															  + "does not yet exist in the database. "
															  + "Would you like to add him/her?",
															     "Add Director?" , JOptionPane.YES_NO_OPTION))
			{
				successfulAdd = false;
				addNewDirector = false; // if user selected yes the first time and no the second, still needs changed
				errorMessage = errorMessage + "     - Director not in database \n";
			}

			else
				addNewDirector = true; // flag to add director later on
		}

		if(successfulAdd)
		{
			String query;
			ResultSet resultSet;
			// generate new movie key

			errorMessage = "The following errors have occurred: \n";

			try{
				query = "SELECT * "+
			      		"FROM KeysTable k "+
			            "WHERE k.developer_ID = 'abc123' ";

				resultSet = myStatement.executeQuery(query);
				resultSet.first();
				newMovieID = resultSet.getInt("movie_ID"); //run a query to get movie ID key from KeysTable
				directorID = resultSet.getInt("dir_ID"); //run a query to get director ID key from KeysTable
				resultSet.close();

				int updateID = newMovieID + 1 ;
				query = "UPDATE KeysTable"
					  + " SET movie_ID = "+ updateID
					  + " WHERE developer_ID = 'abc123' ";

				myStatement.executeUpdate(query); // update the keys table

				System.out.println("Movie ID updated on keys table");

				// if they chose to add the new director
				if(addNewDirector)
				{
					updateID = directorID + 1 ;
					query = "UPDATE KeysTable"
						  + " SET dir_ID = "+ updateID
						  + " WHERE developer_ID = 'abc123' ";

					myStatement.executeUpdate(query);

					System.out.println("Director ID updated on keys table");

					// put the new director into the table
					query = "INSERT into Directors VALUES ("
							+ directorID + ",'" + director + "');";
					myStatement.executeUpdate(query);

					System.out.println("Director Added");
				}

				else
				{
					query = "SELECT dirID FROM directors WHERE dir_name = '" + director + "';";

					resultSet = myStatement.executeQuery(query);

					resultSet.first();

					directorID = resultSet.getInt("dirID"); // get the director's ID

					resultSet.close();
				}

				query = "INSERT INTO Items VALUES ("
					  + "'" + newMovieID + "',"
					  + "'" + releaseDate + "',"
					        + quantityAvailable + ","
					  + "'" + movieTitle + "');";

				myStatement.executeUpdate(query); // put new item in items table

				System.out.println("Item added");

				query = "INSERT INTO Movies VALUES ("
					  + "'" + newMovieID + "',"
				      + "'" + directorID + "',"
				      + "'" + genre + "');";

				myStatement.executeUpdate(query); // put new movie in movies table

				System.out.println("Movie added");

				Set<Integer> keys = newActorsAdded.keySet(); // get all keys of hashtable
				for(Integer key: keys) // iterate through keys and add actors
				{
					query = "INSERT INTO Actors VALUES ("
							  + key + ","
						      + "'" +  newActorsAdded.get(key) + "');";

					myStatement.executeUpdate(query); // add new actor to actors table

					System.out.println("Actor " + key + " added to actors");
				}

				keys = actorsInMovie.keySet(); // get all keys of hashtable
				for(Integer key: keys) // iterate through keys and add actors
				{
					query = "INSERT INTO Casted_By VALUES ("
							  + "'" + key + "',"
						      + "'" + newMovieID + "');";

					myStatement.executeUpdate(query); // add actor to castedby table

					System.out.println("Actor " + key + " added to castedby");
				}

				if(!predecessorID.equals("")) // if this movie is a sequel to something
				{
					query = "INSERT INTO sequel VALUES ("
						  + "'" + predecessorID + "',"
						  + "'" + newMovieID + "');";

					myStatement.executeUpdate(query); // add movie and predecessor to sequels table
					System.out.println("Sequel added");
				}

				if(!awardName.equals(""))
				{
					query = "INSERT INTO award VALUES ("
							  + "'" + awardName + "',"
							  + "'" + awardYear + "',"
							  + "'" + newMovieID + "');";

						myStatement.executeUpdate(query); // add new award associated with this movie
						System.out.println("Award added");
				}

				JOptionPane.showMessageDialog(this, movieTitle + " sucessfully added to database!");
				dispose();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		else
		{
			JOptionPane.showMessageDialog(this, errorMessage, "Add Movie Error", JOptionPane.ERROR_MESSAGE);
			errorMessage = "";
		}
		
		successfulAdd = true;
	}

//==================================================================================================================

	private int generateActorID()
	{
		int actorID = -1;

		try{
		String keyQuery = "SELECT * "+
			      		  "FROM KeysTable k "+
			              "WHERE k.developer_ID = 'abc123' ";

		ResultSet resultSet = myStatement.executeQuery(keyQuery);
		resultSet.first();
		actorID = resultSet.getInt("cast_ID"); //run a query to get Actor ID key from KeysTable
		resultSet.close();

		int newActorID = actorID + 1 ;
		String updateKeyQuery = "UPDATE KeysTable"
							  + " SET cast_ID = "+ newActorID
							  + " WHERE developer_ID = 'abc123' ";

		myStatement.executeUpdate(updateKeyQuery);//to be on save side update the new cast ID key first on KeysTable
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
		return actorID;
	}

//==================================================================================================================

    private void setupMainFrame()
    {
        Toolkit tk;
        Dimension d;
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(960, 640);
        setLocation(0, 0);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Add Movie");
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setVisible(true);
    }

	@Override
	public void valueChanged(ListSelectionEvent arg0)
	{
		if(arg0.getSource() == lsm)
		{
			addActorButton.setEnabled(!lsm.isSelectionEmpty());
			// can't add if nothing is selected

			if(!lsm.isSelectionEmpty()) // can't check null values in hastable
			{
				// if the actor is already added, don't let 'em add again
				if (actorsInMovie.containsKey(Integer.parseInt((String)dtm.getValueAt(existingActorsTable.getSelectedRow(), 0))))
					addActorButton.setEnabled(false);
				else
					addActorButton.setEnabled(true);
			}
		}

		else if (arg0.getSource() == lsm2)
		{
			removeActorButton.setEnabled(!lsm2.isSelectionEmpty());
			// can't remove if nothing is selected
		}
	}

//==================================================================================================================

	boolean checkIfValueExists(String tableName,String columnName,String value)
	{
		 String SQL_Query = "SELECT * from "+tableName+" WHERE "+columnName+"='"+ value + "' ";

		 try{
			 ResultSet resultSet = myStatement.executeQuery(SQL_Query);// query database

			 if(!resultSet.next()) // doesn't exist
			 {
				 resultSet.close();
				 return false;
			 }
			 else
			 {
				resultSet.close(); // exists
				return true;
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 return false; // problem occurred
		 }
	}
} // end AddMovieDialog class




