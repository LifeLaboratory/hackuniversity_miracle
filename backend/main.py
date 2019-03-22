# coding=utf-8
import sys
import os
sys.path.append(os.getcwd()+'/../')
sys.path.append(os.getcwd()+'../')
import flask
from flask_restful import Api
from backend.route.route_list import ROUTES


_app = flask.Flask(__name__)
_app.config['JSON_AS_ASCII'] = False
api = Api(_app)
HEADER = {'Access-Control-Allow-Origin': '*'}


@_app.errorhandler(404)
def not_found(error):
    return {'error': 'Not found'}, 404


if __name__ == '__main__':
    try:
        for route_class, route in ROUTES.items():
            api.add_resource(route_class, route)
        _app.run(host='0.0.0.0', port=12452, threaded=True)
    except Exception as e:
        print('Main except = ', e)