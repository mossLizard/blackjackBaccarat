import java.util.ArrayList;
import java.util.List;



class Hand {
    private List<String> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public void addCard(String card) {
        this.cards.add(card);
    }

    public List<String> getCards() {
        return cards;
    }

    public int getValue(CardDeck deck) {
        int value = 0;
        int numAces = 0;
        for (String card : cards) {
            String rank = card.split("Of")[0]; // Extract the rank (e.g., "Ace", "Two")
            int cardValue = deck.GetCardValue(rank);
            value += cardValue;
            if (rank.equals("Ace")) {
                numAces++;
            }
        }
        while (value > 21 && numAces > 0) {
            value -= 10; // Change an Ace from 11 to 1
            numAces--;
        }
        return value;
    }

    @Override
    public String toString() {
        return String.join(", ", cards);
    }
}

