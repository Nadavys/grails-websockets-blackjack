package grails.stomp

import grails.transaction.Transactional

@Transactional
class GamePlayerSessionService {

    GamePlayer getCurrentGamePlayer(session){
        session['gamePlayer']?:null
    }


    GamePlayer setCurrentGamePlayer(session, GamePlayer gamePlayer){
        session['gamePlayer'] = gamePlayer
    }

}
