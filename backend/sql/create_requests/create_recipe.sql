CREATE TABLE public.recipe
(
  id_recipe integer NOT NULL DEFAULT nextval('recipe_id_recipe_seq'::regclass),
  name text NOT NULL,
  description text NOT NULL,
  CONSTRAINT recipe_pk PRIMARY KEY (id_recipe)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.recipe
  OWNER TO postgres;
