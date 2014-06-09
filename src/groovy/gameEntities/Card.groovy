package gameEntities

class Card {
    static enum Rank {
        DEUCE([2]), THREE([3]), FOUR([4]), FIVE([5]), SIX([6]), SEVEN([7]), EIGHT([8]), NINE(
                [9]), TEN([10]), JACK([10]), QUEEN([10]), KING([10]), ACE([1,11]);
        List<Integer> rankpoints

        Rank(rankPoints){
            this.rankpoints = rankPoints
        }
    }

    static enum Suit {
        CLUBS(2), DIAMONDS(3), HEARTS(4), SPADES(1);
        def Integer suitpoints

        Suit(suitPoints){
            this.suitpoints = suitPoints
        }
    }

     Card.Rank rank
     Card.Suit suit
    Boolean isVisible = true
//    final Rank rank
//    final Suit suit

    def String toString() {
        "$rank of $suit"
    }

    void hide(){
        this.isVisible = false
    }

}
