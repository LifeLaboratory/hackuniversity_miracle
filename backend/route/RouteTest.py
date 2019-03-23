import json
from flask_restful import Resource, reqparse
from flask import jsonify
import backend.helpers.sql as sql_request
from backend.api_db.api_pg import Gis as gs
import backend.helpers.names as names
import backend.helpers.base_errors as errors

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
    def __init__(self):
        self._parser = reqparse.RequestParser()
        self._parser.add_argument(names.ID_RECIPE)
        self.__args = self._parser.parse_args()

    def parse_data(self):
        try:
            data = dict()
            data[names.ID_RECIPE] = self.__args.get(names.ID_RECIPE, None)
        except:
            return errors.PARSE_DATA, None
        if data[names.ID_RECIPE] is None:
            return errors.PARSE_DATA, None
        else:
            return errors.OK, data

    def post(self):
        error, data = self.parse_data()
        db_result = {}
        if error == errors.OK:
            db_message, db_result = gs.SqlQuery(sql_request.SQL_SELECT_RECIPE_DESCRIPTION.
                                                format(id_recipe=data[names.ID_RECIPE]))
        # return jsonify(result)
        return db_result


class RouteSetRecipeScore(Resource):
    def post(self):
        result = {"status": 0}
        # return jsonify(result)
        return result



