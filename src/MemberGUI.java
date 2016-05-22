import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.event.*;
import java.sql.*;
import java.util.Random;

public class MemberGUI extends JFrame implements ActionListener,ChangeListener
{
	JTabbedPane 	jtp;
	Statement 		myStatement;
	String 			myUsername;
	EditInfoPanel	editInfoPanel;
	LoginDialog		myLoginDialog;

	MemberGUI(LoginDialog urLoginDialog,Statement urStatement,String urUsername)
	{
		myLoginDialog = urLoginDialog;
		myStatement = urStatement;
		myUsername = urUsername;

		jtp = new JTabbedPane();
		jtp.addChangeListener(this);

		jtp.addTab("Search for Movies", new MoviePanel(myStatement,myUsername));
		jtp.addTab("Search for Games", new GamePanel(myStatement,myUsername));
		editInfoPanel = new EditInfoPanel(myStatement,myUsername);
		jtp.addTab("View/edit Personal info", editInfoPanel);

		setJMenuBar(newMenuBar());
		add(jtp);
		setupMainFrame();
	}

	//=====================================================================
    public void stateChanged(ChangeEvent e)
    {
		JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
		int tabIndex = tabbedPane.getSelectedIndex();
		if(tabIndex == 2)//if it is the tab for EditInfoPanel
		{
			boolean tabIsVisible = tabbedPane.isEnabledAt(tabIndex);
			if ( tabIsVisible )
			{
				//if the EditInfoPanel is visible then populate EditInfoPanel's all textfields with member's info
				fillUpAllTextFields();
			}
		}
    }
    //=====================================================================
    //this function will populate EditInfoPanel's all textfields with member's info
    void fillUpAllTextFields()
    {
		try
		{
			String query = "SELECT * "+
						   "FROM Members m "+
						   "WHERE m.userID = "+"'"+myUsername+"'";
			ResultSet resultSet = myStatement.executeQuery(query);//get member's all info from the database
			resultSet.first();

			String userID = resultSet.getString("userID");
			String password = resultSet.getString("password");
			String email = resultSet.getString("email");
			String firstName = resultSet.getString("first_name");
			String lastName = resultSet.getString("last_name");
			String  address = resultSet.getString("address");
			String  phone = resultSet.getString("phone");
			int member_quota = resultSet.getInt("member_quota");

			//populate all the textfields with member's info
			editInfoPanel.userIDField.setText(userID);
			editInfoPanel.passwordField.setText(password);
			editInfoPanel.quotaField.setText( Integer.toString(member_quota));
			editInfoPanel.firstNameField.setText(firstName);
			editInfoPanel.lastNameField.setText(lastName);
			editInfoPanel.emailField.setText(email);
			editInfoPanel.phoneField.setText(phone);
			editInfoPanel.addressField.setText(address);

			editInfoPanel.saveButton.setEnabled(false);
			editInfoPanel.repaint();

		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	//==============================================================================================
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("SEQUELS"))
		{
			try
			{
				String sequelQuery =  "SELECT i1.title as 'Original Movies', i2.title AS 'Sequel Movies' "
									 +"FROM Sequel s, Items i1,Items i2 "
									 +"WHERE s.original_itemID = i1.itemID AND s.sequel_itemID = i2.itemID ";

				ResultSet resultSet = myStatement.executeQuery( sequelQuery );
				new TableDialog(resultSet);//just view all the Sequel movies on the TableDialog
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
			}
		}
		//---------------------------------------------------------------------------------------------------
		else if(e.getActionCommand().equals("HISTORY"))
		{
			try
			{
				String historyQuery =   "select r.rentalID as 'Rental ID',i.title as 'Item Title',r.checkout_date as 'Checkout Date',r.returned_date as 'Returned Date', "
									  + "(case when m.movie_genre is not null then 'Movie' else 'Games' end) as 'Item Category' "
									  + "from Rent_Description r "
									  + "left join Movies m on m.itemID = r.itemID "
									  + "left join Games g on g.itemID = r.itemID "
									  + "inner join Items i on r.itemID = i.itemID "
									  + "where r.userID = '" + myUsername + "'";

				ResultSet resultSet = myStatement.executeQuery( historyQuery );
				new TableDialog(resultSet);//just View The order History on the TableDialog
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
			}
		}
		//----------------------------------------------------------------------------------------------------
		else if(e.getActionCommand().equals("LOGOUT"))
		{
			try
			{
				this.dispose();//dispode the MemberGUI frame
				myStatement.close();
				myLoginDialog.connection.close();
				myLoginDialog.setVisible(true);//show the LoginDialog again
			}
			catch (Exception exception)
			{
				exception.printStackTrace();
			}
		}
	}
	//====================================================================================
	//Function to create JMenuBar
	private JMenuBar newMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu subMenu = new JMenu("Menu");
		subMenu.getAccessibleContext().setAccessibleDescription("View the menu");

        JMenuItem sequelMenuItem = newItem("View All Sequel Movies","SEQUELS",this,KeyEvent.VK_S,KeyEvent.VK_S,"View list of all sequel movies");
        sequelMenuItem.setIcon(new ImageIcon("iconSequel.png"));
		subMenu.add(sequelMenuItem);
        JMenuItem historyMenuItem = newItem("View Rental History","HISTORY",this,KeyEvent.VK_H,KeyEvent.VK_H,"View detailed history of rentals");
        historyMenuItem.setIcon(new ImageIcon("iconOrderHis.png"));
		subMenu.add(historyMenuItem);
        JMenuItem logoutMenuItem = newItem("Logout","LOGOUT",this,KeyEvent.VK_L,KeyEvent.VK_L,"Log out and exit");
        logoutMenuItem.setIcon(new ImageIcon("iconLogout.png"));
		subMenu.add(logoutMenuItem);
		menuBar.add(subMenu);
		return menuBar;
	}
	//===========================================================================================================
	//Function to create JMenuItem
	private JMenuItem newItem(String label,String actionCommand,ActionListener menuListener,int mnemonic, int keyEvent,String toolTipText)
	{
		JMenuItem m;
		m=new JMenuItem(label,mnemonic);
		m.setAccelerator(KeyStroke.getKeyStroke(keyEvent,ActionEvent.ALT_MASK));
		m.getAccessibleContext().setAccessibleDescription(toolTipText);
		m.setToolTipText(toolTipText);
		m.setActionCommand(actionCommand);
		m.addActionListener(menuListener);
		return m;
	}

	//===================================================================================
	void setupMainFrame()
	{
		Toolkit tk;
		Dimension d;
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setSize(750, 400);
		setResizable(false);
		setLocation(d.width/10, d.height/4);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      //program terminates when closed
		setTitle("Welcome to Movies R Us");
		setVisible(true);									 //Now we can see the window.
	}
}//end of class
