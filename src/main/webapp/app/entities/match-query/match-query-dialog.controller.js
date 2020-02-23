(function() {
    'use strict';

    angular
        .module('imdbexplorerApp')
        .controller('MatchQueryDialogController', MatchQueryDialogController);

    MatchQueryDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'MatchQuery', 'Person'];

    function MatchQueryDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, MatchQuery, Person) {
        var vm = this;

        vm.matchQuery = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.people = Person.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.matchQuery.id !== null) {
                MatchQuery.update(vm.matchQuery, onSaveSuccess, onSaveError);
            } else {
                MatchQuery.save(vm.matchQuery, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('imdbexplorerApp:matchQueryUpdate', result);
            //$uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setImage = function ($file, matchQuery) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        matchQuery.image = base64Data;
                        matchQuery.imageContentType = $file.type;
                    });
                });
            }
        };

    }
})();
