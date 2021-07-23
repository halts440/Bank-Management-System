package def_pkg;

public class Transaction_History {
	private String serial_no;
	private String amount;
	private String type;
	private String date;
	private String time;
	private String account_num;
	private String recv_acc_num;
	private String cheque_num;
	
	// default constructor
	public Transaction_History() {
		this.serial_no = "";
		this.amount = "";
		this.type = "";
		this.date = "";
		this.time = "";
		this.account_num = "";
		this.recv_acc_num = "";
		this.cheque_num = "";
	}
	
	// parameterized constructor
	public Transaction_History(String serial_no, String amount, String type, String date, String time, String account_num, String recv_acc_num, String cheque_num) {
		this.serial_no = serial_no;
		this.amount = amount;
		this.type = type;
		this.date = date;
		this.time = time;
		this.account_num = account_num;
		this.recv_acc_num = recv_acc_num;
		this.cheque_num = cheque_num;
	}
	
	public String getSerialNo() {
		return this.serial_no;
	}
	
	public String getAmount() {
		return this.amount;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public String getTime() {
		return this.time;
	}
	
	public String getAccountNumber() {
		return this.account_num;
	}
	
	public String getRecvAccNum() {
		return this.recv_acc_num;
	}
	
	public String getChequeNum() {
		return this.cheque_num;
	}
}
