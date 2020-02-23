(function() {
    'use strict';

    angular
        .module('imdbexplorerApp')
        .controller('MatchQueryDetailController', MatchQueryDetailController);

    MatchQueryDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'MatchQuery', 'Person'];

    function MatchQueryDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, MatchQuery, Person) {
        var vm = this;

        vm.matchQuery = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('imdbexplorerApp:matchQueryUpdate', function(event, result) {
            vm.matchQuery = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
