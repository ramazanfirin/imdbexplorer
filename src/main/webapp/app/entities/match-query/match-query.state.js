(function() {
    'use strict';

    angular
        .module('imdbexplorerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('match-query', {
            parent: 'entity',
            url: '/match-query?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imdbexplorerApp.matchQuery.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/match-query/match-queries.html',
                    controller: 'MatchQueryController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('matchQuery');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('match-query-detail', {
            parent: 'match-query',
            url: '/match-query/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imdbexplorerApp.matchQuery.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/match-query/match-query-detail.html',
                    controller: 'MatchQueryDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('matchQuery');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'MatchQuery', function($stateParams, MatchQuery) {
                    return MatchQuery.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'match-query',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('match-query-detail.edit', {
            parent: 'match-query-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/match-query/match-query-dialog.html',
                    controller: 'MatchQueryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MatchQuery', function(MatchQuery) {
                            return MatchQuery.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('match-query.new', {
            parent: 'match-query',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/match-query/match-query-dialog.html',
                    controller: 'MatchQueryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                image: null,
                                imageContentType: null,
                                url: null,
                                result: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('match-query', null, { reload: 'match-query' });
                }, function() {
                    $state.go('match-query');
                });
            }]
        })
        .state('match-query.edit', {
            parent: 'match-query',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/match-query/match-query-dialog.html',
                    controller: 'MatchQueryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MatchQuery', function(MatchQuery) {
                            return MatchQuery.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('match-query', null, { reload: 'match-query' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('match-query.delete', {
            parent: 'match-query',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/match-query/match-query-delete-dialog.html',
                    controller: 'MatchQueryDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['MatchQuery', function(MatchQuery) {
                            return MatchQuery.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('match-query', null, { reload: 'match-query' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
