<%@page import="grails.converters.JSON" defaultCodec="none" %><!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>

    <asset:javascript src="jquery" />
    <asset:javascript src="spring-websocket" />
    <asset:javascript src="bootstrap"/>
    <asset:javascript src="application-angular"/>
    <asset:javascript src="angularApp-blackjackGame"/>
    <asset:stylesheet src="blackjack-cards.css"/>

    <script type="text/javascript">
      window.currentPlayer = ${ [userId: gamePlayer.internalId, name: gamePlayer.name] as JSON };
    </script>
</head>
<body>

 <div ng-app="app" >
 <div ng-controller="GameTableController">

<!-- Main jumbotron for a primary marketing message or call to action -->
<div class="" >
    <h1>Welcome ${gamePlayer.name}</h1>
    <p></p>
</div>


     <div ng-show="generalMessage.message" class="alert alert-{{generalMessage.alertLevel}}">{{ generalMessage.message }}</div>


            **<pre>{{players | json}}</pre>**
 <!--         **<pre>{{isCurrentPlayerInGame()}}</pre>**
            **<pre>{{game.round}}</pre>**   -->

     <div class="alert alert-danger" ng-show="game.round == 'START'">Time to new game: <span >{{game.newGameCountDown}}</span></div>
     <div class="row" ng-show="!currentUserAskedToJoin && (game.round == 'GAMEOVER' || game.round == 'START' || game.round == null)">

         <p>
         <button class="btn btn-lg btn-danger blink" ng-click="cmdJoinGame()" >Join Game</button>
     </p>
     </div>


     <div class="row" >
    <div>

        <div class="user well row">
            <label>Dealer</label>
            <div class="hand-area">
         <hand hand="dealer.hand"/>

            </div>
        </div>

        <!--players-->
        <div ng-repeat="player in players" class="user row well"  ng-class="{ 'currentTurn' : player.userId == game.activePlayer, 'hand-won': player.hand.status == 'WON', 'hand-bust': player.hand.status == 'BUST'}">

            <div class="col-md-2" style="position: relative">
                <span ng-show="player.hand.status == 'BUST'"  class="playerStatus" >
                    <span class="glyphicon glyphicon-remove"></span>
                    Bust
                </span>

                <span ng-show="player.hand.status == 'WON'" class="playerStatus">
                    <span  class="glyphicon glyphicon-ok"></span>
                    Won
                </span>
                <span ng-show="player.hand.status == 'EVEN'"  class="playerStatus">
                    <span  class="glyphicon glyphicon-ok"></span>
                    Even
                </span>


                <img ng-src="{{player.imageUrl}}" class="img-responsive img-circle">
            </div>

                <div class="col-md-10">
                    <div class="row">
                        <div class="name" style="">{{player.name}}

                                <!--<div ng-show="hand.status == 'IN_GAME'">
             <span class="glyphicon glyphicon-camera"xxxx></span>
         </div>
                                <span ng-show="player.hand.status == 'BUST'"  style="color:red">
                                    <span class="glyphicon glyphicon-remove"></span>
                                    Bust
                                </span>
                                <span ng-show="player.hand.status == 'WON'" style="color:green">
                                    <span  class="glyphicon glyphicon-ok"></span>
                                    Won
                                </span>
                                <span ng-show="player.hand.status == 'EVEN'" >
                                    <span  class="glyphicon glyphicon-ok"></span>
                                    Even
                                </span>
                                      -->
                        </div>
                    </div>
                     <div class="row">
                         <div class="hand-area">
                             <hand hand="player.hand"/>
                         </div>
                         <div class="btn-block" ng-show="player.userId == currentUser.userId && game.activePlayer == currentUser.userId" >
                             <button class="btn btn-danger" ng-click="cmdPlayerMove('HIT')" >Hit</button>
                             <button class="btn btn-primary" ng-click="cmdPlayerMove('STAND')" >Stand</button>
                         </div>

                         <div class="btn btn-lg btn-danger disabled" ng-show="game.round == 'PLAYER_MOVE' && game.activePlayer != currentUser.userId"> Playing... </div>
                     </div>
                </div>
       </div>

    </div>
</div>

     <div class="well">
         <div class="row btn-block">
             <h4>this panel for development</h4>
             <button class="" ng-click="cmdEndGame()" >Force End Game</button>
             <button class="" ng-click="cmdNewGame()" >Trigger New Game</button>
         </div>

     <div class=" row">
         <dl class="dl-horizontal">
             <dt>Game Status</dt>
             <dd>{{game.status}}</dd>

             <dt>round</dt>
             <dd>{{game.round}}</dd>

             <dt>timeStarted</dt>
             <dd>{{ game.timeStarted | date:'shortTime'}}</dd>

         </dl>
     </div>
     </div>

 </div>
 </div>
</body>
</html>