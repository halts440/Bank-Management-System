package def_pkg;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.itextpdf.html2pdf.HtmlConverter;

public class DB_Handler {
	
	private String url = "jdbc:mysql://localhost:3307/bank_schema";       
	private String username = "root";
	private String password = "sunshine";
	private Connection conn;
	
	
	
	public DB_Handler() {    
        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established successfully!");
        }
        catch (SQLException e) {
            throw new IllegalStateException("Unable to connect to the database. " + e.getMessage());
        } 
	}
	
	
	
	public Login_Account signIn( String username, String password ) {
		Login_Account user = new Login_Account();
		try {	
			// finding the account in database
			String laQuery = "Select * From bank_schema.login_account Where username = \""+username+"\" and "
					+ "password = \""+password+"\"";
			System.out.println(laQuery);
			Statement laSt = conn.createStatement();
			ResultSet laRs = laSt.executeQuery(laQuery);
			if( laRs.next() ) {
	        	// removing old instance of bank account and adding new instance with information
				user = new Login_Account( laRs.getString("login_id"), laRs.getString("username"), "", laRs.getString("type") );
	        }
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while checking if account exists");
		}
		return user;
	}
	
	
	
	public Client getClient( String login_id ) {
		Client client = new Client();
		try {	
			// finding the account in database
			String cQuery = "Select * From bank_schema.client Where client_id = (select client_id from bank_schema.bank_account where "
					+ "login_id = "+login_id+")";
			System.out.println(cQuery);
			Statement cSt = conn.createStatement();
			ResultSet cRs = cSt.executeQuery(cQuery);
			if( cRs.next() ) {
	        	// removing old instance of bank account and adding new instance with information
				client = new Client( cRs.getString("client_id"), cRs.getString("f_name"), cRs.getString("l_name"), cRs.getString("father_name"), cRs.getString("mother_name"), cRs.getString("CNIC"), cRs.getString("DOB"), cRs.getString("phone"), cRs.getString("email"), cRs.getString("address") );
	        }
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while checking if account exists");
		}
		return client;
	}
	
	
	
	public Bank_Account getAccount( String login_id ) {
		Bank_Account account = new Bank_Account();
		try {	
			// finding the account in database
			String aQuery = "Select * from bank_schema.bank_account where login_id = "+login_id;
			System.out.println(aQuery);
			Statement aSt = conn.createStatement();
			ResultSet aRs = aSt.executeQuery(aQuery);
			if( aRs.next() ) {
	        	// removing old instance of bank account and adding new instance with information
				account = new Bank_Account( aRs.getString("acc_num"), aRs.getString("client_id"), aRs.getString("login_id"), aRs.getString("type"), aRs.getString("balance"), aRs.getString("status"), aRs.getString("opening_date"), aRs.getString("closing_date"), aRs.getString("card_num") );
	        }
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while checking if account exists");
		}
		return account;
	}
	
	
	
	public boolean is_card_active(int card_num)
	{
		try
		{
			String uaQuery = "Select * From bank_schema.card Where card_num = "+ card_num;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);
	        if( uaRs.next() )
	        {
	        	String card_status = uaRs.getString("status");
	        	if (card_status.compareTo("A") == 0 || card_status.compareTo("a")==0)
	        	{
	        		return true;
	        	}
	        	else
	        	{
	        		System.out.println("The card with card_num:" + card_num + " is blocked as status is neither A nor a");
	        		return false;
	        	}
	        }
	        else
	        	return false;
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong while verifying cnic");
		}
		return false;
	}
	
	
	
	public String add_cardless_entry(int acc_num, int card_num, int amount, String temp_pin)
	{
		Random rand = new Random();
		String temp_OTC = ( String.valueOf( (rand.nextInt(9999)+1) ) + String.valueOf( (rand.nextInt(9999)+1) ) + String.valueOf( (rand.nextInt(9999)+1) ) );
		try
		{
			String uaQuery = "Insert into bank_schema.cardless_withdrawl values (NULL, " +card_num +", " +amount +", \"" + temp_OTC + "\", \"" +temp_pin +"\", \"p\", CURDATE(), CURRENT_TIME() )";																	
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			uaSt.executeUpdate(uaQuery);
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong while adding entry to cardless table");
		}
		
		try
		{
			String uaQuery = "Select * from bank_schema.cardless_withdrawl where card_no = " + card_num + " and amount = " + amount + " and temp_pin = \"" + temp_pin + "\"";			
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);
			if (uaRs.next())
			{
				int temp_serial = uaRs.getInt("serial_no");
				if (temp_serial != 0)
					return temp_OTC;
				else
					return "";
			}
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong while retrieving OTP from database");
		}
		return "";
	}
	
	
	
	public void reduce_balance(int amount, int acc_num)
	{
		try
		{
			String uaQuery = "Update bank_schema.bank_account set balance = " + amount + " where acc_num = " + acc_num;			
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			uaSt.executeUpdate(uaQuery);
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong while verifying cnic");
		}
	}
	
	
	
	public int get_client_id(int acc_num)
	{
		try
		{
			String uaQuery = "Select client_id From bank_schema.bank_account Where acc_num = "+ acc_num;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);
	        if( uaRs.next() )
	        {
	        	return uaRs.getInt("client_id");	
	        }
	        else
	        	return -1;
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while getting client_id");
		}
		return -1;
	}
	
	
	
	public int get_account_status(int acc_num)
	{
		try {	
			// Check if client already exists
			String uaQuery = "Select * From bank_schema.bank_account Where acc_num = " + acc_num;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);
	        if( uaRs.next() ) {
	        		return uaRs.getInt("status");
	        }
	        else 
	        	return -1;
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while getting account_type");
		}
		return -1;
	}
	
	
	
	public String get_cnic(int client_id)
	{
		try
		{
			String uaQuery = "Select * From bank_schema.client Where client_id = "+ client_id;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);
	        if( uaRs.next() )
	        {
	        	return uaRs.getString("CNIC"); 
	        }
	        else
	        	return "";
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong while verifying cnic");
		}
		return "";
	}
	
	
	
	public void block_account(int acc_num)
	{
		try {	
			// Check if client already exists
			String uaQuery = "Update bank_schema.bank_account set status = 2 Where acc_num = " + acc_num;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			uaSt.executeUpdate(uaQuery);
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while getting current password");
		}
	}
	
	
	
	public void unblock_account(int acc_num)
	{
		try {	
			// Check if client already exists
			String uaQuery = "Update bank_schema.bank_account set status = 1 Where acc_num = " + acc_num;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			uaSt.executeUpdate(uaQuery);
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while getting current password");
		}
	}
	
	
	
	public int get_card_num(int acc_num)
	{
		try
		{
			String uaQuery = "Select * From bank_schema.bank_account Where acc_num = "+ acc_num;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);
	        if( uaRs.next() )
	        {
	        	return uaRs.getInt("card_num");
	        }
	        else
	        	return 0;
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong while verifying cnic");
		}
		return 0;
	}
	
	
	
	public void block_card(int card_num)
	{
		try
		{
			String uaQuery = "Update bank_schema.card set status=\"B\" where card_num = " + card_num;			//B=Blocked		
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			uaSt.executeUpdate(uaQuery);
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong while blocking card");
		}
	}
	
	
	
	public void unblock_card(int card_num)
	{
		try
		{
			String uaQuery = "Update bank_schema.card set status=\"A\" where card_num = " + card_num;			//A=Active
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			uaSt.executeUpdate(uaQuery);
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong while unblocking card");
		}
	}
	
	
	
	public void close_account(int acc_num)
	{
		try
		{
			String uaQuery = "Update bank_schema.bank_account set status = 0 Where acc_num = "+ acc_num;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			uaSt.executeUpdate(uaQuery);
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong while verifying cnic");
		}
	}
	
	
	
	public boolean login_exists(int client_id)
	{
		try
		{
			String uaQuery = "Select * From bank_schema.bank_account Where client_id = "+ client_id;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);
	        if( uaRs.next() )
	        {
	        	int temp_login_id = uaRs.getInt("login_id");
	        	System.out.println("login id of client with client id=" + client_id +" is: " + temp_login_id);
	        	if ( temp_login_id == 0)
	        		return true;      
	        	else
	        		return false;
	        }
	        else
	        	return false;
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong while checking existanec of login account");
		}
		return false;
	}
	
	
	
	public boolean verify_cnic(int client_id, String cnic)
	{
		try
		{
			String uaQuery = "Select * From bank_schema.client Where client_id = "+ client_id;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);
	        if( uaRs.next() )
	        {
	        	String temp_cnic = uaRs.getString("CNIC"); 
	        	System.out.println("Client id=" + client_id + "\tCNIC=" + temp_cnic);
	        	if (  temp_cnic.equals(cnic))
	        		return true;
	        	else
	        		return false;
	        }
	        else
	        	return false;
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong while verifying cnic");
		}
		return false;
	}
	
	
	
	public int create_login(String username, String password)
	{
		try
		{
			String uaQuery = "INSERT INTO bank_schema.login_account VALUES (NULL,\"" + username + "\",\"" + password + "\",\"C\");"; 
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			uaSt.executeUpdate(uaQuery);
		}
		catch (SQLException e)
		{
			throw new IllegalStateException("Unable to insert values into login_account " + e.getMessage());
		}
		
		try
		{
			String uaQuery = "select login_id from bank_schema.login_account where username = \"" + username + "\" and password = \"" + password + "\"";
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);
	        if( uaRs.next() )
	        {
	        	return uaRs.getInt("login_id");
	        }
	        else
	        	return -1;
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong is creating login account");
		}
		return -1;
	
	}
	
	
	
	public void set_login_id(int login_id, int client_id)
	{
		try
		{
			String uaQuery = "update bank_schema.bank_account set login_id = " + login_id + " where client_id = " + client_id ;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			uaSt.executeUpdate(uaQuery);
			
			uaQuery = "select login_id from bank_schema.bank_account where client_id = " + client_id ;
			System.out.println(uaQuery);			
			uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);			
			if( uaRs.next() )
	        {
	        	System.out.println("The login_id of this account is " + uaRs.getInt("login_id") );
	        }
	        else
	        {
	        	System.out.println("The login id could not be found");
	        	return;
	        }
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong in setting login_id of account");
		}
	}
	

	
	public int DB_CreateAccount( Client new_client, String aType ) {
		int res = 0;
		try {
			// Check if client already exists
			String dQuery = "Select * From bank_schema.client Where cnic = \""+new_client.getCNIC()+"\"";
			System.out.println(dQuery);
			Statement dSt = conn.createStatement();
			ResultSet dRs = dSt.executeQuery(dQuery);
	        int dRecord = 0;
	        
	        while( dRs.next() )
	        	dRecord++;
	        System.out.println(dRecord);
			if( dRecord < 1 ) {
				System.out.println("No duplicate record");
				
				// Insert new client
				String ciQuery = "Insert Into bank_schema.client Values(NULL, \""+new_client.getFName()+"\", \""+
						new_client.getLName()+"\", \""+new_client.getFatherName()+"\", \""+
						new_client.getMotherName()+"\", \""+new_client.getCNIC()+"\", STR_TO_DATE(\""+
						new_client.getDOB()+"\", \"%d,%m,%Y\"), \""+
						new_client.getPhone()+"\", \""+new_client.getEmail()+"\", \""+new_client.getAddress()+"\")";
				System.out.println(ciQuery);
				Statement ciSt = conn.createStatement();
		        ciSt.executeUpdate(ciQuery);
		        
		        // Find the client id
				String cQuery = "Select * From bank_schema.client Where cnic = \""+new_client.getCNIC()+"\"";
				Statement cSt = conn.createStatement();
				ResultSet cRs = cSt.executeQuery(cQuery);
				if( cRs.next() ) {
					System.out.println("Client was added "+cRs.getString("client_id"));
					int c_id = cRs.getInt("client_id");
					// Make client's bank account
					String baQuery = "Insert Into bank_schema.bank_account Values(NULL, "+String.valueOf(c_id)+", NULL, \""+
							aType+"\", 0, 1, CURDATE(), NULL, NULL)";
					System.out.println(baQuery);
					Statement baSt = conn.createStatement();
			        baSt.executeUpdate(baQuery);
			        
			        res = 1;
				}   
			}
			else 
				res = 2;
		}
		catch (SQLException e) {
			System.out.println("Something went wrong");
		}
		return res;
	}
	
	
	
	public int TransferMoney( Client client, String rAccNum, int amount) {
		try {	
			// Check if receiving client already exists
			String uaQuery = "Select acc_num, balance From bank_schema.bank_account Where acc_num = "+rAccNum+" and status = 1";
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);
	        if( uaRs.next() ) {
	        	int recv_balance = uaRs.getInt("balance");
	        	String bQuery = "Select acc_num, balance From bank_schema.bank_account Where client_id = "+client.getClientID();
				System.out.println(bQuery);
				Statement bSt = conn.createStatement();
				ResultSet bRs = uaSt.executeQuery(bQuery);
				if( bRs.next() ) {
					String snd_acc_num = bRs.getString("acc_num");
					int snd_balance = bRs.getInt("balance");
					if( snd_balance >= amount ) {
						snd_balance -= amount;
						recv_balance += amount;
						
						String usQuery = "Update bank_schema.bank_account Set balance = "+snd_balance+" where client_id="+client.getClientID();		
						System.out.println("SQL-> "+usQuery);
						Statement usSt = conn.createStatement();
				        usSt.executeUpdate(usQuery);
				        
				        String urQuery = "Update bank_schema.bank_account Set balance = "+recv_balance+" where acc_num="+rAccNum;	
						System.out.println("SQL-> "+urQuery);
						Statement urSt = conn.createStatement();
				        urSt.executeUpdate(urQuery);
				        
				        // insert into transaction history
				        String thQuery = "Insert into bank_schema.transaction_history values(NULL,"+String.valueOf(amount)+", \"transfer\", CURDATE(), CURRENT_TIME(), "+snd_acc_num+", "+rAccNum+" , NULL )";		
						System.out.println("SQL-> "+thQuery);
						Statement thSt = conn.createStatement();
				        thSt.executeUpdate(thQuery);
				        
				        return 3;
					}
					else
						return 2;
				}
	        }
	        else
	        	return 1;
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while logging in");
		}
		
		return 0;
	}
	

	
	// Check if any account with provided account number and CNIC is present or not and returning the account if found
	Bank_Account searchAccount1( String accountNum, String CNIC ) {
		Bank_Account account = new Bank_Account();
		try {	
			// finding the account in database
			String aQuery = "Select * From bank_schema.bank_account Where acc_num = "+accountNum+" and "
					+ " client_id = (select client_id from bank_schema.client where CNIC=\""+CNIC+"\")";
			System.out.println(aQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(aQuery);
	        
			if( uaRs.next() ) {
	        	// removing old instance of bank account and adding new instance with information
				account = new Bank_Account( uaRs.getString("acc_num"), uaRs.getString("client_id"), uaRs.getString("login_id"), uaRs.getString("type"), uaRs.getString("balance"), uaRs.getString("status"), uaRs.getString("opening_date"), uaRs.getString("closing_date"), uaRs.getString("card_num") );
	        }
	        return account;
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while checking if account exists");
			return account;
		}
	}

	
	
	// Check if any client with provided CNIC and bank account number is present or not and returning that client information
	Client searchClient1( String accountNum, String CNIC ) {
		Client client = new Client();
		try {	
			// finding the client in database
			String aQuery = "Select * From bank_schema.client Where CNIC = \""+CNIC+"\" and ( select count(*)"
					+ " from bank_schema.bank_account where acc_num = "+accountNum+") = 1";
			System.out.println(aQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(aQuery);
	        
			if( uaRs.next() ) {
	        	// removing old instance of client and adding new instance with information
	        	client = new Client( uaRs.getString("client_id"), uaRs.getString("f_name"), uaRs.getString("l_name"), uaRs.getString("father_name"), uaRs.getString("mother_name"), uaRs.getString("CNIC"), uaRs.getString("DOB"), uaRs.getString("phone"), uaRs.getString("email"), uaRs.getString("address") );
	        }
	        return client;
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while checking if client exists");
			return client;
		}
	}
	
	
	
	// Check if any account with provided account number is present or not and returning the account if found
	Bank_Account searchAccount2( String accountNum ) {
		Bank_Account account = new Bank_Account();
		try {	
			// finding the account in database
			String aQuery = "Select * From bank_schema.bank_account Where acc_num = "+accountNum;
			System.out.println(aQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(aQuery);
	        
			if( uaRs.next() ) {
	        	// removing old instance of bank account and adding new instance with information
				account = new Bank_Account( uaRs.getString("acc_num"), uaRs.getString("client_id"), uaRs.getString("login_id"), uaRs.getString("type"), uaRs.getString("balance"), uaRs.getString("status"), uaRs.getString("opening_date"), uaRs.getString("closing_date"), uaRs.getString("card_num") );
	        }
	        return account;
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while checking if account exists");
			return account;
		}
	}

	
	
	// Check if any client with provided CNIC and bank account number is present or not and returning that client information
	Client searchClient2( String accountNum ) {
		Client client = new Client();
		try {	
			// finding the client in database
			String aQuery = "Select * From bank_schema.client Where client_id = ( select client_id "
					+ " from bank_schema.bank_account where acc_num = "+accountNum+") = 1";
			System.out.println(aQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(aQuery);
	        
			if( uaRs.next() ) {
	        	// removing old instance of client and adding new instance with information
	        	client = new Client( uaRs.getString("client_id"), uaRs.getString("f_name"), uaRs.getString("l_name"), uaRs.getString("father_name"), uaRs.getString("mother_name"), uaRs.getString("CNIC"), uaRs.getString("DOB"), uaRs.getString("phone"), uaRs.getString("email"), uaRs.getString("address") );
	        }
	        return client;
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while checking if client exists");
			return client;
		}
	}
	
	
	
	public int updateBalance( Bank_Account account, int balance, int t ) {
		int res = 0;
		try {	
			// update balance of account
			int temp_balance = Integer.valueOf( account.getBalance() ) + balance;
			String bsQuery = "Update bank_schema.bank_account Set balance = "+temp_balance+" where acc_num="+account.getAccountNum();		
			System.out.println("SQL-> "+bsQuery);
			Statement bsSt = conn.createStatement();
	        bsSt.executeUpdate(bsQuery);
	        
	        // check if it is updated successfully
        	String bQuery = "Select balance From bank_schema.bank_account Where acc_num = "+account.getAccountNum();
			System.out.println(bQuery);
			Statement bSt = conn.createStatement();
			ResultSet bRs = bSt.executeQuery(bQuery);
			if( bRs.next() ) {
				int b = bRs.getInt("balance");
				if( b == temp_balance ) {
					
					String type_ = "deposit";
					if( t == 2 ) {
						type_ = "withdraw";
						balance *= -1;
					}
					// make an entry in transaction history
					String thQuery = "Insert into bank_schema.transaction_history values(NULL,"+balance+", \""+type_+"\", CURDATE(), CURRENT_TIME(), "+account.getAccountNum()+", NULL, NULL  )";		
					System.out.println("SQL-> "+thQuery);
					Statement thSt = conn.createStatement();
			        bsSt.executeUpdate(thQuery);
		
					res = 1;
				}
			}
	        return res;
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while checking if account exists");
			return res;
		}	
	}
	
	
	
	public List<Transaction_History> getTransactions(String accNum, String From, String To) {
		List<Transaction_History> list=new ArrayList<Transaction_History>();  
		try {	
			// finding the transactions
			String tQuery = "Select * From bank_schema.transaction_history Where ( account_num = "+accNum+" or recv_acc_num = "+accNum+" ) and ( date between '"+From+"'"
					+ " and '"+To+"')";
			System.out.println(tQuery);
			Statement tSt = conn.createStatement();
			ResultSet tRs = tSt.executeQuery(tQuery);
			while( tRs.next() ) {
	        	// removing old instance of client and adding new instance with information
				Transaction_History th = new Transaction_History( tRs.getString("serial_no"), tRs.getString("amount"), tRs.getString("type"), tRs.getString("date"), tRs.getString("time"), tRs.getString("account_num"), tRs.getString("recv_acc_num"), tRs.getString("cheque_num") );
				System.out.print("@-");
				list.add( th );
	        }
		}
		catch (SQLException e) {
			System.out.println("Something went wrong");
		}
		return list;
	}
	
	
	
	public int createPDF( Client client, Bank_Account account, String From, String To ) { 
		int res = 0;
		try {	
			
			String html_start = "<body> <h1>ABC Bank</h1><h4>F9 Branch, Islamabad</h4>"
					+ "<p>"+client.getFName()+" "+client.getLName()+"<br>Account Number: "+account.getAccountNum()+"<br>Current Account"
					+ "<br>Transaction Dates: "+From+" - "+To+"</p><h4>E Bank Statement</h4>"
					+ "<table border=1 ><tr>"
					+ "<th width=300px>Serial No</th><th width=300px >Amount</th><th width=300px >Type</th><th width=300px >Date</th><th width=300px >Time</th><th width=300px >Account Num</th><th width=300px >Reciever Account Num</th><th width=300px >Cheque Num</th>"
					+ "</tr>";
			String html_end = "</table></body><style>table { text-align: center; }</style>";
			String html_data="";
			
			// finding the transactions
			String tQuery = "Select * From bank_schema.transaction_history Where ( account_num = "+account.getAccountNum()+" or recv_acc_num = "+account.getAccountNum()+" ) and ( date between '"+From+"'"
					+ " and '"+To+"')";
			System.out.println(tQuery);
			Statement tSt = conn.createStatement();
			ResultSet tRs = tSt.executeQuery(tQuery);
			int rows = 0;
			while( tRs.next() ) {
				rows++;
				html_data += "<tr><td>"+tRs.getString("serial_no")+"</td>"
						+ "<td>"+tRs.getString("amount")+"</td>"
						+ "<td>"+tRs.getString("type")+"</td>"
						+ "<td>"+tRs.getString("date")+"</td>"
						+ "<td>"+tRs.getString("time")+"</td>"
						+ "<td>"+tRs.getString("account_num")+"</td>"
						+ "<td>"+tRs.getString("recv_acc_num")+"</td>"
						+ "<td>"+tRs.getString("cheque_num")+"</td></tr>";
	        }
			if( rows > 0 ) {
				String html = html_start + html_data + html_end;
				try {
					String file_name = "E_Statement_" + java.time.LocalDateTime.now() + ".pdf";
					file_name = file_name.replaceAll(":", "_");
			    	HtmlConverter.convertToPdf( html, new FileOutputStream(file_name));
			        res = 1;
			    }
				catch( Exception e) {
					System.out.println("Unknown error");
				}
			}
		}
		catch (SQLException e) {
			System.out.println("Something went wrong");
		}
		return res;
	}
	
	
	
	public int chequeDeposit( String accNum, String chequeNum, int amount ) { 
		int res = 0;
		try {		
			// finding the account in database
			String aQuery = "Select * From bank_schema.bank_account Where acc_num = "+accNum;
			System.out.println(aQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(aQuery);
			if( uaRs.next() ) {
				if( Integer.valueOf( uaRs.getString("status")) == 0 )
				{
					res = 4; // closed
				}
				else if( Integer.valueOf( uaRs.getString("status")) == 2 )
				{
					res = 3; // blocked
				}
				else {
					int balance = amount + Integer.valueOf( uaRs.getString("balance") );
					// account is found
					String bsQuery = "Update bank_schema.bank_account Set balance = "+balance+" where acc_num="+accNum;		
					System.out.println("SQL-> "+bsQuery);
					Statement bsSt = conn.createStatement();
			        bsSt.executeUpdate(bsQuery);
				
			        // make an entry in transaction history
					String thQuery = "Insert into bank_schema.transaction_history values(NULL,"+balance+", \"deposit\", CURDATE(), CURRENT_TIME(), "+accNum+", NULL, "+chequeNum+")";		
					System.out.println("SQL-> "+thQuery);
					Statement thSt = conn.createStatement();
					thSt.executeUpdate(thQuery);
				
			        res = 1;  // successful
				}
			}
			else 
				res = 2; // not found
		}
		catch (SQLException e) {
			System.out.println("Something went wrong");
		}
		return res;
	}
	
	
	
	// update client info
	public void updateClientInfo(String client_id, String phone, String email, String address ) {
	 try {   
		 	String ucQuery = "Update bank_schema.client Set phone = \""+phone+"\" , email = \""+email+"\" , address = \""+ address
		 			+ "\" where client_id="+client_id;		
			System.out.println( ucQuery );
			Statement ucSt = conn.createStatement();
	        ucSt.executeUpdate(ucQuery);
        }
        catch (SQLException e) {
        	System.out.println("Something went wrong");
        }
	}
	
	public int getLoginID(int acc_num)
	{
		try
		{
			String uaQuery = "Select login_id From bank_schema.bank_account Where acc_num = "+ acc_num;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);
	        if( uaRs.next() )
	        {
	        	return uaRs.getInt("login_id");	
	        }
	        else
	        	return -1;
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while getting login_id");
		}
		return -1;
	}
	
	public String get_password(int login_id)
	{
		try {	
			// Check if client already exists
			String uaQuery = "Select * From bank_schema.login_account Where login_id = " + login_id;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(uaQuery);
	        if( uaRs.next() ) {
	        		return uaRs.getString("password");
	        }
	        else 
	        	return "";
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while getting current password");
		}
		return "";
	}
	
	public void change_password(String pass, int login_id)
	{
		try {	
			// Check if client already exists
			String uaQuery = "Update bank_schema.login_account set password = \"" + pass + "\" Where login_id = " + login_id;
			System.out.println(uaQuery);
			Statement uaSt = conn.createStatement();
			uaSt.executeUpdate(uaQuery);
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while getting current password");
		}
	}
	
	public String getName(String id) {
		String name = "";
		try
		{
			String eQuery = "Select * From bank_schema.employee Where login_id = "+ id;
			System.out.println(eQuery);
			Statement eSt = conn.createStatement();
			ResultSet eRs = eSt.executeQuery(eQuery);
	        if( eRs.next() )
	        {
	        	name = eRs.getString("f_name") + " " + eRs.getString("l_name");
	        }
		}
		catch (SQLException e)
		{
			System.out.println("Something went wrong while verifying cnic");
		}
		return name;
	}
	
	public String getBalance(String acc_num) {
		String b = "";
		try {	
			// finding the account in database
			String aQuery = "Select * From bank_schema.bank_account Where acc_num = "+acc_num;
			System.out.println(aQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(aQuery);
			if( uaRs.next() ) {
	        	 b = uaRs.getString("balance");
	        }
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while checking if account exists");
		}
		return b;
	}
	
	public String getAccNum(String CNIC) {
		String b = "";
		try {	
			String aQuery = "Select * From bank_schema.bank_account Where client_id = (select client_id from bank_schema.client where CNIC=\""+CNIC+"\")";
			System.out.println(aQuery);
			Statement uaSt = conn.createStatement();
			ResultSet uaRs = uaSt.executeQuery(aQuery);
			if( uaRs.next() ) {
	        	 b = uaRs.getString("acc_num");
	        }
		}
		catch (SQLException e) {
			System.out.println("Something went wrong while checking if account exists");
		}
		return b;
	}
	
	public void finalize() {
        try {   
        	System.out.println("Connection Closed");
            conn.close();
        }
        catch (SQLException e) {
            throw new IllegalStateException("Trying to close a not opened db connection" + e.getMessage());
        }
	}
	
}
