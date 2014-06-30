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
      window.currentPlayerId = ${gamePlayer.id};
    </script>
</head>
<body>

 <div ng-app="app" >
 <div ng-controller="GameTableController">

<div class="" >
    <h1>Welcome ${gamePlayer.name}</h1>
    <p></p>
</div>
     <div ng-show="generalMessage.message" class="alert alert-{{generalMessage.alertLevel}}">{{ generalMessage.message }}</div>

     <div class="alert alert-danger" ng-show="game.round == 'PLACE_BETS'">Time to new game: <span >{{game.countDownTimer}}</span></div>

     <div class="joinGameButton" ng-show="!currentUserAskedToJoin && (game.round == 'GAMEOVER' || game.round == 'PLACE_BETS' || game.round == null)">
         <button class="btn btn-lg btn-danger blink" ng-click="cmdJoinGame()" >Click to Join Game</button>
     </div>

     <div class="row" >

        <gameplayer player="dealer" game="game"></gameplayer>

        <gameplayer player="currentPlayer" game="game" current-player-id="currentPlayerId" ng-if="currentPlayer"  ></gameplayer>

        <!--players-->
        <div ng-repeat="player in players" ng-if="currentPlayerId != player.id" >
               <gameplayer player="player" game="game"/>
       </div>

 </div>

 </div>
 </div>

</body>
</html>