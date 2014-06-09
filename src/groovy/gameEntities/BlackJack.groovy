package gameEntities

import grails.stomp.GamePlayer


import groovy.transform.ToString

@ToString(excludes = 'deck,players')
class BlackJack extends CardGame{
    static final Integer initialCardsNum = 2
    static final Integer initialCredits= 100
    static final Integer newGameCountDownInitial = 5
    enum PlayerChoices { HIT, STAND}
    enum GameStatus{ DEALER_BUST, DEALER_WON, PLAY}
    //todo: add round 'BET'
    enum Round{ START(0), DEAL(1), PLAYER_MOVE(2), DEALER_MOVE(3), RESOLUTION(4), GAMEOVER(5)
        Integer order
        Round(order){
            this.order = order
        }
    }

    List<GamePlayer> players = []
    Deck deck
    GamePlayer dealer
    Round round = Round.START
    GameStatus status
    Date timeStarted  = new Date()

    GamePlayer activePlayer

    Integer newGameCountDown


    Hand newHand(){
        //TODO: check if new deck needed
        new Hand(deck ,  BlackJack.initialCardsNum)
    }

    void newDeck(){
        //TODO: check if new deck needed
        deck = new Deck()
    }
    BlackJack(){
        this.name = "Blackjack"
        this.instructions = "Don't go over 21"
        this.newDeck()
        this.round = BlackJack.Round.START
        this.newGameCountDown = this.newGameCountDownInitial

        dealer = new GamePlayer(name:"Dealer", internalId: 0)
    }



    PlayerChoices dealerChoice(GamePlayer player){
        Integer value = player.hand.getBestValue()
        if(17 > value){
            return BlackJack.PlayerChoices.HIT
        }
        return BlackJack.PlayerChoices.STAND
    }


    boolean isGameOver(){
        (this.round == BlackJack.Round.GAMEOVER)
    }

}
