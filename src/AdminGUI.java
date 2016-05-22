import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class AdminGUI extends JFrame implements ActionListener
{
    Statement 		myStatement;
    String 			myUsername;
    LoginDialog		myLoginDialog;

    JButton twentyFourButton,
    		topTenButton,
    		addMemberButton,
    		removeMemberButton,
    		addMovieButton,
    		removeMovieGameButton,
    		addGameButton,
    		logoutButton,
    		updateInventoryButton;

    JLabel 	logoLabel;

    JPanel 	buttonPanel,metaPanel;

    AdminGUI(LoginDialog urLoginDialog,Statement urStatement, String urUsername)
    {
		myLoginDialog = urLoginDialog;
        myStatement = urStatement;
        myUsername = urUsername;

        logoLabel = new JLabel(new ImageIcon("logo.png"));
        buttonPanel = new JPanel(new GridLayout(3,3,20,20));
        metaPanel = new JPanel();

        twentyFourButton = new JButton("View Rentals in past 24 hours",new ImageIcon("icon24Hours.png"));
        twentyFourButton.addActionListener(this);

        topTenButton = new JButton("View Top 10 Rentals this Month",new ImageIcon("icon1Month.png"));
        topTenButton.addActionListener(this);

        updateInventoryButton = new JButton("Process Returns",new ImageIcon("iconReturn.png"));
        updateInventoryButton.addActionListener(this);

        addMemberButton = new JButton("Add a Member to Database",new ImageIcon("iconUser.png"));
        addMemberButton.addActionListener(this);

        removeMemberButton = new JButton("Remove a Member",new ImageIcon("iconDelUser.png"));
        removeMemberButton.addActionListener(this);

        addMovieButton = new JButton("Add a Movie to Database",new ImageIcon("iconMovie.png"));
        addMovieButton.addActionListener(this);

        removeMovieGameButton = new JButton("Remove a Movie/Game",new ImageIcon("iconDelete.png"));
        removeMovieGameButton.addActionListener(this);

        addGameButton = new JButton("Add a Game to Database",new ImageIcon("iconGame.png"));
        addGameButton.addActionListener(this);

        logoutButton = new JButton("Logout",new ImageIcon("iconLogout.png"));
        logoutButton.addActionListener(this);
        logoutButton.setActionCommand("LOGOUT");

        metaPanel.add(twentyFourButton);

        buttonPanel.add(twentyFourButton);
        buttonPanel.add(topTenButton);
        buttonPanel.add(updateInventoryButton);
        buttonPanel.add(addMemberButton);
        buttonPanel.add(addMovieButton);
        buttonPanel.add(addGameButton);
        buttonPanel.add(removeMemberButton);
        buttonPanel.add(removeMovieGameButton);
        buttonPanel.add(logoutButton);

		setJMenuBar(newMenuBar());
        add(logoLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setupMainFrame();
    }

	//==================================================================================================
    public void actionPerformed(ActionEvent e)
    {
    	ResultSet rs;
        String query = "";
        try
        {
			if (e.getSource() == twentyFourButton)
			{
				query =   "SELECT r.checkout_date as 'Checkout Date',r.rentalID 'Rental ID',i.title AS 'Rented Item', r.userID as 'User ID', "
						+ " m.first_name as 'First Name', m.last_name as 'Last Name', m.address as 'Address' "
						+ "FROM Rent_Description r, Members m, Items i "
						+ "WHERE datediff(curdate(),r.checkout_date) = 0 "
						+ "AND   r.userID = m.userID "
						+ "AND r.itemID = i.itemID";

				rs = myStatement.executeQuery(query);// query to get rentals within the past 24 hours
				if(!rs.next())
				{
					JOptionPane.showMessageDialog(null,"No results found, please try again.");
				}
				else
				{
					rs.previous();//move ResultSet cursor to previous row
					new TableDialog(rs);   // some results found..so showing the JTable
				}
			}
			//----------------------------------------------------------------------------------------
			else if (e.getSource() == topTenButton)
			{
				query =   "select temp.title as 'Item Title',temp.Total_Rental as 'Total Rental',"
						+ "(case when m.movie_genre is not null then 'Movie' "
						+ "else 'Games' end) as 'Item Category' "
						+ "from (SELECT r.itemID,i.title,count(*) as Total_Rental "
						+ "FROM  Rent_Description r,Items i "
						+ "where datediff(curdate(),r.checkout_date) <=30 "
						+ "and i.itemID = r.itemID "
						+ "group by r.itemID "
						+ "order by Total_Rental DESC LIMIT 10)temp "
						+ "left join Movies m on m.itemID = temp.itemID "
						+ "left join Games g on g.itemID = temp.itemID";

				rs = myStatement.executeQuery(query);
				if(!rs.next())
				{
					//show Joptionpane if result set is empty
					JOptionPane.showMessageDialog(null,"No results found, please try again.");
				}
				else
				{
					rs.previous();//move ResultSet cursor to previous row
					new TableDialog(rs);   // some results found..so showing the JTable
				}
			}
			//----------------------------------------------------------------------------------------------------
			else if(e.getSource() == addMemberButton)
			{
				new AddUserDialog(myStatement);//show JDailog
			}
			//----------------------------------------------------------------------------------------------------
			else if(e.getSource() == updateInventoryButton)
			{
				new ReturnDialog(myStatement);//show JDailog
			}
			//----------------------------------------------------------------------------------------
			else if( e.getSource() == removeMovieGameButton )
			{
				new RemoveItemDialog(myStatement);//show JDailog
			}
			//----------------------------------------------------------------------------------------
			else if( e.getSource() == removeMemberButton )
			{
				new RemoveMemberDialog(myStatement);//show JDailog
			}
			//--------------------------------------------------------------------------------------
			else if( e.getSource() == addMovieButton )
			{
				new AddMovieDialog(myStatement);//show JDailog
			}
			//--------------------------------------------------------------------------------------
			else if( e.getSource() == addGameButton )
			{
				new AddGameDialog(myStatement);//show JDailog
			}
			//----------------------------------------------------------------------------------------------------
			else if(e.getActionCommand().equals("LOGOUT"))
			{
				this.dispose();//dispose the AdminGUI frame
				myStatement.close();
				myLoginDialog.connection.close();
				myLoginDialog.setVisible(true);//show the LoginDialog again
			}

    	} //end try
    	catch (Exception e1)
    	{
    		e1.printStackTrace();
    	}
    }
	//====================================================================================
	//Function to create JMenuBar
	private JMenuBar newMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu subMenu = new JMenu("Menu");
		subMenu.getAccessibleContext().setAccessibleDescription("View the menu");

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

    //=========================================================================================
    void setupMainFrame()
    {
        Toolkit tk;
        Dimension d;
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(800,300);
        setResizable(false);
        setLocation(d.width/10, d.height/4);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      //program terminates when closed
        setTitle("Movies-R-Us Admin");
        setVisible(true);                                    //Now we can see the window.
    }
}//end of class
