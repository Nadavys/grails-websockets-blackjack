package grails.stomp

import gameEntities.BlackJack
import gameEntities.Card
import gameEntities.Hand
import grails.transaction.Transactional
import org.springframework.messaging.simp.SimpMessageSendingOperations

@Transactional
class GameNotificationService {
    SimpMessageSendingOperations brokerMessagingTemplate

    def notifyGameStatus(BlackJack currentGame, doSendSocket = true){
        if(!currentGame){
            return false
        }

        def data = [
                command: 'update.game',
                entity: convert(currentGame)
        ]
        if(doSendSocket){
            brokerMessagingTemplate.convertAndSend "/topic/pushGameStatus", data
        }else{
            return data
        }
    }

    def notfiyUpdatedHand(GamePlayer player, doSendSocket = true){
        println "new cards for player ${player.internalId}"
        def data = [
                command: 'update.hand',
                entity: convert(player)
        ]
        if(doSendSocket){
            brokerMessagingTemplate.convertAndSend "/topic/pushGameStatus", data
        }else{
            return data
        }
    }

    def notfiyUpdatedPlayer(GamePlayer player, doSendSocket = true){
        //println "new player ${player.internalId}"
        def data = [
                command: 'update.player',
                entity: convert(player)
        ]
        if(doSendSocket){
            brokerMessagingTemplate.convertAndSend "/topic/pushGameStatus", data
        }else{
            return data
        }
    }

    def notifyGeneralMessage(message, alertLevel = "info"){
        brokerMessagingTemplate.convertAndSend "/topic/pushGameStatus", [command:'generalMessage', entity: [message: message, alertLevel: alertLevel]]
    }


    /*workaround until i find better solution - socets is using jackson, not default JSON marshaller*/
    private convert(BlackJack blackJack){
        def resultMap = [:]
        resultMap['round'] = blackJack.round.toString()
        resultMap['status'] = blackJack.status.toString()
        resultMap['timeStarted'] = blackJack.timeStarted
        resultMap['countDownTimer'] = blackJack.countDownTimer
        resultMap['activePlayer'] = blackJack.activePlayer?.internalId?: ''

        resultMap
    }

    private convert(GamePlayer player){
        def resultMap = [:]
        resultMap['userId'] = player.internalId
        resultMap['name'] = player.name
        resultMap['hand'] = player.hand?(convert(player.hand)) : null
        resultMap['imageUrl'] = player.imageUrl
        resultMap
    }

    private convert(Hand hand){
        Boolean allVisibleCards =  hand?.cards.findAll{ !it.isVisible }.size() == 0

        def resultMap = [:]
        resultMap['cards'] = hand.cards.collect{convert(it)}
        resultMap['handValue'] = allVisibleCards? hand.handValue: ''
        resultMap['bestValue'] = allVisibleCards? hand.bestValue: ''
        resultMap['status'] = hand.status.toString()
        resultMap['isValid'] = hand.isValidHand()

        resultMap
    }

    private convert(Card card){
        def resultMap = [:]
        resultMap['rank'] = card.isVisible? card.rank.toString() : ''
        resultMap['suit'] = card.isVisible? card.suit.toString() : ''
        resultMap['isVisible'] = card.isVisible
        resultMap
    }


}
