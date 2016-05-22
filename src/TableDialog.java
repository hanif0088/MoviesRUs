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


public class TableDialog extends JDialog implements ActionListener,ListSelectionListener
{
    public static void main(String[] x)
    {
		try
		{
			Class.forName( "com.mysql.jdbc.Driver" );
			Connection connection = DriverManager.getConnection( "jdbc:mysql://localhost/sys", "root", "root");
			//Connection connection = DriverManager.getConnection( "jdbc:mysql://localhost/moviesrus", "root", "Password123");
			//Connection connection = DriverManager.getConnection( "jdbc:mysql://falcon-cs.fairmontstate.edu/DB09", "hmirza", "F00237417" );

			Statement statement = connection.createStatement();
			//new TableDialog(resultSet);
			statement.close();
			connection.close();
		}
		catch ( Exception e )
		{
			System.out.println( "Exception"+e.getMessage() );
		}

    }
	//----------------------------------------------------------------------------------------------------------
    JButton                     	orderButton;
    JButton                     	cancelButton;
    Container                   	contentPane;
    JTable                      	myJTable;
    JScrollPane                 	scroller;
    ListSelectionModel          	myListSelectionModel;
    JPanel							southButtonPanel;
    ResultSet 						myResultSet;
    DefaultTableModel           	tableModel;

    int[]                       	selectionList = new int[100];
    String							myUsername;
	Statement 						myStatement;
	Hashtable<String,Integer> 		orderHashtable = new Hashtable<String, Integer>();

    //==================================================================================
    TableDialog(ResultSet urResultSet,Statement urStatement,String urUsername) throws Exception
    {
		myResultSet = urResultSet;
		myUsername = "'"+urUsername+"'";
		myStatement = urStatement;

		ResultSetMetaData metaData = myResultSet.getMetaData();// process query results
		int numberOfColumns = metaData.getColumnCount();

		Vector<Object> columnNames = new Vector<Object>();// columnNames holds the column names of the query result
		Vector<Object> rows = new Vector<Object>();//rows is a vector of vectors, each vector is a vector of values representing a certain row of the query result

		for ( int i = 1; i <= numberOfColumns; i++ )
		{
			columnNames.addElement(metaData.getColumnLabel(i));
	    }
		columnNames.addElement("Quantity");//add "Quantity" column at last

		while ( myResultSet.next() )
		{
			Vector<Object> currentRow = new Vector<Object>();
			for ( int i = 1; i <= numberOfColumns; i++ )
			{
			   currentRow.addElement(myResultSet.getObject(i));
			}
			currentRow.addElement("1");
			rows.addElement(currentRow);
		}
		tableModel = new DefaultTableModel(rows, columnNames)
		                 {
							 //Override the method to make only the quantity column editable
							 @Override
							 public boolean isCellEditable(int row, int col)
							 {
							 	 return col == (getColumnCount()-1);
							 }
                         };

		myJTable = new JTable(tableModel);//create new jtable
        myJTable.setFont(new Font("Courier", Font.BOLD,12));
        myJTable.setMinimumSize(new Dimension(10, 10));
        scroller = new  JScrollPane(myJTable);
        myJTable.setPreferredScrollableViewportSize(new Dimension(420, 60));
        myJTable.setRowHeight(18);
        myListSelectionModel=myJTable.getSelectionModel();
        myListSelectionModel.addListSelectionListener(this);
        myJTable.setAutoCreateRowSorter(true);// make the table sorted when the user click the column header

        orderButton = new JButton("Order",new ImageIcon("iconOrder.png"));
        orderButton.addActionListener(this);
        orderButton.setEnabled(false);
        cancelButton = new JButton("Cancel",new ImageIcon("iconCancel.png"));
        cancelButton.addActionListener(this);

        southButtonPanel=new JPanel(new FlowLayout());
        southButtonPanel.add(orderButton);
        southButtonPanel.add(cancelButton);

        contentPane=getContentPane();
        contentPane.add(scroller,BorderLayout.CENTER);
        contentPane.add(southButtonPanel,BorderLayout.SOUTH);
        setupMainFrame();
        myResultSet.close();
    }
    //==================================================================================
    //this constructor is just to view the table and the table is non-editable
    TableDialog(ResultSet urResultSet) throws Exception
    {
		myResultSet = urResultSet;
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
        scroller = new  JScrollPane(myJTable);
        myJTable.setPreferredScrollableViewportSize(new Dimension(420, 60));
        myJTable.setRowHeight(18);
        myJTable.setAutoCreateRowSorter(true);//This function will perform the search and show a Jtable with the search result

        cancelButton = new JButton("Cancel",new ImageIcon("iconCancel.png"));
        cancelButton.addActionListener(this);

        southButtonPanel=new JPanel(new FlowLayout());
        southButtonPanel.add(cancelButton);
        contentPane=getContentPane();
        contentPane.add(scroller,BorderLayout.CENTER);
        contentPane.add(southButtonPanel,BorderLayout.SOUTH);
        setupMainFrame();
        myResultSet.close();
    }
    //==================================================================================================================
    public void valueChanged(ListSelectionEvent lse)
    {
		orderButton.setEnabled(false);
		selectionList=myJTable.getSelectedRows();
		if(selectionList.length > 0)
		{
			orderButton.setEnabled(true);//make order button enable when member selects one or more rows
		}
	}
    //============================================================================================================
    public void actionPerformed(ActionEvent e)
    {
		if(e.getSource() == orderButton)
		{
			if (myJTable.isEditing())
			{
    			myJTable.getCellEditor().stopCellEditing();//stop JTable's cell editing. Which will stop the cursur from editing quantity value
			}
			completeTheOrder();
		}
		else if(e.getSource() == cancelButton)
		{
			this.dispose();//dispose the dialog
		}

	}
	//===================================================================================
	//this function will complete the order
	void completeTheOrder()
	{
		int quantity = 0;
		int inStockQuantity = 0;
		int totalQuatity = 0;
		ResultSet resultSet;
		orderHashtable.clear();
		try
		{
			for (int i=0; i<selectionList.length; i++)
			{
				String itemID = tableModel.getValueAt(selectionList[i],0).toString();//get itemID which is the first column
				Object quantityObject = tableModel.getValueAt(selectionList[i],tableModel.getColumnCount()-1 ) ;//get the quantity number which is the last column

				if (quantityObject != null)
				{
						String quantityString = quantityObject.toString();
						quantity = Integer.parseInt(quantityString);//convert the quantity number
				}
				String inStockString = tableModel.getValueAt(selectionList[i],tableModel.getColumnCount()-2 ).toString() ;//get the in stock number which is the 2nd last column
				inStockQuantity = Integer.parseInt(inStockString);

				if (quantity > inStockQuantity)
					throw new MyException("Quantity can't be more than In Stock copies!");//throw an exception

				orderHashtable.put(itemID, quantity ); //map the quantity number with it's itemID
				totalQuatity = totalQuatity + quantity;
			}

			String query = "SELECT * "+
						   "FROM Members m "+
						   "WHERE m.userID = "+myUsername;
			resultSet = myStatement.executeQuery(query);
			resultSet.first();
			int member_quota = resultSet.getInt("member_quota");//run a query to get member's quota

			if( totalQuatity > member_quota)
				throw new MyException("You've exceeded your quota! Your available quota is "+member_quota);//throw an exception

			String keyQuery = "SELECT * "+
						      "FROM KeysTable k "+
						      "WHERE k.developer_ID = 'abc123' ";
			resultSet = myStatement.executeQuery(keyQuery);
			resultSet.first();
			int retalID = resultSet.getInt("rental_ID"); //run a query to get rental ID key from KeysTable
			resultSet.close();

			int newRentalID = retalID + totalQuatity;

			String updateKeyQuery = "UPDATE KeysTable"
						      	 + " SET rental_ID = "+ newRentalID
						      	 + " WHERE developer_ID = 'abc123' ";
			myStatement.executeUpdate(updateKeyQuery);// First update the new rantal ID key on KeysTable (to be on save side )

			int m = 0;
			Set<String> keys = orderHashtable.keySet();
			for(String key: keys)
			{
				int itemQuantity = orderHashtable.get(key);//get the quantity number of each selected item
				for(int i=0; i < itemQuantity; i++)
				{
					String retalIDString = "'"+retalID+"'";
					String itemID = "'"+key+"'";

		    		String insertQuery = "INSERT INTO rent_description " +
                                 		 "VALUES (" +retalIDString+ "," +myUsername+ "," +itemID+ ",curdate(), NULL )";
					m = myStatement.executeUpdate( insertQuery);//insert new rental description to the table called rent_description

					retalID++;
				}
			}

			if( m > 0)
			{
				this.dispose();//dispose the table dialog
				JOptionPane.showMessageDialog(this,"Congratulation! Your order has been successfully placed!");
			}

		}//end of try
		catch (NullPointerException npe)
		{
			JOptionPane.showMessageDialog(this,"Quantity can't be empty!");
		}
		catch (NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(this,"Please enter a valid quantity number!");
			//nfe.printStackTrace();
		}
		catch(MyException myEx)
		{
			JOptionPane.showMessageDialog(this,myEx.getMessage());
		}
		catch ( SQLException sqlException )
		{
			sqlException.printStackTrace();
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

}//end of class TableDialog

