package BlackJack2;

import java.awt.Image;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

//ゲームのメイン進行クラス
class GameBlack extends JFrame{
	static String userName = "";						//ユーザーの名前
	static Image im[] = new Image[53];					//トランプ画像を保存する

	static ArrayList<Integer> user = new ArrayList<>();			//ユーザーの持ち札のデータ
	static ArrayList<Integer> dealer = new ArrayList<>();			//ディーラーの持ち札のデータ
	private ArrayList<Integer> trumpCard = new ArrayList<Integer>();	//トランプの数値をいれたもの　ゲームごとにシャッフル
	static int userSum, dealerSum;						//ユーザーとディーラーのそれぞれの持ち札の合計
	static int userCount , dealerCount ;					//userとdealerのトランプ枚数カウント　paintComponent表示で使用
	static int drawTimes;							//トランプを引いた回数
	private boolean userAce, dealerAce;					//Aのカードを持っているか
	private int hold, holdCard;						//ディーラーの2枚目に引いたトランプデータを保存
	static int money;							//現在の所持金
	static int betMoney;							//ベットした金額
	static int payout;							//支払いされる金額
	static boolean stand;							//ユーザーがSTANDを実行しているかの判定
	static boolean hit;							//HITのアクションを行っているかの判定
	static boolean userBJ, dealerBJ;					//ブラックジャックになったかの判定
	static boolean draw,lose,win;						//リザルトでの判定でいずれかがtrueに変更しpaintComponentで反映
	boolean guest = false;							//「とにかくプレイ」でゲームをしている場合のSQL実行を回避する判定
	static boolean result;							//ゲームの結果が出ているかの判定　paintComponentで反映


	public GameBlack() {

		this.setSize(800, 700);
		this.setLocationRelativeTo(null);				// PC画面中央に表示させる
		this.setTitle("Black Jack");
		this.add(new GPanel());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        Toolkit tk = getToolkit();
        for(int i=0; i<=52; i++)
        	im[i] = tk.getImage("D:/java/eclipse/workspace/BlackJack/src/BlackJack2/trump_img/torannpu-illust" + i + ".png");

        setVisible(true);

        JTextField name = new JTextField(10);
        JTextField newname = new JTextField(10);
        JTextField pass = new JPasswordField(10);
        JTextField newpass = new JPasswordField(10);
        JTextField newpass2 = new JPasswordField(10);

        User_Login lg = new User_Login();

        JPanel myPanel = new JPanel();

        myPanel.add(new JLabel("ユーザー名："));
        myPanel.add(name);
        myPanel.add(Box.createVerticalStrut(15)); // スペーサー
        myPanel.add(new JLabel("パスワード："));
        myPanel.add(pass);

        JPanel myPanel2 = new JPanel();

        myPanel2.add(new JLabel("ユーザー名入力"));
        myPanel2.add(newname);
        myPanel2.add(Box.createHorizontalStrut(15));
        myPanel2.add(new JLabel("パスワード入力"));				// スペーサー
        myPanel2.add(newpass);
        myPanel2.add(new JLabel("もう一度パスワード入力"));
        myPanel2.add(newpass2);


        while(LoginData.log == false) {
        	String selectvalues[] = {"ログイン", "とにかくプレイ", "新規登録"};
        	int select = JOptionPane.showOptionDialog(this,myPanel,"ログイン画面",
        			JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,selectvalues,selectvalues[0]);

			switch(select) {
				case 0:
					lg.userInput(name.getText(), pass.getText());
					break;
				case 1:
					System.out.println("とにかくプレイ");
					LoginData.log = true;
					userName = "PLAYER";
					money = 10000;
					guest = true;
					return;

				case 2:
					System.out.println("新規登録");
					String selectvalues2[] = {"登録", "キャンセル"};

						int select2 = JOptionPane.showOptionDialog(this,myPanel2,"新規登録",
							JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,selectvalues2,selectvalues2[0]);
					if(select2 == 0 && newpass.getText().equals(newpass2.getText()) && !(newpass.getText().equals("")))
						lg.newUser(newname.getText(), newpass.getText());
					else if(select2 == 1){
						continue;
					}
					break;
			}
			if(LoginData.log == false)
				JOptionPane.showMessageDialog(this, new JLabel("ユーザー名　または　パスワードが違います"), "失敗", JOptionPane.ERROR_MESSAGE);
			else {
				userName = LoginData.nameData;
				money = LoginData.moneyData;
			}
        }
	}

	public int playGame() {
		user.clear(); dealer.clear(); trumpCard.clear();		//ユーザー、ディーラー、トランプカードのデータを初期化
		drawTimes = 0;
		userSum = 0; dealerSum = 0;
		userCount = 0; dealerCount = 0;
		betMoney = 0; payout = 0;
		hold = 0; holdCard = 0;
		boolean okay = false;
		stand = false; hit = false;
		draw = false; lose = false; win = false;
		userAce = false; dealerAce = false;
		userBJ = false; dealerBJ = false;
		result = false;
		repaint();
		if (money < 100) {
			JOptionPane.showMessageDialog(this, new JLabel("所持チップがありませんでしたので特別に5000チップ差し上げます。"), "救済", JOptionPane.INFORMATION_MESSAGE);
//			マネーの金額がなめらかに表示させる
			for(int n=0; n<5000; n+=10) {
				try {
					Thread.sleep(2);
				}catch(InterruptedException e) {}
				money += 10;
				repaint();
			}
		}
		System.out.println("\n現在の所持金：" + money + "円");

		for(int i=1; i<=52; i++) {
			trumpCard.add(i);
		}
		Collections.shuffle(trumpCard);	//トランプをシャッフルする

		String comment = "ベットしてください (100単位)";
		while(okay == false) {
			try {
				betMoney = Integer.parseInt(JOptionPane.showInputDialog(this, comment));
			}catch(Exception e) {comment = "数値を入力してください";}
			okay = true;

			if (money < betMoney) {
				comment = "チップが足りません！！";
				okay = false;
			}else if(betMoney < 100 || betMoney%100 != 0 ) {		//0やマイナスのベット入力防止
				comment = "ベットを正しく入力してください (100単位)";
				okay = false;
			}
		}

//		マネーの金額の増減をなめらかに表示させる
		for(int n=0; n<betMoney; n+=10) {
			try {
				Thread.sleep(2);
			}catch(InterruptedException e) {}
			money -= 10;
			repaint();
		}
//		初回それぞれのカードを2枚引く
		for (int i=0; i<2; i++) {
			try {								//一秒間停止させる
				Thread.sleep(1000);
			}catch(InterruptedException e) {}
			this.readUser();

			try {								//一秒間停止させる
				Thread.sleep(1000);
			}catch(InterruptedException e) {}
			this.readDealer();
		}

		if(userSum == 21) {
			System.out.println("Ｂ　Ｌ　Ａ　Ｃ　Ｋ　　Ｊ　Ａ　Ｃ　Ｋ　！");
			stand = true;		//ユーザーの合計値が既に２１になっていれば自動でSTAND扱いにする
			userBJ = true;
			repaint();
		}

		//コンソール表示用の呼び出し
		dealerShow();
		userShow();


		int pressKey = 5;

		while(stand == false) {
			String selectvalues[] = {"ＨＩＴ", "ＳＴＡＮＤ"};
			int select = JOptionPane.showOptionDialog(this,"どうされますか？","HIT or STAND?",
					JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,selectvalues,selectvalues[0]);

			if (select == JOptionPane.CLOSED_OPTION){
				stand = false;
			}else{
				pressKey = select;
			}

			switch(pressKey) {
				case 0:
					System.out.println("ＨＩＴ");
					hit = true;
					repaint();
					try {
						Thread.sleep(1500);
					}catch(InterruptedException e) {}
					this.readUser();
					hit = false;
					try {
						Thread.sleep(1000);
					}catch(InterruptedException e) {}
					System.out.println("\t" + user.get(userCount-1));
					System.out.println("ＴＯＴＡＬ　" + userSum);
					if(userSum > 21) {
						System.out.println("ＢＵＳＴ！");
						stand = true;
					}else if(userSum == 21) {
						stand = true;
					}
					break;
				case 1:
					System.out.println("ＳＴＡＮＤ！");
					stand = true;
					break;
			}
			repaint();
		}
		repaint();
		try {
			Thread.sleep(1000);
		}catch(InterruptedException e) {}

		dealerShow();		//コンソール表示用

		dealer.set(1,holdCard);
		dealerSum += hold;
		if(dealerSum == 21)
			dealerBJ = true;
		repaint();
		while(dealerSum < 17) {
			System.out.println("合計が17未満なので、ディーラーがカードを引きます\n");
			try {
				Thread.sleep(1500);
			}catch(InterruptedException e) {}
			readDealer();
			System.out.println("\t" + dealer.get(dealerCount-2) + "\t" + dealer.get(dealerCount-1));
			System.out.println("ＴＯＴＡＬ　" + dealerSum);
			if(dealerSum > 21 ) {
				System.out.println("ディーラーＢＵＳＴ！");
			}
		}

		resultGame();
		System.out.print("\nゲームを続けますか？　１　ＹＥＳ　　other key　ＮＯ　－＞");
		String selectvalues[] = {"はい", "いいえ"};
		int select = JOptionPane.showOptionDialog(this,
			      "ゲームを続けますか？",
			      "Retry?",
			      JOptionPane.YES_NO_OPTION,
			      JOptionPane.QUESTION_MESSAGE,
			      null,
			      selectvalues,
			      selectvalues[0]
			    );

			    if (select == JOptionPane.CLOSED_OPTION){
			    	stand = false;
			    }else if(select == 1) {
//			    	ダイアログで「いいえ」を選択したらウィンドウを閉じて終了
			    	this.setVisible(false);
			    }else{

			    }
			    pressKey = select;
		return pressKey;
	}

	//ゲームの結果を表示等をするメソッド
	public void resultGame()
	{
		try {
			Thread.sleep(1500);
		}catch(InterruptedException e) {}

		System.out.println("\n\nーーーRESULTーーー\n");

		if(userSum > 21) {
			System.out.println(userName + "\t\t\tＢＵＳＴ");
		}else if(userBJ == true){
			System.out.println(userName + "\t\t\tＢＪ");
		}else {
			System.out.println(userName + "\t\t\t" + userSum);
		}

		if(dealerSum > 21) {
			System.out.println("ディーラー\t\tＢＵＳＴ");
		}else if(dealerBJ == true){
			System.out.println("ディーラー\t\tＢＪ");
		}else {
			System.out.println("ディーラー\t\t" + dealerSum);
		}

		if (dealerSum <= 21 && userSum <= 21 && dealerSum == userSum && userBJ == false && dealerBJ == false || userBJ == true && dealerBJ == true) {
			System.out.println("Ｄ　Ｒ　Ａ　Ｗ　！");
			draw = true; payout = 0;
		}else if(userSum < dealerSum && dealerSum <= 21 || userSum > 21 || userBJ == false && dealerBJ == true) {
			System.out.println("Ｌ　Ｏ　Ｓ　Ｅ　！");
			lose = true; payout -= betMoney;
		}else if(userSum > dealerSum && userSum <= 21 || dealerSum >21 || userBJ == true && dealerBJ == false) {
			System.out.println("Ｗ　Ｉ　Ｎ　！");
			win = true;
			if(userBJ == true && dealerBJ == false) {
				payout = (int)(betMoney*1.5);
			}else {
				payout = betMoney;
			}
		}

		try {
			Thread.sleep(1000);
		}catch(InterruptedException e) {}

		result = true;
		repaint();

//		マネーの金額の増減をなめらかに表示させる
		for(int n=0; n<(betMoney+payout); n+=10) {
			try {
				Thread.sleep(2);
			}catch(InterruptedException e) {}
			money += 10;
			repaint();
		}
		try {
			Thread.sleep(2000);
		}catch(InterruptedException e) {}

		if (money < 100) {
			System.out.println("ＧＡＭＥＯＶＥＲ");
			JOptionPane.showMessageDialog(this, new JLabel("ＧＡＭＥ　ＯＶＥＲ"), "GAME OVER", JOptionPane.INFORMATION_MESSAGE);
		}

		System.out.println("\n現在の所持金　" + money + "円");

		if(guest == false) {		//ゲストでなければデータベースの所持金更新
			try {
	            Connection conn =
	                DriverManager.getConnection("jdbc:mysql://localhost/blackjack_data?"+
	                                "useUnicode=true&characterEncoding=utf8&user=root&password=");

	            Statement stmt = conn.createStatement();

	            stmt.executeUpdate		//所持金データをゲーム終了時にデータベースを更新
	    				("UPDATE money_data SET money = " + money + " WHERE name = '" + userName + "'");

			}catch (Exception e) {
				System.out.println("エラー");
	            e.printStackTrace();
			}
		}
	}


	public void readUser()		//ユーザーがトランプを引いたときに呼び出すメソッド
	{
//		引いたトランプをユーザーの手札の配列に入れる
		int drawTrump = trumpCard.get(drawTimes);
		user.add(drawTrump);
//		トランプの数値を判定して合計に加算する
		if(drawTrump%13 >= 10 || drawTrump%13 == 0) {
			userSum += 10;
		}else if (drawTrump%13 == 1){
			if(userSum >= 11 || userAce == true) {
				userSum += 1;
			}else {
				userSum += 11;
				userAce = true;
			}
		}else {
			userSum += drawTrump%13;
		}
		if(userSum>21 && userAce == true) {
			userSum -= 10;
			userAce = false;
		}
		userCount += 1;
		drawTimes += 1;
		repaint();
	}


	public void readDealer()		//ディーラーがトランプを引いたときに呼び出すメソッド
	{
		int addSum;
//		引いたトランプをディーラーの手札として配列に入れる
		int drawTrump = trumpCard.get(drawTimes);
		dealer.add(drawTrump);
//		トランプの数値を判定して合計に加算する
		if(drawTrump%13 >= 10 || drawTrump%13 == 0) {
			addSum = 10;
		}else if (drawTrump%13 == 1){
			if(dealerSum >= 11 || dealerAce == true) {
				addSum = 1;
			}else {
				addSum = 11;
				dealerAce = true;
			}
		}else {
			addSum = drawTrump%13;
		}
		if(dealerSum>21 && dealerAce == true) {
			addSum = 10;
			dealerAce = false;
		}
		dealerCount += 1;
//		ディーラーのトランプの2枚目をhold変数に逃がしてトランプを伏せた表示する為の処理
		if(dealerCount ==2) {
			hold = addSum;
			holdCard = dealer.get(1);
			dealer.set(1, 0);
			addSum = 0;
		}
		dealerSum += addSum;
		drawTimes += 1;
		repaint();
	}

//	コンソール表示用のメソッド
	public void userShow()
	{
		System.out.println("\n＜＜ 　" + userName + "　の手札＞＞");
		for(int i=0; i<userCount; i+=2) {
			try {
				Thread.sleep(1000);
			}catch(InterruptedException e) {}
			System.out.println("\t" + user.get(i) + "\t" + user.get(i+1));
		}
		System.out.println("ＴＯＴＡＬ　" + userSum);
	}

//	コンソール表示用のメソッド
	public void dealerShow()
	{
		System.out.println("\n＜＜　ディーラー　の手札＞＞");
		for(int k=0; k<dealerCount; k+=2) {
			try {
				Thread.sleep(1000);
			}catch(InterruptedException e) {}
			if (k == 2 && stand == false) {
				System.out.println("　ｘｘｘｘ");
			}else {
				System.out.println("\t" + dealer.get(k) + "\t" + dealer.get(k+1));
			}
		}
		if(dealerCount != 4 || stand == true)
			System.out.println("ＴＯＴＡＬ　" + dealerSum);
	}
}

