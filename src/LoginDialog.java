//COMP 4410 Group Project
//Group Members: Hanif Mirza, Benjamin Wilfong
//Submission date: 12/04/15

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;
import java.awt.Color;
import javax.swing.UIManager;

public class LoginDialog extends JDialog implements ActionListener,DocumentListener
{
    public static void main(String[] x)
    {
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new LoginDialog();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
    //---------------------------------------------------------------------------------------
    static final String 	JDBC_DRIVER = "com.mysql.jdbc.Driver";// JDBC driver name and database URL - for MySQL
	static final String 	DATABASE_URL = "";//FSU server, removed for security reasons w/ github

    Connection 				connection = null; // manages connection
    Statement 				statement = null;   // query statement
    String 					SQL_Query = null;

    String                  username;
    String                  password;
    JButton                 loginButton;
    JButton                 cancelButton;
    JPanel                  buttonPanel;

    JLabel                  usernameLabel;
    JTextField              usernameTextField;
    JPanel                  usernamePanel;

    JLabel                  passwordLabel;
    JPasswordField          passwordTextField;
    JPanel                  passwordPanel;

    JPanel                  mainPanel;
    GridBagConstraints      gbc;
    String                  messageReceived;

	//====================================================================================================
    LoginDialog()
    {
        usernameTextField = new JTextField(15);
        usernameLabel=new JLabel("Username:");
        usernamePanel=new JPanel();
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTextField);

        passwordTextField=new JPasswordField(15);
        passwordLabel=new JLabel("Password:");
        passwordPanel=new JPanel();
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordTextField);

        loginButton = new JButton("Login",new ImageIcon("iconLogin.png"));
        cancelButton = new JButton("Cancel",new ImageIcon("iconCancel.png"));

        loginButton.setActionCommand("LOGIN");
        cancelButton.setActionCommand("CANCEL");
        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);
        cancelButton.setVerifyInputWhenFocusTarget(false);

        buttonPanel=new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        getRootPane().setDefaultButton(loginButton);

        mainPanel=new JPanel(new GridBagLayout());

        gbc= new GridBagConstraints();
        gbc.insets= new Insets(5,0,5,0);

        gbc.gridx=0;
        gbc.gridy=1;
        mainPanel.add(usernamePanel,gbc);

        gbc.gridx=0;
        gbc.gridy=2;
        mainPanel.add(passwordPanel,gbc);

        gbc.gridx=0;
        gbc.gridy=3;
        mainPanel.add(buttonPanel,gbc);
        add(mainPanel);

        loginButton.setEnabled(false);
        usernameTextField.getDocument().addDocumentListener(this);
        passwordTextField.getDocument().addDocumentListener(this);
        setupMainFrame();
    }

    //========================================================================================================
    public void insertUpdate(DocumentEvent de)
    {
        username = usernameTextField.getText().trim();
        char[] charPassword = passwordTextField.getPassword();
        password = new String(charPassword);

        if( !username.equals("") &&  !password.equals(""))
        {
			//if both username and password aren't empty then enable login button
            loginButton.setEnabled(true);
        }
    }
    //========================================================================================================
    public void changedUpdate(DocumentEvent de){}
    //=======================================================================================
    public void removeUpdate(DocumentEvent de)
    {
        username = usernameTextField.getText().trim();
        char[] charPassword = passwordTextField.getPassword();
        password = new String(charPassword);

        if( username.equals("") ||  password.equals(""))
        {
			//disable login button if username or password is empty
            loginButton.setEnabled(false);
        }
    }
    //=======================================================================================================
    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("CANCEL"))
        {
            dispose();
        }
        //----------------------------------------------------------------------------------------------
        else if(e.getActionCommand().equals("LOGIN"))
        {
            username = usernameTextField.getText().trim();

			char[] charPassword = passwordTextField.getPassword();
			password = new String(charPassword);

			try
			{
				 Class.forName( JDBC_DRIVER ); // load database driver class
				 //connection = DriverManager.getConnection( "jdbc:mysql://localhost/sys", "", "");//Hanif's local server
				 //connection = DriverManager.getConnection( "jdbc:mysql://localhost/moviesrus", "", "");//Ben's local server

				 //connection = DriverManager.getConnection( DATABASE_URL, "", "" ); // establish connection to database
				 statement = connection.createStatement();// create Statement for querying database

				 if(checkIfValueExists("Admins","userID",username))
				 {
					 //System.out.printf( "Admin's userID is exist!");
					 if(checkIfValueExists("Admins","password",password))
					 {
						 //System.out.printf( "Admin's password is exist!");
						 new AdminGUI(this,statement,username); //show admin's GUI
						 this.dispose();
					 }
					 else
					 {
						JOptionPane.showMessageDialog(this, "Logon failure: unknown username or bad password");
				     }
			     }
                 else if(checkIfValueExists("Members","userID",username))
                 {
					 //System.out.printf( "Member's userID is exist");
					 if(checkIfValueExists("Members","password",password))
					 {
						 //System.out.printf( "Member's password is exist");
						 new MemberGUI(this,statement,username);//show Member's GUI
						 this.dispose();
					 }
					 else
					 {
						JOptionPane.showMessageDialog(this,"Logon failure: unknown username or bad password");
				     }
			     }
			     else
			     {
				 	JOptionPane.showMessageDialog(this,"User hasn't registered yet!");
			     }
			}// end try
			catch ( SQLException sqlException )
			{
				sqlException.printStackTrace();
				JOptionPane.showMessageDialog(this, sqlException.getMessage() );
			}
			catch ( ClassNotFoundException classNotFound )
			{
				classNotFound.printStackTrace();
				JOptionPane.showMessageDialog(this, classNotFound.getMessage() );
			}
			catch ( Exception exception )
			{
				exception.printStackTrace();
				JOptionPane.showMessageDialog(this, exception.getMessage() );
			}
        }//end..else if(.."LOGIN")

    }//end of actionPerformed()

    //=================================================================================
    //this function will return true if a value exists in a specific column of a MySQL table
    boolean checkIfValueExists(String tableName,String columnName,String value) throws Exception
    {
		 SQL_Query = "SELECT * from "+tableName+" WHERE "+columnName+"='"+ value + "' ";
		 ResultSet resultSet = statement.executeQuery(SQL_Query);// query database
		 if(!resultSet.next())
		 {
			 //System.out.printf( "Value isn't exists");
			 resultSet.close();
			 return false;
		 }
		 else
		 {
			//System.out.printf( "Value exists");
			resultSet.close();
			return true;
		 }
    }
    //============================================================================
    void setupMainFrame()
    {
        Toolkit tk;
        Dimension d;
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(d.width / 4, d.height / 4);
        setLocation(d.width / 4, d.height / 4);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Movies-R-Us");
        setVisible(true);
    }

}//end of class LoginDialog
