package BlackJack2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

//グラフィック表示用クラス
class GPanel extends JPanel {
	public void paintComponent(Graphics g)
	{
	 super.paintComponent(g);
//	 トランプ画像の表示位置
	 int u = getSize().width/2 - (((GameBlack.userCount-1)*25)+50);
	 int d = getSize().width/2 - (((GameBlack.dealerCount-1)*25)+50);
//	 数値を表示させるため文字列へ変換
	 String ustr = Integer.toString(GameBlack.userSum);
	 String dstr = Integer.toString(GameBlack.dealerSum);
	 String bet = Integer.toString(GameBlack.betMoney);
	 String mo = Integer.toString(GameBlack.money);
	 String pay = Integer.toString(GameBlack.payout);

	 Font font1 = new Font("HGS創英ﾌﾟﾚｾﾞﾝｽEB",Font.PLAIN,20);
	 Font font2 = new Font("HGS創英ﾌﾟﾚｾﾞﾝｽEB",Font.BOLD,40);
	 Font font3 = new Font("HGS創英ﾌﾟﾚｾﾞﾝｽEB",Font.BOLD,100);

	 g.setFont(font1);
//	 背景の緑の描写
	 g.setColor(new Color(00,99,44));
	 g.fillRect(0, 0, getSize().width - 1, getSize().height - 1);
//	 ユーザー名とディーラーの名前の背景
	 g.setColor(new Color(20,20,99));
	 g.fillRoundRect(20, 10, 200, 50,10,10);
	 g.fillRoundRect(20, 600, 200, 50,10,10);
	 g.setColor(Color.white);
	 g.drawRoundRect(20, 10, 200, 50,10,10);
	 g.drawRoundRect(20, 600, 200, 50,10,10);
//	 現在ステータス表示の背景
	 g.setColor(new Color(85,50,0)); g.fillRect(620, 500, 150, 150);
	 g.setColor(Color.orange); g.drawLine(620, 575, 770, 575);
	 g.setColor(Color.gray); g.drawLine(620, 535, 770, 535); g.drawLine(620, 610, 770, 610);
	 g.setColor(Color.orange); g.drawRect(620, 500, 150, 150);

	 for(int i=0; i<GameBlack.userCount; i++ ) {
		 g.drawImage(GameBlack.im[GameBlack.user.get(i)], u, 420, 100, 150, this);
		 u += 50;
	 }

	 for(int i=0; i<GameBlack.dealerCount; i++) {
	 	g.drawImage(GameBlack.im[GameBlack.dealer.get(i)], d, 100, 100, 150, this);
	 	d += 50;
	}
	 if(GameBlack.userBJ == true) {
		 g.setFont(font2);
		 g.setColor(Color.white);
		 g.drawString("BLACK JACK" , getSize().width/2 - (g.getFontMetrics().stringWidth("BLACK JACK")/2), 610);
		 g.setFont(font1);
	 }else if(GameBlack.userSum > 21) {
		 g.setFont(font2);
		 g.setColor(Color.black);
		 g.drawString("BUST" , getSize().width/2 - (g.getFontMetrics().stringWidth("BUST")/2), 610);
		 g.setFont(font1);
	 }else if(GameBlack.hit == true) {
		 g.setFont(font2);
		 g.setColor(Color.red);
		 g.drawString("HIT" , getSize().width/2 - (g.getFontMetrics().stringWidth("HIT")/2), 610);
		 g.setFont(font1);
	 }else if(GameBlack.stand == true) {
		 g.setFont(font2);
		 g.setColor(Color.blue);
		 g.drawString("STAND" , getSize().width/2 - (g.getFontMetrics().stringWidth("STAND")/2), 610);
		 g.setFont(font1);
	 }

//	 ディーラー側の表示
	 if(GameBlack.dealerBJ == true) {
		 g.setFont(font2);
		 g.setColor(Color.white);
		 g.drawString("BLACK JACK" , getSize().width/2 - (g.getFontMetrics().stringWidth("BLACK JACK")/2), 90);
		 g.setFont(font1);
	 }
	 if(GameBlack.dealerSum > 21) {
		 g.setFont(font2);
		 g.setColor(Color.black);
		 g.drawString("BUST" , getSize().width/2 - (g.getFontMetrics().stringWidth("BUST")/2), 90);
		 g.setFont(font1);
	 }

//	 ゲーム結果の勝敗の表示
	 if(GameBlack.draw == true) {
		 g.setFont(font3);
		 g.setColor(new Color(50,180,255));
		 g.drawString("DRAW" , getSize().width/2 - (g.getFontMetrics().stringWidth("DRAW")/2), 400);
		 g.setFont(font1);
	 }else if(GameBlack.win == true) {
		 g.setFont(font3);
		 g.setColor(new Color(250,210,0));
		 g.drawString("WIN" , getSize().width/2 - (g.getFontMetrics().stringWidth("WIN")/2), 400);
		 g.setFont(font1);
	 }else if(GameBlack.lose == true) {
		 g.setFont(font3);
		 g.setColor(new Color(255,0,150));
		 g.drawString("LOSE" , getSize().width/2 - (g.getFontMetrics().stringWidth("LOSE")/2), 400);
		 g.setFont(font1);
	 }
//	 ゲーム結果の詳細表示
	 if(GameBlack.result == true) {
		 g.setColor(Color.red); g.setFont(new Font("HGS創英ﾌﾟﾚｾﾞﾝｽEB",Font.BOLD,30));
		 g.drawString("RESULT" , 695 - (g.getFontMetrics().stringWidth("RESULT")/2), 400);
		 g.setColor(Color.white); g.setFont(font1);
		 g.drawString("BET" , 620, 430);
		 g.drawString(bet , 770 - g.getFontMetrics().stringWidth(bet), 430);
		 g.drawString("PAYOUT", 620, 452);
		 g.drawString(pay , 770 - g.getFontMetrics().stringWidth(pay), 452);
		 g.setColor(Color.orange);
		 g.drawString("TOTAL", 620, 480);
		 g.drawString(Integer.toString(GameBlack.betMoney + GameBlack.payout) , 770 - g.getFontMetrics().stringWidth(Integer.toString(GameBlack.betMoney + GameBlack.payout)), 480);
	 }
//	 ユーザーとディーラーの名前表示
	 g.setColor(Color.white);
	 g.drawString(GameBlack.userName , 120 - (g.getFontMetrics().stringWidth(GameBlack.userName)/2), 635);
	 g.drawString("DEALER" , 120 - (g.getFontMetrics().stringWidth("DEALER")/2), 43);

//	 カードの合計数値の表示
	 g.setColor(Color.yellow); g.setFont(new Font("HGS創英ﾌﾟﾚｾﾞﾝｽEB",Font.BOLD,30));
	 if(GameBlack.userSum != 0)
	 	g.drawString(ustr , getSize().width/2 - (g.getFontMetrics().stringWidth(ustr)/2), 647);
	 if(GameBlack.dealerSum != 0)
	 	g.drawString(dstr , getSize().width/2 - (g.getFontMetrics().stringWidth(dstr)/2), 40);

//	 右下の所持金とベット額の表示
	 g.setFont(font1);
	 g.setColor(new Color(50,180,255));
	 g.drawString("BET" , 695 - (g.getFontMetrics().stringWidth("BET")/2), 525);
	 g.drawString("CHIPS" , 695 - (g.getFontMetrics().stringWidth("CHIPS")/2), 600);

	 g.setColor(Color.white);
	 g.drawString(bet , 695 - (g.getFontMetrics().stringWidth(bet)/2), 565);
	 g.drawString(mo , 695 - (g.getFontMetrics().stringWidth(mo)/2), 640);

	}
}

