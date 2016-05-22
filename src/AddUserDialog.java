import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class AddUserDialog extends JDialog implements ActionListener, DocumentListener
{
    public static void main(String args[])
    {
        try{
        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
          System.out.println("Exception time baby!");
        }

        new AddUserDialog(null);
    }

    JLabel userLabel,
           passwordLabel,
           verifyLabel,
           firstNameLabel,
           lastNameLabel,
           emailLabel,
           addressLabel,
           phoneLabel,
           memTypeLabel;

    JTextField userTF,
               firstNameTF,
               lastNameTF,
               emailTF,
               addressTF,
               phoneTF;

    JPasswordField passwordTF,
                   verifyTF;

    JButton    addButton,
               cancelButton;

    String[]   memTypes = {"STANDARD: 5 ITEMS",
                           "PLUS: 10 ITEMS",
                           "PREMIUM: 15 ITEMS",
                           "GOLD: 20 ITEMS",
                           "PLATINUM: 25 ITEMS"};

    String     userName,
               password,
               verifyInput,
               firstName,
               lastName,
               email,
               address,
               phoneNumber,
               errorStatement,
               query;

    char[]     passwordArray,
               verifyPasswordArray;

    int        memberQuota;

    boolean    validSignup;

    JComboBox<String>  memTypeSelection;

    GridBagConstraints gc;

    Statement myStatement;

    AddUserDialog(Statement urStatement)
    {
    	myStatement = urStatement;

        gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(10,10,10,10);

        setLayout(new GridBagLayout());

        userLabel = new JLabel("Username: (3-15 Alphanumeric Characters)");
        passwordLabel = new JLabel("Password: (6-20 Alphanumeric charcters or symbols (@#$%_-))");
        verifyLabel = new JLabel("Verify Password:");
        firstNameLabel = new JLabel("First Name:");
        lastNameLabel = new JLabel("Last Name:");
        emailLabel = new JLabel("Email:");
        addressLabel = new JLabel("Address:");
        phoneLabel = new JLabel("Phone number: (ex. 123-456-7890 OR 1234567890)");
        memTypeLabel = new JLabel("Membership Type");

        userTF = new JTextField(30);
        userTF.getDocument().addDocumentListener(this);
        firstNameTF = new JTextField(30);
        firstNameTF.getDocument().addDocumentListener(this);
        lastNameTF = new JTextField(30);
        lastNameTF.getDocument().addDocumentListener(this);
        emailTF = new JTextField(30);
        emailTF.getDocument().addDocumentListener(this);
        addressTF = new JTextField(30);
        addressTF.getDocument().addDocumentListener(this);
        phoneTF = new JTextField(30);
        phoneTF.getDocument().addDocumentListener(this);

        passwordTF = new JPasswordField(30);
        verifyTF = new JPasswordField(30);

        addButton = new JButton("Add User");
        addButton.addActionListener(this);
        addButton.setEnabled(false);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        memTypeSelection = new JComboBox<String>(memTypes);

        addComponents(gc);

        this.setVisible(true);
        this.setSize(500,500);

        pack(); // method does stuff for GridBagLayouts..

    }// end AddUserDialog Constructor

//==================================================================================================

    public void actionPerformed(ActionEvent e)
    {

        if (e.getSource() == cancelButton)
            this.dispose();

        else if (e.getSource() == addButton)
        {
        	validSignup = true; // will be true unless made false by some error
        	query = "";
        	errorStatement = "The following errors have occurred: \n"; // in case of errors, this will be appended

            // get member type selection and assign a quota to send to database
        	populateFields();

            switch(memTypeSelection.getSelectedIndex()){
                case 0: memberQuota = 5;
                break;

                case 1: memberQuota = 10;
                break;

                case 2: memberQuota = 15;
                break;

                case 3: memberQuota = 20;
                break;

                case 4: memberQuota = 25;
                break;
            }

            validateFields();

            if (validSignup)
            {
				lastName = lastName.replaceAll("'", "\\\\'");
				address = address.replaceAll("'", "\\\\'");

            	query = "INSERT INTO Members VALUES ("
            			+ "'" + userName + "',"
            			+ "'" + password + "',"
            			+ "'" + email + "',"
            			+ "'" + firstName + "',"
            			+ "'" + lastName + "',"
            			+ "'" + address + "',"
            			+ "'" + memberQuota + "',"
            			+ "'" + phoneNumber + "');";
            	try
            	{
            		myStatement.executeUpdate(query);
            		JOptionPane.showMessageDialog(this, userName + " successfully added to the database!");
            		this.dispose();
            	}
            	catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}

            } // end if addbutton clicked

            else
            	JOptionPane.showMessageDialog(this, errorStatement, "User Signup Error", JOptionPane.ERROR_MESSAGE);
        }
    }

//==================================================================================================

    private void populateFields()
    {
        passwordArray = passwordTF.getPassword();
        password = new String(passwordArray);

        verifyPasswordArray = verifyTF.getPassword();
        verifyInput = new String(verifyPasswordArray);

        userName    = userTF.getText().trim();
        firstName   = firstNameTF.getText().trim();
        lastName    = lastNameTF.getText().trim();
        email       = emailTF.getText().trim();
        address     = addressTF.getText().trim();
        phoneNumber = phoneTF.getText().trim();

    } // end populate fields

//==================================================================================================

    public void insertUpdate(DocumentEvent e)
    {
    	populateFields();

    	if( !password.equals("") && !verifyInput.equals("") && !firstName.equals("") &&
    			!lastName.equals("") &&  !email.equals("") && !address.equals("") && !phoneNumber.equals("") )
        {
            addButton.setEnabled(true);
        }
    }

    public void removeUpdate(DocumentEvent e)
    {
    	populateFields();

    	if( password.equals("") || verifyInput.equals("") || firstName.equals("") ||
    			lastName.equals("") ||  email.equals("") || address.equals("") ||  phoneNumber.equals("") )
        {
            addButton.setEnabled(false);
        }
    }

    public void changedUpdate(DocumentEvent e){};

//==================================================================================================
    private void validateFields()
    {
    	validateUsername();
        validatePassword();

        if(!Validate.isFirstNameValid(firstName))
        {
        	//System.out.println("Invalid first name input");
        	validSignup = false;
        	errorStatement = errorStatement + "    -Invalid First Name input\n";
        }


        if(!Validate.isLastNameValid(lastName))
        {
        	//System.out.println("Invalid last name input");
        	validSignup = false;
        	errorStatement = errorStatement + "    -Invalid Last Name input\n";
        }

        if(address.length() < 4)
        {
        	//System.out.println("Invald address input");
        	validSignup = false;
        	errorStatement = errorStatement + "    -Invalid Address input\n";
        }

        validateEmail();

        if(!Validate.isPhoneValid(phoneNumber))
        {
        	//System.out.println("Invalid phone input");
        	validSignup = false;
        	errorStatement = errorStatement + "    -Invalid phone input\n";
        }
    } // end validateFields()

//==================================================================================================

    private void validateUsername()
    {
    	ResultSet rs;

        if(!Validate.isUsernameValid(userName))
        {
        	//System.out.println("Invalid username input");
        	errorStatement = errorStatement + "    -Invalid Username Format\n";
        	validSignup = false;
        }

        else
        {
        	query = "SELECT * FROM Members m WHERE m.userID = '" + userName + "';";

        	try
        	{

				rs = myStatement.executeQuery(query);

				if(rs.next()) // there's something returned (username exists)
				{
					//System.out.println("Username already exists!");
					errorStatement = errorStatement + "    -Username already exists\n";
					validSignup = false;
				}
				rs.close();
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        		validSignup = false;
        	}
        }
    } // end validateUsername()

//==================================================================================================

    private void validateEmail()
    {
    	ResultSet rs;

        if(!Validate.isEmailValid(email))
        {
        	//System.out.println("Invalid email input");
        	errorStatement = errorStatement + "    -Invalid email Format\n";
        	validSignup = false;
        }

        else
        {
        	query = "SELECT * FROM Members m WHERE m.email = '" + email + "';";

        	try
        	{

				rs = myStatement.executeQuery(query);

				if(rs.next()) // the email already exists
				{
					//System.out.println("Email already exists!");
					errorStatement = errorStatement + "    -Email already exists\n";
					validSignup = false;
				}
				rs.close();
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        		validSignup = false;
        	}
        }
    } // end validateEmail()

//==================================================================================================

    private void validatePassword()
    {
        if (!password.equals(verifyInput))
        {
        	//System.out.println(password + " " + verifyInput + ", passwords do not match!");
        	errorStatement = errorStatement + "    -Passwords do not match\n";
        	validSignup = false;
        	return;
        }

        if (!Validate.isPasswordValid(password))
        {
        	//System.out.println("Invalid password format");
        	errorStatement = errorStatement + "    -Invalid password format\n";
        	validSignup = false;
        }
    } // end validatePassword()

//==================================================================================================

    private void addComponents(GridBagConstraints gc)
    {
        // inserts all components where they should be on the dialog

        gc.gridx = 0;
        gc.gridy = 0;
        add(userLabel,gc);

        gc.gridx = 1;
        gc.gridy = 0;
        add(userTF,gc);

        gc.gridx = 0;
        gc.gridy = 1;
        add(passwordLabel,gc);

        gc.gridx = 1;
        gc.gridy = 1;
        add(passwordTF,gc);

        gc.gridx = 0;
        gc.gridy = 2;
        add(verifyLabel,gc);

        gc.gridx = 1;
        gc.gridy = 2;
        add(verifyTF,gc);

        gc.gridx = 0;
        gc.gridy = 3;
        add(firstNameLabel,gc);

        gc.gridx = 1;
        gc.gridy = 3;
        add(firstNameTF,gc);

        gc.gridx = 0;
        gc.gridy = 4;
        add(lastNameLabel,gc);

        gc.gridx = 1;
        gc.gridy = 4;
        add(lastNameTF,gc);

        gc.gridx = 0;
        gc.gridy = 5;
        add(emailLabel,gc);

        gc.gridx = 1;
        gc.gridy = 5;
        add(emailTF,gc);

        gc.gridx = 0;
        gc.gridy = 6;
        add(addressLabel, gc);

        gc.gridx = 1;
        gc.gridy = 6;
        add(addressTF, gc);

        gc.gridx = 0;
        gc.gridy = 7;
        add(phoneLabel,gc);

        gc.gridx = 1;
        gc.gridy = 7;
        add(phoneTF,gc);

        gc.gridx = 0;
        gc.gridy = 8;
        add(memTypeLabel,gc);

        gc.gridx = 1;
        gc.gridy = 8;
        add(memTypeSelection,gc);

        gc.gridx = 0;
        gc.gridy = 9;
        add(addButton,gc);

        gc.gridx = 1;
        gc.gridy = 9;
        add(cancelButton,gc);
    }// end addComponents()
}
