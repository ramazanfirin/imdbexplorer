(function() {
    'use strict';

    angular
        .module('imdbexplorerApp')
        .controller('PersonDetailController', PersonDetailController);

    PersonDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Person'];

    function PersonDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Person) {
        var vm = this;

        vm.person = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('imdbexplorerApp:personUpdate', function(event, result) {
            vm.person = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
