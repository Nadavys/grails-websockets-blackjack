package grails.stomp

class EnterController {
    GamePlayerSessionService gamePlayerSessionService


    def index(){
        String playerName
        if(params.isSubmit){
            playerName = params.playerName
            Random random = new Random()
            GamePlayer gamePlayer = new GamePlayer(name: playerName, internalId: random.nextLong().abs(),
                    type: GamePlayer.Type.DEALER)
            if(gamePlayer.validate()){
                //want the player to have an ID
                gamePlayer.save(flush: true)
                gamePlayerSessionService.setCurrentGamePlayer(session, gamePlayer)
                //all good, go to the game table
                redirect(controller: 'game', action: 'table')
                return false
            }
        }
        [playerName: playerName, gamePlayer: gamePlayer]
    }
}
