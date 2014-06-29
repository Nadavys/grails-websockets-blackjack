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

<div class="" >
    <h1>Welcome ${gamePlayer.name}</h1>
    <p></p>
</div>


     <div ng-show="generalMessage.message" class="alert alert-{{generalMessage.alertLevel}}">{{ generalMessage.message }}</div>

     <div class="alert alert-danger" ng-show="game.round == 'START'">Time to new game: <span >{{game.countDownTimer}}</span></div>


     <div class="row" >
    <div>
        <gameplayer player="dealer" game="game"></gameplayer>

        <!--players-->
        <div ng-repeat="player in players">

               <gameplayer player="player" game="game" current-user="currentUser"/>
       </div>


</div>
   <!--
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
         -->
 </div>
     <div id="joinGameButton" ng-show="!currentUserAskedToJoin && (game.round == 'GAMEOVER' || game.round == 'START' || game.round == null)">
         <button class="btn btn-lg btn-danger blink" ng-click="cmdJoinGame()" >Click to Join Game</button>
     </div>
 </div>
 </div>

</body>
</html>