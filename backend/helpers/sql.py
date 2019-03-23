SQL_SELECT_RECIPE_LIST = """
SELECT id, name FROM reciple LIMIT 20
"""

SQL_SELECT_RECIPE_DESCRIPTION = """
SELECT 
  r.id_recipe, 
  r.name, 
  r.description,
  r.duration, 
  rs.seq_number,
  rs.duration AS ,
FROM recipe r 
  LEFT JOIN recipe_steps rs ON r.id_recipe=rs.id_recipe 
ORDER BY     
"""