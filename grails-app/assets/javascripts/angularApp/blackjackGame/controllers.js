'use strict';
/* Controllers */
angular.module('app.controllers', [])
    .controller('GameTableController', ['$scope', '$window', '$timeout','RPCService', function($scope, $window, $timeout, RPCService) {

        //todo: more elegant
        $scope.currentPlayerId = $window.currentPlayerId
        $scope.currentPlayer = null
        console.warn("currentPlayerId", $scope.currentPlayerId)

        $scope.game = {};
        $scope.players = [];
        $scope.dealer = {id:0, hand:null, name:"Dealer"};
        $scope.generalMessage = [];
        $scope.lastGameRound = null
        $scope.currentUserAskedToJoin = false

       //on load, get table status via ajax get
        RPCService.getTableStatus().then(function(data){
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
             //todo: add round 'BET'
             enum Round{ PLACE_BETS(0), DEAL(1), PLAYER_MOVE(2), RESOLUTION(3), END(4)
             */
            var entity = data.entity
            switch (data.command){
                case 'update.game':
                    //track changes
                    if(entity.round != $scope.game.round){
                        onRoundChange(entity.round, $scope.game.round)
                    }
                    $scope.game.round = entity.round
                    $scope.game.timeStarted = entity.timeStarted

                    $scope.game.activePlayer = entity.activePlayer
                    $scope.game.countDownTimer = entity.countDownTimer
                    break;

                case 'update.hand':
                    if(entity.type === "DEALER"){
                        $scope.dealer.hand = entity.hand
                       // console.info("dealer newhand",$scope.dealer.hand)
                    }else{
                        angular.forEach($scope.players,
                            function(player, index){
                                if(player.id == entity.id){
                                    $scope.players[index].hand = entity.hand
                                }
                            });
                    }
                    break;

                case 'update.player':
                    if(entity.type === "DEALER"){
                    $scope.dealer = entity
                    // console.info("dealer newhand",$scope.dealer.hand)
                }else{
                    var gamePlayerUpdated = null
                    /* if already exists, replace, otherwise append new player*/
                    angular.forEach($scope.players,
                        function(player, index){
                            if(player.id == entity.id){
                                $scope.players[index] = entity
                                gamePlayerUpdated = $scope.players[index]
                            }
                        });
                    if(!gamePlayerUpdated){
                           $scope.players.push(entity);
                    }

                        if($scope.currentPlayerId == entity.id ){
                            $scope.currentPlayer = entity
                        }
                    }
                    break;
                case 'generalMessage':
                    $scope.generalMessage.message = entity.message
                    $scope.generalMessage.alertLevel = entity.alertLevel
                    break
            }
            $scope.safeApply();
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



        $scope.resetMessage = function(){
            $scope.messages = [];
        }

        function onRoundChange(newRound, oldRound){
            $scope.resetMessage();
            if(newRound == 'PLACE_BETS'){
                $scope.players = []
                $scope.currentPlayer = null
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
                    if(player.id == id){
                        gamePlayer = $scope.players[index].hand
                    }
                });
            return gamePlayer
        }

        $scope.isCurrentPlayerInGame = function(){
            return ($scope.getPlayerById($scope.currentPlayerId) !== null)? true : false;
        }

    }]);
