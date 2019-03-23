# -*- coding: utf-8 -*-
import psycopg2
from psycopg2.extras import RealDictCursor
from backend.config.config import DATABASE
import logging
import json

from datetime import date, datetime


def db_connect_new():
    try:
        connect = psycopg2.connect("dbname='{dbname}' user='{user}' host='{host}' password='{password}'".format(**DATABASE))
        return connect, connect.cursor(cursor_factory=RealDictCursor)
    except:
        print('Fatal error: connect database')
        raise

class Gis:
    @staticmethod
    def SqlQuery(query):
        """
        Метод выполняет SQL запрос к базе
        :param query: str SQL запрос
        :return: dict результат выполнения запроса
        """
        connect, current_connect = db_connect_new()
        result = None
        db_result = None
        try:
            #print(query)
            current_connect.execute(query)
            connect.commit()
        except psycopg2.Error as e:
            db_result = e.pgerror
            return db_result, result
        finally:
            try:
                result = current_connect.fetchall()
            except psycopg2.Error as e:
                return db_result, result
            connect.close()
            return db_result, result

    @staticmethod
    def __converter_data(param):
        if isinstance(param, date):
            return param.strftime('%Y.%m.%d %H:%M:%S')
        if isinstance(param, datetime):
            return param.strptime('%Y.%m.%d %H:%M:%S')

    @staticmethod
    def converter(js):
        """
        Метод преобразовывает передаваемый json в Dict и наоборот
        :param js: str или json
        :return: str или dict преобразованный элемент
        """
        return json.dumps(js, default=Gis.__converter_data) if isinstance(js, dict) \
            else json.loads(js)

class Sql:
    @staticmethod
    def connect():
        config_connect = "dbname='{dbname}' user='{user}' host='{host}' password='{password}'"
        try:
            connect = psycopg2.connect(config_connect.format(**DATABASE))
            return connect, connect.cursor(cursor_factory=RealDictCursor)
        except:
            raise

    @staticmethod
    def exec(query=None, args=None, file=None):
        try:
            return Sql._switch(query=query, args=args, file=file)
        except:
            return None

    @staticmethod
    def _switch(query=None, args=None, file=None):
        if query and args:
            return Sql._query_exec_args(query, args)
        if query and not args:
            return Sql._query_exec(query)
        if file:
            return Sql._query_file_exec(file)
        return None

    @staticmethod
    def _query_exec(query):
        return Sql._exec(query)

    @staticmethod
    def _query_file_exec(file):
        with open(file, 'r') as f:
            query = f.read()
            return Sql._exec(query)

    @staticmethod
    def _query_exec_args(query, args):
        query.format(**args)
        return Sql._exec(query)

    @staticmethod
    def _exec(query):
        """
        Метод выполняет SQL запрос к базе
        :param query: str SQL запрос
        :return: dict результат выполнения запроса
        """
        connect, current_connect = Sql.connect()
        result = None
        try:
            current_connect.execute(query)
        except psycopg2.Error as e:
            print(e.pgerror)
            print(e.diag.message_primary)
            print(psycopg2.errorcodes.lookup(e.pgcode))
        finally:
            try:
                result = current_connect.fetchall()
                connect.commit()
            except:
                connect.rollback()
            finally:
                connect.close()
                return result
