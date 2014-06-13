import gameEntities.BlackJack
import gameEntities.Hand
import grails.converters.JSON
import grails.stomp.GamePlayer

class BootStrap {

    def init = { servletContext ->

        JSON.registerObjectMarshaller(BlackJack, 3) { BlackJack blackJack ->
            def resultMap = [:]
            resultMap['round'] = blackJack.round.toString()
            resultMap['status'] = blackJack.status.toString()
            resultMap['timeStarted'] = blackJack.timeStarted
            resultMap['countDownTimer'] = blackJack.countDownTimer
            resultMap['players'] = blackJack.players.collect{[name:it.name, id: it.internalId]}

            resultMap
        }

        JSON.registerObjectMarshaller(GamePlayer, 3) { GamePlayer player ->
            def resultMap = [:]
            resultMap['userId'] = player.internalId
            resultMap['name'] = player.name
            resultMap['hand'] = player.hand
            resultMap
        }

        JSON.registerObjectMarshaller(Hand, 3) { Hand hand ->
            def resultMap = [:]
            resultMap['cards'] = hand.cards
            resultMap['handValue'] = hand.handValue
            resultMap['bestValue'] = hand.bestValue()
            resultMap['status'] = hand.status
            resultMap['isValid'] = hand.isValidHand()

            resultMap
        }

    }
    def destroy = {
    }
}
