import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PrintACard
 */
@WebServlet("/PrintACard")
public class PrintACard extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Random rand = new Random();
	private CardUtils cardUtils = new CardUtils("/CardGame_1");
	//private Waa_Serv_1 Waa = new Waa_Serv_1();
	private int cardX = 200;
	private int cardY = 200;
	private int cardCount = 5;
	private int testArray[] = {0,1,2,4,8,16,32,64};
	private boolean doSetup = true;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PrintACard() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(doSetup){
			System.out.println("Setup requested. Initializing session attributes...");
			doSetup = false;
			request.getSession().setAttribute("cardX",cardX);
			request.getSession().setAttribute("cardY",cardY);
			request.getSession().setAttribute("cardCount",cardCount);
			request.getSession().setAttribute("testArray",testArray);
		}
		
	
		PrintWriter out = response.getWriter(); // set up writer
		String choice = request.getParameter("choice"); // return a string indicating what was clicked.
		String ctxtPath = request.getContextPath();
		
		// get attributes
		cardX = (int) request.getSession().getAttribute("cardX");
		cardY = (int) request.getSession().getAttribute("cardY");
		cardCount = (int) request.getSession().getAttribute("cardCount");
		testArray = (int[]) request.getSession().getAttribute("testArray");
		
		// write header
		
		out.println("<head>\r\n"
				+ "    <meta charset=\"ISO-8859-1\">\r\n"
				+ "    <title>For fun & not profit</title>\r\n"
				+ "    <link href=\'css_b.css\' rel=\"stylesheet\" type=\"text/css\" media=\"all\">\r\n"
				+ "    <!-- js scripts go here if I need any  -->\r\n"
				+ "</head>");
		
		
		// user input time!
		if("moreCards".equals(choice)){
			cardCount = Math.min(cardCount + 5,200);
		}
		else if("fewerCards".equals(choice)){
			cardCount = Math.max(cardCount - 5,5);
		}
		
		// print card(s)
		String board = "";
		board = board + "<img src = '"+ctxtPath+"/img/cards/"+cardUtils.randomCard()+".png' style = 'position:absolute;"
		+"  left:" + 0
		+ "px; top:" + 0
		+ "px' > ";
		for(int i=0; i < cardCount;i++) {
		
			/*board = board + "<img src = '"+ctxtPath+"/img/cards/"+cardUtils.randomCard()+".png' style = 'position:absolute;"
			+"  left:" + cardX
			+ "px; top:" + cardY
			+ "px' > ";*/
			cardX = rand.nextInt(1200-50);
			cardY = rand.nextInt(1200-80);
			String nextCard = cardUtils.randomCard();
			int nextCardPos[] = {cardX, cardY};
			board = board + cardUtils.generateImgFromCard(nextCard, nextCardPos);
		}
		String controls = "<a href = 'PrintACard?choice=randomize'> Randomize </a>---\r\n"
						+ "<a href = 'PrintACard?choice=moreCards'> More Cards </a>---\r\n"
						+ "<a href = 'PrintACard?choice=fewerCards'> Fewer Cards </a>";
		
		out.println(   "<h1> Let's play "+cardCount+" card pickup!</h1>\r\n"
					 + "some may be duplicated because I forgot to use the deck function\r\n <br>"
					 + "<p>"+controls + "\r\n"
					 + "<br> CHOSEN: "+choice+"</p> \r\n"
					 + "<div class = gameContainer>\r\n"
					 + board + "\r\n"
					 + "</div> <br>"
					 + "DONE");
		request.getSession().setAttribute("cardX",cardX);
		request.getSession().setAttribute("cardY",cardY);
		request.getSession().setAttribute("cardCount",cardCount);
		request.getSession().setAttribute("testArray",testArray);
		out.close();// End Of Page
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
