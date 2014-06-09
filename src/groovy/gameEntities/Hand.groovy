package gameEntities

import groovy.transform.ToString

@ToString()
class Hand {
    enum HandStatus{ IN_GAME, WON, BUST, EVEN}
    List<Card> cards
    List handValue
    HandStatus status = HandStatus.IN_GAME

    Hand(Deck d, Integer sum) {
        cards = []
        sum.times {
            if(!d.cards.size()){
                d = new Deck()
                println("New Deck")
            }
            drawFromDeck(d)
        }
    }

    def Card drawFromDeck(Deck d){
        Card card = d.drawFromDeck()
        cards.add( card )
        calculateHandStatus()
        card
    }

    /*there may be several sums, on account of several ACES*/
    void calculateHandStatus(){
        def possibleSums = [0]
        this.cards.each{ Card card ->
            def cardValues = []
            card.rank.rankpoints.each { currentCardValue ->
                cardValues += possibleSums.collect {it + currentCardValue}
            }
            possibleSums = cardValues
        }
        if(possibleSums.find{it == 21}){
            this.status = HandStatus.WON
            //remove irrelevant possibilities
            possibleSums = [21]
        }else if(!possibleSums.find{it < 22}){
            //cant find cards value under 22
            this.status = HandStatus.BUST
        }else{
            //continue playing, remove irrelevant items
            this.status = HandStatus.IN_GAME
            possibleSums = possibleSums.findAll{it < 21}
        }
        this.handValue = possibleSums
    }

    void setBust(){
      this.status = HandStatus.BUST
    }

    void setWon(){
        this.status = HandStatus.WON
    }

    void setEven(){
        this.status = HandStatus.EVEN
    }

    Integer getBestValue(){
        handValue.findAll{ it <= 21}.max()
    }

    Boolean isValidHand(){
        this.status == HandStatus.IN_GAME
    }

    void revealAllCards(){
        cards.each{
            it.isVisible = true
        }
    }
}
