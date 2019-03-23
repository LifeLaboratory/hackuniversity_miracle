from backend.api_db.api_pg import Gis as gs

SQL_SELECT_RECIPE_DESCRIPTION = """
SELECT 
  r.id_recipe AS id_recipe, 
  r.description AS recipe_description,
  rs.ord,
  rs.time_step,
  rs.description AS step_description
FROM recipe r 
  LEFT JOIN recipe_steps rs ON r.id_recipe=rs.id_recipe 
WHERE 
  r.id_recipe={id_recipe}
ORDER BY ord
"""

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
    print(gs.SqlQuery(SQL_SELECT_RECIPE_DESCRIPTION.format(id_recipe=1)))

#test_db_interconnection()
