import json
from flask_restful import Resource, reqparse
from flask import jsonify


class RouteTest(Resource):

    def post(self):
        result = {'id': 1, 'user': 'boris007_i_kabuki'}
        # return jsonify(result)
        return result