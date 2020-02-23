(function() {
    'use strict';
    angular
        .module('imdbexplorerApp')
        .factory('MatchQuery', MatchQuery);

    MatchQuery.$inject = ['$resource'];

    function MatchQuery ($resource) {
        var resourceUrl =  'api/match-queries/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
