import json
from flask_restful import Resource, reqparse
from flask import jsonify
import backend.helpers.sql as sql_request
from backend.api_db.api_pg import Gis as gs

class RouteTest(Resource):

    def post(self):
        result = {'id': 1, 'user': 'boris007_i_kabuki'}
        # return jsonify(result)
        return result


class RouteGetRecipeList(Resource):
    def post(self):
        db_message, db_result = gs.SqlQuery(sql_request.SQL_SELECT_RECIPE_LIST)
#        result = {[{"id": 1,"name": 'First'},{"id": 2,"name": 'Second'}]}
        # return jsonify(result)
        print(db_message)
        return db_result


class RouteGetRecipeDescription(Resource):
    def post(self):
        result = {
            "id": 1,
            "name": "Last",
            "description": "This is the best recipe",
            "duration": 123,
            "steps": [
                {
                    "id": 1,
                    "seq_number": 1,
                    "description": "Desc_1",
                    "duration": 0
                },
                {
                    "id": 2,
                    "seq_number": 2,
                    "description": "Desc_2",
                    "duration": 122
                }
            ]
        }
        # return jsonify(result)
        return result


class RouteSetRecipeScore(Resource):
    def post(self):
        result = {"status": 0}
        # return jsonify(result)
        return result
