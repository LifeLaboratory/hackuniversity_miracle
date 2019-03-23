SQL_SELECT_RECIPE_LIST = """
SELECT id_recipe, name, total_time FROM recipe LIMIT 20
"""

SQL_SELECT_RECIPE_DESCRIPTION = """
SELECT 
  r.id_recipe AS id_recipe, 
  r.description AS recipe_description,
  r.total_time,
  rs.ord,
  rs.time_step,
  rs.description AS step_description
FROM recipe r 
  LEFT JOIN recipe_steps rs ON r.id_recipe=rs.id_recipe 
WHERE 
  r.id_recipe={id_recipe}
ORDER BY ord
"""