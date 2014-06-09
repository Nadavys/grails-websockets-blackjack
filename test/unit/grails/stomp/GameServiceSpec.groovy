package grails.stomp

import gameEntities.BlackJack
import gameEntities.Hand
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(GameService)
@Build([GamePlayer])
//@Mock([Card])
class GameServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        setup:
        def player1 = new GamePlayer(name:'player Rajnish')
        def player2 = new GamePlayer(name:'player Ganesh')
        def bj = service.newBlackjackGame([player1, player2])

        service.newHandRound(bj)

        playerStat('dealer:',bj.dealer)

        if(!bj.isOver){
            bj.round = BlackJack.Round.PLAYER_MOVE
            bj.players.findAll{it.hand.status != Hand.HandStatus.IN_GAME}.each{
                println "*** ${it.name} is ${it.hand.status}"
            }

            bj.players.findAll{it.hand.status == Hand.HandStatus.IN_GAME}.each{
                GamePlayer player ->
                    playerStat('b4',player)
                    def action
                    while(action != BlackJack.PlayerChoices.STAND && player.hand.status == Hand.HandStatus.IN_GAME){
                        action  = bj.dealerChoice(player)
                        println "${player.name} wants to *** $action ***"
                        if(BlackJack.PlayerChoices.HIT == action){
                            def cardDrawn = player.hand.drawFromDeck(bj.deck)
                            println "Carddrawn: "+cardDrawn
                        }
                        playerStat('after',player)
                    }
            }

            service.dealerFinalDraw(bj)

            //resolution
            bj.round = BlackJack.Round.RESOLUTION

            service.gameFinale(bj)
            println bj

        }

        expect:
        1 == 1
        //(bj instanceof CardGame)
    }

    private playerStat(title, GamePlayer player){
        println title
        println "name: " +player.name
        println "handvalue: " + player.hand.handValue
        println "player status: " + player.hand.status
    }
}
