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

public class EditInfoPanel extends JPanel implements ActionListener,DocumentListener
{
	JLabel			userIDLabel;
	JLabel			passwordLabel;
	JLabel			quotaLabel;
	JLabel			firstNameLabel;
	JLabel			lastNameLabel;
	JLabel			emailLabel;
	JLabel			phoneLabel;
	JLabel			addressLabel;

	JTextField		userIDField;
	JPasswordField	passwordField;
	JTextField		quotaField;
	JTextField		firstNameField;
	JTextField		lastNameField;
	JTextField		emailField;
	JTextField		phoneField;
	JTextField		addressField;

	JButton         saveButton;
	String			firstName;
	String			lastName;
	String			password;
	String			email;
	String			phone;
	String			address;

	Statement 		myStatement;
	String 			myUsername;

	//====================================================================================
	EditInfoPanel(Statement urStatement,String urUsername)
	{
		myStatement = urStatement;
		myUsername = "'" + urUsername+ "'";

		userIDLabel = new JLabel("User ID:");
		userIDLabel.setBounds(100, 20, 200, 20);
		userIDField = new JTextField(30);
		userIDField.setBounds(260, 20, 280, 20);
		userIDField.setEditable(false);

		passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(100, 50, 200, 20);
		passwordField = new JPasswordField(30);
		passwordField.setBounds(260, 50, 280, 20);

		quotaLabel = new JLabel("Member Quota:");
		quotaLabel.setBounds(100, 80, 200, 20);
		quotaField = new JTextField(30);
		quotaField.setBounds(260, 80, 280, 20);
		quotaField.setEditable(false);

		firstNameLabel = new JLabel("First Name:");
		firstNameLabel.setBounds(100, 110, 200, 20);
		firstNameField = new JTextField(30);
		firstNameField.setBounds(260, 110, 280, 20);

		lastNameLabel = new JLabel("Last Name:");
		lastNameLabel.setBounds(100, 140, 200, 20);
		lastNameField = new JTextField(30);
		lastNameField.setBounds(260, 140, 280, 20);

		emailLabel = new JLabel("Email:");
		emailLabel.setBounds(100, 170, 200, 20);
		emailField = new JTextField(30);
		emailField.setBounds(260, 170, 280, 20);

		phoneLabel = new JLabel("Phone: (e.g. 123-456-7890)");
		phoneLabel.setBounds(100, 200, 200, 20);
		phoneField = new JTextField(30);
		phoneField.setBounds(260, 200, 280, 20);

		addressLabel = new JLabel("Address:");
		addressLabel.setBounds(100, 230, 200, 20);
		addressField = new JTextField(60);
		addressField.setBounds(260, 230, 280, 20);

		saveButton = new JButton("Update");
		saveButton.setBounds(340, 280, 80, 30);
		saveButton.addActionListener(this);
		saveButton.setEnabled(false);

        passwordField.getDocument().addDocumentListener(this);
        firstNameField.getDocument().addDocumentListener(this);
        lastNameField.getDocument().addDocumentListener(this);
        emailField.getDocument().addDocumentListener(this);
        phoneField.getDocument().addDocumentListener(this);
        addressField.getDocument().addDocumentListener(this);

		setLayout(null);
		add(userIDLabel);
		add(userIDField);
		add(passwordLabel);
		add(passwordField);

		add(quotaLabel);
		add(quotaField);
		add(firstNameLabel);
		add(firstNameField);
		add(lastNameLabel);
		add(lastNameField);

		add(emailLabel);
		add(emailField);
		add(phoneLabel);
		add(phoneField);
		add(addressLabel);
		add(addressField);
		add(saveButton);

	}
    //========================================================================================================
    public void insertUpdate(DocumentEvent de)
    {
        char[] charPassword = passwordField.getPassword();
        password = new String(charPassword);
        firstName = firstNameField.getText().trim();
        lastName = lastNameField.getText().trim();
        email = emailField.getText().trim();
        phone = phoneField.getText().trim();
        address = addressField.getText().trim();

        if( !password.equals("") && !firstName.equals("") && !lastName.equals("") && !email.equals("") && !phone.equals("") && !address.equals("") )
        {
            saveButton.setEnabled(true);//enable the save button if textfields aren't empty
        }
    }
    //========================================================================================================
    public void changedUpdate(DocumentEvent de){}
    //=======================================================================================
    public void removeUpdate(DocumentEvent de)
    {
		saveButton.setEnabled(true);
        char[] charPassword = passwordField.getPassword();
        password = new String(charPassword);
        firstName = firstNameField.getText().trim();
        lastName = lastNameField.getText().trim();
        email = emailField.getText().trim();
        phone = phoneField.getText().trim();
        address = addressField.getText().trim();

        if( password.equals("") || firstName.equals("") || lastName.equals("") || email.equals("") || phone.equals("") || address.equals("") )
        {
            saveButton.setEnabled(false);//disable the save button if textfields are empty
        }
    }
    //=====================================================================================
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == saveButton)
		{
			char[] charPassword = passwordField.getPassword();
			password = new String(charPassword);
			firstName = firstNameField.getText().trim();
			lastName = lastNameField.getText().trim();
			email = emailField.getText().trim();
			phone = phoneField.getText().trim();
			address = addressField.getText().trim();

			try
			{
				if ( Validate.isPasswordValid(password) == false )//throw exception if it isn't valid
					throw new MyException("Invalid password! 6-20 Alphanumeric charcters or symbols (@#$%_-)");

				else if ( Validate.isFirstNameValid(firstName) == false )//throw exception if it isn't valid
						throw new MyException("Invalid first name! Only uppercase and lowercase letters are allowed!");

				else if ( Validate.isLastNameValid(lastName) == false )//throw exception if it isn't valid
						throw new MyException("Invalid last name! Only uppercase, lowercase letters, one space or special characters '- are allowed!");

				else if ( Validate.isEmailValid(email) == false )//throw exception if it isn't valid
						throw new MyException("Please enter a valid email address!");

				else if ( Validate.isPhoneValid(phone) == false )//throw exception if it isn't valid
						throw new MyException("Invalid phone number! (ex. 123-456-7890 OR 1234567890)");

				String query = "SELECT * FROM Members m WHERE m.email = '" + email +"'"+ " and m.userID != "+myUsername +";";
				ResultSet rs = myStatement.executeQuery(query);

				if(rs.next()) // if the email already exists then throw exception
					throw new MyException("The email address you entered is associated with an existing account!");
				rs.close();

				password = "'" + password + "'";
				firstName = "'" + firstName + "'";
				lastName = "'" + lastName.replaceAll("'", "\\\\'") + "'";//replace occurrences of ' with \'
				email = "'" + email + "'";
				phone = "'" + phone + "'";
				address = "'" + address.replaceAll("'", "\\\\'") + "'";//replace occurrences of ' with \'

				String updateTableSQL =
						   "UPDATE members"
						+ " SET password = "+ password +","
						+ " first_name = "+ firstName +","
						+ " last_name = "+ lastName +","
						+ " email = "+ email +","
						+ " phone = "+ phone +","
						+ " address = "+ address
						+ " WHERE userID = "+myUsername+";";

				int m = myStatement.executeUpdate(updateTableSQL); //update member's info on the database

				if(m>0)
					JOptionPane.showMessageDialog(this,"Your information has been successfully updated!");

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
	}//end of actionPerformed(...)
}//end of class
