from backend.api_db.api_pg import Gis as gs

SQL_CREATE_TABLE_TEST = """
CREATE TABLE "common_users" (
"user_id" serial PRIMARY KEY,
"user_name" varchar NOT NULL,
"timestamp" TIMESTAMP NOT NULL
) WITH (
OIDS=FALSE
)
"""

SQL_INSERT_DATA_TEST = """
INSERT INTO common_users (user_name, timestamp)
VALUES ('{user_name}', '{timestamp}') RETURNING user_id
"""

SQL_SELECT_DATA_TEST_ALL = """
SELECT * FROM common_users 
"""


SQL_DROP_TABLE = """
DROP TABLE "common_users"
"""

def db_create():
    db_msg, db_result = gs.SqlQuery(SQL_CREATE_TABLE_TEST)
    print(db_msg)
    return db_result


def db_drop_table():
    db_msg, db_result = gs.SqlQuery(SQL_DROP_TABLE)
    print(db_msg)
    return db_result


def db_is_present():
    return


def db_data_insert_test(user_name, timestamp):
    db_msg, db_result = gs.SqlQuery(SQL_INSERT_DATA_TEST.format(user_name=user_name,
                                                                timestamp=timestamp))
    print(db_msg)
    return db_result


def db_data_select_test():
    db_msg, db_result = gs.SqlQuery(SQL_SELECT_DATA_TEST_ALL)
    print(db_msg)
    return db_result


def db_select_tables(table_name):
    db_msg, db_result = gs.SqlQuery("")
    return db_result


def test_db_interconnection():
    print(db_drop_table())
    print(db_create())
    print(db_data_insert_test("denis", "2019-03-22 01:33:22"))
    print(db_data_insert_test("roma", "2019-02-12 11:53:22"))
    print(db_data_insert_test("borya", "2017-12-12 05:03:24"))
    print(db_data_select_test())


test_db_interconnection()

