'use strict';
/* Controllers */
angular.module('app.controllers', [])
    .controller('GameTableController', ['$scope', '$window' ,'RPCService', function($scope, $window, RPCService) {

        //todo: more elegant
        $scope.currentUser = $window.currentPlayer
        console.warn($scope.currentUser)

        $scope.game = {};
        $scope.players = [];
        $scope.dealer = {hand:null};
        $scope.generalMessage = [];
        $scope.lastGameRound = null
        $scope.currentUserAskedToJoin = false

       //on load, get table status via ajax get
        RPCService.getTableStatus().then(function(data){
         //  console.info("getTableStatus", data)
            angular.forEach(data, function(dataItem, index){
                    handleNewInput(dataItem)
                }
            );
        });


        $scope.safeApply = function(fn) {
            var phase = this.$root.$$phase;
            if(phase == '$apply' || phase == '$digest') {
                if(fn && (typeof(fn) === 'function')) {
                    fn();
                }
            } else {
                this.$apply(fn);
            }
        };


        var handleNewInput =  function(data){
            /*
             *     enum GameStatus{ DEALER_BUST, DEALER_WON, PLAY}
             //todo: add round 'BET'
             enum Round{ START(0), DEAL(1), PLAYER_MOVE(2), RESOLUTION(3), END(4)
             */
            var entity = data.entity
            switch (data.command){
                case 'update.game':
                    //track changes
                    if(entity.round != $scope.game.round){
                        onRoundChange(entity.round, $scope.game.round)
                    }
                    $scope.game.status = entity.status
                    $scope.game.round = entity.round
                    $scope.game.timeStarted = entity.timeStarted

                    $scope.game.activePlayer = entity.activePlayer
                    $scope.game.newGameCountDown = entity.newGameCountDown
                    break;

                case 'update.hand':
                    if(entity.userId == 0){
                        $scope.dealer.hand = entity.hand
                       // console.info("dealer newhand",$scope.dealer.hand)
                    }else{
                        angular.forEach($scope.players,
                            function(player, index){
                                if(player.userId == entity.userId){
                                    $scope.players[index].hand = entity.hand
                                }
                            });

                    }
                    break;

                case 'update.player':
                    var gamePlayerUpdated = null
                    /* if already exists, replace, otherwise append new player*/
                    angular.forEach($scope.players,
                        function(player, index){
                            if(player.userId == entity.userId){
                                $scope.players[index] = entity
                                gamePlayerUpdated = $scope.players[index]
                            }
                        });
                    if(!gamePlayerUpdated){
                        console.error("** gamePlayerUpdated",gamePlayerUpdated, $scope.players)
                        $scope.players.push(entity)
                    }
                    break;
                case 'generalMessage':
                    console.warn("message",entity)
                    $scope.generalMessage.message = entity.message
                    $scope.generalMessage.alertLevel = entity.alertLevel
                    break
            }
            $scope.safeApply()
        };

        $scope.socket = []
        $scope.initSockets = function() {
            $scope.socket.client = new SockJS(BASE_URL + '/stomp');
            $scope.socket.stomp = Stomp.over($scope.socket.client);
            $scope.socket.stomp.connect({}, function() {
                $scope.socket.stomp.subscribe("/topic/pushGameStatus", function(data){
                    console.warn('data: ',data.body)
                    if(data.body && typeof data.body === 'string'){
                        var data = JSON.parse(data.body)
                        handleNewInput(data)
                    }
                });
            });
            $scope.socket.client.onclose = $scope.reconnect;
        };

        $scope.initSockets();


        $scope.cmdNewGame = RPCService.newGame;
        $scope.cmdEndGame = RPCService.endGame;
        $scope.cmdJoinGame = function(){
            $scope.currentUserAskedToJoin = true;
            RPCService.joinGame();
        }

        $scope.cmdPlayerMove = function(action){

            $scope.isPlayerMoveRequired = false
            RPCService.playerMove(action)
        }

        $scope.resetMessage = function(){
            $scope.messages = [];
        }

        function onRoundChange(newRound, oldRound){
            $scope.resetMessage();
            if(newRound == 'START'){
                $scope.players = []
                $scope.dealer = {hand:null}

            }

            if(newRound == 'GAMEOVER'){
                $scope.currentUserAskedToJoin = false;

            }
        }



        $scope.getPlayerById = function(id){
            var gamePlayer = null;
            angular.forEach($scope.players,
                function(player, index){
                    if(player.userId == id){
                        gamePlayer = $scope.players[index].hand
                    }
                });
            console.log("gameplayer:: ",gamePlayer)
            return gamePlayer
        }

        $scope.isCurrentPlayerInGame = function(){
            return ($scope.getPlayerById($scope.currentUser.userId) !== null)? true : false;
        }

    }]);
