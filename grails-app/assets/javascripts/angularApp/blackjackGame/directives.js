'use strict';

/* Directives */

angular.module('app.directives', []).directive('hand', function() {
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
               console.log('controller',$scope.hand)

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
