package grails.stomp

class EnterController {
    GamePlayerSessionService gamePlayerSessionService


    def index(){
        String playerName
        GamePlayer gamePlayer
        if(params.isSubmit){
            playerName = params.playerName
            gamePlayer = new GamePlayer(name: playerName, type: GamePlayer.Type.PLAYER)
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
