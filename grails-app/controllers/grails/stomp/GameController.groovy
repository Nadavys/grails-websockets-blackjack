package grails.stomp

import gameEntities.BlackJack
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
        if(currentGame?.round == BlackJack.Round.PLACE_BETS){
        GamePlayer player = gamePlayerSessionService.getCurrentGamePlayer(session)
        if(!currentGame.players.contains(player)){
            //empty your hand on new game
            player.hand = null
            currentGame.players << player
            gameNotificationService.notifyGameStatus(currentGame)
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

            this.currentGame = gameService.newBlackjackGame()

            gameNotificationService.notifyGameStatus(currentGame)
            gameNotificationService.notifyGeneralMessage("A new game is about to start, join in now", 'success')
            gameNotificationService.notfiyUpdatedPlayer(currentGame.dealer)
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
                currentGame.countDownTimer--
                gameNotificationService.notifyGameStatus(currentGame)
                //old school
                Thread.sleep(1000)
            }
            dealAllPlayers()
        }
    }
    /*todo: make this scheduled and async*/
    private startPlayerMoveCountdown(GamePlayer player){
        Thread.start {
            currentGame.resetPlayerMoveTimer()
            while(currentGame.activePlayer && currentGame.countDownTimer > 1 && player.id == currentGame.activePlayer.id){
                currentGame.countDownTimer--
                gameNotificationService.notifyGameStatus(currentGame)
                //old school
                Thread.sleep(1000)
            }
            if( player?.id ==
                    currentGame.activePlayer?.id){
                //timeout! player will stand
                gameNotificationService.notifyGeneralMessage("Player timed out")
                playerMove()
            }else{
                //player must have responded
            }
        }
    }

    private dealAllPlayers(){

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
       gameService.getNextActivePlayer(this.currentGame)

       if(currentGame.activePlayer){
           // start timer. player has given time to react
           startPlayerMoveCountdown(currentGame.activePlayer)
           gameNotificationService.notifyGeneralMessage("Player " + currentGame.activePlayer.name +" may `hit` or `stand`")
       }else{
           //no motr players, dealer moves
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
        gameNotificationService.notfiyUpdatedHand(currentGame.dealer)
        //todo: handle end of game
        gameOver()
    }


    //todo: resolution
    private gameOver(){
        gameService.gameFinale(currentGame)
        gameNotificationService.notifyGameStatus(currentGame)
        gameNotificationService.notifyGeneralMessage("Game Over", 'danger')
        currentGame.players.each {
            gameNotificationService.notfiyUpdatedPlayer(it)
        }
    }

    /*
     * player responds to the application via ajax, not socket,  maybe change this
     */
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
                currentGame.resetPlayerMoveTimer()
                //gameNotificationService.notifyRequirePlayerMove()
            }else{
                playerMove()
            }
        }else{
            //ooops
        }

    }

    /*
     * ajax - when user enters or refreshes the page
     */
    def tableStatus(){
        //send all info about the game
        def data = []
        if(currentGame){
            data <<  gameNotificationService.notifyGameStatus(currentGame, false)
            data << gameNotificationService.notfiyUpdatedPlayer(currentGame.dealer, false)
            currentGame?.players?.each{
                data << gameNotificationService.notfiyUpdatedPlayer(it, false)
            }
        }
        render (data as JSON)
    }
}

