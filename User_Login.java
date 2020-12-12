package BlackJack2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

//ユーザーログインのクラス
class User_Login extends JFrame implements ActionListener{

	JPasswordField pass;
	//JPasswordField（Jパスワードフィールド）Passにに入力された内容が画面に表示されないようになる
	JTextField text;
	//JTextField はユーザーが入力を行うことが出来る入力ボックス

	public  void userInput(String n, String p) {

		String name = n;
		String pass = p;
		String dataPass = "";

		try {
		//tryは例外が発生したかを調べる
          Connection conn =
              DriverManager.getConnection("jdbc:mysql://localhost/blackjack_data?"+
                                          "useUnicode=true&characterEncoding=utf8&user=root&password=");

          Statement stmt = conn.createStatement();

          ResultSet rset = stmt.executeQuery		//データベースへアクセスして入力されたユーザーが存在していたら値（ユーザ名、パスワード、所持金）を返す
          		("SELECT user_master.name,user_master.pass,money_data.money from user_master,money_data where user_master.name=money_data.name and user_master.name = '" + name + "'");

          // if(rset.next())でSELECT検索で結果が返ってこないか（ユーザーが存在しないか）の条件式
          if(rset.next()) {
          	rset.beforeFirst();
          	while ( rset.next() ) {
          		LoginData.nameData = rset.getString(1);
	            	System.out.println("ユーザー名　：　" + rset.getString(1));
	                dataPass = rset.getString(2);
	                LoginData.moneyData = rset.getInt(3);
          	}
          }else {
          	LoginData.log = false;
          	return;
          }

//          入力したパスワードとデータベース上のパスワードが一致しているか
          if(pass.equals(dataPass)) {
          	System.out.println("パスワードが一致しました！");
          	JOptionPane.showMessageDialog(this, new JLabel( LoginData.nameData + "　でログインします"), "OK", JOptionPane.INFORMATION_MESSAGE);
          	System.out.println("\nユーザー名　：" + name + "　でログインします。");
          	LoginData.log = true;
          }else {
          	System.out.println("パスワードが一致しません！\n");
          }
		}catch (Exception e) {
			System.out.println("エラー");
          		e.printStackTrace();
		}
		return ;
	}

	public void actionPerformed(ActionEvent e){
	    String password = new String(pass.getPassword());

	    if (password.equals("0000")){
	      JLabel label = new JLabel("認証に成功しました");
	      JOptionPane.showMessageDialog(this, label, "成功", JOptionPane.INFORMATION_MESSAGE);
	    }else{
	      JLabel label = new JLabel("パスワードが違います");
	      JOptionPane.showMessageDialog(this, label, "失敗", JOptionPane.ERROR_MESSAGE);
	      pass.setText("");
	    }
	}
//	ユーザー新規登録処理のメソッド
	public void newUser(String n, String p) {

		LoginData.nameData = n;
		LoginData.moneyData = 10000;
		String pass = p;

		System.out.println("\nユーザー名　：" + LoginData.nameData);

		try {
          Connection conn =
              DriverManager.getConnection("jdbc:mysql://localhost/blackjack_data?"+
                              "useUnicode=true&characterEncoding=utf8&user=root&password=");

          Statement stmt = conn.createStatement();

          stmt.executeUpdate
          		("INSERT INTO user_master (name, pass) VALUES ( '" + LoginData.nameData  + "' , '" + pass + "')");
          stmt.executeUpdate
  				("INSERT INTO money_data (name, money) VALUES ( '" + LoginData.nameData  + "'," + LoginData.moneyData + ")");

          JOptionPane.showMessageDialog(this, new JLabel(LoginData.nameData + "　でユーザー登録しました"), "OK", JOptionPane.INFORMATION_MESSAGE);
          LoginData.log = true;

		}catch (Exception e) {
			System.out.println("エラー");
          		e.printStackTrace();
		}
		return;
	}
}

