package def_pkg;

import java.util.List;

public class Manager {
	
	private String name;
	
	Manager() {
		name = "";
	}
	
	Manager(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	// other functions here
	public int createAccount(Client newClient, String type) {
		DB_Handler db = new DB_Handler();
		return db.DB_CreateAccount(newClient, type );
	}
	
	int block_account(int acc_num, String cnic)
	{
		DB_Handler db = new DB_Handler();
		int temp_client = db.get_client_id(acc_num);
		int temp_acc_type = db.get_account_status(acc_num);
		System.out.println("Corresponding client_id:"+temp_client+"  acc_type:"+temp_acc_type);
		
		if ( cnic.equals(db.get_cnic(temp_client)) == false)
		{
			return -1; 							//-1 means that the cnic and account number are of different accounts
		}
		else if (temp_acc_type == 0 || temp_acc_type == -1)
		{
			return -2;							//-2 means there is no active account with these details
		}
		else if (temp_acc_type == 2)
		{
			return -3;							//-3 means that the card is blocked
		}
		else
		{
			db.block_account(acc_num);
		}
		return 0;
	}
	
	int unblock_account(int acc_num, String cnic)
	{
		DB_Handler db = new DB_Handler();
		int temp_client = db.get_client_id(acc_num);
		int temp_acc_type = db.get_account_status(acc_num);
		System.out.println("Corresponding client_id:"+temp_client+"  acc_type:"+temp_acc_type);
		
		if ( cnic.equals(db.get_cnic(temp_client)) == false)
		{
			return -1; 							//-1 means that the cnic and account number are of different accounts
		}
		else if (temp_acc_type == 0 || temp_acc_type == -1)
		{
			return -2;							//-2 means there is no active account with these details
		}
		else if (temp_acc_type == 1)
		{
			return -3;							//-3 means that the card is blocked
		}
		else
		{
			db.unblock_account(acc_num);
		}
		return 0;
	}
	
	int block_card(int acc_num, String cnic, String card_no)
	{
		DB_Handler db = new DB_Handler();
		
		int temp_client_id = db.get_client_id(acc_num);
		String temp_cnic = db.get_cnic(temp_client_id);
		int temp_card_no = db.get_card_num(acc_num);
		
		if (temp_cnic.compareTo(cnic) != 0)
		{
			return -1;						//-1 means no account with this details
		}
		else if (temp_card_no != Integer.parseInt(card_no))
		{
			return -2;
		}
		else if ( db.is_card_active(temp_card_no) == false )
		{
			return -3;
		}
		else
		{
			db.block_card(temp_card_no);
		}
		return 0;
	}
	
	int unblock_card(int acc_num, String cnic, String card_no)
	{
		DB_Handler db = new DB_Handler();
		
		int temp_client_id = db.get_client_id(acc_num);
		String temp_cnic = db.get_cnic(temp_client_id);
		int temp_card_no = db.get_card_num(acc_num);
		
		if (temp_cnic.compareTo(cnic) != 0)
		{
			return -1;						//-1 means no account with this details
		}
		else if (temp_card_no != Integer.parseInt(card_no))
		{
			return -2;
		}
		else if ( db.is_card_active(temp_card_no) == true )
		{
			return -3;
		}
		else
		{
			db.unblock_card(temp_card_no);
		}
		return 0;
	}
	
	int close_account(String account_num, String cnic)
	{
		DB_Handler db = new DB_Handler();	
		int temp_client_id = db.get_client_id(Integer.parseInt(account_num));
		
		if ( cnic.equals(db.get_cnic(temp_client_id)) )
		{
			db.close_account(Integer.parseInt(account_num));
			return 0;
		}
		else
			return -1;
	}
	
	public Client getClientInfo(String acc_num) {
		DB_Handler db = new DB_Handler();	
		Client client = db.searchClient2(acc_num);
		return client;
	}
	
	public Bank_Account getAccountInfo(String acc_num) {
		DB_Handler db = new DB_Handler();	
		Bank_Account account = db.searchAccount2(acc_num);
		return account;
	}
	
	public void updateClientInfo(String client_id, String phone, String email, String address ) {
		DB_Handler db = new DB_Handler();	
		db.updateClientInfo(client_id, phone, email, address);
	}
	
	public String getAccNum(String CNIC) {
		DB_Handler db = new DB_Handler();	
		String acc_num = "";
		acc_num = db.getAccNum(CNIC);
		return acc_num;
	}
	
	// end of class
}
