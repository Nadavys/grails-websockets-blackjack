package gameEntities

class Deck {
    def List cards

    // Initialize prototype deck
    Deck() {
        List<Card> cards = []
        for (Card.Suit suit : Card.Suit.values())
            for (Card.Rank rank : Card.Rank.values())
                cards << new Card(rank: rank, suit: suit)
        Collections.shuffle(cards)
        this.cards = cards
    }

    public Card drawFromDeck(){
        cards.remove(0)
    }

}
