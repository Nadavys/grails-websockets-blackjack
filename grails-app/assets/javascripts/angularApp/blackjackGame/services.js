'use strict';

angular.module('app.services', [])
    .service('RPCService',[ '$http','$q', '$window', function($http,$q, $window){
        return{
            newGame: function() {

                var deferred = $q.defer();
                $http.get($window.BASE_URL + "/game/startNewGame/").success(function(data){
                    deferred.resolve(data.data);
                }).error(function(data){
                        alert("Something wrong happened")
                        //GlobalMessagesService.somethingReallyBadHappenedError();
                    });
                return deferred.promise;
            } ,
            endGame: function() {

                var deferred = $q.defer();
                $http.get($window.BASE_URL + "/game/endGame/").success(function(data){
                    deferred.resolve(data.data);
                }).error(function(data){
                        alert("Something wrong happened")
                        //GlobalMessagesService.somethingReallyBadHappenedError();
                    });
                return deferred.promise;
            },
            joinGame: function() {

                var deferred = $q.defer();
                $http.get($window.BASE_URL + "/game/joinGame/" ).success(function(data){
                    deferred.resolve(data.data);
                }).error(function(data){
                        alert("Something wrong happened")
                        //GlobalMessagesService.somethingReallyBadHappenedError();
                    });
                return deferred.promise;
            },
            playerMove: function(action) {

                var deferred = $q.defer();
                $http.get($window.BASE_URL + "/game/getPlayerMove/" + action ).success(function(data){
                    deferred.resolve(data);
                }).error(function(data){
                        alert("Something wrong happened" )
                        //GlobalMessagesService.somethingReallyBadHappenedError();
                    });
                return deferred.promise;
            },
            //new player, get snapshot of table status
            getTableStatus: function() {

                var deferred = $q.defer();
                $http.get($window.BASE_URL + "/game/tableStatus/" ).success(function(data){
                    deferred.resolve(data);
                }).error(function(data){
                        alert("Something wrong happened" )
                        //GlobalMessagesService.somethingReallyBadHappenedError();
                    });
                return deferred.promise;
            }

        }
    }]);