package BlackJack2;

//複数の戻り値を使用するためのクラス
class LoginData{
	static String nameData ="";
	static int moneyData = 0;
	static boolean log = false;
	static boolean guest = false;
}

//実行用メインクラス
public class BlackJack2 {

	public static void main(String[] args) {

		int keepGame = 0;		//ゲームを続行するかの判定

		GameBlack bj = new GameBlack();

		while(keepGame == 0) {
			keepGame = bj.playGame();
		}
		System.out.println("\nゲームを終了します");
		System.exit(0);
	}

}
