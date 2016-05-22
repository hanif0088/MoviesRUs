import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.lang.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Validate
{
	static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";//"Username length should be 3 to 15 with any A-Z or a-z or 0-9 or with _-"
	static final String PASSWORD_PATTERN = "^[A-Za-z0-9@#$%_-]{6,20}$";   //"Password length should be 6 to 20 with any A-Z or a-z or 0-9 or @#$%_- "

	static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
										+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	static final String PHONE_PATTERN1 = "\\d{3}-\\d{3}-\\d{4}"; //only xxx-xxx-xxxx
	static final String PHONE_PATTERN2 = "\\d{10}"; //only xxxxxxxxxx (10 digits)
	//==============================================================
	public static boolean isUsernameValid(String username)
	{
		Matcher 		matcher;
		Pattern 		pattern;
		pattern = Pattern.compile(USERNAME_PATTERN);
		matcher = pattern.matcher(username);
		return matcher.matches();
	}
	//=====================================================================
	public static boolean isPasswordValid(String password)
	{
		Matcher 		matcher;
		Pattern 		pattern;
		pattern = Pattern.compile(PASSWORD_PATTERN);
		matcher = pattern.matcher(password);
		return matcher.matches();
	}
	//=====================================================================
	public static boolean isFirstNameValid(String firstName)
	{
		return firstName.matches( "[a-zA-Z]*" );//only upper and lower case alphabets
	}
	//=====================================================================
	public static boolean isLastNameValid(String lastName)
	{
		return lastName.matches( "[a-zA-z]+([ '-][a-zA-Z]+)*" );//only upper and lower case alphabets also spaces ' - are allowed
	}
	//=====================================================================
	public static boolean isEmailValid(String email)
	{
		Matcher 		matcher;
		Pattern 		pattern;
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}
	//=====================================================================
	public static boolean isPhoneValid(String phoneNo)
	{
		if (phoneNo.matches(PHONE_PATTERN1))
			return true;
		else if (phoneNo.matches(PHONE_PATTERN2))
			return true;
		else
			return false;
	}
	//======================================================================
	public static boolean isValidDate(String inDate)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		try
		{
			dateFormat.parse(inDate.trim());
		}
		catch (ParseException pe)
		{
			return false;
		}
		return true;
	}
	//=====================================================================
	public static boolean isQuantityValid(String quantity)
	{
		if (quantity.matches("\\d{1,8}"))//numeric digits only(minimum 1 and maximum 8 digits)
			return true;
		else
			return false;
	}

	//=============================================================================
	public static int validateNumber(String numerals)
	{
		int value;

		if (numerals.equals(""))
			value = -1;

		else
		{
			try
			{
				value = Integer.parseInt(numerals);
			}

			catch(NumberFormatException nfe)
			{
				value = -1;
				System.out.println("Number Format Exception");
			}
		}

		return value;
	}
}