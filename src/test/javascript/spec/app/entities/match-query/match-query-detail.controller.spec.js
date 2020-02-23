'use strict';

describe('Controller Tests', function() {

    describe('MatchQuery Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockMatchQuery, MockPerson;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockMatchQuery = jasmine.createSpy('MockMatchQuery');
            MockPerson = jasmine.createSpy('MockPerson');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'MatchQuery': MockMatchQuery,
                'Person': MockPerson
            };
            createController = function() {
                $injector.get('$controller')("MatchQueryDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'imdbexplorerApp:matchQueryUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
