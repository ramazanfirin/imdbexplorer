(function() {
    'use strict';

    angular
        .module('imdbexplorerApp')
        .controller('PersonDialogController', PersonDialogController);

    PersonDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Person'];

    function PersonDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Person) {
        var vm = this;

        vm.person = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.person.id !== null) {
                Person.update(vm.person, onSaveSuccess, onSaveError);
            } else {
                Person.save(vm.person, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('imdbexplorerApp:personUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setAfid = function ($file, person) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        person.afid = base64Data;
                        person.afidContentType = $file.type;
                    });
                });
            }
        };
        vm.datePickerOpenStatus.insert = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
