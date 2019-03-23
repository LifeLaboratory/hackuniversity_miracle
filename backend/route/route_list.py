from .RouteTest import *


PARAMS = {
    RouteGetRecipeList: {},
    RouteGetRecipeDescription: {"id": 1},
    RouteSetRecipeScore: {"id": 1, "score": 4}
}

"""
ANSWER = {
    RouteGetRecipeList: {
        [
            {
                "id": 1,
                "name": 'First'
            },
            {
                "id": 2,
                "name": 'Second'
            }
        ]
    },
    RouteGetRecipeDescription: {
        "id": 1,
        "name": "Last",
        "desc": "This is the best recipe",
        "duration": 123,
        "steps": [
            {
                "id": 1,
                "desc": "Desc_1",
                "duration": 0
            },
            {
                "id": 2,
                "desc": "Desc_2",
                "duration": 122
            }
        ]
    },
    RouteSetRecipeScore: {"status": 0} # OR "status": 1 if FALSE
}

"""
ROUTES = {
    RouteTest: '/get_json_data',
    RouteGetRecipeList: '/get_recipe_list',
    RouteGetRecipeDescription: '/get_recipe_desc',
    RouteSetRecipeScore: '/set_recipe_score'
}
