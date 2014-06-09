'use strict';

angular.module('app.services', [])
    .service('RPCService', function($http,$q){
        return{
            newGame: function() {

                var deferred = $q.defer();
                $http.get(BASE_URL + "/game/startNewGame/").success(function(data){
                    deferred.resolve(data.data);
                }).error(function(data){
                        alert("Something wrong happened")
                        //GlobalMessagesService.somethingReallyBadHappenedError();
                    });
                return deferred.promise;
            } ,
            endGame: function() {

                var deferred = $q.defer();
                $http.get(BASE_URL + "/game/endGame/").success(function(data){
                    deferred.resolve(data.data);
                }).error(function(data){
                        alert("Something wrong happened")
                        //GlobalMessagesService.somethingReallyBadHappenedError();
                    });
                return deferred.promise;
            },
            joinGame: function() {

                var deferred = $q.defer();
                $http.get(BASE_URL + "/game/joinGame/" ).success(function(data){
                    deferred.resolve(data.data);
                }).error(function(data){
                        alert("Something wrong happened")
                        //GlobalMessagesService.somethingReallyBadHappenedError();
                    });
                return deferred.promise;
            },
            playerMove: function(action) {

                var deferred = $q.defer();
                $http.get(BASE_URL + "/game/getPlayerMove/" + action ).success(function(data){
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
                $http.get(BASE_URL + "/game/tableStatus/" ).success(function(data){
                    deferred.resolve(data);
                }).error(function(data){
                        alert("Something wrong happened" )
                        //GlobalMessagesService.somethingReallyBadHappenedError();
                    });
                return deferred.promise;
            }
            /*,
            certifyForTask: function(raterIds, taskDef) {
                console.log(arguments)
                var params = {};
                params.raterIds = raterIds;
                params.taskDef = taskDef;
                console.log(params)

                var deferred = $q.defer();
                $http.post(BASE_URL + "/adminRater/certifyRatersForTask", params).success(function(data){
                    deferred.resolve(data.data);
                }).error(function(data){
                        GlobalMessagesService.somethingReallyBadHappenedError();
                    });
                return deferred.promise;
            },
            raterDetails: function(raterId) {
                var deferred = $q.defer();
                $http.get(BASE_URL + "/adminRater/raterDetails/" + raterId).success(function(data){
                    deferred.resolve(data.data);
                }).error(function(data){
                        GlobalMessagesService.somethingReallyBadHappenedError();
                    });
                return deferred.promise;
            }   */
        }
    });