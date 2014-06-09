package grails.stomp

import gameEntities.BlackJack
import gameEntities.Hand
import grails.converters.JSON


class GameController {
    GameService gameService
    GameNotificationService gameNotificationService

    GamePlayerSessionService gamePlayerSessionService
   /* signleton game */
    static BlackJack currentGame
    def index() {
        if(gamePlayerSessionService.getCurrentGamePlayer(session)){
            redirect(action: 'table')
            return false
        }else{
            redirect(controller: 'enter')
        }
    }

    def table(){
        GamePlayer gamePlayer = gamePlayerSessionService.getCurrentGamePlayer(session)
        if(!gamePlayer){
            redirect(controller: 'enter')
            return  false
        }
        [gamePlayer: gamePlayer]
    }


    def startNewGame(){
        newGame()
        render([command:'newGame', success: true] as JSON)
    }

    def joinGame(){
        newGame()
        def retVal = [command:'joinGame', success: true]
        if(currentGame?.round == BlackJack.Round.START){
        GamePlayer player = gamePlayerSessionService.getCurrentGamePlayer(session)
        if(!currentGame.players.contains(player)){
            //empty your hand on new game
            player.hand = null
            currentGame.players << player
            gameNotificationService.notfiyUpdatedPlayer(player)
            gameNotificationService.notifyGeneralMessage("Player "+ player.name +" joined")
        }
        }else{
            //wrong time to join
            retVal.success = false
        }

        render(retVal as JSON)
    }

    private newGame(){
        if(!this.currentGame || this.currentGame.isGameOver() ){
            gameNotificationService.notifyGameStatus(currentGame)

            this.currentGame = gameService.newBlackjackGame()
            gameNotificationService.notifyGeneralMessage("A new game is about to start, join in now", 'success')
            startNewGameCountdown()
        }

    }

    def endGame(){
        this.currentGame?.round = BlackJack.Round.GAMEOVER
        this.currentGame?.activePlayer = null
       gameNotificationService.notifyGameStatus(currentGame)
        gameNotificationService.notifyGeneralMessage("Game Over", 'danger')
        render([command:'endGame', success: true] as JSON)
    }

    /*todo: make this scheduled and async*/
    private startNewGameCountdown(){
        Thread.start {
            currentGame.newGameCountDownInitial.times {
                currentGame.newGameCountDown--
                gameNotificationService.notifyGameStatus(currentGame)
                //old school
                Thread.sleep(1000)
            }
            dealAllPlayers()
        }
    }

    private dealAllPlayers(){
        this.currentGame.round = BlackJack.Round.DEAL
        gameService.newHandRound(this.currentGame)

        gameNotificationService.notfiyUpdatedHand(currentGame.dealer)
        currentGame.players.each{
            gameNotificationService.notfiyUpdatedHand(it)
        }
       gameNotificationService.notifyGameStatus(currentGame)
        gameNotificationService.notifyGeneralMessage("All player have been dealt a new hand", 'success')
        Thread.start {
            Thread.sleep(1000)
            playerMove()
        }
    }


   private playerMove(){
       if(currentGame.players){
           if(currentGame.round != BlackJack.Round.PLAYER_MOVE){
               currentGame.round = BlackJack.Round.PLAYER_MOVE
               currentGame.activePlayer = currentGame.players.first()
           }else{
               currentGame.activePlayer = (currentGame.players[currentGame.players.indexOf(currentGame.activePlayer)+1])?:null
           }

          // //skip player already won/bust
           while(currentGame.activePlayer && currentGame.activePlayer.hand.status != Hand.HandStatus.IN_GAME){
               currentGame.activePlayer = (currentGame.players[currentGame.players.indexOf(currentGame.activePlayer)+1])?:null
           }
       }
       if(currentGame.activePlayer){
           gameNotificationService.notifyGeneralMessage("Player " + currentGame.activePlayer.name +" may `hit` or `stand`")
       }else{
               dealerMove()
           }
      gameNotificationService.notifyGameStatus(currentGame)
   }



    private dealerMove(){
        this.currentGame.round = BlackJack.Round.DEALER_MOVE
        currentGame.dealer.hand.revealAllCards()
        gameNotificationService.notfiyUpdatedHand(currentGame.dealer)
        //add drama!
//        Thread.wait(1000)
        gameService.dealerFinalDraw(currentGame)
        println "notify new dealer card"
        gameNotificationService.notfiyUpdatedHand(currentGame.dealer)
        //todo: handle end of game
        gameResolution()
    }

    private gameResolution(){
        gameService.gameFinale(currentGame)
        gameNotificationService.notifyGameStatus(currentGame)
        gameOver()
    }

    //todo: resolution
    private gameOver(){
        currentGame.round = BlackJack.Round.GAMEOVER
       gameNotificationService.notifyGameStatus(currentGame)
        gameNotificationService.notifyGeneralMessage("Game Over", 'danger')
        currentGame.players.each {
            gameNotificationService.notfiyUpdatedPlayer(it)
        }
    }

    /* ajax, not socket,  maybe change this*/
    def getPlayerMove(){
        def response = [command:'playerMove', success: true]

        GamePlayer player = gamePlayerSessionService.getCurrentGamePlayer(session)
        if(player == this.currentGame.activePlayer){
            def action = params.id
            handlePlayerResponse(action)
        }else{
            response.success = false
        }

        render(response as JSON)
    }

    def handlePlayerResponse(action){
        if(action == BlackJack.PlayerChoices.STAND.toString()){
            //move to next player
            playerMove()
        }else if (action == BlackJack.PlayerChoices.HIT.toString()){
            this.currentGame.activePlayer.hand.drawFromDeck(currentGame.deck)
            gameNotificationService.notfiyUpdatedHand(currentGame.activePlayer)
            if(this.currentGame.activePlayer.hand.isValidHand()){
                //keep playing
                //gameNotificationService.notifyRequirePlayerMove()
            }else{
                playerMove()
            }
        }else{
            //ooops
        }

    }

    //ajaxx
    def tableStatus(){
        //send all info about the game
        def data = []
        if(currentGame){
            data <<  gameNotificationService.notifyGameStatus(currentGame, false)
            data << gameNotificationService.notfiyUpdatedHand(currentGame.dealer, false)
            currentGame?.players?.each{
                data <<  gameNotificationService.notfiyUpdatedPlayer(it, false)
            }
        }
        render (data as JSON)
    }
}

