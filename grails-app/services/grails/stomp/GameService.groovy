package grails.stomp

import grails.transaction.Transactional

@Transactional
class GameService {
        def newBlackjackGame(){
            def game = new gameEntities.BlackJack()
            return game
        }

        def newBlackjackGame(Collection<GamePlayer> players){
            def game = new gameEntities.BlackJack()
            game.players = players

            return game
        }

        void newHandRound(gameEntities.BlackJack blackJack){
            blackJack.dealer.hand = blackJack.newHand()
            //might want to hide a card, if dealer not won or bust
            if(blackJack.dealer.hand.isValidHand()){
                blackJack.dealer.hand.cards[1].hide()
            }

            blackJack.players.each{ GamePlayer player ->
                player.hand = blackJack.newHand()
            }
            //if dealer has lost or won, its game over
            switch (blackJack.dealer.hand.status){
                case gameEntities.Hand.HandStatus.WON:
                    blackJack.status = gameEntities.BlackJack.GameStatus.DEALER_WON
                    blackJack.isOver = true
                    break
                case gameEntities.Hand.HandStatus.BUST:
                    blackJack.status = gameEntities.BlackJack.GameStatus.DEALER_BUST
                    blackJack.isOver = true
                    break
                default:
                    blackJack.status = gameEntities.BlackJack.GameStatus.PLAY
            }
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


        def gameFinale(gameEntities.BlackJack blackJack){
            blackJack.round = gameEntities.BlackJack.Round.RESOLUTION
            GamePlayer dealer = blackJack.dealer
            List players = blackJack.players.findAll{ GamePlayer p -> p.hand.status == gameEntities.Hand.HandStatus.IN_GAME}
            if(!players.size()){
                //all are out
                blackJack.status = gameEntities.BlackJack.GameStatus.DEALER_WON
                println "All player already out"
            }else{
                if(dealer.hand.status == gameEntities.Hand.HandStatus.BUST){
                    players.each { GamePlayer p -> p.hand.setWon()}
                    blackJack.status = gameEntities.BlackJack.GameStatus.DEALER_BUST
                }
                else if(dealer.hand.status == gameEntities.Hand.HandStatus.WON){
                    players.each { GamePlayer p -> p.hand.setBust()}
                    blackJack.status = gameEntities.BlackJack.GameStatus.DEALER_WON
                }
                else{
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
                }
            }

            ///todo: remove output
            println "** dealer: ${blackJack.dealer.hand.bestValue}"
            players.each { GamePlayer p ->
                println "** ${p.name}: ${p.hand.bestValue} -> ${p.hand.status}"
            }

        }

    }
