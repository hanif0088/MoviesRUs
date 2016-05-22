import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.*;


public class RemoveItemDialog extends JDialog implements ActionListener,ListSelectionListener, DocumentListener
{
    public static void main(String[] x)
    {
		try
		{
			Class.forName( "com.mysql.jdbc.Driver" );
			Connection connection = DriverManager.getConnection( "jdbc:mysql://localhost/sys", "root", "root");

			Statement statement = connection.createStatement();
			new RemoveItemDialog(statement);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

    }
	//----------------------------------------------------------------------------------------------------------
	JLabel						itemIDLabel;
	JLabel						titleLabel;

	JTextField					itemIDField;
	JTextField					titleField;

	JButton                     searchButton;
    JButton                     removeButton;
    JButton                     cancelButton;

    JTable                      myJTable;
    JScrollPane                 scroller;
    ListSelectionModel          myListSelectionModel;
    DefaultTableModel           tableModel;

    int[]                       selectionList = new int[100];

	String						itemID;
	String						itemTitle;
	Statement 					myStatement;

    //==================================================================================
    RemoveItemDialog(Statement urStatement)
    {
		myStatement = urStatement;

		itemIDLabel = new JLabel("Item ID: ");
		itemIDLabel.setBounds(40, 20, 120, 20);
		itemIDField = new JTextField(30);
		itemIDField.getDocument().addDocumentListener(this);
		itemIDField.setBounds(180, 20, 200, 20);


		titleLabel = new JLabel("Item's Title: ");
		titleLabel.setBounds(40, 50, 120, 20);
		titleField = new JTextField(30);
		titleField.getDocument().addDocumentListener(this);
		titleField.setBounds(180, 50, 200, 20);

		searchButton = new JButton("Search");
		searchButton.setBounds(420, 25, 80, 40);
		searchButton.addActionListener(this);
		searchButton.setEnabled(false);

		removeButton = new JButton("Remove");
		removeButton.setBounds(180, 300, 90, 30);
		removeButton.addActionListener(this);
		removeButton.setEnabled(false);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setBounds(290, 300, 90, 30);

		setLayout(null);
		add(itemIDLabel);
		add(itemIDField);
		add(titleLabel);
		add(titleField);
		add(searchButton);
		add(removeButton);
		add(cancelButton);

        setupMainFrame();
    }
    //========================================================================================================
    public void insertUpdate(DocumentEvent de)
    {
        itemID = itemIDField.getText().trim();
        itemTitle = titleField.getText().trim();

        if( !itemID.equals("") )
        {
           searchButton.setEnabled(true);
           titleField.setEditable(false);
        }
        if( !itemTitle.equals("") )
        {
           searchButton.setEnabled(true);
           itemIDField.setEditable(false);
        }
    }

    //=========================================================================================
    public void changedUpdate(DocumentEvent de){}
    //=======================================================================================
    public void removeUpdate(DocumentEvent de)
    {
        itemID = itemIDField.getText().trim();
        itemTitle = titleField.getText().trim();

        if( itemID.equals("") && itemTitle.equals(""))
        {
           searchButton.setEnabled(false);
           titleField.setEditable(true);
        }
        if( itemTitle.equals("") && itemID.equals("") )
        {
           searchButton.setEnabled(false);
           itemIDField.setEditable(true);
        }
    }
    //==============================================================================================
    public void valueChanged(ListSelectionEvent lse)
    {
		//admin can delete only one item at a time
		removeButton.setEnabled(false);
		selectionList=myJTable.getSelectedRows();
		if(selectionList.length == 1)
		{
			removeButton.setEnabled(true);//enable the remove button if the admin select only one row from the table
		}
	}
    //============================================================================================================
    public void actionPerformed(ActionEvent e)
    {
		if(e.getSource() == searchButton)
		{
			if(scroller!=null)
				remove(scroller);//if Jtable exists then remove it
			repaint();

			performSearch();
		}
		else if(e.getSource() == removeButton)
		{
			deleteTheSelectedItem();
		}
		else if(e.getSource() == cancelButton)
		{
			this.dispose();
		}

	}
	//========================================================================================
	//This function will perform the search and show a Jtable with the search result
	void performSearch()
	{
		ResultSet		myResultSet;
		String 			sqlQuery = null;

		itemID = itemIDField.getText().trim();
		itemTitle = titleField.getText().trim();

		if( !itemID.equals("") )//if itemID field isn't empty
		{

			itemID = "'"+ itemID.replaceAll("'", "\\\\'") +"'";//replace occurrences of ' with \'
			sqlQuery = "SELECT i.itemID as 'ItemID',i.title as 'Title', i.number_copies_available as 'In Stock' "
					   +"From Items i where i.itemID = "+itemID;
		}
		else if( !itemTitle.equals("") )//if item title field isn't empty
		{
			itemTitle = "'%"+ itemTitle.replaceAll("'", "\\\\'") +"%'";//replace occurrences of ' with \'
			sqlQuery = "SELECT i.itemID as 'ItemID',i.title as 'Title', i.number_copies_available as 'In Stock' "
					 + "From Items i where i.title LIKE "+itemTitle;
		}

		try
		{
			myResultSet = myStatement.executeQuery(sqlQuery);//perform the query
			if(!myResultSet.next())
			{
				//show Joptionpane if result set is empty
				JOptionPane.showMessageDialog(null,"No results found, please try again.");
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
				tableModel = new DefaultTableModel(rows, columnNames)
								 {
									 //Override the method to make all cells non-editable
									 @Override
									 public boolean isCellEditable(int row, int col)
									 {
										 return false;
									 }
								 };

				myJTable = new JTable(tableModel);//create new jtable
				myJTable.setFont(new Font("Courier", Font.BOLD,12));
				myJTable.setMinimumSize(new Dimension(10, 10));
				myJTable.setPreferredScrollableViewportSize(new Dimension(420, 60));
				myJTable.setRowHeight(18);
        		myListSelectionModel=myJTable.getSelectionModel();
        		myListSelectionModel.addListSelectionListener(this);
        		myJTable.setAutoCreateRowSorter(true);// make the table sorted when the user click the column header

				scroller = new  JScrollPane(myJTable);
				scroller.setSize(460,180);
				scroller.setLocation(100, 100);
				add(scroller);
				myResultSet.close();
			}
		}
		catch ( SQLException exception )
		{
			System.out.println( exception.getMessage() +"****");
			exception.printStackTrace();
		}
		catch ( Exception e )
		{

			e.printStackTrace();
		}
	}
	//===================================================================================
	//this function will delete the selected item from the database
	void deleteTheSelectedItem()
	{
		try
		{
			for (int i=0; i<selectionList.length; i++)
			{
				String itemID = tableModel.getValueAt(selectionList[i],0).toString();//get itemID which is the first column
				itemID = "'"+ itemID +"'";

				String deleteQuery = "DELETE FROM Items " +
									 "WHERE itemID = " + itemID;
				int m = myStatement.executeUpdate( deleteQuery );//delete that selected item
				if( m > 0)
				{
					this.dispose();//dispose the table dialog
					JOptionPane.showMessageDialog(this,"Item has been successfully deleted!");
				}
			}
		}

		catch ( SQLException sqlException )
		{
			//sqlException.printStackTrace();
			System.out.println( sqlException.getMessage() );
			JOptionPane.showMessageDialog( this,sqlException.getMessage());//show the error message
		}
		catch ( Exception e )
		{
			e.printStackTrace();
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

