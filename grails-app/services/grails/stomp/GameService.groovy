package grails.stomp

import gameEntities.BlackJack
import gameEntities.Hand
import grails.transaction.Transactional

@Transactional
class GameService {
        def newBlackjackGame(){
            def game = new gameEntities.BlackJack()
            return game
        }

        def newBlackjackGame(Collection<GamePlayer> players){
            def game = new BlackJack()
            game.players = players

            return game
        }

        void newHandRound(BlackJack blackJack){
            blackJack.dealer.hand = blackJack.newHand()
            if(blackJack.dealer.hand.isValidHand()){
                blackJack.dealer.hand.cards[1].hide()
            }

            blackJack.players.each{ GamePlayer player ->
                player.hand = blackJack.newHand()
            }
            resolveGameRound(blackJack)
        }


        def dealerFinalDraw(gameEntities.BlackJack blackJack){
            //dealer move
            def dealerAction

            while(dealerAction != gameEntities.BlackJack.PlayerChoices.STAND && blackJack.dealer.hand.status == gameEntities.Hand.HandStatus.IN_GAME){
                println "^^ dealer before: ${blackJack.dealer.hand.bestValue}"
                dealerAction  = blackJack.dealerChoice( blackJack.dealer)
                println "${blackJack.dealer.name} wants to *** $dealerAction ***"
                if(gameEntities.BlackJack.PlayerChoices.HIT == dealerAction){
                    blackJack.dealer.hand.drawFromDeck(blackJack.deck)
                    println "^^ dealer before after: ${blackJack.dealer.hand.bestValue}"
                }
            }
        }

    def resolveGameRound(BlackJack blackJack){
        GamePlayer dealer = blackJack.dealer
        List players = blackJack.players.findAll{ GamePlayer p -> p.hand.status == Hand.HandStatus.IN_GAME}
        if(players.size()){
            if(dealer.hand.status == gameEntities.Hand.HandStatus.BUST){
                players.each { GamePlayer p -> p.hand.setWon()}
            }
            else if(dealer.hand.status == gameEntities.Hand.HandStatus.WON){
                players.each { GamePlayer p -> p.hand.setBust() }
            }
        }

        //in no more players with valid hand, its game over
        if(!blackJack.players.any{ GamePlayer p -> p.hand.status == Hand.HandStatus.IN_GAME}){
            blackJack.setGameOver()
        }
    }

        def gameFinale(gameEntities.BlackJack blackJack){
            resolveGameRound(blackJack)
            List players = blackJack.players.findAll{ GamePlayer p -> p.hand.status == gameEntities.Hand.HandStatus.IN_GAME}
                    //all hand values, including dealer, values are below 21
                    Integer dealerValue = blackJack.dealer.hand.bestValue
                    players.each { GamePlayer p ->
                        if(dealerValue == p.hand.bestValue){
                            p.hand.setEven()
                        }else if(dealerValue < p.hand.bestValue){
                            p.hand.setWon()
                        }else{
                            p.hand.setBust()
                        }
                    }
            blackJack.setGameOver()
        }


    void getNextActivePlayer(BlackJack currentGame){
    if(currentGame.players){
        if(currentGame.round != BlackJack.Round.PLAYER_MOVE){
            currentGame.round = BlackJack.Round.PLAYER_MOVE
            currentGame.activePlayer = currentGame.players.first()
        }else{
            //move to next player
            currentGame.activePlayer = (currentGame.players[currentGame.players.indexOf(currentGame.activePlayer)+1])?:null
        }

        // //skip player already won/bust
        while(currentGame.activePlayer && currentGame.activePlayer.hand.status != Hand.HandStatus.IN_GAME){
            currentGame.activePlayer = (currentGame.players[currentGame.players.indexOf(currentGame.activePlayer)+1])?:null
        }
    }
    }

    }
