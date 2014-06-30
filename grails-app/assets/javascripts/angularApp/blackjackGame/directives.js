'use strict';

/* Directives */

angular.module('app.directives', ['app.services'])

    .directive('hand', function() {
        return {
            transclude: true,
            restrict: 'A',
            scope: {
                hand: '=hand'
            },
            templateUrl: window.BASE_URL + '/partials/directives/hand.tpl.html',

            link: function($scope, $element, $attrs){

                $scope.$watch('hand', function(newVal) {
                    $scope.hand = newVal;
                });
            },
            controller: function($scope){
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
            }
        }
    })

    .directive('gameplayer', function(RPCService) {
        return {
            transclude: true,
            restrict: 'AE',
            scope: {
                player: '=player' ,
                game: '=game',
                currentPlayerId: '=currentPlayerId'
            },
            templateUrl: window.BASE_URL + '/partials/directives/player.tpl.html',

            link: function($scope, $element, $attrs){

                console.warn("gameplayer", $scope.player)

                $scope.$watch('player', function(newVal) {
                    $scope.player = newVal;
                });

                $scope.$watch('game', function(newVal) {
                    $scope.game = newVal;
                });

            },
            controller: function($scope){

                $scope.cmdPlayerMove = function(action){

                    $scope.isPlayerMoveRequired = false
                    RPCService.playerMove(action)
                }


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
            }
        }
    });
