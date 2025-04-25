

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Servlet implementation class BlackjackServlet
 */
@WebServlet("/BlackjackServlet")
public class BlackjackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public BlackjackServlet() {super();}
    
    public BlackjackGame2 gameInstance = new BlackjackGame2(200);
    private CardUtils cardUtils = new CardUtils("/CardGame_1"); // CHANGE THIS IF YOU SWITCH PROJECT NAME
    private String displayName = "TestGameDisplay";
    
    public boolean doInit = true;
    public int inputCheck = 0;
    // a way to detect refreshes. 
    // When a button is pressed, it is passed as the parameter "check", which is stored to B.
    // any time the screen refreshes, A increments.
    // If A and B are the same, it means the refresh was due to a button click.
    // technically this will fail if the user refreshes an absurd number of times but at that point you deserve the single frame of interaction.
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ctxtPath = request.getContextPath();
		PrintWriter out = response.getWriter(); // basics
		String header = "<head>\r\n"
				+ "    <meta charset=\"ISO-8859-1\">\r\n"
				+ "    <title>Blackjack? on the internet?!</title>\r\n"
				+ "    <link href=\'styles.css\' rel=\"stylesheet\" type=\"text/css\" media=\"all\">\r\n"
				+ "	   <script type='text/javascript' src = 'js/index_parallax.js' > </script>"
				+ "    <!-- js scripts go here if I need any  -->\r\n"
				+ "</head>";
		out.println(header);
		out.println("<body background = '/CardGame_1/img/bg/cardsTiling_0.png'>");
		
		
		
		out.println("<a href=\"index.jsp\" class=\"back-link\">Back to Games</a>");
		if(doInit) { // just started
			doInit = false;
			System.out.println("starting");
			request.setAttribute("check", inputCheck);
			
		}
		int oldInputCheck = inputCheck - 1; // default to reading as a refresh
		try {oldInputCheck = Integer.parseInt(request.getParameter("check"));} // if it was a button click, oldInputCheck = inputCheck
		catch(NumberFormatException e){
			System.out.println(" DISPLAY: Old Input Check was null. If no buttons have ever been pressed, this is normal. Otherwise, big problem.");
			System.out.println(" DISPLAY: Exception was (" + e + ")");
		}
		
		String gameInputs[] = {"refresh","none"}; // default behaviour: read interaction as a refresh.
		
		if(oldInputCheck == inputCheck) { // BUTTON
			String choice = request.getParameter("choice"); // if this is null but oic == ic, we are in trouble...
			System.out.println(" DISPLAY: Clicked!");
			gameInputs[0] = "click";
			gameInputs[1] = choice;
			
		}
		inputCheck = inputCheck + 1;
		
		// check for bet amount submitted
		String auxInputs[] = new String[16];
		String betAmount = request.getParameter("betAmount");
		if(betAmount==null){System.out.println(" DISPLAY: no betAmount");}
		else{
			auxInputs[0] = betAmount;
			System.out.println(" DISPLAY: yes betAmount (" + betAmount + ")");
		}
		
		// GAME LOGIC TIME
		ArrayList<ArrayList<String>> gameOutputs = gameInstance.runGameLogic(gameInputs[0], gameInputs[1], auxInputs);
		System.out.println(gameOutputs);
		String gameState = gameOutputs.get(0).get(0);
		ArrayList<String> playerHand = gameOutputs.get(3);
		ArrayList<String> dealerHand = gameOutputs.get(2);
		
		// END GAME LOGIC
		
		
	
		String gameboard = "";
		String bigOlText = "";
		
		if(gameState.equals("reset")){
			//out.println("<h1> Welcome to Blackjack! </h1><br>\r\n");
			bigOlText = "Welcome to Blackjack!";
			gameboard = gameboard + cardUtils.generateButton("Begin",new int[] {5,5}, "button", "BlackjackServlet?choice=setup&check="+inputCheck);
		}
		else if(gameState.equals("setup")){
			bigOlText = "Place your bets!";
			String minBet = gameOutputs.get(1).get(0);
			String maxBet = gameOutputs.get(1).get(1);
			gameboard = gameboard + ("<form method='GET'>\r\n"
					+ "<input type=\"hidden\" name=\"check\" value=\""+inputCheck+"\" />"
					+ "<input type=\"hidden\" name=\"choice\" value=\"submit\" />"
					+ "<input type=\"number\" name=\"betAmount\" min=\""+minBet+"\"  max=\""+maxBet+"\" value=\"5\" font-size=\"30px\" required >\r\n"
					+ "<input type=\"submit\" value=\"Confirm Bet (awkward horizontal space)\">\r\n"
					+ "</form>");
			//gameboard = gameboard + cardUtils.generateTxt("Hi I couldn't kinda forgot how to <br> make the spinbox bigger so <br> it's just going to be up here okay?", new int[] {10,40}, "linkLarge");
			gameboard = gameboard + drawChipCounter(gameOutputs.get(0).get(1),"???");
			//gameboard = gameboard + cardUtils.generateImg("/img/ui/logo1.png", new int[] {200, 250}, "card");
			//out.println("<a href = \"BlackjackServlet?choice=reset&check="+inputCheck+"\"> RESET </a>");
		}
		else if(gameState.equals("choice") || gameState.equals("resolvePlayer")){
			//out.println("<p> CHOICE STATE </p> <br>\r\n");
			//out.println("<a href = \"BlackjackServlet?choice=reset&check="+inputCheck+"\"> RESET </a>");
			gameboard = gameboard + gimmeCardsHandsw(playerHand, dealerHand, gameOutputs);
			String lastPlayerAction = gameOutputs.get(0).get(2);
			bigOlText = "Choose an option:";
			if(lastPlayerAction.equals("HitSuccess")) {bigOlText = "You hit successfully and gained another card! <br> It is still your turn.";}
			else if(lastPlayerAction.equals("HitFailed")) {bigOlText = "You hit, but went over 21! <br> It is now the dealer's turn.";}
			else if(lastPlayerAction.equals("DoubleSuccess")) {bigOlText = "You doubled down successfully! <br> It is now the dealer's turn.";}
			else if(lastPlayerAction.equals("DoubleFailed")) {bigOlText = "You doubled down, but failed! <br> It is now the dealer's turn.";}
			else if(lastPlayerAction.equals("Stand")) {bigOlText = "You stood. <br> It is now the dealer's turn";}
			gameboard = gameboard + drawChipCounter(gameOutputs.get(0).get(1),gameOutputs.get(0).get(5));
		}
		else if(gameState.equals("dealer") || gameState.equals("resolveDealer")){
			//out.println("<p> DEALER STATE </p> <br>\r\n");
			//out.println("<a href = \"BlackjackServlet?choice=reset&check="+inputCheck+"\"> RESET </a>");
			gameboard = gameboard + gimmeCardsHandsw(playerHand, dealerHand, gameOutputs);
			String lastDealerAction = gameOutputs.get(0).get(2);
			bigOlText = "It is the dealer's turn.";
			if(lastDealerAction.equals("Hit")) {bigOlText = "The dealer hit successfully. It is still their turn.";}
			else if(lastDealerAction.equals("HitFailed")) {bigOlText = "The dealer hit, but went over 21!";}
			else if(lastDealerAction.equals("Stand")) {bigOlText = "The dealer stood.";}
			gameboard = gameboard + drawChipCounter(gameOutputs.get(0).get(1),gameOutputs.get(0).get(5));
		}
		else if(gameState.equals("resolve")){
			//out.println("<p> RESOLVE STATE </p> <br>\r\n");
			//out.println("<a href = \"BlackjackServlet?choice=reset&check="+inputCheck+"\"> RESET </a>");
			gameboard = gameboard + gimmeCardsHandsw(playerHand, dealerHand, gameOutputs);
			String lastPlayerAction = gameOutputs.get(0).get(2);
			bigOlText = "Unhandled Resolution!";
			if(lastPlayerAction.equals("playerWin")) {bigOlText = "You have won! You have gained "+gameOutputs.get(0).get(5)+" chips.";}
			else if(lastPlayerAction.equals("dealerWin")) {bigOlText = "The dealer won! You have lost "+gameOutputs.get(0).get(5)+" chips.";}
			else  {bigOlText = "It's a tie! Your chips have remained unchanged.";} // just a catch-all I guess????
			gameboard = gameboard + drawChipCounter(gameOutputs.get(0).get(1),gameOutputs.get(0).get(5));
		}
		else{
			out.println("<p> ERROR INVALID GAMESTATE " + gameState + "! </p>/r/n <br>");
		}

		gameboard = gameboard + cardUtils.generateTxt(bigOlText, new int[] {10,40}, "bigOlText");
		out.println("<div class = gameContainer width = '400px'>");	
		out.println(gameboard);
		out.println("</div>"); // end of gameContainer

	    out.println("</body>");
		
		out.close();
		System.out.println(" DISPLAY: Printing complete. IC="+inputCheck+" & OIC="+oldInputCheck+"\r\n");
		//gameInstance.startGame();
	}
	
	private String drawAHand(ArrayList<String> hand, int[] pos, int[] ofset) {
		String sto = "";
		for(int i = 0; i < hand.size(); i++) {
			sto = sto +
				cardUtils.generateImgFromCard(hand.get(i), 
						new int[] {pos[0] + (ofset[0] * i) , pos[1] + (ofset[1] * i)});	// link back with choice & IC update
		}
		return sto;
	}
	
	private String drawButtons(ArrayList<String> buttons, int[] pos, int[] ofset) {
		String sto = "";
		for(int i = 0; i < buttons.size(); i++) {
			sto = sto +
				cardUtils.generateButton(buttons.get(i), 
						new int[] {pos[0] + (ofset[0] * i) , pos[1] + (ofset[1] * i)}, "button","BlackjackServlet?choice="+buttons.get(i)+"&check="+inputCheck);	// link back with choice & IC update
		}
		return sto;
	}
	
	private String gimmeCardsHandsw(ArrayList<String> playerHand, ArrayList<String> dealerHand, ArrayList<ArrayList<String>> gameOutputs) {
		String sto = "";
		sto = sto + drawAHand(playerHand, new int[] {40,180}, new int[] {110,0});
		sto = sto + cardUtils.generateTxt("Your hand (" + gameOutputs.get(0).get(4)+")", new int[] {50,300}, "handSubtitle");
		sto = sto + drawAHand(dealerHand, new int[] {40,400}, new int[] {110,0});
		sto = sto + cardUtils.generateTxt("Dealer's hand (" + gameOutputs.get(0).get(3)+")", new int[] {50,520}, "handSubtitle");
		sto = sto + drawButtons(gameOutputs.get(1),new int[] {5,5}, new int[] {90,0});
	return sto;
	}
	
	private String drawChipCounter(String totalChips, String betAmount) {
		return drawChipCounter(totalChips, betAmount, new int[] {500, 25});
	}
	private String drawChipCounter(String totalChips, String betAmount, int[] pos) {
		String sto = "";
		sto = sto + cardUtils.generateImg("/img/ui/bigChipWithX.png", new int[] {pos[0], pos[1]}, "doubleScale");
		sto = sto + cardUtils.generateTxt("" + betAmount + "/" + totalChips, new int[] {pos[0] - 20, pos[1] + 65}, "handSubtitle");
		return sto;
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {doGet(request, response);} // stub

	


}
