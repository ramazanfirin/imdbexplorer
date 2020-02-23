(function() {
    'use strict';

    angular
        .module('imdbexplorerApp')
        .controller('MatchQueryDeleteController',MatchQueryDeleteController);

    MatchQueryDeleteController.$inject = ['$uibModalInstance', 'entity', 'MatchQuery'];

    function MatchQueryDeleteController($uibModalInstance, entity, MatchQuery) {
        var vm = this;

        vm.matchQuery = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            MatchQuery.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
